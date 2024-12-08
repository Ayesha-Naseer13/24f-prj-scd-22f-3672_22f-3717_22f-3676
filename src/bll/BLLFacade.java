package bll;

import java.io.File;
import java.util.List;
import java.util.Map;

import dal.DALFacade;
import dal.IDALFacade;
import dal.SegmentDAO;
import dto.Translation;
import dto.WordDTO;
import dto.WordTranslation;
import javafx.collections.ObservableList;

public class BLLFacade implements IBLLFacade {
    private ISearchBO searchBO;        
    private DictionaryService dictionaryService; 
    private ICustomDictionaryBO customDic;
    private ISearchHistoryBO searchHistoryBO;
    private IViewBO viewBO;
    private IFavouriteWordsBO favouriteBO;
    private IScrapBO scrapBO;
    private IWordServiceBLO wordBO;
    private ISegmentBO segmentBO;
    private SegmentDAO segmentdao;
    public BLLFacade() {
        IDALFacade dalFacade = new DALFacade();
        this.searchBO = new SearchBO();
        this.dictionaryService = new DictionaryServiceImpl();
        this.customDic = new CustomDictionaryBO();
        this.searchHistoryBO = new SearchHistoryBO();
        this.viewBO = new ViewBO();
        this.favouriteBO = new FavouriteWordsBO();
        this.scrapBO = new ScrapBO();
        this.segmentBO = new SegmentBO(segmentdao);
        this.wordBO = new WordServiceBLO();
    }
    

    @Override
    public List<String> importCSV(String filePath) {
        return dictionaryService.importCSV(filePath);
    }

    @Override
    public List<String> getSuggestions(String input, boolean searchByKey, String language) {
        return searchBO.getSuggestions(input, searchByKey, language);
    }

    @Override
    public List<String> searchWord(String searchTerm, boolean searchByKey, String language) {
        return searchBO.searchWord(searchTerm, searchByKey, language);
    }

    @Override
    public ObservableList<WordTranslation> getTranslationsForStory(String storyText) {
        return customDic.getTranslationsForStory(storyText);
    }

    @Override
    public String importStoryFromFile(File file) {
        return customDic.importStoryFromFile(file);
    }

    @Override
    public List<String> getSearchHistory() {
        return searchHistoryBO.getSearchHistory();
    }

   
   

	@Override
	public String addWordFromUrl(String url) {
		// TODO Auto-generated method stub
		return scrapBO.addWordFromUrl(url);
	}

	@Override
	public String addWordFromFile(String filePath) {
		// TODO Auto-generated method stub
		return scrapBO.addWordFromFile(filePath);
	}

	@Override
	public List<String[]> getWords() {
		return favouriteBO.getWords();
	}

	@Override
	public boolean addToFavourite(String word) {
		return favouriteBO.addToFavourite(word);
	}

	@Override
	public List<String[]> getFavouriteWords() {
		return favouriteBO.getFavouriteWords();
	}

	@Override
	public int removeFromFavourite(int wordId) {
		return favouriteBO.removeFromFavourite(wordId);
		
	}

	@Override
	public List<String> viewAllWords() {
		return viewBO.viewAllWords();
	}

	@Override
	public List<Translation> viewWordDetails(String word) {
		return viewBO.viewWordDetails(word);
	}

	@Override
	public void processWords() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<String, String>> segmentAndFetchMeaning(String compoundWord) {
		// TODO Auto-generated method stub
		return segmentBO.segmentAndFetchMeaning(compoundWord);
	}


	@Override
	public boolean addWord(WordDTO word) {
		// TODO Auto-generated method stub
		return wordBO.addWord(word);
	}


	@Override
	public boolean deleteWord(String word) {
		// TODO Auto-generated method stub
		return wordBO.deleteWord(word);
	}


	@Override
	public boolean updateWord(WordDTO word) {
		// TODO Auto-generated method stub
		return wordBO.updateWord(word);
	}


	@Override
	public void closeService() {
		// TODO Auto-generated method stub
		
	}
}
