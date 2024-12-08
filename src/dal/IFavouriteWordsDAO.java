package dal;

import java.util.List;

public interface IFavouriteWordsDAO {

	public List<String[]> getWords();
	public int getWordIdByWord(String word);
	public boolean addToFavourite(int wordId, String word);
	public List<String[]> getFavouriteWords();
	public int removeFromFavourite(int wordId);
}
