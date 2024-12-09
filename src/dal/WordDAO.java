
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dto.WordDTO;

public class WordDAO implements IWordDAO {

    @Override
    public boolean addWord(WordDTO word) {
        String wordQuery = "INSERT INTO words (word) VALUES (?)";
        String urduMeaningQuery = "INSERT INTO urdu_meanings (word_id, urdu_mean) VALUES (?, ?)";
        String persianMeaningQuery = "INSERT INTO persian_meanings (word_id, persian_mean) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement wordStmt = connection.prepareStatement(wordQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement urduStmt = connection.prepareStatement(urduMeaningQuery);
             PreparedStatement persianStmt = connection.prepareStatement(persianMeaningQuery)) {

            // Start transaction
            connection.setAutoCommit(false);

            // Insert the word into the words table
            wordStmt.setString(1, word.getWord());
            int wordInserted = wordStmt.executeUpdate();

            if (wordInserted > 0) {
                // Get the last inserted word ID
                try (var generatedKeys = wordStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long wordId = generatedKeys.getLong(1); // This is the ID of the newly inserted word

                        // Set parameters for urdu_meanings
                        urduStmt.setLong(1, wordId); // word_id
                        urduStmt.setString(2, word.getUrduTranslation()); // urdu_mean
                        int urduInserted = urduStmt.executeUpdate();

                        // Set parameters for persian_meanings
                        persianStmt.setLong(1, wordId); // word_id
                        persianStmt.setString(2, word.getPersianTranslation()); // persian_mean
                        int persianInserted = persianStmt.executeUpdate();

                        if (urduInserted > 0 && persianInserted > 0) {
                            connection.commit(); // Commit transaction if both insertions were successful
                            return true;
                        } else {
                            connection.rollback(); // Rollback transaction if insertions failed
                        }
                    }
                }
            }
            connection.rollback(); // Rollback if word was not inserted
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }

    @Override
    public boolean updateWord(WordDTO word) {
        String urduQuery = "UPDATE urdu_meanings SET urdu_mean = ? WHERE word_id = (SELECT id FROM words WHERE word = ?)";
        String persianQuery = "UPDATE persian_meanings SET persian_mean = ? WHERE word_id = (SELECT id FROM words WHERE word = ?)";
        boolean updated = false;

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            if (word.getUrduTranslation() != null) {
                try (PreparedStatement stmt = connection.prepareStatement(urduQuery)) {
                    stmt.setString(1, word.getUrduTranslation());
                    stmt.setString(2, word.getWord());
                    updated = stmt.executeUpdate() > 0;
                }
            }

            if (word.getPersianTranslation() != null) {
                try (PreparedStatement stmt = connection.prepareStatement(persianQuery)) {
                    stmt.setString(1, word.getPersianTranslation());
                    stmt.setString(2, word.getWord());
                    updated = stmt.executeUpdate() > 0 || updated;
                }
            }

            if (updated) {
                connection.commit(); // Commit transaction if any update occurs
            } else {
                connection.rollback(); // Rollback if no update occurs
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }

    @Override
    public boolean deleteWord(String word) {
        String deleteUrduQuery = "DELETE FROM urdu_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)";
        String deletePersianQuery = "DELETE FROM persian_meanings WHERE word_id = (SELECT id FROM words WHERE word = ?)";
        String deleteWordQuery = "DELETE FROM words WHERE word = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = connection.prepareStatement(deleteUrduQuery)) {
                stmt.setString(1, word);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(deletePersianQuery)) {
                stmt.setString(1, word);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(deleteWordQuery)) {
                stmt.setString(1, word);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    connection.commit(); // Commit transaction if word deleted successfully
                    return true;
                }
                connection.rollback(); // Rollback if no rows deleted
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void closeConnection() {
        // No explicit close required, as connections are managed within try-with-resources
    }
}
