package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.CustomDictionaryDAO;
import dal.DatabaseConfig;
import dto.WordTranslation;
import javafx.collections.ObservableList;

class CustomDictionaryDAOTest {

    private CustomDictionaryDAO dictionaryDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        DatabaseConfig.setConnection(mockConnection);

        dictionaryDAO = new CustomDictionaryDAO();
    }

    @AfterEach
    void tearDown() {
        DatabaseConfig.setConnection(null);
    }

    @Test
    void testGetTranslationsForStory() throws SQLException {
        String storyText = "hello world";

        // Mock story existence check
        PreparedStatement checkStoryStatement = mock(PreparedStatement.class);
        ResultSet storyResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement("SELECT id FROM stories WHERE story_text = ?")).thenReturn(checkStoryStatement);
        when(checkStoryStatement.executeQuery()).thenReturn(storyResultSet);
        when(storyResultSet.next()).thenReturn(true); // Story exists
        when(storyResultSet.getInt("id")).thenReturn(1); // Story ID is 1

        // Mock translation fetch
        PreparedStatement fetchTranslationsStatement = mock(PreparedStatement.class);
        ResultSet translationsResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement("""
                SELECT words.word AS arabic,
                       urdu_meanings.urdu_mean AS urdu,
                       persian_meanings.persian_mean AS persian
                FROM story_translations
                JOIN words ON story_translations.word_id = words.id
                LEFT JOIN urdu_meanings ON words.id = urdu_meanings.word_id
                LEFT JOIN persian_meanings ON words.id = persian_meanings.word_id
                WHERE story_translations.story_id = ?
                ORDER BY story_translations.sequence_number
                """)).thenReturn(fetchTranslationsStatement);

        when(fetchTranslationsStatement.executeQuery()).thenReturn(translationsResultSet);
        when(translationsResultSet.next()).thenReturn(true, true, false); // Two results, then end
        when(translationsResultSet.getString("arabic")).thenReturn("hello", "world");
        when(translationsResultSet.getString("urdu")).thenReturn("\u062D\u0644\u0627\u0645", null); // Urdu translation for "hello", none for "world"
        when(translationsResultSet.getString("persian")).thenReturn(null, "\u062C\u0647\u0627\u0646"); // Persian translation for "world", none for "hello"

        ObservableList<WordTranslation> translations = dictionaryDAO.getTranslationsForStory(storyText);

        // Assertions for translation list size and contents
        assertEquals(2, translations.size());
        assertEquals("hello", translations.get(0).getArabic());
        assertEquals("\u062D\u0644\u0627\u0645", translations.get(0).getUrdu());
        assertNull(translations.get(0).getPersian());
        assertEquals("world", translations.get(1).getArabic());
        assertNull(translations.get(1).getUrdu());
        assertEquals("\u062C\u0647\u0627\u0646", translations.get(1).getPersian());
    }

    @Test
    void testImportStoryFromFile_Success() throws IOException {
        File tempFile = File.createTempFile("testStory", ".txt");
        Files.writeString(tempFile.toPath(), "This is a test story.");

        String storyText = dictionaryDAO.importStoryFromFile(tempFile);

        assertEquals("This is a test story.", storyText);
        tempFile.deleteOnExit();
    }

    @Test
    void testImportStoryFromFile_Error() {
        File nonExistentFile = new File("nonExistent.txt");

        String result = dictionaryDAO.importStoryFromFile(nonExistentFile);

        assertTrue(result.startsWith("Error reading file:"));
    }

    


   


	@Test
    void testAddWord_NewWord() throws SQLException {
        String newWord = "example";

        // Mock behavior for checking if the word exists
        PreparedStatement checkStatement = mock(PreparedStatement.class);
        ResultSet checkResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement("SELECT id FROM words WHERE word = ?")).thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(checkResultSet);
        when(checkResultSet.next()).thenReturn(false); // Word does not exist

        // Mock behavior for inserting the word
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        ResultSet insertResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement("INSERT INTO words (word) VALUES (?)", Statement.RETURN_GENERATED_KEYS)).thenReturn(insertStatement);
        when(insertStatement.getGeneratedKeys()).thenReturn(insertResultSet);
        when(insertResultSet.next()).thenReturn(true); // Key generation successful
        when(insertResultSet.getInt(1)).thenReturn(42); // Generated ID

        int wordId = dictionaryDAO.addWord(newWord);

        assertEquals(42, wordId); // Verify the returned word ID
        verify(insertStatement, times(1)).executeUpdate(); // Ensure the insert was called
    }


    @Test
    void testAddWord_ExistingWord() throws SQLException {
        String existingWord = "example";

        // Mock word found
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(42);

        int wordId = dictionaryDAO.addWord(existingWord);

        assertEquals(42, wordId);
        verify(mockPreparedStatement, never()).executeUpdate();
    }
}