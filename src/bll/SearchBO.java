package bll;

import java.util.List;

import dal.DALFacade;
import dal.IDALFacade;

public class SearchBO implements ISearchBO {
    private IDALFacade dalFacade;

    public SearchBO() {
        this.dalFacade = new DALFacade();
    }

    @Override
    public List<String> getSuggestions(String input, boolean searchByKey, String language) {
        return dalFacade.getSuggestions(input, searchByKey, language);
    }

    @Override
    public List<String> searchWord(String searchTerm, boolean searchByKey, String language) {
      
        dalFacade.addSearchTermToHistory(searchTerm);

       
        return dalFacade.searchWord(searchTerm, searchByKey, language);
    }
}
