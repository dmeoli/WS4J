package edu.uniba.di.lacam.kdde.ws4j;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.*;

import java.util.List;

public abstract class RelatednessCalculator {
	
	protected final static String illegalSynset = "Synset is null.";
	protected final static String identicalSynset = "Synsets are identical.";

	public final static boolean useRootNode = true;

	protected ILexicalDatabase db;
	protected PathFinder pathFinder;
	protected DepthFinder depthFinder;

	private WordSimilarityCalculator wordSimilarity = new WordSimilarityCalculator();

	protected final double min;
	protected final double max;

	public RelatednessCalculator(ILexicalDatabase db, double min, double max) {
		this.db = db;
		this.min = min;
		this.max = max;
		pathFinder = new PathFinder(db);
		depthFinder = new DepthFinder(db);
	}

	protected abstract Relatedness calcRelatedness(Concept synset1, Concept synset2);

	public abstract List<POS[]> getPOSPairs();

	public Relatedness calcRelatednessOfSynsets(Concept synset1, Concept synset2) {
		long t0 = System.currentTimeMillis();
		Relatedness r = calcRelatedness(synset1, synset2);
		long t1 = System.currentTimeMillis();
		r.appendTrace("Process done in " + (t1-t0) + " msec. \n");
		return r;
	}
	
	public double calcRelatednessOfWords(String word1, String word2) {
		return wordSimilarity.calcRelatednessOfWords(word1, word2, this);
	}
	
	public double[][] getSimilarityMatrix(String[] words1, String[] words2) {
		return MatrixCalculator.getSimilarityMatrix(words1, words2, this);
	}
	
	public double[][] getNormalizedSimilarityMatrix(String[] words1, String[] words2) {
		return MatrixCalculator.getNormalizedSimilarityMatrix(words1, words2, this);
	}

	public ILexicalDatabase getLexicalDB() {
		return db;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
