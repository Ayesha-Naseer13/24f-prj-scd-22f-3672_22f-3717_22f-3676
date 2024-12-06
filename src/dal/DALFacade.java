package dal;

import java.io.File;
import java.util.List;
import java.util.Map;

import dto.WordTranslation;
import javafx.collections.ObservableList;

public class DALFacade implements IDALFacade {
    private IDictionaryDAO dictionaryDAO;
    private ISearchDAO searchDAO;
    private ICustomDictionaryDAO custom;
    private IScrapDAO scrapDAO;
    private ISearchHistoryDAO searchHistoryDAO;
    private ISegmentDAO segmentDAO;
    private IFavouriteWordsDAO frvtDAO;
    private IViewDAO viewDAO;
    private IDAOFactory factory;
	
		
    public DALFacade() {
    	factory = new MySQLDAOFactory();
        this.dictionaryDAO = factory.createDictionaryDAO();
        this.searchDAO = factory.createSearchDAO();
        this.custom = factory.createCustomDictionaryDAO();
        this.scrapDAO = factory.createScrapDAO();
        this.searchHistoryDAO = factory.createSearchHistoryDAO();
        this.segmentDAO = factory.createSegmentDAO();
        this.frvtDAO = factory.createFavouriteWordsDAO();
        this.viewDAO = factory.createViewDAO();
    }

    // Methods related to DictionaryDAO
    @Override
    public List<String> importCSV(String filePath) {
        return dictionaryDAO.importCSV(filePath);
    }

    @Override
    public List<String> getSuggestions(String input, boolean searchByKey, String language) {
        return searchDAO.getSuggestions(input, searchByKey, language);
    }

    @Override
    public List<String> searchWord(String searchTerm, boolean searchByKey, String language) {
        return searchDAO.searchWord(searchTerm, searchByKey, language);
    }

    // Methods related to CustomDictionaryDAO
    @Override
    public ObservableList<WordTranslation> getTranslationsForStory(String storyText) {
        return custom.getTranslationsForStory(storyText);
    }

    @Override
    public String importStoryFromFile(File file) {
        return custom.importStoryFromFile(file);
    }

    // Methods related to ScrapDAO
    @Override
    public String scrapeAndInsertWordFromFile(String filePath) {
        return scrapDAO.scrapeAndInsertWordFromFile(filePath);
    }

    @Override
    public String scrapeAndInsertWordFromUrl(String url) {
        return scrapDAO.scrapeAndInsertWordFromUrl(url);
    }

    // Methods related to SearchHistoryDAO
    @Override
    public List<String> getSearchHistory() {
        return searchHistoryDAO.getSearchHistory();
    }

    // Additional methods for SearchTerm history management (if necessary)
    @Override
    public void addSearchTermToHistory(String searchTerm) {
    	searchDAO.addSearchTermToHistory(searchTerm);  // Directly call the method on searchHistoryDAO
    }


    @Override
    public Map<String, Map<String, String>> getMeanings(String[] words) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String[]> getPosDetails(String arabicWord) {
        // TODO Auto-generated method stub
        return null;
    }
}
