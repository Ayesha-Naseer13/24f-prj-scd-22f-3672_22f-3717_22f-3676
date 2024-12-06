package bll;

import java.io.File;

import dal.DALFacade;
import dal.IDALFacade;
import dto.WordTranslation;
import javafx.collections.ObservableList;

public class CustomDictionaryBO implements ICustomDictionaryBO {

    private final IDALFacade dictionaryDAO;

    public CustomDictionaryBO(IDALFacade dictionaryDAO) {
        this.dictionaryDAO = dictionaryDAO != null ? dictionaryDAO : new DALFacade();
    }

    public CustomDictionaryBO() {
        this(new DALFacade());
    }

    @Override
    public ObservableList<WordTranslation> getTranslationsForStory(String storyText) {
        if (storyText == null || storyText.trim().isEmpty()) {
            throw new IllegalArgumentException("Story text cannot be null or empty.");
        }

        return dictionaryDAO.getTranslationsForStory(storyText);
    }

    @Override
    public String importStoryFromFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return "Error: Invalid file. Please provide a valid file.";
        }

        String storyText = dictionaryDAO.importStoryFromFile(file);
        if (storyText == null || storyText.isEmpty()) {
            return "Error: The file is empty or could not be read.";
        }
        return storyText;
    }
}
