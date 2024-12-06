package bll;

import dal.DALFacade;
import dal.IDALFacade;

public class ScrapBO implements IScrapBO {
    private IDALFacade dalFacade;

    public ScrapBO() {
        this.dalFacade = new DALFacade();
    }

    @Override
    public String addWordFromUrl(String url) {
        try {
            return dalFacade.scrapeAndInsertWordFromUrl(url);
        } catch (Exception e) {
            return "Failed to add word from URL: " + e.getMessage();
        }
    }

    @Override
    public String addWordFromFile(String filePath) {
        try {
            return dalFacade.scrapeAndInsertWordFromFile(filePath);
        } catch (Exception e) {
            return "Failed to add word from file: " + e.getMessage();
        }
    }
}
