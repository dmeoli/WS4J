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
 * This class calculates the Resnik's similarity score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Resnik: Resnik’s (1995) approach was, to our knowledge,
 * the first to bring together ontology and corpus.
 * Guided by the intuition that the similarity between a
 * pair of concepts may be judged by “the extent to which
 * they share information”,Resnik defined the similarity between
 * two concepts lexicalized in WordNet to be the information
 * content of their lowest super-ordinate (most
 * specific common concept) lso(c1; c2):
 * <div style="padding:20px"><code>sim<sub>R</sub>(c<sub>1</sub>, c<sub>2</sub>) = -log p(lso(c<sub>1</sub>, c<sub>2</sub>)).</code></div>
 * where p(c) is the probability of encountering an instance
 * of a synset c in some specific corpus.
 * </blockquote>
 *
 * @author Hideki Shima
 */
public class Resnik extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = Double.MAX_VALUE;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.NOUN, POS.NOUN});
		add(new POS[]{POS.VERB, POS.VERB});
	}};

	public Resnik(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
		StringBuilder tracer = new StringBuilder();
		if (concept1 == null || concept2 == null) return new Relatedness(min, null, illegalSynset);
		if (concept1.getSynsetID().equals(concept2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<PathFinder.Subsumer> lcsList = ICFinder.getIC().getLCSbyIC(pathFinder, concept1, concept2, subTracer);
		if (Objects.requireNonNull(lcsList).size() == 0) return new Relatedness(min, tracer.toString(), null);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append(Objects.requireNonNull(subTracer).toString());
			for (PathFinder.Subsumer lcs : lcsList) {
				tracer.append("Lowest Common Subsumer(s): ");
				tracer.append(lcs.concept.getSynsetID()).append(" (IC = ").append(lcs.ic).append(")\n");
			}
		}
		PathFinder.Subsumer subsumer = lcsList.get(0);
		double score = subsumer.ic;
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
