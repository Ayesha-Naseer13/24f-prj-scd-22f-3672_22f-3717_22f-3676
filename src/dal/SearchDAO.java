package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchDAO implements ISearchDAO {
    private Connection connection;

    public SearchDAO() {
        try {
            this.connection = DatabaseConfig.getConnection(); // Open connection once
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getSuggestions(String input, boolean searchByKey, String language) {
        List<String> suggestions = new ArrayList<>();
        String query;

        if (searchByKey) {
            query = "SELECT word FROM words WHERE word LIKE ?";
        } else {
            query = language.equalsIgnoreCase("Urdu")
                    ? "SELECT DISTINCT urdu_mean FROM urdu_meanings WHERE urdu_mean LIKE ?"
                    : "SELECT DISTINCT persian_mean FROM persian_meanings WHERE persian_mean LIKE ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, input + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                suggestions.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching suggestions: " + e.getMessage(), e);
        }

        return suggestions;
    }

    @Override
    public List<String> searchWord(String searchTerm, boolean searchByKey, String language) {
        List<String> results = new ArrayList<>();
        String query;

        if (searchByKey) {
            query = "SELECT " +
                    "GROUP_CONCAT(DISTINCT u.urdu_mean SEPARATOR ', ') AS urdu_mean, " +
                    "GROUP_CONCAT(DISTINCT p.persian_mean SEPARATOR ', ') AS persian_mean " +
                    "FROM words w " +
                    "LEFT JOIN urdu_meanings u ON w.id = u.word_id " +
                    "LEFT JOIN persian_meanings p ON w.id = p.word_id " +
                    "WHERE w.word = ?";
        } else {
            query = language.equalsIgnoreCase("Urdu")
                    ? "SELECT w.word, " +
                      "GROUP_CONCAT(DISTINCT p.persian_mean SEPARATOR ', ') AS persian_mean " +
                      "FROM urdu_meanings u " +
                      "JOIN words w ON u.word_id = w.id " +
                      "LEFT JOIN persian_meanings p ON w.id = p.word_id " +
                      "WHERE u.urdu_mean = ? " +
                      "GROUP BY w.word"
                    : "SELECT w.word, " +
                      "GROUP_CONCAT(DISTINCT u.urdu_mean SEPARATOR ', ') AS urdu_mean " +
                      "FROM persian_meanings p " +
                      "JOIN words w ON p.word_id = w.id " +
                      "LEFT JOIN urdu_meanings u ON w.id = u.word_id " +
                      "WHERE p.persian_mean = ? " +
                      "GROUP BY w.word";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, searchTerm);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (searchByKey) {
                    String urdu = rs.getString("urdu_mean");
                    String persian = rs.getString("persian_mean");
                    results.add("Urdu: " + (urdu != null ? urdu : "N/A") + ", Persian: " + (persian != null ? persian : "N/A"));
                } else {
                    String word = rs.getString("word");
                    if (language.equalsIgnoreCase("Urdu")) {
                        String persian = rs.getString("persian_mean");
                        results.add("Word: " + word + (persian != null ? ", Persian: " + persian : ""));
                    } else {
                        String urdu = rs.getString("urdu_mean");
                        results.add("Word: " + word + (urdu != null ? ", Urdu: " + urdu : ""));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching search results: " + e.getMessage(), e);
        }

        return results;
    }

 
    @Override
    public void addSearchTermToHistory(String searchTerm) {
        String query = "INSERT INTO search_history (search_term, search_date) VALUES (?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, searchTerm);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding search term to history: " + e.getMessage(), e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close(); // Explicitly close the connection
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
