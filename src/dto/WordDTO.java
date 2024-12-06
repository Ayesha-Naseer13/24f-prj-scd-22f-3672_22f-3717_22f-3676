package dto;

public class WordDTO {
    private String word;
    private String persianTranslation; 
    private String urduTranslation; 

    public WordDTO(String word, String persianTranslation, String urduTranslation) {
        this.word = word;
        this.persianTranslation = persianTranslation;
        this.urduTranslation = urduTranslation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPersianTranslation() {
        return persianTranslation;
    }

    public void setPersianTranslation(String persianTranslation) {
        this.persianTranslation = persianTranslation;
    }

    public String getUrduTranslation() {
        return urduTranslation;
    }

    public void setUrduTranslation(String urduTranslation) {
        this.urduTranslation = urduTranslation;
    }

    @Override
    public String toString() {
        return "Word: " + word + ", Persian: " + persianTranslation + ", Urdu: " + urduTranslation;
    }
    
    
}