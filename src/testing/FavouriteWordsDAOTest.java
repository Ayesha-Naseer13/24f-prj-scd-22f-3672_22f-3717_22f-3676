//package testing;
//
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import dal.FavouriteWordsDAO;
//
//class FavouriteWordsDAOTest {
//
//    private FavouriteWordsDAO favouriteWordsDAO;
//    private Connection mockConnection;
//    private PreparedStatement mockStatement;
//    private ResultSet mockResultSet;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        mockConnection = mock(Connection.class);
//        mockStatement = mock(PreparedStatement.class);
//        mockResultSet = mock(ResultSet.class);
//
//        favouriteWordsDAO = new FavouriteWordsDAO() {
//            protected Connection getConnection() throws SQLException {
//                return mockConnection; // Use mock connection
//            }
//        };
//    }
//
//    @Test
//    void testGetAllWords() throws SQLException {
//        // Mock database behavior
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(true, true, false);
//        when(mockResultSet.getInt("id")).thenReturn(1, 2);
//        when(mockResultSet.getString("word")).thenReturn("apple", "banana");
//        when(mockResultSet.getString("urdu_mean")).thenReturn("سیب", "کیلا");
//        when(mockResultSet.getString("persian_mean")).thenReturn("سیب", "موز");
//
//        List<String[]> words = favouriteWordsDAO.getAllWords();
//
//        assertEquals(2, words.size());
//        assertArrayEquals(new String[]{"1", "apple", "سیب", "سیب"}, words.get(0));
//        assertArrayEquals(new String[]{"2", "banana", "کیلا", "موز"}, words.get(1));
//
//        verify(mockStatement, times(1)).executeQuery();
//    }
//
//    @Test
//    void testGetWordIdByWord_Found() throws SQLException {
//        // Mock database behavior for found word
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(true);
//        when(mockResultSet.getInt("id")).thenReturn(42);
//
//        int wordId = favouriteWordsDAO.getWordIdByWord("apple");
//
//        assertEquals(42, wordId);
//        verify(mockStatement, times(1)).setString(1, "apple");
//        verify(mockStatement, times(1)).executeQuery();
//    }
//
//    @Test
//    void testGetWordIdByWord_NotFound() throws SQLException {
//        // Mock database behavior for word not found
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(false);
//
//        int wordId = favouriteWordsDAO.getWordIdByWord("unknown");
//
//        assertEquals(-1, wordId);
//        verify(mockStatement, times(1)).setString(1, "unknown");
//        verify(mockStatement, times(1)).executeQuery();
//    }
//
//    @Test
//    void testAddToFavourite_Success() throws SQLException {
//        // Mock database behavior for successful insertion
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeUpdate()).thenReturn(1);
//
//        boolean result = favouriteWordsDAO.addToFavourite(42, "apple");
//
//        assertTrue(result);
//        verify(mockStatement, times(1)).setInt(1, 42);
//        verify(mockStatement, times(1)).setString(2, "apple");
//        verify(mockStatement, times(1)).executeUpdate();
//    }
//
//    @Test
//    void testAddToFavourite_Failure() throws SQLException {
//        // Mock database behavior for failed insertion
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeUpdate()).thenReturn(0);
//
//        boolean result = favouriteWordsDAO.addToFavourite(42, "apple");
//
//        assertFalse(result);
//        verify(mockStatement, times(1)).setInt(1, 42);
//        verify(mockStatement, times(1)).setString(2, "apple");
//        verify(mockStatement, times(1)).executeUpdate();
//    }
//
//    @Test
//    void testGetFavouriteWords() throws SQLException {
//        // Mock database behavior for fetching favorite words
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(true, false);
//        when(mockResultSet.getInt("word_id")).thenReturn(42);
//        when(mockResultSet.getString("word")).thenReturn("apple");
//        when(mockResultSet.getString("urdu_mean")).thenReturn("سیب");
//        when(mockResultSet.getString("persian_mean")).thenReturn("سیب");
//
//        List<String[]> favourites = favouriteWordsDAO.getFavouriteWords();
//
//        assertEquals(1, favourites.size());
//        assertArrayEquals(new String[]{"42", "apple", "سیب", "سیب"}, favourites.get(0));
//
//        verify(mockStatement, times(1)).executeQuery();
//    }
//
//    @Test
//    void testRemoveFromFavourite() throws SQLException {
//        // Mock database behavior for removing a favorite word
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//
//        favouriteWordsDAO.removeFromFavourite(42);
//
//        verify(mockStatement, times(1)).setInt(1, 42);
//        verify(mockStatement, times(1)).executeUpdate();
//    }
//
//    @Test
//    void testGetAllWords_ExceptionHandling() throws SQLException {
//        // Simulate an SQLException
//        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                favouriteWordsDAO.getAllWords());
//
//        assertTrue(exception.getMessage().contains("Database error"));
//    }
//}
