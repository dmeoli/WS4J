package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.ArrayList;
import java.util.List;

// TODO finish implementation
public class Vector extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = 1.0D;

	private static List<POS[]> posPairs = new ArrayList<POS[]>(){{ }};

	public Vector(ILexicalDatabase db) {
		super(db, min, max);
	}

	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		if (synset1.getSynsetID().equals(synset2.getSynsetID())) return new Relatedness(max, identicalSynset, null);

		return new Relatedness(0);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return posPairs;
	}
}
