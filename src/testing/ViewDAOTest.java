package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.ViewDAO;
import dto.Translation;

class ViewDAOTest {

    private ViewDAO viewDAO;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock the connection and inject it into the DAO
        viewDAO = new ViewDAO() {
            protected Connection getConnection() throws SQLException {
                return mockConnection;
            }
        };
    }

    @Test
    void testGetAllWords() throws SQLException {
        // Mock behavior for fetching all words
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("word")).thenReturn("apple", "banana");

        List<String> words = viewDAO.getAllWords();

        assertEquals(2, words.size());
        assertEquals("apple", words.get(0));
        assertEquals("banana", words.get(1));

        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetWordDetails() throws SQLException {
        // Mock behavior for fetching word details
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("part_of_speech")).thenReturn("noun");
        when(mockResultSet.getString("stem_word")).thenReturn("appl");
        when(mockResultSet.getString("root_word")).thenReturn("apple");
        when(mockResultSet.getString("urdu_mean")).thenReturn("سیب");
        when(mockResultSet.getString("persian_mean")).thenReturn("سیب");

        List<Translation> translations = viewDAO.getWordDetails("apple");

        assertEquals(1, translations.size());
        Translation translation = translations.get(0);
        assertEquals("noun", translation.getPartOfSpeech());
        assertEquals("appl", translation.getStemWord());
        assertEquals("apple", translation.getRootWord());
        assertEquals("سیب", translation.getUrduMeaning());
        assertEquals("سیب", translation.getPersianMeaning());

        verify(mockStatement, times(1)).setString(1, "apple");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetWordDetails_NoMeanings() throws SQLException {
        // Mock behavior for no meanings
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("part_of_speech")).thenReturn("noun");
        when(mockResultSet.getString("stem_word")).thenReturn("appl");
        when(mockResultSet.getString("root_word")).thenReturn("apple");
        when(mockResultSet.getString("urdu_mean")).thenReturn("");
        when(mockResultSet.getString("persian_mean")).thenReturn("");

        List<Translation> translations = viewDAO.getWordDetails("apple");

        assertEquals(0, translations.size());
        verify(mockStatement, times(1)).setString(1, "apple");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetAllWords_ExceptionHandling() throws SQLException {
        // Simulate SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        List<String> words = viewDAO.getAllWords();

        assertEquals(0, words.size());
    }

    @Test
    void testGetWordDetails_ExceptionHandling() throws SQLException {
        // Simulate SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        List<Translation> translations = viewDAO.getWordDetails("apple");

        assertEquals(0, translations.size());
    }
}
