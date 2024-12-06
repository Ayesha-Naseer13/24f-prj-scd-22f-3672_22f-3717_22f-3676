package dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import dto.WordTranslation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomDictionaryDAO implements ICustomDictionaryDAO {

	private Connection connection;

	public CustomDictionaryDAO() {
		try {
			connectToDatabase();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to the database: " + e.getMessage(), e);
		}
	}

	private void connectToDatabase() throws SQLException {
		Properties dbProperties = loadDBProperties();

		String url = dbProperties.getProperty("db.url");
		String user = dbProperties.getProperty("db.user");
		String password = dbProperties.getProperty("db.password");

		connection = DriverManager.getConnection(url, user, password);
	}

	// Load database properties from the db.properties file
	private Properties loadDBProperties() {
		Properties properties = new Properties();
		try (InputStream input = new FileInputStream("db.properties")) {
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Error loading database properties: " + e.getMessage(), e);
		}
		return properties;
	}

	@Override
	public ObservableList<WordTranslation> getTranslationsForStory(String storyText) {
		try {
			int storyId = findOrSaveStory(storyText);
			return fetchStoryTranslations(storyId);
		} catch (SQLException e) {
			throw new RuntimeException("Database Error: Unable to process the story. " + e.getMessage(), e);
		}
	}

	private int findOrSaveStory(String storyText) throws SQLException {
		PreparedStatement checkStoryStmt = connection.prepareStatement("SELECT id FROM stories WHERE story_text = ?");
		checkStoryStmt.setString(1, storyText);
		ResultSet storyRs = checkStoryStmt.executeQuery();

		if (storyRs.next()) {
			return storyRs.getInt("id");
		} else {
			return saveStory(storyText);
		}
	}

	private int saveStory(String storyText) throws SQLException {
		PreparedStatement storyStmt = connection.prepareStatement("INSERT INTO stories (story_text) VALUES (?)",
				Statement.RETURN_GENERATED_KEYS);
		storyStmt.setString(1, storyText);
		storyStmt.executeUpdate();

		ResultSet rs = storyStmt.getGeneratedKeys();
		if (rs.next()) {
			int storyId = rs.getInt(1);
			saveStoryWords(storyId, storyText);
			return storyId;
		}
		throw new SQLException("Failed to save the story.");
	}

	private void saveStoryWords(int storyId, String storyText) throws SQLException {
		String[] words = storyText.split("\\s+");
		int sequence = 1;

		for (String word : words) {
			word = word.trim();
			if (word.isEmpty())
				continue;

			int wordId = addWord(word);
			if (wordId != -1) {
				PreparedStatement translationStmt = connection.prepareStatement(
						"INSERT INTO story_translations (story_id, word_id, sequence_number) VALUES (?, ?, ?)");
				translationStmt.setInt(1, storyId);
				translationStmt.setInt(2, wordId);
				translationStmt.setInt(3, sequence++);
				translationStmt.executeUpdate();
			}
		}
	}

	private int addWord(String word) throws SQLException {
		PreparedStatement checkStmt = connection.prepareStatement("SELECT id FROM words WHERE word = ?");
		checkStmt.setString(1, word);
		ResultSet rs = checkStmt.executeQuery();

		if (rs.next()) {
			return rs.getInt("id");
		} else {
			PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO words (word) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			insertStmt.setString(1, word);
			insertStmt.executeUpdate();

			ResultSet generatedKeys = insertStmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			}
		}
		return -1;
	}

	private ObservableList<WordTranslation> fetchStoryTranslations(int storyId) throws SQLException {
		ObservableList<WordTranslation> translations = FXCollections.observableArrayList();

		String query = """
				    SELECT words.word AS arabic,
				           urdu_meanings.urdu_mean AS urdu,
				           persian_meanings.persian_mean AS persian
				    FROM story_translations
				    JOIN words ON story_translations.word_id = words.id
				    LEFT JOIN urdu_meanings ON words.id = urdu_meanings.word_id
				    LEFT JOIN persian_meanings ON words.id = persian_meanings.word_id
				    WHERE story_translations.story_id = ?
				    ORDER BY story_translations.sequence_number
				""";

		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, storyId);

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			String arabic = rs.getString("arabic");
			String urdu = rs.getString("urdu");
			String persian = rs.getString("persian");
			translations.add(new WordTranslation(arabic, urdu, persian));
		}
		return translations;
	}

	@Override
	public String importStoryFromFile(File file) {
		StringBuilder storyText = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				storyText.append(line).append("\n");
			}
		} catch (IOException e) {
			return "Error reading file: " + e.getMessage();
		}
		return storyText.toString().trim(); // Trim to remove trailing newlines
	}
}
