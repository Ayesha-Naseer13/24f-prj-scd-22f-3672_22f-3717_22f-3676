package bll;

import java.util.List;

import dal.FavouriteWordsDAO;
import main.WordNotFoundException;

public class FavouriteWordsBO implements IFavouriteWordsBO{
    private FavouriteWordsDAO favouriteWordsDAO;

    public FavouriteWordsBO() {
        this.favouriteWordsDAO = new FavouriteWordsDAO();
    }
    @Override
    public List<String[]> getWords() {
        return favouriteWordsDAO.getWords();
    }
    @Override
    public boolean addToFavourite(String word) {
        int wordId = favouriteWordsDAO.getWordIdByWord(word);
        if (wordId == -1) {
            throw new WordNotFoundException("Word '" + word + "' not found in the dictionary.");
        }
        return favouriteWordsDAO.addToFavourite(wordId, word);
    }
    @Override
    public List<String[]> getFavouriteWords() {
        return favouriteWordsDAO.getFavouriteWords();
    }
    @Override
    public int removeFromFavourite(int wordId) {
        return favouriteWordsDAO.removeFromFavourite(wordId);
    }
}
