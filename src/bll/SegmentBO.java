package bll;

import dal.SegmentDAO;

import java.util.Map;

public class SegmentBO implements ISegmentBO {
    private final SegmentDAO segmentDAO;

    public SegmentBO(SegmentDAO segmentDAO) {
        this.segmentDAO = segmentDAO;
    }

    /**
     * Segments a compound word and fetches meanings for its segments.
     *
     * @param compoundWord The input compound word.
     * @return Map of each segment and its meanings.
     */
    public Map<String, Map<String, String>> segmentAndFetchMeaning(String compoundWord) {
        // Segment the compound word
        String[] segmentedWords = ArabicSegementer.segmentArabicText(compoundWord);

        // Fetch meanings for segmented words
        return segmentDAO.getMeanings(segmentedWords);
    }
}
