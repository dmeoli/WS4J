package edu.uniba.di.lacam.kdde.ws4j.similarity;

import static org.junit.Assert.assertEquals;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import edu.uniba.di.lacam.kdde.ws4j.util.WordSimilarityCalculator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link Lin}.
 *
 * @author Hideki Shima
 */
public class LinTest extends RelatednessCalculatorTest {

	private static RelatednessCalculator rc;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = new MITWordNet();
		rc = new Lin(db);
	}
	
	/**
	 * Test method for {@link Lin#calcRelatednessOfSynsets(Concept, Concept)}.
	 */
	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(0.9813, rc.calcRelatednessOfSynsets(n1Concepts.get(1), n2Concepts.get(0)).getScore(), 0.0001D);
		assertEquals(0, rc.calcRelatednessOfSynsets(n1Concepts.get(0), n2Concepts.get(0)).getScore(), 0.0001D);
		assertEquals(0.9463, rc.calcRelatednessOfSynsets(v1Concepts.get(0), v2Concepts.get(0)).getScore(), 0.0001D);
		assertEquals(0, rc.calcRelatednessOfSynsets(v1Concepts.get(1), v2Concepts.get(0)).getScore(), 0.0001D);
	}

	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWords() {
		assertEquals(0.9813, rc.calcRelatednessOfWords(n1, n2), 0.0001D);
		assertEquals(0.9463, rc.calcRelatednessOfWords(v1, v2), 0.0001D);
	}

	/**
	 * Test method for {@link RelatednessCalculator#calcRelatednessOfWords(String, String)}.
	 */
	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(0.8144, rc.calcRelatednessOfWords(nv1 + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
                nv2 + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
                nv2 + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                nv2 + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(0.7243, rc.calcRelatednessOfWords(nv1 + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                nv2 + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + WordSimilarityCalculator.SEPARATOR + "other",
                nv2 + WordSimilarityCalculator.SEPARATOR + "other"), 0.0001D);
	}
	
	@Test
	public void testOnSameSynsets() {
		assertEquals(rc.getMax(), rc.calcRelatednessOfSynsets(n1Concepts.get(0), n1Concepts.get(0)).getScore(), 0.0001D);
	}
	
	@Test
	public void testOnUnknownSynsets() {
		assertEquals(rc.getMin(), rc.calcRelatednessOfSynsets(null, n1Concepts.get(0)).getScore(), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords(null, n1), 0.0001D);
		assertEquals(rc.getMin(), rc.calcRelatednessOfWords("", n1), 0.0001D);
	}
}
