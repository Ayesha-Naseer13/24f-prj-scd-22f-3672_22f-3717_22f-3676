package bll;

import java.util.List;

public interface IFavouriteWordsBO {

	 public List<String[]> getAllWords();
	 public boolean addToFavourite(String word);
	 public List<String[]> getFavouriteWords();
	 public void removeFromFavourite(int wordId);
	 
}
