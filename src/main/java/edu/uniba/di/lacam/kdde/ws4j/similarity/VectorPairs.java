package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.util.ArrayList;
import java.util.List;

// TODO finish implementation
public class VectorPairs extends RelatednessCalculator {

	protected static double min = 0.0D;
	protected static double max = Double.MAX_VALUE;

	private static List<POS[]> posPairs = new ArrayList<POS[]>(){{ }};

	public VectorPairs(ILexicalDatabase db) {
		super(db, min, max);
	}

	protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
		if (synset1 == null || synset2 == null) return new Relatedness(min, null, illegalSynset);
		
		return new Relatedness(0);
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return posPairs;
	}
}
