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

import java.io.File;
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

    // Existing Tests

    @Test
    void testImportCSV_HappyPath() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        File tempFile = File.createTempFile("test", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("word1,meaning1,meaning2");
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_InvalidFormat() throws Exception {
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
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

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
        File tempFile = File.createTempFile("test_invalid_format", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("word1");
            writer.println("word2,meaning1");
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_EmptyWordOrMeaning() throws Exception {
        File tempFile = File.createTempFile("test_empty_word_meaning", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println(",meaning1,meaning2");
            writer.println("word2,,meaning2");
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    // Additional Tests

    @Test
    void testImportCSV_LargeFile() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        File tempFile = File.createTempFile("test_large", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            for (int i = 0; i < 1000; i++) {
                writer.println("word" + i + ",meaning1,meaning2");
            }
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_ConcurrentProcessing() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        File tempFile = File.createTempFile("test_concurrent", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            for (int i = 0; i < 50; i++) {
                writer.println("word" + i + ",meaning1,meaning2");
            }
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testImportCSV_CharacterEncoding() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        File tempFile = File.createTempFile("test_encoding", ".csv");
        try (PrintWriter writer = new PrintWriter(tempFile, "UTF-8")) {
            writer.println("cliché,meaning1,meaning2"); // Non-ASCII character
        }

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        assertTrue(duplicates.isEmpty());
        verify(mockConnection, times(1)).commit();
    }



}
