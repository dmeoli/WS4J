package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.Traverser;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class calculates the Hirst and St-Onge relatedness score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Hirst and St-Onge: The idea behind Hirst and St-Onge’s
 * (1998) measure of semantic relatedness is that two lexicalized
 * concepts are semantically close if their WordNet
 * synsets are connected by a path that is not too long and
 * that “does not change direction too often”. The strength
 * of the relationship is given by:
 * <div style="padding:20px"><code>rel<sub>HS</sub>(c<sub>1</sub>, c<sub>2</sub>) = C - path_length - k * d.</code></div>
 * where d is the number of changes of direction in the
 * path, and C and k are constants; if no such path exists,
 * rel_HS(c1, c2) is zero and the synsets are deemed unrelated.
 * </blockquote>
 *
 * From WS:
 * Unless a problem occurs, the return value is the relatedness
 * score, which is greater-than or equal-to 0 and less-than or equal-to 16.
 * If an error occurs, then the error level is set to non-zero and an error
 * string is created (see the description of getError()).
 *
 * @author Hideki Shima
 */
public class HirstStOnge extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = 16.0D;

	private static List<POS[]> POSPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.ADJECTIVE, POS.ADJECTIVE});
		add(new POS[]{POS.ADJECTIVE, POS.ADVERB});
		add(new POS[]{POS.ADJECTIVE, POS.NOUN});
		add(new POS[]{POS.ADJECTIVE, POS.VERB});

		add(new POS[]{POS.ADVERB, POS.ADJECTIVE});
		add(new POS[]{POS.ADVERB, POS.ADVERB});
		add(new POS[]{POS.ADVERB, POS.NOUN});
		add(new POS[]{POS.ADVERB, POS.VERB});

		add(new POS[]{POS.NOUN, POS.ADJECTIVE});
		add(new POS[]{POS.NOUN, POS.ADVERB});
		add(new POS[]{POS.NOUN, POS.NOUN});
		add(new POS[]{POS.NOUN, POS.VERB});

		add(new POS[]{POS.VERB, POS.ADJECTIVE});
		add(new POS[]{POS.VERB, POS.ADVERB});
		add(new POS[]{POS.VERB, POS.NOUN});
		add(new POS[]{POS.VERB, POS.VERB});
	}};
	
	public HirstStOnge(ILexicalDatabase db) {
		super(db, min, max);
	}

	@Override
	protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
		StringBuilder tracer = new StringBuilder();
		if (concept1 == null || concept2 == null) return new Relatedness(min, null, illegalSynset);
		if (concept1.equals(concept2)) return new Relatedness(max, identicalSynset, null);
		Set<Concept> horizontal1 = Traverser.getHorizontalSynsets(concept1);
		Set<Concept> horizontal2 = Traverser.getHorizontalSynsets(concept2);
		boolean inHorizon = horizontal2.contains(concept1) || horizontal1.contains(concept2);
		if (inHorizon) return new Relatedness(max);
		Set<Concept> upward1 = Traverser.getUpwardSynsets(concept1);
		Set<Concept> upward2 = Traverser.getUpwardSynsets(concept2);
		Set<Concept> downward1 = Traverser.getDownwardSynsets(concept1);
		Set<Concept> downward2 = Traverser.getDownwardSynsets(concept2);
		if (WS4JConfiguration.getInstance().useTrace()) {
		    tracer.append("HSO(").append(concept1).append(", ").append(concept2).append(")\n");
			tracer.append("Horizontal Links(").append(concept1.toString()).append(") = ").append(horizontal1).append("\n");
			tracer.append("Horizontal Links(").append(concept2.toString()).append(") = ").append(horizontal1).append("\n");
			tracer.append("Upward Links(").append(concept1.toString()).append(") = ").append(upward1).append("\n");
			tracer.append("Upward Links(").append(concept2.toString()).append(") = ").append(upward2).append("\n");
			tracer.append("Downward Links(").append(concept1.toString()).append(") = ").append(downward1).append("\n");
			tracer.append("Downward Links(").append(concept2.toString()).append(") = ").append(downward2).append("\n");
		}
		boolean contained = Traverser.contained(concept1, concept2);
		boolean inUpOrDown = upward2.contains(concept1) || downward2.contains(concept1);
		if (contained && inUpOrDown) {
			tracer.append("Strong Rel (Compound Word Match).\n");
			return new Relatedness(max, tracer.toString(), null);
		}
		int score = new MedStrong().medStrong(0, 0, 0, concept1, concept1.getSynsetID(), concept2);
		return new Relatedness(score, tracer.toString(), null);
	}

	@Override
	public List<POS[]> getPOSPairs() {
		return POSPairs;
	}

	private static class MedStrong {

		int medStrong(int state, int distance, int chdir, Concept from, String path, Concept endSynset) {
			if (from.equals(endSynset) && distance > 1) return 8 - distance - chdir;
			if (distance >= 5) return 0;
			Set<Concept> horizontal = Traverser.getHorizontalSynsets(from);
			Set<Concept> upward = (state == 0 || state == 1) ? Traverser.getUpwardSynsets(from) : null;
			Set<Concept> downward = (state != 6) ? Traverser.getDownwardSynsets(from) : null;
			if (state == 0) {
				int retU = findU(upward, 1, distance, 0, path, endSynset);
				int retD = findD(downward, 2, distance, 0, path, endSynset);
				int retH = findH(horizontal, 3, distance, 0, path, endSynset);
				if (retU > retD && retU > retH) return retU;
				if (retD > retH) return retD;
				return retH;
			} else if (state == 1) {
				int retU = findU(upward, 1, distance, 0, path, endSynset);
				int retD = findD(downward, 4, distance, 1, path, endSynset);
				int retH = findH(horizontal, 5, distance, 1, path, endSynset);
				if (retU > retD && retU > retH) return retU;
				if (retD > retH) return retD;
				return retH;
			} else if (state == 2) {
				int retD = findD(downward, 2, distance, 0, path, endSynset);
				int retH = findH(horizontal, 6, distance, 0, path, endSynset);
				return retD > retH ? retD : retH;
			} else if (state == 3) {
				int retD = findD(downward, 7, distance, 0, path, endSynset);
				int retH = findH(horizontal, 3, distance, 0, path, endSynset);
				return retD > retH ? retD : retH;
			} else if (state == 4) {
				return findD(downward, 4, distance, 1, path, endSynset);
			} else if (state == 5) {
				int retD = findD(downward, 4, distance, 2, path, endSynset);
				int retH = findH(horizontal, 5, distance, 1, path, endSynset);
				return retD > retH ? retD : retH;
			} else if (state == 6) {
				return findH(horizontal, 6, distance, 1, path, endSynset);
			} else if (state == 7) {
				return findD(downward, 7, distance, 1, path, endSynset);
			}
			return 0;
		}

		private int findD(Set<Concept> downward, int state, int distance, int chdir, String path, Concept endSynset) {
			return find(downward, state, distance, chdir, path, endSynset, "D");
		}

		private int findU(Set<Concept> upward, int state, int distance, int chdir, String path, Concept endSynset) {
			return find(upward, state, distance, chdir, path, endSynset, "U");
		}

		private int findH(Set<Concept> horizontal, int state, int distance, int chdir, String path, Concept endSynset) {
			return find(horizontal, state, distance, chdir, path, endSynset, "H");
		}

		private int find(Set<Concept> synsetGroup, int state, int distance, int chdir, String path, Concept endSynset,
						 String abbreviation) {
			int ret = 0;
			for (Concept synset : synsetGroup) {
				int retT = medStrong(state, distance+1, chdir, synset, path + " [" + abbreviation + "] " +
						synset, endSynset);
				if (retT > ret) ret = retT;
			}
			return ret;
		}
	}
}
