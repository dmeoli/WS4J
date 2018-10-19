package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkArgument;

public class WordSimilarityCalculator {

    public static final char SEPARATOR = '#';

    private ConcurrentMap<String, Double> cache;

    public WordSimilarityCalculator() {
        if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
    }

    private String normalize(String word) {
        word = word.toLowerCase();
        word = word.replace(' ', '_');
        return word;
    }

    public double calcRelatednessOfWords(String word1, String word2, RelatednessCalculator rc) {
        if (word1 != null && word1.equals(word2)) return rc.getMax();
        if (word1 == null || word2 == null || word1.length() == 0 || word2.length() == 0) return rc.getMin();

        word1 = normalize(word1);
        word2 = normalize(word2);

        String key = word1 + " & " + word2;
        if (WS4JConfiguration.getInstance().useCache()) {
            Double cachedObj = cache.get(key);
            if (cachedObj != null) return cachedObj;
            String reverseKey = word2 + " & " + word1;
            cachedObj = cache.get(reverseKey);
            if (cachedObj != null) return cachedObj;
        }

        POS pos1 = null;
        int sense1 = 0;
        int offset1POS = word1.indexOf(SEPARATOR);
        int offset1Sense = word1.lastIndexOf(SEPARATOR);
        if (offset1POS != -1) {
            if ((pos1 = POS.getPOS(word1.charAt(offset1POS + 1))) == null) return rc.getMin();
            if (offset1Sense != -1 && offset1POS != offset1Sense) {
                if ((sense1 = Character.getNumericValue(word1.charAt(offset1Sense + 1))) == 0)
                    throw new IllegalArgumentException("Sense number must be greater than 0");
            }
            word1 = word1.substring(0, offset1POS);
            if (sense1 > 0) {
                int word1Senses;
                if (sense1 > (word1Senses = rc.getLexicalDB().getAllConcepts(word1, pos1).size()))
                    throw new IllegalArgumentException(word1Senses == 1 ? "Invalid sense number. The word " +
                            '\"' + word1 + '\"' + " has only one sense in WordNet" : "Sense number not found in WordNet. " +
                            "Enter a sense number less or " + "equal than " + word1Senses + " for " + "\"" + word1 + "\".");
            }
        }

        POS pos2 = null;
        int sense2 = 0;
        int offset2POS = word2.indexOf(SEPARATOR);
        int offset2Sense = word2.lastIndexOf(SEPARATOR);
        if (offset2POS != -1) {
            if ((pos2 = POS.getPOS(word2.charAt(offset2POS + 1))) == null) return rc.getMin();
            if (offset2Sense != -1 && offset2POS != offset2Sense) {
                if ((sense2 = Character.getNumericValue(word2.charAt(offset2Sense + 1))) == 0)
                    throw new IllegalArgumentException("Sense number must be greater than 0");
            }
            word2 = word2.substring(0, offset2POS);
            if (sense2 > 0) {
                int word2Senses;
                if (sense2 > (word2Senses = rc.getLexicalDB().getAllConcepts(word2, pos2).size()))
                    throw new IllegalArgumentException(word2Senses == 1 ? "Invalid sense number. The word " +
                            '\"' + word2 + '\"' + " has only one sense in WordNet" : "Sense number not found in WordNet. " +
                            "Enter a sense number less or " + "equal than " + word2Senses + " for " + "\"" + word2 + "\".");
            }
        }

        double maxScore = -1.0D;
        for (POS[] POSPair : rc.getPOSPairs()) {
            if (pos1 != null && pos1 != POSPair[0]) continue;
            if (pos2 != null && pos2 != POSPair[1]) continue;
            if (sense1 > 0 && sense2 > 0) {
                maxScore = rc.calcRelatednessOfSynsets(
                        rc.getLexicalDB().getConcept(word1, POSPair[0], sense1),
                        rc.getLexicalDB().getConcept(word2, POSPair[1], sense2)).getScore();
            } else if (sense1 == 0 && sense2 > 0) {
                for (Concept concept : rc.getLexicalDB().getAllConcepts(word1, POSPair[0])) {
                    Relatedness relatedness = rc.calcRelatednessOfSynsets(concept,
                            rc.getLexicalDB().getConcept(word2, POSPair[1], sense2));
                    double score = relatedness.getScore();
                    if (score > maxScore) maxScore = score;
                }
            } else if (sense1 > 0 && sense2 == 0) {
                for (Concept concept : rc.getLexicalDB().getAllConcepts(word2, POSPair[1])) {
                    Relatedness relatedness = rc.calcRelatednessOfSynsets(
                            rc.getLexicalDB().getConcept(word1, POSPair[0], sense1), concept);
                    double score = relatedness.getScore();
                    if (score > maxScore) maxScore = score;
                }
            } else if (WS4JConfiguration.getInstance().useMFS()) {
                Concept concept1 = rc.getLexicalDB().getConcept(word1, POSPair[0], 1);
                Concept concept2 = rc.getLexicalDB().getConcept(word2, POSPair[1], 1);
                maxScore = rc.calcRelatednessOfSynsets(concept1, concept2).getScore();
            } else {
                for (Concept concept1 : rc.getLexicalDB().getAllConcepts(word1, POSPair[0])) {
                    for (Concept concept2 : rc.getLexicalDB().getAllConcepts(word2, POSPair[1])) {
                        Relatedness relatedness = rc.calcRelatednessOfSynsets(concept1, concept2);
                        double score = relatedness.getScore();
                        if (score > maxScore) maxScore = score;
                    }
                }
            }
        }
        if (maxScore == -1.0D) maxScore = 0.0D;
        maxScore = Math.abs(maxScore);
        checkArgument(maxScore >= rc.getMin() && maxScore <= rc.getMax());
        if (WS4JConfiguration.getInstance().useCache()) cache.put(key, maxScore);
        return maxScore;
    }
}
