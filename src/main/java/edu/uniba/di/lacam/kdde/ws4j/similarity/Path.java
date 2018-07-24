package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.PathFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Computing semantic relatedness of word senses by counting
 * nodes in the noun and verb WordNet 'is-a' hierarchies.
 *
 * @author Hideki Shima
 */
public class Path extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = 1.0D;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.NOUN, POS.NOUN});
		add(new POS[]{POS.VERB, POS.VERB});
	}};

	public Path(ILexicalDatabase db) {
		super(db, min, max);
	}

	protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
		StringBuilder tracer = new StringBuilder();
		if (concept1 == null || concept2 == null) return new Relatedness(min, null, illegalSynset);
		if (concept1.getSynsetID().equals(concept2.getSynsetID())) return new Relatedness(max, identicalSynset, null);
		StringBuilder subTracer = WS4JConfiguration.getInstance().useTrace() ? new StringBuilder() : null;
		List<PathFinder.Subsumer> shortestPaths = pathFinder.getShortestPaths(concept1, concept2, subTracer);
		if (shortestPaths.size() == 0) return new Relatedness(min);
		PathFinder.Subsumer path = shortestPaths.get(0);
		int dist = path.getLength();
		double score;
		if (dist > 0) score = 1.0D / (double) dist;
		else score = -1.0D;
		if (WS4JConfiguration.getInstance().useTrace()) {
			tracer.append(Objects.requireNonNull(subTracer).toString());
			tracer.append("Shortest path: ").append(path).append("\n");
			tracer.append("Path length = ").append(dist).append("\n");
		}
		return new Relatedness(score, tracer.toString(), null);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}
}
