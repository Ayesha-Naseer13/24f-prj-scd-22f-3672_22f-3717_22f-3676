package testing;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CustomDictionaryDAOTest.class,
    DictionaryDAOTest.class,
    SearchDAOTest.class
})
public class AllTestsSuite {
}
