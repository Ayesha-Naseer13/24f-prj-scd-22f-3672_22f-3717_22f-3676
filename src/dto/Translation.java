package dto;

public class Translation {
	private String word;
	private String urduMeaning;
	private String persianMeaning;
	private String partOfSpeech;
	private String stemWord;
	private String rootWord;

	public Translation(String urduMeaning, String persianMeaning, String partOfSpeech, String stemWord, String rootWord,
			String word) {
		this.urduMeaning = urduMeaning;
		this.persianMeaning = persianMeaning;
		this.partOfSpeech = partOfSpeech;
		this.stemWord = stemWord;
		this.rootWord = rootWord;
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public String getUrduMeaning() {
		return urduMeaning;
	}

	public String getPersianMeaning() {
		return persianMeaning;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public String getStemWord() {
		return stemWord;
	}

	public String getRootWord() {
		return rootWord;
	}
}
