package testing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.DatabaseConfig;
import dal.SearchDAO;

class SearchDAOTest {

    private SearchDAO searchDAO;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Use a mock connection instead of the real one
        DatabaseConfig.setConnection(mockConnection);
        searchDAO = new SearchDAO();
    }

    @AfterEach
    void tearDown() {
        searchDAO.closeConnection();
    }

    @Test
    void testGetSuggestions_SearchByKey() throws SQLException {
        // Mock behavior for search by key
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString(1)).thenReturn("apple", "apricot");

        List<String> suggestions = searchDAO.getSuggestions("ap", true, "English");

        assertEquals(2, suggestions.size());
        assertEquals("apple", suggestions.get(0));
        assertEquals("apricot", suggestions.get(1));

        verify(mockStatement, times(1)).setString(1, "ap%");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetSuggestions_SearchByLanguage_Urdu() throws SQLException {
        // Mock behavior for search by language (Urdu)
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString(1)).thenReturn("\u0627\u0645\u0644");

        List<String> suggestions = searchDAO.getSuggestions("\u0627", false, "Urdu");

        assertEquals(1, suggestions.size());
        assertEquals("\u0627\u0645\u0644", suggestions.get(0));

        verify(mockStatement, times(1)).setString(1, "\u0627%");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testSearchWord_ByKey() throws SQLException {
        // Mock behavior for search by key
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("urdu_mean")).thenReturn("\u0627\u0645\u0644");
        when(mockResultSet.getString("persian_mean")).thenReturn("عمل");

        List<String> results = searchDAO.searchWord("action", true, "English");

        assertEquals(1, results.size());
        assertEquals("Urdu: \u0627\u0645\u0644, Persian: عمل", results.get(0));

        verify(mockStatement, times(1)).setString(1, "action");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testSearchWord_ByUrduMeanings() throws SQLException {
        // Mock behavior for Urdu meanings
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("word")).thenReturn("action");
        when(mockResultSet.getString("persian_mean")).thenReturn("عمل");

        List<String> results = searchDAO.searchWord("\u0627\u0645\u0644", false, "Urdu");

        assertEquals(1, results.size());
        assertEquals("Word: action, Persian: عمل", results.get(0));

        verify(mockStatement, times(1)).setString(1, "\u0627\u0645\u0644");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testAddSearchTermToHistory() throws SQLException {
        // Mock behavior for adding search term to history
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        searchDAO.addSearchTermToHistory("test term");

        verify(mockStatement, times(1)).setString(1, "test term");
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    void testCloseConnection() throws SQLException {
        searchDAO.closeConnection();

        verify(mockConnection, times(1)).close();
    }

    @Test
    void testGetSuggestions_ExceptionHandling() throws SQLException {
        // Simulate SQLException during query execution
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                searchDAO.getSuggestions("test", true, "English"));

        assertTrue(exception.getMessage().contains("Error fetching suggestions"), 
                   "Exception message should indicate an error fetching suggestions");
    }

}