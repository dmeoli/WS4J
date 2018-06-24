package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.ICFinder;
import edu.uniba.di.lacam.kdde.ws4j.util.PathFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class calculates the Jiang-Conrath distance score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Jiang–Conrath: Jiang and Conrath’s (1997) approach
 * also uses the notion of information content, but in the
 * form of the conditional probability of encountering an instance
 * of a child-synset given an instance of a parent synset.
 * Thus the information content of the two nodes, as
 * well as that of their most specific subsumer, plays a part.
 * Notice that this formula measures semantic distance, the
 * inverse of similarity.
 * <div style="padding:20px"><code>dist<sub>JS</sub>(c<sub>1</sub>, c<sub>2</sub>) =
 * 2 * log(p(lso(c<sub>1</sub>, c<sub>2</sub>))) - (log(p(c<sub>1</sub>))+log(p(c<sub>2</sub>))).</code></div>
 * </blockquote>
 *
 * @author Hideki Shima
 */
public class JiangConrath extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = Double.MAX_VALUE;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.NOUN, POS.NOUN});
		add(new POS[]{POS.VERB, POS.VERB});
	}};
	
	public JiangConrath(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		StringBuilder tracer = new StringBuilder();
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		if (synset1.getSynsetID().equals(synset2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<PathFinder.Subsumer> lcsList = ICFinder.getIC().getLCSbyIC(pathFinder, synset1, synset2, subTracer);
		if (Objects.requireNonNull(lcsList).size() == 0) return new Relatedness(min, tracer.toString(), null);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append(Objects.requireNonNull(subTracer).toString());
			for (PathFinder.Subsumer lcs : lcsList) {
				tracer.append("Lowest Common Subsumer(s): ");
				tracer.append(lcs.subsumer.getSynsetID()).append(" (IC = ").append(lcs.ic).append(")\n");
			}
		}
		PathFinder.Subsumer subsumer = lcsList.get(0);
		String lcsSynset = subsumer.subsumer.getSynsetID();
		double lcsIC = subsumer.ic;
		/* Commented out as maxScore is not used */
		// int lcsFreq = ICFinder.getIC().getFrequency(lcsSynset);
		// double maxScore;
		Concept rootSynset = pathFinder.getRoot(lcsSynset);
		rootSynset.setPOS(subsumer.subsumer.getPOS());
		int rootFreq = ICFinder.getIC().getFrequency(rootSynset);
		if (rootFreq > 0){
		    /* Commented out as maxScore is not used */
			// maxScore = 2D * -Math.log(0.001D / (double) rootFreq) + 1; // add -1 smoothing
		} else return new Relatedness(min, tracer.toString(), null);
		double IC1 = ICFinder.getIC().IC(pathFinder, synset1);
		double IC2 = ICFinder.getIC().IC(pathFinder, synset2);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append("Concept1: ").append(synset1.getSynsetID()).append(" (IC = ").append(IC1).append(")\n");
			tracer.append("Concept2: ").append(synset2.getSynsetID()).append(" (IC = ").append(IC2).append(")\n");
		}
		double distance;
		if (IC1 > 0 && IC2 > 0) distance = IC1 + IC2 - (2 * lcsIC);
		else return new Relatedness(min, tracer.toString(), null);
		double score;
		if (distance == 0.0D) {
			if (rootFreq > 0.01D) score = 1.0D / - Math.log(((double) rootFreq - 0.01D) / (double) rootFreq);
			else return new Relatedness(min, tracer.toString(), null);
		} else score = 1.0D / distance;
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
