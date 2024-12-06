package dal;

import java.io.File;

import dto.WordTranslation;
import javafx.collections.ObservableList;

public interface ICustomDictionaryDAO {

	ObservableList<WordTranslation> getTranslationsForStory(String storyText);
    String importStoryFromFile(File file);
}
