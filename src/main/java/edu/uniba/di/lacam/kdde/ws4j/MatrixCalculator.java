package edu.uniba.di.lacam.kdde.ws4j;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;

import java.util.*;

public class MatrixCalculator {

	private static ILexicalDatabase db;

	public MatrixCalculator(ILexicalDatabase db) {
		MatrixCalculator.db = db;
	}

	static double[][] getSimilarityMatrix(String[] words1, String[] words2, RelatednessCalculator rc) {
		double[][] result = new double[words1.length][words2.length];
		for (int i = 0; i < words1.length; i++) {
			for (int j = 0; j < words2.length; j++) {
				double score = rc.calcRelatednessOfWords(words1[i], words2[j]);
				result[i][j] = score;
			}
		}
		return result;
	}
	
	static double[][] getNormalizedSimilarityMatrix(String[] words1, String[] words2, RelatednessCalculator rc) {
		double[][] scores = getSimilarityMatrix(words1, words2, rc);
		double bestScore = 1.0D;
		for (double[] score : scores) {
			for (double aScore : score) {
				if (aScore > bestScore && aScore != Double.MAX_VALUE) bestScore = aScore;
			}
		}
		for (int i = 0; i < scores.length; i++) {
			for (int j = 0; j < scores[i].length; j++) {
				if (scores[i][j] == Double.MAX_VALUE) scores[i][j] = 1;
				else scores[i][j] /= bestScore;
			}
		}
		return scores;
	}

	static double[][] getSynonymyMatrix(String[] words1, String[] words2) {
		List<Set<String>> synonyms1 = new ArrayList<>(words1.length);
		Arrays.asList(words1).forEach(aWords1 -> {
            Set<String> synonyms = new HashSet<>();
            Arrays.asList(POS.values()).forEach(pos -> db.getAllConcepts(aWords1, pos)
                    .forEach(concept -> synonyms.add(concept.getSynsetID())));
            synonyms1.add(synonyms);
        });
        List<Set<String>> synonyms2 = new ArrayList<>(words2.length);
        Arrays.asList(words2).forEach(aWords2 -> {
            Set<String> synonyms = new HashSet<>();
            Arrays.asList(POS.values()).forEach(pos -> db.getAllConcepts(aWords2, pos)
                    .forEach(concept -> synonyms.add(concept.getSynsetID())));
            synonyms2.add(synonyms);
        });
		double[][] result = new double[words1.length][words2.length];
		for (int i = 0; i < words1.length; i++) {
			for (int j = 0; j < words2.length; j++) {
				String w1 = words1[i];
				String w2 = words2[j];
				if (w1.equals(w2)) {
					result[i][j] = 1.0D;
					continue;
				}
				Set<String> s1 = synonyms1.get(i);
				Set<String> s2 = synonyms2.get(j);
				result[i][j] = (s1.contains(w2) || s2.contains(w1)) ? 1.0D : 0.0D;
			}
		}
		return result;
	}
}
