package dal;

import dto.WordDTO;

public interface IWordDAO {
    boolean addWord(WordDTO word);
    boolean deleteWord(String word);
    boolean updateWord(WordDTO word);
    void closeConnection();
}
