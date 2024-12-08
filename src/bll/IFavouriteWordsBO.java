package bll;

import java.util.List;

public interface IFavouriteWordsBO {

	 public List<String[]> getWords();
	 public boolean addToFavourite(String word);
	 public List<String[]> getFavouriteWords();
	 public int removeFromFavourite(int wordId);
	 
}
