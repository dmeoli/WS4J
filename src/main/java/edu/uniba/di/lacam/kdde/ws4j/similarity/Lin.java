package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.data.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.ICFinder;
import edu.uniba.di.lacam.kdde.ws4j.util.PathFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class calculates the Lin's similarity score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Lin: Lin’s (1998) similarity measure follows from his
 * theory of similarity between arbitrary objects. It uses the
 * same elements as distJC, but in a different fashion:
 * <div style="padding:20px"><code>sim<sub>L</sub>(c<sub>1</sub>, c<sub>2</sub>) =
 * 2 * log p(lso(c<sub>1</sub>, c<sub>2</sub>)) / (log p(c<sub>1</sub>) + log p(c<sub>2</sub>)).</code></div>
 * </blockquote>
 *
 * @author Hideki Shima
 */
public class Lin extends RelatednessCalculator {
	
	protected static double min = 0.0D;
	protected static double max = 1.0D;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.n, POS.n});
		add(new POS[]{POS.v, POS.v});
	}};

	public Lin(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		StringBuilder tracer = new StringBuilder();
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		if (synset1.getSynsetID().equals(synset2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = new StringBuilder();
		List<PathFinder.Subsumer> lcsList = ICFinder.getIC().getLCSbyIC(pathFinder, synset1, synset2, subTracer);
		if (Objects.requireNonNull(lcsList).size() == 0) return new Relatedness(min, tracer.toString(), null);
		double ic1 = ICFinder.getIC().IC(pathFinder, synset1);
		double ic2 = ICFinder.getIC().IC(pathFinder, synset2);
		double score = (ic1 > 0 && ic2 > 0) ? (2D * lcsList.get(0).ic / (ic1 + ic2)) : 0D;
		tracer.append(Objects.requireNonNull(subTracer).toString());
		for (PathFinder.Subsumer lcs : lcsList) {
			tracer.append("Lowest Common Subsumer(s): ");
			tracer.append(lcs.subsumer.getSynsetID()).append(" (IC = ").append(lcs.ic).append(")\n");
		}
		tracer.append("Concept1: ").append(synset1.getSynsetID()).append(" (IC = ").append(ic1).append(")\n");
		tracer.append("Concept2: ").append(synset2.getSynsetID()).append(" (IC = ").append(ic2).append(")\n");
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
