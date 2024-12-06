package dal;

public interface IScrapDAO {
    String scrapeAndInsertWordFromFile(String filePath);
    String scrapeAndInsertWordFromUrl(String url);
}
