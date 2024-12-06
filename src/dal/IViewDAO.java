package dal;

import java.util.List;

import dto.Translation;

public interface IViewDAO {

	 public List<String> getAllWords() ;
	 public List<Translation> getWordDetails(String word);
	 
}
