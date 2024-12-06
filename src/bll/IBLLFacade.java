package bll;

import java.util.List;

public interface IBLLFacade extends ISearchBO , DictionaryService,ICustomDictionaryBO,IScrapBO,IFavouriteWordsBO,IViewBO,IBusinessLayer,ISearchHistoryBO,ISegmentBO{

	List<String> getSearchHistory();
	

}
