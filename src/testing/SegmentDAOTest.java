package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.SegmentDAO;

class SegmentDAOTest {

    private SegmentDAO segmentDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Establish a real database connection for testing
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "");
        segmentDAO = new SegmentDAO();

        // Setup: Create tables if they don't exist
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS words (id INT AUTO_INCREMENT PRIMARY KEY, word VARCHAR(255) UNIQUE)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS persian_meanings (id INT AUTO_INCREMENT PRIMARY KEY, word_id INT, persian_mean VARCHAR(255), FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS urdu_meanings (id INT AUTO_INCREMENT PRIMARY KEY, word_id INT, urdu_mean VARCHAR(255), FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE)");

            // Insert some test data only if the word doesn't already exist
            insertWordIfNotExists(stmt, "hello", "سلام", "ہیلو");
            insertWordIfNotExists(stmt, "world", "دنیا", "دنیا");
            insertWordIfNotExists(stmt, "friend", "دوست", "دوست");
        }
    }

    private void insertWordIfNotExists(Statement stmt, String word, String persianMeaning, String urduMeaning) throws SQLException {
        stmt.executeUpdate("INSERT IGNORE INTO words (word) VALUES ('" + word + "')");
        stmt.executeUpdate("INSERT IGNORE INTO persian_meanings (word_id, persian_mean) SELECT id, '" + persianMeaning + "' FROM words WHERE word = '" + word + "'");
        stmt.executeUpdate("INSERT IGNORE INTO urdu_meanings (word_id, urdu_mean) SELECT id, '" + urduMeaning + "' FROM words WHERE word = '" + word + "'");
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop foreign key constraints temporarily to ensure seamless cleanup
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            
            // Delete all data and drop tables
            stmt.executeUpdate("DELETE FROM persian_meanings");
            stmt.executeUpdate("DELETE FROM urdu_meanings");
            stmt.executeUpdate("DELETE FROM words");
            stmt.executeUpdate("DROP TABLE IF EXISTS persian_meanings");
            stmt.executeUpdate("DROP TABLE IF EXISTS urdu_meanings");
            stmt.executeUpdate("DROP TABLE IF EXISTS words");
            
            // Re-enable foreign key checks
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }

        // Close the connection
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testGetMeanings_wordWithBothMeanings() throws SQLException {
        String[] words = {"hello"};
        Map<String, Map<String, String>> result = segmentDAO.getMeanings(words);
        assertTrue(result.containsKey("hello"));
        Map<String, String> wordMeanings = result.get("hello");
        assertEquals("\u0633\u0644\u0627\u0645", wordMeanings.get("Persian"));
        assertEquals("\u06C1\u06CC\u0644\u0648", wordMeanings.get("Urdu"));
    }

    @Test
    void testGetMeanings_wordWithOnlyPersianMeaning() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO words (word) VALUES ('uniquePersian')");
            stmt.executeUpdate("INSERT INTO persian_meanings (word_id, persian_mean) VALUES ((SELECT id FROM words WHERE word = 'uniquePersian'), 'دنیا')");
        }

        String[] words = {"uniquePersian"};
        Map<String, Map<String, String>> result = segmentDAO.getMeanings(words);
        assertTrue(result.containsKey("uniquePersian"));
        Map<String, String> wordMeanings = result.get("uniquePersian");
        assertEquals("\u062F\u0646\u06CC\u0627", wordMeanings.get("Persian"));
        assertEquals("Meaning not found", wordMeanings.get("Urdu"));
    }

    @Test
    void testGetMeanings_wordWithOnlyUrduMeaning() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO words (word) VALUES ('uniqueUrdu')");
            stmt.executeUpdate("INSERT INTO urdu_meanings (word_id, urdu_mean) VALUES ((SELECT id FROM words WHERE word = 'uniqueUrdu'), 'دوست')");
        }

        String[] words = {"uniqueUrdu"};
        Map<String, Map<String, String>> result = segmentDAO.getMeanings(words);
        assertTrue(result.containsKey("uniqueUrdu"));
        Map<String, String> wordMeanings = result.get("uniqueUrdu");
        assertEquals("Meaning not found", wordMeanings.get("Persian"));
        assertEquals("\u062F\u0648\u0633\u062A", wordMeanings.get("Urdu"));
    }

    @Test
    void testGetMeanings_wordWithNoMeanings() throws SQLException {
        String[] words = {"nonexistent"};
        Map<String, Map<String, String>> result = segmentDAO.getMeanings(words);
        assertTrue(result.containsKey("nonexistent"));
        Map<String, String> wordMeanings = result.get("nonexistent");
        assertEquals("Meaning not found", wordMeanings.get("Persian"));
        assertEquals("Meaning not found", wordMeanings.get("Urdu"));
    }
}
