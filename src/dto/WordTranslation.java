package dto;

public class WordTranslation {

	private final String arabic;
	private final String urdu;
	private final String persian;

	public WordTranslation(String arabic, String urdu, String persian) {
		this.arabic = arabic;
		this.urdu = urdu;
		this.persian = persian;
	}

	public String getArabic() {
		return arabic;
	}

	public String getUrdu() {
		return urdu;
	}

	public String getPersian() {
		return persian;
	}
}
