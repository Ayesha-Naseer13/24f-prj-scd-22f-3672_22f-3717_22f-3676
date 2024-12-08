package dal;

import java.io.File;
import java.util.List;
import java.util.Map;

import dto.Translation;
import dto.WordDTO;
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
    private IDataAccessLayer posDAO;
    private IDAOFactory factory;
    private IWordDAO wordDAO;;
		
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
        this.posDAO = factory.createDataAccessLayer();
        this.wordDAO=factory.createWordDAO();
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
        return segmentDAO.getMeanings(words);
    }

    @Override
    public List<String[]> getPosDetails(String arabicWord) {
        return posDAO.getPosDetails(arabicWord);
    }
    
    @Override
    public List<String> getAllWords() {
    	return viewDAO.getAllWords();
    }
   
    @Override
    public List<Translation> getWordDetails(String word){
    	return viewDAO.getWordDetails(word);
    }
    
    @Override
    public List<String[]> getWords() {
        return frvtDAO.getWords();
    }
    
    @Override
    public int getWordIdByWord(String word) {
    	 return frvtDAO.getWordIdByWord(word);
    }
    
    @Override
    public boolean addToFavourite(int wordId, String word) {
    	return frvtDAO.addToFavourite(wordId,word);
    }
    
    @Override
    public List<String[]> getFavouriteWords() {
    	return frvtDAO.getFavouriteWords();
    }
    
    @Override
    public int removeFromFavourite(int wordId) {
    	return frvtDAO.removeFromFavourite(wordId);
    }

	@Override
	public boolean addWord(WordDTO word) {
		// TODO Auto-generated method stub
		return wordDAO.addWord(word);
	}

	@Override
	public boolean deleteWord(String word) {
		// TODO Auto-generated method stub
		return wordDAO.deleteWord(word);
	}

	@Override
	public boolean updateWord(WordDTO word) {
		// TODO Auto-generated method stub
		return wordDAO.updateWord(word);
	}

	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
}
