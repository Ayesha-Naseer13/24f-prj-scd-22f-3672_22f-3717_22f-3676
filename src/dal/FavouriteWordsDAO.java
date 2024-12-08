package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavouriteWordsDAO implements IFavouriteWordsDAO{
    private static final String URL = "jdbc:mysql://localhost:3306/dictionary";
    private static final String USER = "root";
    private static final String PASSWORD = "";
   
    @Override
    public List<String[]> getWords() {
        List<String[]> words = new ArrayList<>();
        String query = "SELECT w.id, w.word, u.urdu_mean, p.persian_mean " +
                       "FROM words w " +
                       "LEFT JOIN urdu_meanings u ON w.id = u.word_id " +
                       "LEFT JOIN persian_meanings p ON w.id = p.word_id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                words.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("word"),
                        rs.getString("urdu_mean") != null ? rs.getString("urdu_mean") : "N/A",
                        rs.getString("persian_mean") != null ? rs.getString("persian_mean") : "N/A"
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
        return words;
    }
    @Override
    public int getWordIdByWord(String word) {
        String query = "SELECT id FROM words WHERE word = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, word);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;  // Word not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching word ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean addToFavourite(int wordId, String word) {
        String insertQuery = "INSERT INTO favourite_words (word_id, word) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, wordId);
            stmt.setString(2, word);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding word to favourites: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String[]> getFavouriteWords() {
        List<String[]> favourites = new ArrayList<>();
        String query = "SELECT f.word_id, w.word, u.urdu_mean, p.persian_mean " +
                       "FROM favourite_words f " +
                       "JOIN words w ON f.word_id = w.id " +
                       "LEFT JOIN urdu_meanings u ON w.id = u.word_id " +
                       "LEFT JOIN persian_meanings p ON w.id = p.word_id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                favourites.add(new String[]{
                        String.valueOf(rs.getInt("word_id")),
                        rs.getString("word"),
                        rs.getString("urdu_mean") != null ? rs.getString("urdu_mean") : "N/A",
                        rs.getString("persian_mean") != null ? rs.getString("persian_mean") : "N/A"
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching favourite words: " + e.getMessage(), e);
        }
        return favourites;
    }

    @Override
    public int removeFromFavourite(int wordId) {
        String query = "DELETE FROM favourite_words WHERE word_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, wordId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing word from favourites: " + e.getMessage(), e);
        }
        return wordId;
    }
}
