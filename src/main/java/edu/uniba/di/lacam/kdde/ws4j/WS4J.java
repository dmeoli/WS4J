package edu.uniba.di.lacam.kdde.ws4j;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.ws4j.util.MatrixCalculator;
import edu.uniba.di.lacam.kdde.ws4j.similarity.*;

/**
 * This is a facade class that provides simple APIs for end users.
 */
public class WS4J {

	private static ILexicalDatabase db;

    private static RelatednessCalculator lin;
	private static RelatednessCalculator wup;
	private static RelatednessCalculator hso;
	private static RelatednessCalculator lch;
	private static RelatednessCalculator jcn;
	private static RelatednessCalculator lesk;
	private static RelatednessCalculator path;
	private static RelatednessCalculator res;

	static {
		db = new MITWordNet();
		lin = new Lin(db);
		wup = new WuPalmer(db);
		hso = new HirstStOnge(db);
		lch = new LeacockChodorow(db);
		jcn = new JiangConrath(db);
		lesk = new Lesk(db);
		path = new Path(db);
		res = new Resnik(db);
	}
	

	public static double runHSO(String word1, String word2) {
		return hso.calcRelatednessOfWords(word1, word2);
	}
	

	public static double runLCH(String word1, String word2) {
		return lch.calcRelatednessOfWords(word1, word2);
	}
	

	public static double runRES(String word1, String word2) {
		return res.calcRelatednessOfWords(word1, word2);
	}
	

	public static double runJCN(String word1, String word2) {
		return jcn.calcRelatednessOfWords(word1, word2);
	}

	public static double runLIN(String word1, String word2) {
		return lin.calcRelatednessOfWords(word1, word2);
	}
	

	public static double runLESK(String word1, String word2) {
		return lesk.calcRelatednessOfWords(word1, word2);
	}

	public static double runPATH(String word1, String word2) {
		return path.calcRelatednessOfWords(word1, word2);
	}

	public static double runWUP(String word1, String word2) {
		return wup.calcRelatednessOfWords(word1, word2);
	}
				
	public static double[][] getSynonymyMatrix(String[] words1, String[] words2) {
		return MatrixCalculator.getSynonymyMatrix(words1, words2);
	}
}
