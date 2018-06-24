package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.DepthFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class calculates semantic relatedness of word senses using
 * the edge counting method of the of Wu & Palmer (1994).
 *
 * @author Hideki Shima
 */
public class WuPalmer extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = 1.0D;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.NOUN, POS.NOUN});
		add(new POS[]{POS.VERB, POS.VERB});
	}};

	public WuPalmer(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		StringBuilder tracer = new StringBuilder();
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		if (synset1.getSynsetID().equals(synset2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<DepthFinder.Depth> lcsList = depthFinder.getRelatedness(synset1, synset2, subTracer);
		if (lcsList.size() == 0) return new Relatedness(min);
		int depth = lcsList.get(0).depth;
		int depth1 = depthFinder.getShortestDepth(synset1);
		int depth2 = depthFinder.getShortestDepth(synset2);
		double score = 0;
		if (depth1 > 0 && depth2 > 0) score = (double) (2 * depth) / (double) (depth1 + depth2);
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append(Objects.requireNonNull(subTracer).toString());
			for (DepthFinder.Depth lcs : lcsList) {
				tracer.append("Lowest Common Subsumer(s): ");
				tracer.append(lcs.leaf).append(" (Depth = ").append(lcs.depth).append(")\n");
			}
			tracer.append("Depth1(").append(synset1.getSynsetID()).append(") = ").append(depth1).append("\n");
			tracer.append("Depth2(").append(synset2.getSynsetID()).append(") = ").append(depth2).append("\n");
		}
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
