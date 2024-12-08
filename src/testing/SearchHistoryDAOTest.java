package testing;

import dal.SearchHistoryDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchHistoryDAOTest {

    private SearchHistoryDAO searchHistoryDAO;
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/dictionary";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = " ";

    @BeforeEach
    void setUp() throws SQLException {
        // Initialize DAO
        searchHistoryDAO = new SearchHistoryDAO();

        // Insert test data into the database before each test
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)) {
            Statement stmt = conn.createStatement();

            // Clean up old test data before each test run
            stmt.execute("DELETE FROM search_history");

            // Insert some search history data for testing
            stmt.execute("INSERT INTO search_history (search_term) VALUES ('term1')");
            stmt.execute("INSERT INTO search_history (search_term) VALUES ('term2')");
            stmt.execute("INSERT INTO search_history (search_term) VALUES ('term3')");
        }
    }

    @Test
    void testGetSearchHistory() {
        // Call the method under test
        List<String> searchHistory = searchHistoryDAO.getSearchHistory();

        // Assert that the size of the search history is as expected
        assertEquals(3, searchHistory.size(), "Expected 3 search terms in the history.");

        // Assert that the search terms are as expected
        assertTrue(searchHistory.contains("term1"));
        assertTrue(searchHistory.contains("term2"));
        assertTrue(searchHistory.contains("term3"));
    }

    // Optionally, you can write tests for other methods if necessary
}
