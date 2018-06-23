package edu.uniba.di.lacam.kdde.ws4j.demo;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.similarity.*;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

public class SimilarityCalculationDemo {

    private static RelatednessCalculator[] rcs;

    static {
        WS4JConfiguration.getInstance().setMemoryDB(false);
        WS4JConfiguration.getInstance().setLeskNormalize(false);
        WS4JConfiguration.getInstance().setMFS(false);
        ILexicalDatabase db = new MITWordNet();
        rcs = new RelatednessCalculator[]{
                new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
                new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
        };
    }

	public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        for (RelatednessCalculator rc : rcs) {
            System.out.println(rc.getClass().getName() + "\t" + rc.calcRelatednessOfWords("act", "moderate"));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Done in " + (t1-t2) + " msec.");
	}
}