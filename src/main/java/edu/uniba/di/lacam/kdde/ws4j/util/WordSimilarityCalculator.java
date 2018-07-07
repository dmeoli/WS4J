package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WordSimilarityCalculator {

    public static final char SEPARATOR = '#';

    private ConcurrentMap<String, Double> cache;

	public WordSimilarityCalculator() {
		if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
	}

    public double calcRelatednessOfWords(String word1, String word2, RelatednessCalculator rc) {
        if (word1 != null && word1.equals(word2)) return rc.getMax();
        if (word1 == null || word2 == null || word1.length() == 0 || word2.length() == 0) return rc.getMin();
        word1 = word1.replaceAll(" ", "_").toLowerCase();
        word2 = word2.replaceAll(" ", "_").toLowerCase();
        String key = word1 + " & " + word2;
        if (WS4JConfiguration.getInstance().useCache()) {
            Double cachedObj = cache.get(key);
            if (cachedObj != null) return cachedObj;
        }
        POS pos1 = null;
        int sense1 = 0;
        int offset1POS = word1.indexOf(SEPARATOR);
        int offset1Sense = word1.lastIndexOf(SEPARATOR);
        if (offset1POS != -1) {
            if ((pos1 = POS.getPOS(word1.charAt(offset1POS+1))) == null) return rc.getMin();
            if (offset1Sense != -1 && offset1POS != offset1Sense) {
                if ((sense1 = Character.getNumericValue(word1.charAt(offset1Sense+1))) == 0)
                    throw new IllegalArgumentException("Sense number must be greater than 0");
            }
            word1 = word1.substring(0, offset1POS);
        }
        POS pos2 = null;
        int sense2 = 0;
        int offset2POS = word2.indexOf(SEPARATOR);
        int offset2Sense = word2.lastIndexOf(SEPARATOR);
        if (offset2POS != -1) {
            if ((pos2 = POS.getPOS(word2.charAt(offset2POS+1))) == null) return rc.getMin();
            if (offset2Sense != -1 && offset2POS != offset2Sense) {
                if ((sense2 = Character.getNumericValue(word2.charAt(offset2Sense+1))) == 0) {
                    throw new IllegalArgumentException("Sense number must be greater than 0");
                }
            }
            word2 = word2.substring(0, offset2POS);
        }
        double maxScore = -1.0D;
        for (POS[] POSPair : rc.getPOSPairs()) {
            if (pos1 != null && pos1 != POSPair[0]) continue;
            if (pos2 != null && pos2 != POSPair[1]) continue;
            if (sense1 > 0 && sense2 > 0) {
                int word1Senses;
                if (sense1 > (word1Senses = rc.getLexicalDB().getAllConcepts(word1, POSPair[0]).size()))
                    throw new IllegalArgumentException("Enter a sense number less or equal than " + word1Senses +
                            " for " + '\"' + word1 + '\"');
                int word2Senses;
                if (sense2 > (word2Senses = rc.getLexicalDB().getAllConcepts(word2, POSPair[1]).size()))
                    throw new IllegalArgumentException("Enter a sense number less or equal than " + word2Senses +
                            " for " + '\"' + word2 + '\"');
                Concept concept1 = rc.getLexicalDB().getConcept(word1, POSPair[0], sense1);
                Concept concept2 = rc.getLexicalDB().getConcept(word2, POSPair[1], sense2);
                maxScore = rc.calcRelatednessOfSynsets(concept1, concept2).getScore();
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
        if (WS4JConfiguration.getInstance().useCache()) cache.put(key, maxScore);
        return maxScore;
    }
}
