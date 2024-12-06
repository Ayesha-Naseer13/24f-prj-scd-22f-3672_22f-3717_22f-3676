package bll;

import java.io.File;

import dto.WordTranslation;
import javafx.collections.ObservableList;

public interface ICustomDictionaryBO {
	public ObservableList<WordTranslation> getTranslationsForStory(String storyText);

    String importStoryFromFile(File file);

}
