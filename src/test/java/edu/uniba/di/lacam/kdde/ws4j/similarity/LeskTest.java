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
 * Test class for {@link Lesk}.
 *
 * @author Hideki Shima
 */
public class LeskTest extends RelatednessCalculatorTest {

	private static RelatednessCalculator rc;

	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = new MITWordNet();
		rc = new Lesk(db);
	}

	/**
	 * Test method for {@link Lesk#calcRelatednessOfSynsets(Concept, Concept)}.
	 */
	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(184, rc.calcRelatednessOfSynsets(n1Synsets.get(1), n2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(3, rc.calcRelatednessOfSynsets(n1Synsets.get(0), n2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(73, rc.calcRelatednessOfSynsets(v1Synsets.get(0), v2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfSynsets(v1Synsets.get(1), v2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(36, rc.calcRelatednessOfSynsets(r1Synsets.get(0), r2Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfSynsets(n3Synsets.get(1), v3Synsets.get(0)).getScore(), 0.0001D);
	}

	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWords() {
		assertEquals(184, rc.calcRelatednessOfWords(n1, n2), 0.0001D);
		assertEquals(73, rc.calcRelatednessOfWords(v1, v2), 0.0001D);
		assertEquals(36, rc.calcRelatednessOfWords(r1, r2), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfWords(n3, v3), 0.0001D);
	}

	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(72, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#n"), 0.0001D);
		assertEquals(3, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#v"), 0.0001D);
		assertEquals(5, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#n"), 0.0001D);
		assertEquals(8, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#v"), 0.0001D);
		assertEquals(0, rc.calcRelatednessOfWords(nv1 + "#other", nv2 + "#other"), 0.0001D);
	}

	@Test
	public void testOnUnknownSynsets() {
		assertEquals(rc.getMin(), rc.calcRelatednessOfSynsets(null, n1Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords(null, n1), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords("", n1), 0.0001D);
	}
}
