package testing;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.ScrapDAO;

class ScrapDAOTest {
    private ScrapDAO scrapDAO;
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/dictionary";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = " ";

    @BeforeAll
    static void setupDatabase() {
        try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            // Set up mock tables
            statement.execute("CREATE TABLE IF NOT EXISTS words (id INT AUTO_INCREMENT PRIMARY KEY, word VARCHAR(255) UNIQUE)");
            statement.execute("CREATE TABLE IF NOT EXISTS urdu_meanings (id INT AUTO_INCREMENT PRIMARY KEY, word_id INT, urdu_mean VARCHAR(255), FOREIGN KEY(word_id) REFERENCES words(id))");
            statement.execute("CREATE TABLE IF NOT EXISTS persian_meanings (id INT AUTO_INCREMENT PRIMARY KEY, word_id INT, persian_mean VARCHAR(255), FOREIGN KEY(word_id) REFERENCES words(id))");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set up test database", e);
        }
    }

    @AfterAll
    static void tearDownDatabase() {
        try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            // Clean up mock tables in correct order
            statement.execute("DELETE FROM urdu_meanings");
            statement.execute("DELETE FROM persian_meanings");
            statement.execute("DELETE FROM words");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to tear down test database", e);
        }
    }

    @BeforeEach
    void setUp() {
        scrapDAO = new ScrapDAO();
    }

   
    @Test
    public void testScrapeAndInsertWordFromUrl() {
        ScrapDAO scrapDAO = new ScrapDAO();
        String testUrl = "https://www.almaany.com/fa/dict/ar-fa/أسرة/"; // Replace with a valid test URL
        
        // Call the method under test
        String result = scrapDAO.scrapeAndInsertWordFromUrl(testUrl);

        // Log the result for debugging
        System.out.println("Result: " + result);

        // Assert the success message
        assertTrue(result.contains("Word and meanings added successfully"), 
            "Expected success message, but got: " + result);
    }

   

    @AfterEach
    void tearDown() {
        try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM urdu_meanings");
            statement.execute("DELETE FROM persian_meanings");
            statement.execute("DELETE FROM words");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test database", e);
        }
    }
}

