package dal;

import java.util.Map;

public interface ISegmentDAO {


    /**
     * Fetches meanings for an array of words.
     *
     * @param words Array of words to fetch meanings for.
     * @return Map with the word as key and a nested map for meanings.
     */
    public Map<String, Map<String, String>> getMeanings(String[] words);
}
