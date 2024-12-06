package bll;

import dal.ISearchHistoryDAO;
import dal.SearchHistoryDAO;

import java.util.List;

public class SearchHistoryBO implements ISearchHistoryBO {
    private ISearchHistoryDAO searchHistoryDAO;

    public SearchHistoryBO() {
        this.searchHistoryDAO = new SearchHistoryDAO();
    }

    @Override
    public List<String> getSearchHistory() {
        return searchHistoryDAO.getSearchHistory();
    }
}
