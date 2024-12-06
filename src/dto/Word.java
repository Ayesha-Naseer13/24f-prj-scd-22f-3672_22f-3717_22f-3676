package dto;

public class Word {
    private int id;
    private String word;

    public Word(int id, String word) {
        this.id = id;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "Word{id=" + id + ", word='" + word + "'}";
    }
}
