package bll;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ArabicSegementer {

    /**
     * Segments an Arabic compound word into individual words using Stanford NLP.
     *
     * @param text The Arabic compound word.
     * @return An array of segmented words.
     */
    public static String[] segmentArabicText(String text) {
       
        text = removeArabicAffixes(text);

      
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit");
        props.setProperty("tokenize.language", "ar");
        props.setProperty("segment.model", "C:\\Users\\lenovo\\Downloads\\stanford-corenlp-4.4.0-models-arabic\\edu\\stanford\\nlp\\models\\segmenter\\arabic\\arabic-segmenter-atb+bn+arztrain.ser.gz");

       
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

       
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

       
        List<String> tokens = document.tokens().stream()
                .map(CoreLabel::word)
                .collect(Collectors.toList());

        return tokens.toArray(new String[0]);
    }

    /**
     * Removes common Arabic prefixes and suffixes from the word.
     *
     * @param word The word to be cleaned.
     * @return The cleaned word.
     */
    private static String removeArabicAffixes(String word) {
      
    	 String[] prefixes = {"ال", "و", "ف", "ب", "ل", "ك", "م", "ت", "ن", "إ"};
         
        
         String[] suffixes = { "ي", "ون", "ات", "ين", "ه", "هم", "ها", "ا", "ينِ", "وا", "يّ"};

       
        for (String prefix : prefixes) {
            if (word.startsWith(prefix)) {
                word = word.substring(prefix.length());
                break; 
            }
        }
 
        for (String suffix : suffixes) {
            if (word.endsWith(suffix)) {
                word = word.substring(0, word.length() - suffix.length());
                break; 
            }
        }

        return word;
    }
}
