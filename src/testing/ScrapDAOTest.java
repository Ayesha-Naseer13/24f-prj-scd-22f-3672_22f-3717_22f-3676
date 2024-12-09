package testing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dal.ScrapDAO;

public class ScrapDAOTest {

    private ScrapDAO scrapDAO;
  

    @BeforeEach
    public void setUp() {
        // Create a mock connection
      
        // Initialize ScrapDAO with the mock connection
        scrapDAO = new ScrapDAO();
    }

   

    @Test
    public void testScrapeAndInsertWordFromUrl_ValidUrl() {
        String url = "https://www.almaany.com/fa/dict/ar-fa/أسرة/";
        // Mock the JSoup connection and simulate the response
        String result = scrapDAO.scrapeAndInsertWordFromUrl(url);
        Assertions.assertEquals("Word and meanings added successfully to the database.", result);
    }

    @Test
    public void testScrapeAndInsertWordFromUrl_NoWordFound() {
        String url = "https://www.almaany.com/fa/dict/ar-fa/lahylna/"; // Non-existent word
        String result = scrapDAO.scrapeAndInsertWordFromUrl(url);
        
        // Assert that the result matches one of the expected values
        Assertions.assertTrue(result.equals("Word or meaning not found in the page") || 
                              result.equals("Error: Unable to scrape data from the URL using available user agents."));
    }


    @Test
    public void testScrapeAndInsertWordFromUrl_LanguageError() {
        String url = "https://www.almaany.com/it/dict/ar-it/أسرة/"; // Language mismatch
        String result = scrapDAO.scrapeAndInsertWordFromUrl(url);
        
        // Assert that the result matches one of the expected error messages
        Assertions.assertTrue(result.equals("Error: Unable to determine the language from the URL.") ||
                              result.equals("Error: Unable to scrape data from the URL using available user agents."));
    }





//    @Test
//    public void testScrapeAndInsertWordFromUrl_ConnectionFailure() {
//        // Arrange: Mock Jsoup.connect to throw an IOException
//        String url = "https://www.almaany.com/fa/dict/ar-fa/أسرة/";
//
//        // Mock Jsoup.connect to throw an IOException
//        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
//            mockedJsoup.when(() -> Jsoup.connect(url)).thenThrow(new IOException("Connection failure"));
//            
//            // Call method under test
//            String result = scrapDAO.scrapeAndInsertWordFromUrl(url);
//
//            // Assert: Verify the error handling for connection failure
//            Assertions.assertTrue(result.contains("Error: Unable to scrape data from the URL"));
//        }
//    }
}
