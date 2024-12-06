package dal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionaryDAO implements IDictionaryDAO {

	
    private Connection connection;

    public DictionaryDAO() {
        try {
            this.connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> importCSV(String filePath) {
        List<String> duplicateWords = new ArrayList<>();
        try {
            connection.setAutoCommit(false); // Begin transaction

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length < 3) {
                        System.err.println("Invalid line format: " + line);
                        continue;
                    }
                    String word = values[0].trim();
                    String urduMeanings = values[1].trim();
                    String persianMeanings = values[2].trim();

                    try {
                        int wordId = saveWord(word);
                        if (wordId == -1) {
                            duplicateWords.add(word); // Track duplicates
                        } else {
                            saveMeanings(wordId, urduMeanings.split("/"), "urdu_meanings", "urdu_mean");
                            saveMeanings(wordId, persianMeanings.split("/"), "persian_meanings", "persian_mean");
                        }
                    } catch (SQLException e) {
                        System.err.println("Error processing word '" + word + "': " + e.getMessage());
                    }
                }
            }

            connection.commit(); // Commit transaction
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + filePath, e);
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction on error
            } catch (SQLException rollbackEx) {
                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
        return duplicateWords;
    }

    private int saveWord(String word) throws SQLException {
        if (isWordDuplicate(word)) {
            System.out.println("Word '" + word + "' already exists in the dictionary.");
            return -1;
        }

        String query = "INSERT INTO words (word) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, word);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return generated ID
                }
            }
        }
        throw new SQLException("Failed to insert word '" + word + "' into database.");
    }

    private void saveMeanings(int wordId, String[] meanings, String tableName, String columnName) throws SQLException {
        String query = String.format("INSERT INTO %s (word_id, %s) VALUES (?, ?)", tableName, columnName);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (String meaning : meanings) {
                stmt.setInt(1, wordId);
                stmt.setString(2, meaning.trim());
                stmt.addBatch();
            }
            stmt.executeBatch(); // Execute all inserts in a batch
        }
    }

    private boolean isWordDuplicate(String word) throws SQLException {
        String query = "SELECT id FROM words WHERE word = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, word);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}