package dal;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ScrapDAO implements IScrapDAO {

    private final Connection connection;

    // Initialize connection using DatabaseConfig
    public ScrapDAO() {
        try {
            this.connection = DatabaseConfig.getConnection(); // Use DatabaseConfig to get connection
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage(), e);
        }
    }

    @Override
    public String scrapeAndInsertWordFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "File not found at path: " + filePath;
        }

        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            Element wordElement = doc.selectFirst("td[id^=w] b");
            Element meaningElement = doc.selectFirst("td[id^=m]");

            if (wordElement == null || meaningElement == null) {
                return "Word or meaning not found in the file.";
            }

            String word = normalizeArabic(wordElement.text());
            String meaning = extractFirstMeaning(meaningElement.text());
            boolean isPersian = filePath.contains("فارسى");
            String persianMeaning = isPersian ? meaning : null;
            String urduMeaning = isPersian ? null : meaning;

            return saveToDatabase(word, urduMeaning, persianMeaning);

        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Override
    public String scrapeAndInsertWordFromUrl(String url) {
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/87.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.2 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPad; CPU OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1",
        };

        for (String userAgent : userAgents) {
            try {
                Document doc = Jsoup.connect(url)
                                    .userAgent(userAgent)
                                    .timeout(5000)
                                    .get();

                Element wordElement = doc.selectFirst("td[id^=w] b");
                Element meaningElement = doc.selectFirst("td[id^=m]");

                if (wordElement == null || meaningElement == null) {
                    return "Word or meaning not found in the page.";
                }

                String word = normalizeArabic(wordElement.text());
                String meaning = extractFirstMeaning(meaningElement.text());
                String persianMeaning = null;
                String urduMeaning = null;

                if (url.contains("fa")) {
                    persianMeaning = meaning;
                } else if (url.contains("ur")) {
                    urduMeaning = meaning;
                } else {
                    return "Error: Unable to determine the language from the URL.";
                }

                return saveToDatabase(word, urduMeaning, persianMeaning);
            } catch (IOException e) {
                System.out.println("Failed with User-Agent: " + userAgent + " - " + e.getMessage());
            }
        }

        return "Error: Unable to scrape data from the URL using available user agents.";
    }

    private String extractFirstMeaning(String meaningText) {
        // Splits meaning into parts by "(number)" and extracts the first part.
        String[] parts = meaningText.split("\\(\\d+\\)");
        return parts.length > 0 ? parts[0].trim() : meaningText.trim();
    }

    private String saveToDatabase(String word, String urduMeaning, String persianMeaning) {
        try {
            int wordId = findOrInsertWord(connection, word);

            if (urduMeaning != null) {
                addUrduMeaningIfNotExists(connection, wordId, urduMeaning);
            }
            if (persianMeaning != null) {
                addPersianMeaningIfNotExists(connection, wordId, persianMeaning);
            }
            return "Word and meanings added successfully to the database.";
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    private int findOrInsertWord(Connection conn, String word) throws SQLException {
        String normalizedWord = normalizeArabic(word);
        String selectQuery = "SELECT id FROM words WHERE word = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setString(1, normalizedWord);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertQuery = "INSERT INTO words (word) VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, normalizedWord);
            insertStmt.executeUpdate();
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        throw new SQLException("Failed to insert word: " + word);
    }

    private void addUrduMeaningIfNotExists(Connection conn, int wordId, String urduMeaning) throws SQLException {
        String selectQuery = "SELECT id FROM urdu_meanings WHERE word_id = ? AND urdu_mean = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, wordId);
            selectStmt.setString(2, urduMeaning);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return;
            }
        }

        String insertQuery = "INSERT INTO urdu_meanings (word_id, urdu_mean) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, wordId);
            insertStmt.setString(2, urduMeaning);
            insertStmt.executeUpdate();
        }
    }

    private void addPersianMeaningIfNotExists(Connection conn, int wordId, String persianMeaning) throws SQLException {
        String selectQuery = "SELECT id FROM persian_meanings WHERE word_id = ? AND persian_mean = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, wordId);
            selectStmt.setString(2, persianMeaning);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return;
            }
        }

        String insertQuery = "INSERT INTO persian_meanings (word_id, persian_mean) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, wordId);
            insertStmt.setString(2, persianMeaning);
            insertStmt.executeUpdate();
        }
    }

    private String normalizeArabic(String text) {
        return text.replaceAll("[\\u064B-\\u065F]", "") // Remove diacritical marks
                   .replaceAll("\\u06CC", "\\u064A")   // Normalize 'ی' to 'ي'
                   .replaceAll("\\u06C1", "\\u0647"); // Normalize 'ہ' to 'ه'
    }
}
