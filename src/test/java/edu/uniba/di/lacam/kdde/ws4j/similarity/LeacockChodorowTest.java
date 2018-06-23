package edu.uniba.di.lacam.kdde.ws4j.similarity;

import static org.junit.Assert.assertEquals;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link LeacockChodorow}.
 *
 * @author Hideki Shima
 */
public class LeacockChodorowTest extends RelatednessCalculatorTest {

	private static RelatednessCalculator rc;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = new MITWordNet();
		rc = new LeacockChodorow(db);
	}
	
	/**
	 * Test method for {@link LeacockChodorow#calcRelatednessOfSynsets(Concept, Concept)}.
	 */
	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(2.9957, rc.calcRelatednessOfSynsets(n1Synsets.get(1), n2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(1.6094, rc.calcRelatednessOfSynsets(n1Synsets.get(0), n2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(2.6391, rc.calcRelatednessOfSynsets(v1Synsets.get(0), v2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(1.9459, rc.calcRelatednessOfSynsets(v1Synsets.get(1), v2Synsets.get(0)).getScore(), 0.0001D);
	}

	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWords() {
		assertEquals(2.9957, rc.calcRelatednessOfWords(n1, n2), 0.0001D);
		assertEquals(2.6391, rc.calcRelatednessOfWords(v1, v2), 0.0001D);
	}
	
	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(2.5902, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#n"), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#v"), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#n"), 0.0001D);
		assertEquals(2.2335, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#v"), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#other", nv2 + "#other"), 0.0001D);
	}

	@Test
	public void testOnUnknownSynsets() {
		assertEquals(rc.getMin(), rc.calcRelatednessOfSynsets(null, n1Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords(null, n1), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords("", n1), 0.0001D);
	}
}
