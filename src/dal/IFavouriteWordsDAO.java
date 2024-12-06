package dal;

import java.util.List;

public interface IFavouriteWordsDAO {

	public List<String[]> getAllWords();
	public int getWordIdByWord(String word);
	public boolean addToFavourite(int wordId, String word);
	public List<String[]> getFavouriteWords();
	public void removeFromFavourite(int wordId);
}
