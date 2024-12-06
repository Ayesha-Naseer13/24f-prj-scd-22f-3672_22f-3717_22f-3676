package bll;

import dal.WordDAO;
import dto.WordDTO;

public class WordServiceBLO implements IWordServiceBLO {
    private WordDAO wordDAO;

    public WordServiceBLO() {
        this.wordDAO = new WordDAO(); 
    }

    @Override
    public boolean addWord(WordDTO word) {
        return wordDAO.addWord(word);
    }

    @Override
    public boolean deleteWord(String word) {
        return wordDAO.deleteWord(word);
    }

    @Override
    public boolean updateWord(WordDTO word) {
        return wordDAO.updateWord(word);
    }

    @Override
    public void closeService() {
        wordDAO.closeConnection(); 
    }
}
