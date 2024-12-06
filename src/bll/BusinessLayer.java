package bll;

import java.util.LinkedList;
import java.util.List;

import dal.DataAccessLayer;
import dto.Word;

public class BusinessLayer implements IBusinessLayer{
    private DataAccessLayer dataAccessLayer;

    public BusinessLayer() throws Exception {
        this.dataAccessLayer = new DataAccessLayer();
    }

    @Override 
    public void processWords() {
        try {
            LinkedList<Word> words = dataAccessLayer.getWords();

            for (Word word : words) {
                List<String[]> posTaggedResults = dataAccessLayer.getPosDetails(word.getWord());

                for (String[] result : posTaggedResults) {
                    String voweledWord = result[0];
                    String stem = result[1];
                    String pos = result[2];
                    String rootWord = result[3];

                    // Insert POS data into the database
                    dataAccessLayer.insertPosData(word.getId(), pos, stem, rootWord);
                    System.out.println("Inserted POS data for word_id " + word.getId() + ": " + pos + ", " + stem + ", " + rootWord);
                }
            }
        } catch (Exception e) {
            // Handle the exception (log it, show a message, etc.)
            System.err.println("Error processing words: " + e.getMessage());
            e.printStackTrace(); // Optionally log the stack trace for debugging
        }
    }


    public void close() throws Exception {
        dataAccessLayer.close();
    }
}
