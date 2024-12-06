package testing;


import org.testng.annotations.*;

import dal.DatabaseConfig;
import dal.DictionaryDAO;

import java.io.*;
import java.sql.*;
import java.util.*;

import static org.testng.Assert.*;

public class DictionaryDAOTest {

    private DictionaryDAO dictionaryDAO;
    private Connection connection;

    @BeforeMethod
    public void setUp() throws SQLException {
        connection = DatabaseConfig.getConnection();
        dictionaryDAO = new DictionaryDAO();
        clearDatabase();
    }

    @AfterMethod
    public void tearDown() throws SQLException {
        clearDatabase();
        connection.close();
    }

    @DataProvider(name = "csvDataProvider")
    public Object[][] csvDataProvider() {
        return new Object[][] {
            {"word1,urduMeaning1/persianMeaning1\nword2,urduMeaning2/persianMeaning2", true, new String[] {"word1", "word2"}},
            {"word1,urduMeaning1/persianMeaning1\nword1,urduMeaning2/persianMeaning2", false, new String[] {"word1"}},
            {"invalid_line\nword1,urduMeaning1/persianMeaning1", false, new String[] {}}
        };
    }

    @Test(dataProvider = "csvDataProvider")
    public void testImportCSV(String csvData, boolean noDuplicates, String[] words) throws IOException, SQLException {
        File tempFile = createTempCSV(csvData);

        List<String> duplicates = dictionaryDAO.importCSV(tempFile.getAbsolutePath());

        if (noDuplicates) {
            assertTrue(duplicates.isEmpty());
        } else {
            assertEquals(duplicates.size(), words.length);
            for (String word : words) {
                assertTrue(duplicates.contains(word));
            }
        }

        for (String word : words) {
            if (noDuplicates) {
                assertWordExists(word);
            }
        }
    }

    @Test
    public void testImportCSVIOException() {
        RuntimeException exception = expectThrows(RuntimeException.class, () -> dictionaryDAO.importCSV("non_existent_file.csv"));
        assertTrue(exception.getMessage().contains("Error reading CSV file"));
    }

    @Test
    public void testImportCSVSQLException() throws IOException, SQLException {
        String csvData = "word1,urduMeaning1/persianMeaning1";
        File tempFile = createTempCSV(csvData);

        connection.close(); // Simulate a database error

        RuntimeException exception = expectThrows(RuntimeException.class, () -> dictionaryDAO.importCSV(tempFile.getAbsolutePath()));
        assertTrue(exception.getMessage().contains("Error processing CSV file"));
    }

    private File createTempCSV(String data) throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(data);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }

    private void assertWordExists(String word) throws SQLException {
        String query = "SELECT id FROM words WHERE word = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, word);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Word not found in database: " + word);
            }
        }
    }

    private void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM persian_meanings");
            stmt.executeUpdate("DELETE FROM urdu_meanings");
            stmt.executeUpdate("DELETE FROM words");
        }
    }
}
