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
 * well as that of their most specific concept, plays a part.
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
	protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
		StringBuilder tracer = new StringBuilder();
		if (concept1 == null || concept2 == null) return new Relatedness(min, null, illegalSynset);
		if (concept1.equals(concept2)) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<PathFinder.Subsumer> lcsList = ICFinder.getInstance().getLCSbyIC(pathFinder, concept1, concept2, subTracer);
		if (Objects.requireNonNull(lcsList).size() == 0) return new Relatedness(min, tracer.toString(), null);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append("JCN(").append(concept1).append(", ").append(concept2).append(")\n");
			tracer.append(Objects.requireNonNull(subTracer).toString());
			lcsList.forEach(lcs -> {
				tracer.append("Lowest Common Subsumer(s): ");
				tracer.append(lcs.getSubsumer().toString()).append(" (IC = ").append(lcs.getIC()).append(")\n");
			});
		}
		PathFinder.Subsumer subsumer = lcsList.get(0);
		double lcsIC = subsumer.getIC();
		Concept rootConcept = pathFinder.getRoot(subsumer.getSubsumer());
		rootConcept.setPOS(subsumer.getSubsumer().getPOS());
		int rootFreq = ICFinder.getInstance().getFrequency(rootConcept);
		if (rootFreq <= 0) return new Relatedness(min, tracer.toString(), null);
		double ic1 = ICFinder.getInstance().IC(pathFinder, concept1);
		double ic2 = ICFinder.getInstance().IC(pathFinder, concept2);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append("IC(").append(concept1.toString()).append(") = ").append(ic1).append("\n");
			tracer.append("IC(").append(concept2.toString()).append(") = ").append(ic2).append("\n");
		}
		double distance;
		if (ic1 > 0 && ic2 > 0) distance = ic1 + ic2 - (2 * lcsIC);
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
