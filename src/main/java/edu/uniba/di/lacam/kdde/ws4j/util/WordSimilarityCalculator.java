package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WordSimilarityCalculator {

    private ConcurrentMap<String, Double> cache;
	
	public WordSimilarityCalculator() {
		if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
	}

    public double calcRelatednessOfWords(String word1, String word2, RelatednessCalculator rc) {
        String key = word1 + " & " + word2;
        if (word1 != null && word1.equals(word2)) return rc.getMax();
        if (word1 == null || word2 == null || word1.length() == 0 || word2.length() == 0) return rc.getMin();
        POS pos1 = null;
        int offset1 = word1.indexOf("#");
        if (offset1 != -1) {
            if ((pos1 = POS.getPOS(word1.charAt(offset1+1))) == null) return rc.getMin();
            word1 = word1.substring(0, offset1);
        }
        POS pos2 = null;
        int offset2 = word2.indexOf("#");
        if (offset2 != -1) {
            if ((pos2 = POS.getPOS(word2.charAt(offset2+1))) == null) return rc.getMin();
            word2 = word2.substring(0, offset2);
        }
        if (WS4JConfiguration.getInstance().useCache()) {
            Double cachedObj = cache.get(key);
            if (cachedObj != null) return cachedObj;
        }
        double maxScore = -1.0D;
        for (POS[] POSPair : rc.getPOSPairs()) {
            if (pos1 != null && pos1 != POSPair[0]) continue;
            if (pos2 != null && pos2 != POSPair[1]) continue;
            if (WS4JConfiguration.getInstance().useMFS()) {
                Concept synset1 = rc.getLexicalDB().getMostFrequentConcept(word1, POSPair[0]);
                Concept synset2 = rc.getLexicalDB().getMostFrequentConcept(word2, POSPair[1]);
                maxScore = rc.calcRelatednessOfSynsets(synset1, synset2).getScore();
            } else {
                for (Concept synset1 : rc.getLexicalDB().getAllConcepts(word1, POSPair[0])) {
                    for (Concept synset2 : rc.getLexicalDB().getAllConcepts(word2, POSPair[1])) {
                        Relatedness relatedness = rc.calcRelatednessOfSynsets(synset1, synset2);
                        double score = relatedness.getScore();
                        if (score > maxScore) maxScore = score;
                    }
                }
            }
        }
        if (maxScore == -1.0D) maxScore = 0.0D;
        if (WS4JConfiguration.getInstance().useCache()) cache.put(key, maxScore);
        return maxScore;
    }
}
