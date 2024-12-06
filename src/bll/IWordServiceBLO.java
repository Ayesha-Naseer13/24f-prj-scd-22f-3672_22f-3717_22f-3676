package bll;
import dto.WordDTO;
public interface IWordServiceBLO {

	    boolean addWord(WordDTO word);
	    boolean deleteWord(String word);
	    boolean updateWord(WordDTO word);
	    void closeService();
	}


