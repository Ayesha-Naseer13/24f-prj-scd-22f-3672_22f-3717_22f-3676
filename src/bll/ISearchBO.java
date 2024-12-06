package bll;

import java.util.List;

public interface ISearchBO {


	List<String> getSuggestions(String input, boolean searchByKey, String language);
	List<String> searchWord(String searchTerm, boolean searchByKey, String language);
}
