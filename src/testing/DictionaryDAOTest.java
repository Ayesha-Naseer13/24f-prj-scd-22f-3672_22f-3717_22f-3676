package testing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.DatabaseConfig;
import dal.DictionaryDAO;

class DictionaryDAOTest {

    private DictionaryDAO dictionaryDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        DatabaseConfig.setConnection(mockConnection);
        dictionaryDAO = new DictionaryDAO();
    }
    @Test
    void testImportCSV_HappyPath() throws Exception {
        // Mock database behavior
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Create a temporary CSV file with content
        File tempFile = File.createTempFile("test", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("word1,meaning1,meaning2"); // Make sure the format is as expected
        }

        
    }

    @Test
    void testImportCSV_InvalidFormat() throws Exception {
        // Create a temporary CSV file with invalid format
        File tempFile = File.createTempFile("test_invalid", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("invalid_line");
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_DuplicateWord() throws Exception {
        // Mock duplicate check behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Word exists

        // Create a temporary CSV file
        File tempFile = File.createTempFile("test_duplicate", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("word1,meaning1,meaning2");
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertEquals(1, duplicates.size());
        assertEquals("word1", duplicates.get(0));
        verify(mockConnection, times(1)).commit();
    }

    


    @Test
    void testImportCSV_EmptyFile() throws Exception {
        // Create an empty temporary CSV file
        File tempFile = File.createTempFile("test_empty", ".csv");

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_FileNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dictionaryDAO.importCSV("non_existent_file.csv");
        });

        assertTrue(exception.getMessage().contains("Error reading CSV file"));
 
    }
    
    
   
    @Test
    void testImportCSV_InvalidCSVFormat() throws Exception {
        // Create a temporary CSV file with invalid format (no comma, only word)
        File tempFile = File.createTempFile("test_invalid_format", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("word1"); // Invalid line format
            writer.println("word2,meaning1"); // Invalid line format
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty(), "Duplicates should be empty for invalid format");
        verify(mockConnection, times(1)).commit(); // Ensure commit happens
    }

    


    @Test
    void testImportCSV_EmptyWordOrMeaning() throws Exception {
        // Create a temporary CSV file with an empty word and meaning
        File tempFile = File.createTempFile("test_empty_word_meaning", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println(",meaning1,meaning2");  // Empty word
            writer.println("word2,,meaning2");    // Empty meaning
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty(), "No duplicates should be added for empty words/meanings");
        verify(mockConnection, times(1)).commit(); // Ensure commit happens
    }

    
}
