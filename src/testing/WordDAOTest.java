package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import org.junit.jupiter.api.*;
import dal.DatabaseConfig;
import dal.WordDAO;
import dto.WordDTO;
import java.util.List;
import java.util.ArrayList;

class WordDAOTest {

    private WordDAO wordDAO;

   

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    @BeforeEach
    void setUp() {
        wordDAO = new WordDAO();
        clearDatabase();
    }

    private void clearDatabase() {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement disableFK = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0");
             PreparedStatement clearUrdu = connection.prepareStatement("DELETE FROM urdu_meanings");
             PreparedStatement clearPersian = connection.prepareStatement("DELETE FROM persian_meanings");
             PreparedStatement clearWords = connection.prepareStatement("DELETE FROM words");
             PreparedStatement enableFK = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {

            // Disable foreign key checks to avoid issues with dependent records
            disableFK.executeUpdate();
            // Delete records in a specific order to avoid foreign key constraint violations
            clearUrdu.executeUpdate();
            clearPersian.executeUpdate();
            clearWords.executeUpdate();
            // Re-enable foreign key checks after cleanup
            enableFK.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testAddWord() {
        WordDTO word = new WordDTO("test", "\u0622\u0632\u0645\u0627\u0626\u0634", "\u0622\u0632\u0645\u0648\u0646\u0634");
        boolean result = wordDAO.addWord(word);
        assertTrue(result, "Word should be added successfully");
    }

    @Test
    void testUpdateWord() {
        WordDTO word = new WordDTO("update", "\u062A\u062C\u062F\u06CC\u062F", "\u0628\u0631\u0648\u0632");
        wordDAO.addWord(word);

        WordDTO updatedWord = new WordDTO("update", "\u0646\u06CC\u0627", "\u0646\u0648\u062A\u0627\u0632\u06AF\u06CC");
        boolean result = wordDAO.updateWord(updatedWord);

        assertTrue(result, "Word translations should be updated successfully");
    }

    @Test
    void testDeleteWord() {
        WordDTO word = new WordDTO("delete", "\u062D\u0632\u0641", "\u0627\u0646\u062D\u0632\u0627\u0641");
        wordDAO.addWord(word);

        boolean result = wordDAO.deleteWord("delete");
        assertTrue(result, "Word should be deleted successfully");
    }

    @Test
    void testAddWordWithDuplicate() {
        WordDTO word = new WordDTO("duplicate", "\u0645\u062A\u0642\u0631\u0631", "\u0645\u062A\u0642\u0631\u0631");
        wordDAO.addWord(word);

        boolean result = wordDAO.addWord(word);
        assertFalse(result, "Duplicate word should not be added");
    }

    @Test
    void testAddWordWithNullFields() {
        WordDTO word = new WordDTO(null, null, null);
        boolean result = wordDAO.addWord(word);
        assertFalse(result, "Word with null fields should not be added");
    }

    @Test
    void testUpdateNonExistentWord() {
        WordDTO word = new WordDTO("nonexistent", "\u0646\u06CC\u0627", "\u0646\u0648\u062A\u0627\u0632\u06AF\u06CC");
        boolean result = wordDAO.updateWord(word);
        assertFalse(result, "Updating a non-existent word should fail");
    }

    @Test
    void testDeleteNonExistentWord() {
        boolean result = wordDAO.deleteWord("nonexistent");
        assertFalse(result, "Deleting a non-existent word should fail");
    }

    @Test
    void testAddMultipleWords() {
        List<WordDTO> words = new ArrayList<>();
        words.add(new WordDTO("word1", "\u0627\u0648\u0644", "\u0627\u0648\u0644"));
        words.add(new WordDTO("word2", "\u062F\u0648\u0645", "\u062F\u0648\u0645"));
        words.add(new WordDTO("word3", "\u0633\u0648\u0645", "\u0633\u0648\u0645"));

        for (WordDTO word : words) {
            assertTrue(wordDAO.addWord(word), "Each word should be added successfully");
        }
    }
}
