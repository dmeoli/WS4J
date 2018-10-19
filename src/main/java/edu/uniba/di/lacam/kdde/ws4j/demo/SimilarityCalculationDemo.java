package edu.uniba.di.lacam.kdde.ws4j.demo;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.similarity.*;

import java.util.Arrays;

public class SimilarityCalculationDemo {

    private static RelatednessCalculator[] rcs;

    static {
        ILexicalDatabase db = new MITWordNet();
        rcs = new RelatednessCalculator[]{
                new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
                new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
        };
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        Arrays.asList(rcs).forEach(rc -> System.out.println(rc.getClass().getName() + "\t" +
                rc.calcRelatednessOfWords("act", "moderate")));
        System.out.println("\nDone in " + (System.currentTimeMillis() - t) + " msec.");
    }
}