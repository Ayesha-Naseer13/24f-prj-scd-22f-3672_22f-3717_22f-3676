package dal;

public interface IDAOFactory {
    IDictionaryDAO createDictionaryDAO();
    ISearchDAO createSearchDAO();
    ICustomDictionaryDAO createCustomDictionaryDAO();
    IScrapDAO createScrapDAO();
    ISegmentDAO createSegmentDAO();
    IViewDAO createViewDAO();
    ISearchHistoryDAO createSearchHistoryDAO();
    IWordDAO createWordDAO();
    IFavouriteWordsDAO createFavouriteWordsDAO();
}
