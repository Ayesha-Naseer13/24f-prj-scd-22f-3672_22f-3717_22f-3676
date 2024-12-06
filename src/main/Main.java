package main;

import dal.*;

public class Main {
    public static void main(String[] args) {
        // Get DAO instances from the factory
        AbstractDAOFactory daoFactory = (AbstractDAOFactory) AbstractDAOFactory.getInstance();

        IDictionaryDAO dictionaryDAO = daoFactory.createDictionaryDAO();
        ISearchDAO searchDAO = daoFactory.createSearchDAO();
        ICustomDictionaryDAO customDAO = daoFactory.createCustomDictionaryDAO();
        IScrapDAO scrapDAO = daoFactory.createScrapDAO();
        ISegmentDAO segmentDAO = daoFactory.createSegmentDAO();
        IViewDAO viewDAO = daoFactory.createViewDAO();
        ISearchHistoryDAO searchHistoryDAO = daoFactory.createSearchHistoryDAO();
        IWordDAO wordDAO = daoFactory.createWordDAO();
        IFavouriteWordsDAO favouriteWordsDAO = daoFactory.createFavouriteWordsDAO();

        // Create the DALFacade with the DAOs
        IDALFacade facade = new DALFacade(
                
        );

    }
}
