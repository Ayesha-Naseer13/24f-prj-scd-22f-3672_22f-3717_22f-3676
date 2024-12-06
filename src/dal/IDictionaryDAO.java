package dal;

import java.util.List;

public interface IDictionaryDAO {

    
    List<String> importCSV(String filePath);
   
}
