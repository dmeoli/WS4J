package edu.uniba.di.lacam.kdde.ws4j;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.List;

abstract public class RelatednessCalculatorTest {

	private static ILexicalDatabase db;

	static {
		WS4JConfiguration.getInstance().setMemoryDB(false);
		WS4JConfiguration.getInstance().setLeskNormalize(false);
		WS4JConfiguration.getInstance().setMFS(false);
        db = new MITWordNet();
	}

	protected String n1 = "cyclone";
	protected String n2 = "hurricane";
	protected List<Concept> n1Concepts =  db.getAllConcepts(n1, POS.NOUN);
	protected List<Concept> n2Concepts = db.getAllConcepts(n2, POS.NOUN);

	protected final String v1 = "migrate";
	protected final String v2 = "emigrate";
	protected final List<Concept> v1Concepts = db.getAllConcepts(v1, POS.VERB);
	protected final List<Concept> v2Concepts = db.getAllConcepts(v2, POS.VERB);
	
	protected final String a1 = "huge";
	protected final String a2 = "tremendous";
	protected final List<Concept> a1Concepts = db.getAllConcepts(a1, POS.ADJECTIVE);
	protected final List<Concept> a2Concepts = db.getAllConcepts(a2, POS.ADJECTIVE);
	
	protected final String r1 = "eventually";
	protected final String r2 = "finally";
	protected final List<Concept> r1Concepts = db.getAllConcepts(r1, POS.ADVERB);
	protected final List<Concept> r2Concepts = db.getAllConcepts(r2, POS.ADVERB);
	
	protected final String n3 = "manuscript";
	protected final String v3 = "write_down";
	protected final List<Concept> n3Concepts = db.getAllConcepts(n3, POS.NOUN);
	protected final List<Concept> v3Concepts = db.getAllConcepts(v3, POS.VERB);
	
	protected final String nv1 = "chat";
	protected final String nv2 = "talk";
	
	abstract public void testHappyPathOnSynsets();
	abstract public void testHappyPathOnWords();
	abstract public void testHappyPathOnWordsWithPOS();
	abstract public void testOnUnknownSynsets();
}
