package testing;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CustomDictionaryDAOTest.class,
    DictionaryDAOTest.class,
    SearchDAOTest.class,
<<<<<<< HEAD
//    ViewDAOTest.class,
//    DataAccessLayerTest.class,
//    FavouriteWordsDAOTest.class,
//    WordDAOTest.class,
//    SearchHistoryDAOTest.class,
//    ScrapDAOTest.class,
//    SegmentDAOTest.class,
//    
=======

    
>>>>>>> 3f24c56293424912052925a6daf091c8dc8809f3
})
public class AllTestsSuite {
}
