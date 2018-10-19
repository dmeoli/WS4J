package edu.uniba.di.lacam.kdde.ws4j;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;
import org.junit.Test;

import java.util.List;

abstract public class RelatednessCalculatorTest {

	protected static ILexicalDatabase db;
	protected static RelatednessCalculator rc;

	static {
		WS4JConfiguration.getInstance().setMemoryDB(false);
		WS4JConfiguration.getInstance().setLeskNormalize(false);
		WS4JConfiguration.getInstance().setMFS(false);
        db = new MITWordNet();
	}

	protected static final String CYCLONE = "cyclone";
	protected static final String HURRICANE = "hurricane";
	protected final List<Concept> cycloneConcepts =  db.getAllConcepts(CYCLONE, POS.NOUN);
	protected final List<Concept> hurricaneConcepts = db.getAllConcepts(HURRICANE, POS.NOUN);

	protected static final String MIGRATE = "migrate";
	protected static final String EMIGRATE = "emigrate";
	protected final List<Concept> migrateConcepts = db.getAllConcepts(MIGRATE, POS.VERB);
	protected final List<Concept> emigrateConcepts = db.getAllConcepts(EMIGRATE, POS.VERB);
	
	protected static final String HUGE = "huge";
	protected static final String TREMENDOUS = "tremendous";
	protected final List<Concept> hugeConcepts = db.getAllConcepts(HUGE, POS.ADJECTIVE);
	protected final List<Concept> tremendousConcepts = db.getAllConcepts(TREMENDOUS, POS.ADJECTIVE);
	
	protected static final String EVENTUALLY = "eventually";
	protected static final String FINALLY = "finally";
	protected final List<Concept> eventuallyConcepts = db.getAllConcepts(EVENTUALLY, POS.ADVERB);
	protected final List<Concept> finallyConcepts = db.getAllConcepts(FINALLY, POS.ADVERB);
	
	protected static final String MANUSCRIPT = "manuscript";
	protected static final String WRITE_DOWN = "write down";
	protected final List<Concept> manuscriptConcepts = db.getAllConcepts(MANUSCRIPT, POS.NOUN);
	protected final List<Concept> writeDownConcepts = db.getAllConcepts(WRITE_DOWN, POS.VERB);
	
	protected static final String CHAT = "chat";
	protected static final String TALK = "talk";

	@Test
	abstract public void testHappyPathOnSynsets();

    @Test
    abstract public void testOnSameSynsets();

    @Test
    abstract public void testOnUnknownSynsets();

	@Test
	abstract public void testHappyPathOnWords();

	@Test
	abstract public void testHappyPathOnWordsWithPOS();

	@Test
    abstract public void testHappyPathOnWordsWithPOSAndSense();
}
