package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.data.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.PathFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class calculates the Leacock-Chodorow similarity score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Leacock-Chodorow: Leacock and Chodorow (1998)
 * also rely on the length len(c1; c2) of the shortest path between
 * two synsets for their measure of similarity. However,
 * they limit their attention to IS-A links and scale the
 * path length by the overall depth D of the taxonomy:
 * <div style="padding:20px"><code>sim<sub>LC</sub>(c<sub>1</sub>, c<sub>2</sub>) =
 * -log(len(c<sub>1</sub>, c<sub>2</sub>) / 2D).</code></div>
 * </blockquote>
 *
 * (from lch.pm) This module computes the semantic relatedness of word senses according
 * to a method described by Leacock and Chodorow (1998). This method counts up
 * the number of edges between the senses in the 'is-a' hierarchy of WordNet.
 * The value is then scaled by the maximum depth of the WordNet 'is-a'
 * hierarchy. A relatedness value is obtained by taking the negative log
 * of this scaled value.
 *
 * @author Hideki Shima
 */
public class LeacockChodorow extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = Double.MAX_VALUE;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.n, POS.n});
		add(new POS[]{POS.v, POS.v});
	}};
	
	public LeacockChodorow(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		StringBuilder tracer = new StringBuilder();
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		if (synset1.getSynsetID().equals(synset2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<PathFinder.Subsumer> lcsList = pathFinder.getLCSByPath(synset1, synset2, subTracer);
		if (lcsList.size() == 0) return new Relatedness(min);
		int maxDepth = 1;
		if (synset1.getPOS().equals(POS.n)) maxDepth = 20;
		else if (synset1.getPOS().equals(POS.v)) maxDepth = 14;
		int length = lcsList.get(0).length;
		double score = -Math.log((double) length / (double) (2 * maxDepth));
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append(Objects.requireNonNull(subTracer).toString());
			for (PathFinder.Subsumer lcs : lcsList) {
				tracer.append("Lowest Common Subsumer(s): ");
				tracer.append(lcs.subsumer.getSynsetID()).append(" (Length = ").append(lcs.length).append(")\n");
			}
		}
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
