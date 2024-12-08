package dal;

public class MySQLDAOFactory extends AbstractDAOFactory {
    @Override
    public IDictionaryDAO createDictionaryDAO() {
        return new DictionaryDAO(); // Your existing DictionaryDAO class
    }

    @Override
    public ISearchDAO createSearchDAO() {
        return new SearchDAO(); // Your existing SearchDAO class
    }

	@Override
	public ICustomDictionaryDAO createCustomDictionaryDAO() {
	
		return new CustomDictionaryDAO();
	}

	@Override
	public IScrapDAO createScrapDAO() {
		// TODO Auto-generated method stub
		return new ScrapDAO();
	}

	@Override
	public ISegmentDAO createSegmentDAO() {
		// TODO Auto-generated method stub
		return new SegmentDAO();
	}

	@Override
	public IViewDAO createViewDAO() {
	        return new ViewDAO(); // Ensure this is a valid cast
	    
	}

	@Override
	public ISearchHistoryDAO createSearchHistoryDAO() {
		// TODO Auto-generated method stub
		return new SearchHistoryDAO();
	}

	@Override
	public IWordDAO createWordDAO() {
		// TODO Auto-generated method stub
		return new WordDAO();
	}

	@Override
	public IFavouriteWordsDAO createFavouriteWordsDAO() {
		// TODO Auto-generated method stub
		return new FavouriteWordsDAO();
	}

	@Override
	public IDataAccessLayer createDataAccessLayer() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
