package dal;


import java.util.List;

public interface ISearchDAO {
	 List<String> getSuggestions(String input, boolean searchByKey, String language) ;
	 List<String> searchWord(String searchTerm, boolean searchByKey, String language) ;
	 void addSearchTermToHistory(String searchTerm);

}
