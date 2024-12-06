
package bll;

import java.util.List;

import dal.ViewDAO;
import dto.Translation;

public class ViewBO {
    private ViewDAO viewDAO;

    public ViewBO() {
        this.viewDAO = new ViewDAO();
    }

    public List<String> viewAllWords() {
        return viewDAO.getAllWords();
    }

    public List<Translation> viewWordDetails(String word) {
        return viewDAO.getWordDetails(word);
    }
}
