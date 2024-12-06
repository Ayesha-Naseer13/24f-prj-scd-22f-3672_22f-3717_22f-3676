package bll;

import java.util.Map;

/**
 * Interface for the Segment Business Object.
 */
public interface ISegmentBO {
    /**
     * Segments a compound word and fetches meanings for its segments.
     *
     * @param compoundWord The input compound word.
     * @return Map of each segment and its meanings.
     */
    Map<String, Map<String, String>> segmentAndFetchMeaning(String compoundWord);
}
