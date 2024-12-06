package bll;

import java.util.List;

import dal.DALFacade;
import dal.IDALFacade;

public class DictionaryServiceImpl implements DictionaryService {
    private IDALFacade dalFacade;

    public DictionaryServiceImpl() {
        this.dalFacade = new DALFacade();
    }

    @Override
    public List<String> importCSV(String filePath) {
        return dalFacade.importCSV(filePath);
    }
}
