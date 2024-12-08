
package bll;

import java.util.List;

import dal.ViewDAO;
import dto.Translation;

public class ViewBO implements IViewBO {
    private ViewDAO viewDAO;

    public ViewBO() {
        this.viewDAO = new ViewDAO();
    }
    
    @Override
    public List<String> viewAllWords() {
        return viewDAO.getAllWords();
    }
    @Override
    public List<Translation> viewWordDetails(String word) {
        return viewDAO.getWordDetails(word);
    }
}
