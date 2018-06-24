package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link Vector}.
 *
 * @author Hideki Shima
 */
public class VectorTest extends RelatednessCalculatorTest {

	private static RelatednessCalculator rc;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = new MITWordNet();
		rc = new Vector(db);
	}
	
	private boolean underDevelopment = true;
	
	/**
	 * Test method for {@link Vector#calcRelatednessOfSynsets(Concept, Concept)}.
	 */
	@Test
	public void testHappyPathOnSynsets() {
		if (!underDevelopment) {
			// English pair
			assertEquals(0.6481, rc.calcRelatednessOfSynsets(n1Synsets.get(1), n2Synsets.get(0)).getScore(), 0.0001D);
			assertEquals(0.0821, rc.calcRelatednessOfSynsets(n1Synsets.get(0), n2Synsets.get(0)).getScore(), 0.0001D);
			assertEquals(0.8569, rc.calcRelatednessOfSynsets(v1Synsets.get(0), v2Synsets.get(0)).getScore(), 0.0001D);
			assertEquals(0.2935, rc.calcRelatednessOfSynsets(v1Synsets.get(1), v2Synsets.get(0)).getScore(), 0.0001D);
			assertEquals(0.4044, rc.calcRelatednessOfSynsets(a1Synsets.get(0), a2Synsets.get(0)).getScore(), 0.0001D);
			assertEquals(0.6317, rc.calcRelatednessOfSynsets(r1Synsets.get(0), r2Synsets.get(0)).getScore(), 0.0001D);
		}
	}

	/**
	 * Test method for {@link Vector#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWords() {
		if (!underDevelopment) {
			assertEquals(0.6481, rc.calcRelatednessOfWords(n1, n2), 0.0001D);
			assertEquals(0.8569, rc.calcRelatednessOfWords(v1, v2), 0.0001D);
		}
	}

	/**
	 * Test method for {@link Vector#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWordsWithPOS() {
		if (!underDevelopment) {
			assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#n"), 0.0001D);
			assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#v"), 0.0001D);
			assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#n"), 0.0001D);
			assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#v"), 0.0001D);
			assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#other", nv2 + "#other"), 0.0001D);
		}
	}
	
	@Test
	public void testOnSameSynsets() {
		assertEquals(Vector.max, rc.calcRelatednessOfSynsets(n1Synsets.get(0), n1Synsets.get(0)).getScore(), 0.0001D);
	}
	
	@Test
	public void testOnUnknownSynsets() {
		assertEquals(Vector.min, rc.calcRelatednessOfSynsets(null, n1Synsets.get(0)).getScore(), 0.0001D);
		assertEquals(Vector.min, rc.calcRelatednessOfWords(null, n1), 0.0001D);
		assertEquals(Vector.min, rc.calcRelatednessOfWords("", n1), 0.0001D);
	}
}
