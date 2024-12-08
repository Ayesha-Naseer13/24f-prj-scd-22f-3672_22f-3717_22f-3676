package testing;
import org.junit.jupiter.api.*;
import java.sql.*;
import dto.WordDTO;
import dal.WordDAO;
import static org.junit.jupiter.api.Assertions.*;

class WordDAOTest {

    private WordDAO wordDAO;
    private Connection connection;
    private WordDTO wordDTO;

    @BeforeEach
    void setUp() throws SQLException {
        // Establish a real database connection for testing
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "");
        wordDAO = new WordDAO();

        // Create test data
        wordDTO = new WordDTO("hello", "سلام", "سلام");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Delete the related entries from the persian_meanings and urdu_meanings tables
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM persian_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)")) {
            stmt.setString(1, wordDTO.getWord());
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM urdu_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)")) {
            stmt.setString(1, wordDTO.getWord());
            stmt.executeUpdate();
        }

        // Delete the word from the words table
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM words WHERE word = ?")) {
            stmt.setString(1, wordDTO.getWord());
            stmt.executeUpdate();
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testAddWord() {
        boolean result = wordDAO.addWord(wordDTO);
        assertTrue(result, "Word should be added successfully.");

        // Verify the word is inserted into the database
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM words WHERE word = ?")) {
            stmt.setString(1, wordDTO.getWord());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                assertEquals(1, rs.getInt(1), "The word should exist in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error while verifying word insertion.");
        }
    }

    @Test
    void testUpdateWord() {
        // First, insert the word into the database
        wordDAO.addWord(wordDTO);

        // Update the word's meanings
        wordDTO.setUrduTranslation("نیا ترجمہ");
        wordDTO.setPersianTranslation("ترجمه جدید");
        boolean result = wordDAO.updateWord(wordDTO);
        assertTrue(result, "Word should be updated successfully.");

        // Verify the word meanings were updated
        try (PreparedStatement stmt = connection.prepareStatement("SELECT urdu_mean FROM urdu_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)")) {
            stmt.setString(1, wordDTO.getWord());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                assertEquals("نیا ترجمہ", rs.getString("urdu_mean"), "The Urdu meaning should be updated.");
            }

            try (PreparedStatement persianStmt = connection.prepareStatement("SELECT persian_mean FROM persian_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)")) {
                persianStmt.setString(1, wordDTO.getWord());
                ResultSet persianRs = persianStmt.executeQuery();
                if (persianRs.next()) {
                    assertEquals("ترجمه جدید", persianRs.getString("persian_mean"), "The Persian meaning should be updated.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error while verifying word update.");
        }
    }


    @Test
    void testDeleteWord() {
        // First, insert the word into the database
        wordDAO.addWord(wordDTO);

        // Now delete the word
        boolean result = wordDAO.deleteWord(wordDTO.getWord());
        assertTrue(result, "Word should be deleted successfully.");

        // Verify the word is deleted from the database
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM words WHERE word = ?")) {
            stmt.setString(1, wordDTO.getWord());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                assertEquals(0, rs.getInt(1), "The word should be deleted from the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error while verifying word deletion.");
        }
    }
}
