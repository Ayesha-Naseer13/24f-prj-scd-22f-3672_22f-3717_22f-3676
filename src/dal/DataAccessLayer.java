package dal;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dto.Word;

public class DataAccessLayer implements IDataAccessLayer{
    private Connection connection;

  
    
    public DataAccessLayer() {
        try {
            String dbUrl = "jdbc:mysql://localhost:3306/dictionary";
            String dbUser = "root";
            String dbPassword = "";
            this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            // Handle the SQLException
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();  // Optionally print the stack trace for debugging purposes
        }
    }

    public List<String[]> getPosDetails(String arabicWord) {
        List<String[]> posDetailsList = new ArrayList<>();

        try {
            File jarFile = new File("AlKhalil-2.1.21.jar");
        	    try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()})) {
                
                Class<?> posTaggerClass = classLoader.loadClass("AlKhalil2.AnalyzedWords");
                Object posTaggerInstance = posTaggerClass.getDeclaredConstructor().newInstance();

                Method analyzedWordsMethod = posTaggerClass.getMethod("analyzedWords", String.class);
                LinkedList<?> analyzedResults = (LinkedList<?>) analyzedWordsMethod.invoke(posTaggerInstance, arabicWord);

                for (Object result : analyzedResults) {
                    String voweledWord = (String) result.getClass().getMethod("getVoweledWord").invoke(result);
                    String stem = (String) result.getClass().getMethod("getStem").invoke(result);
                    String pos = (String) result.getClass().getMethod("getWordType").invoke(result);
                    String rootWord = (String) result.getClass().getMethod("getWordRoot").invoke(result); // Fetch root word

                    posDetailsList.add(new String[]{voweledWord, stem, pos, rootWord});
                }

                savePosDetails(arabicWord, posDetailsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return posDetailsList;
    }

    private void savePosDetails(String arabicWord, List<String[]> posDetailsList) throws Exception {
        int wordId = getWordId(arabicWord);
        if (wordId == -1) {
            System.out.println("Word not found in the database: " + arabicWord);
            return;
        }

        for (String[] posDetail : posDetailsList) {
            String voweledWord = posDetail[0];
            String stem = posDetail[1];
            String pos = posDetail[2];
            String rootWord = posDetail[3];

            insertPosData(wordId, pos, stem, rootWord);
        }
    }

    private int getWordId(String arabicWord) throws Exception {
        String query = "SELECT id FROM words WHERE word = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, arabicWord);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; 
    }

    public LinkedList<Word> getWords() throws Exception {
        LinkedList<Word> words = new LinkedList<>();
        String query = "SELECT id, word FROM words";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                words.add(new Word(id, word));
            }
        }
        return words;
    }

    public void insertPosData(int wordId, String pos, String stemWord, String rootWord) throws Exception {
        String query = "INSERT INTO pos (word_id, part_of_speech, stem_word, root_word) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, wordId);
            stmt.setString(2, pos);
            stmt.setString(3, stemWord);
            stmt.setString(4, rootWord); // Add root word
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
