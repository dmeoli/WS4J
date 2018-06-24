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
		db = new MITWordNet();
		WS4JConfiguration.getInstance().setMemoryDB(false);
		WS4JConfiguration.getInstance().setLeskNormalize(false);
		WS4JConfiguration.getInstance().setMFS(false);
	}

	protected String n1 = "cyclone";
	protected String n2 = "hurricane";
	protected List<Concept> n1Synsets =  db.getAllConcepts(n1, POS.NOUN);
	protected List<Concept> n2Synsets = db.getAllConcepts(n2, POS.NOUN);

	protected final String v1 = "migrate";
	protected final String v2 = "emigrate";
	protected final List<Concept> v1Synsets = db.getAllConcepts(v1, POS.VERB);
	protected final List<Concept> v2Synsets = db.getAllConcepts(v2, POS.VERB);
	
	protected final String a1 = "huge";
	protected final String a2 = "tremendous";
	protected final List<Concept> a1Synsets = db.getAllConcepts(a1, POS.ADJECTIVE);
	protected final List<Concept> a2Synsets = db.getAllConcepts(a2, POS.ADJECTIVE);
	
	protected final String r1 = "eventually";
	protected final String r2 = "finally";
	protected final List<Concept> r1Synsets = db.getAllConcepts(r1, POS.ADVERB);
	protected final List<Concept> r2Synsets = db.getAllConcepts(r2, POS.ADVERB);
	
	protected final String n3 = "manuscript";
	protected final String v3 = "write_down";
	protected final List<Concept> n3Synsets = db.getAllConcepts(n3, POS.NOUN);
	protected final List<Concept> v3Synsets = db.getAllConcepts(v3, POS.VERB);
	
	protected final String nv1 = "chat";
	protected final String nv2 = "talk";
	
	abstract public void testHappyPathOnSynsets();
	abstract public void testHappyPathOnWords();
	abstract public void testHappyPathOnWordsWithPOS();
	abstract public void testOnUnknownSynsets();
}
