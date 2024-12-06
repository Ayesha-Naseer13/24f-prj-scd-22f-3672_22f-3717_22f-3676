package bll;

import java.util.List;

import dto.Translation;

public interface IViewBO {

	public List<String> viewAllWords();
	public List<Translation> viewWordDetails(String word);
}
