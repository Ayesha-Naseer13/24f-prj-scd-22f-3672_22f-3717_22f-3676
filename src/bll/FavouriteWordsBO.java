package bll;

import java.util.List;

import dal.FavouriteWordsDAO;
import main.WordNotFoundException;

public class FavouriteWordsBO {
    private FavouriteWordsDAO favouriteWordsDAO;

    public FavouriteWordsBO() {
        this.favouriteWordsDAO = new FavouriteWordsDAO();
    }

    public List<String[]> getAllWords() {
        return favouriteWordsDAO.getAllWords();
    }

    public boolean addToFavourite(String word) {
        int wordId = favouriteWordsDAO.getWordIdByWord(word);
        if (wordId == -1) {
            throw new WordNotFoundException("Word '" + word + "' not found in the dictionary.");
        }
        return favouriteWordsDAO.addToFavourite(wordId, word);
    }

    public List<String[]> getFavouriteWords() {
        return favouriteWordsDAO.getFavouriteWords();
    }

    public void removeFromFavourite(int wordId) {
        favouriteWordsDAO.removeFromFavourite(wordId);
    }
}
