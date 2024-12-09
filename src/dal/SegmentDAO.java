package dal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SegmentDAO implements ISegmentDAO{
    // Query to fetch Persian and Urdu meanings in one call
    private static final String MEANINGS_QUERY = 
        "SELECT persian_mean, urdu_mean FROM words w " +
        "LEFT JOIN persian_meanings pm ON w.id = pm.word_id " +
        "LEFT JOIN urdu_meanings um ON w.id = um.word_id " +
        "WHERE word = ?";

    /**
     * Fetches meanings for an array of words.
     *
     * @param words Array of words to fetch meanings for.
     * @return Map with the word as key and a nested map for meanings.
     */
    public Map<String, Map<String, String>> getMeanings(String[] words) {
        Map<String, Map<String, String>> meanings = new HashMap<>();

        try (Connection connection = DatabaseConfig.getConnection()) {
            // Prepare statement for the meanings query
            PreparedStatement stmt = connection.prepareStatement(MEANINGS_QUERY);

            // Iterate over each word to fetch its meanings
            for (String word : words) {
                Map<String, String> wordMeanings = new HashMap<>();

                stmt.setString(1, word); // Set the word parameter
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        wordMeanings.put("Persian", result.getString("persian_mean") != null ? result.getString("persian_mean") : "Meaning not found");
                        wordMeanings.put("Urdu", result.getString("urdu_mean") != null ? result.getString("urdu_mean") : "Meaning not found");
                    } else {
                        wordMeanings.put("Persian", "Meaning not found");
                        wordMeanings.put("Urdu", "Meaning not found");
                    }
                }

                // Add the word and its meanings to the map
                meanings.put(word, wordMeanings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to throw a custom exception here to be handled by the business logic layer
        }

        return meanings;
    }
}
