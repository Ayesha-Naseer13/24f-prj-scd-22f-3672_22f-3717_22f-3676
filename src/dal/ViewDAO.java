package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.Translation;

public class ViewDAO implements IViewDAO {

    public ViewDAO() {
		
	}

	private static final String URL = "jdbc:mysql://localhost:3306/dictionary";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    

    // Method to fetch all words
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        String query = "SELECT word FROM words";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                words.add(rs.getString("word"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Method to fetch word details
    public List<Translation> getWordDetails(String word) {
        List<Translation> translations = new ArrayList<>();
        String query = "SELECT p.part_of_speech, p.stem_word, p.root_word, "
                     + "COALESCE(u.urdu_mean, '') AS urdu_mean, "
                     + "COALESCE(pe.persian_mean, '') AS persian_mean "
                     + "FROM words w "
                     + "LEFT JOIN urdu_meanings u ON w.id = u.word_id "
                     + "LEFT JOIN persian_meanings pe ON w.id = pe.word_id "
                     + "LEFT JOIN pos p ON w.id = p.word_id "
                     + "WHERE w.word = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, word);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String pos = rs.getString("part_of_speech");
                    String stemWord = rs.getString("stem_word");
                    String rootWord = rs.getString("root_word");
                    String urduMeaning = rs.getString("urdu_mean");
                    String persianMeaning = rs.getString("persian_mean");

                    // Only add if at least one meaning exists
                    if (!urduMeaning.isEmpty() || !persianMeaning.isEmpty()) {
                        translations.add(new Translation(urduMeaning, persianMeaning, pos, stemWord, rootWord,word));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return translations;
    }
}
