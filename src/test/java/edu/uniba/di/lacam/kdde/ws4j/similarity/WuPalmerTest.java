package edu.uniba.di.lacam.kdde.ws4j.similarity;

import static org.junit.Assert.assertEquals;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import edu.uniba.di.lacam.kdde.ws4j.util.WordSimilarityCalculator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link WuPalmer}.
 *
 * @author Hideki Shima
 */
public class WuPalmerTest extends RelatednessCalculatorTest {

	private static RelatednessCalculator rc;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = MITWordNet.getInstance();
		rc = new WuPalmer(db);
	}

	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(0.9565D, rc.calcRelatednessOfSynsets(cycloneConcepts.get(1), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(0.6957D, rc.calcRelatednessOfSynsets(cycloneConcepts.get(0), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(0.8571D, rc.calcRelatednessOfSynsets(migrateConcepts.get(0), emigrateConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(0.5714D, rc.calcRelatednessOfSynsets(migrateConcepts.get(1), emigrateConcepts.get(0)).getScore(), 0.0001D);
	}

    @Test
    public void testOnSameSynsets() {
        assertEquals(rc.getMax(), rc.calcRelatednessOfSynsets(cycloneConcepts.get(0), cycloneConcepts.get(0)).getScore(), 0.0001D);
    }

    @Test
    public void testOnUnknownSynsets() {
        assertEquals(rc.getMin(), rc.calcRelatednessOfSynsets(null, cycloneConcepts.get(0)).getScore(), 0.0001D);
        assertEquals(rc.getMin(), rc.calcRelatednessOfWords(null, CYCLONE), 0.0001D);
        assertEquals(rc.getMin(), rc.calcRelatednessOfWords("", CYCLONE), 0.0001D);
    }

	@Test
	public void testHappyPathOnWords() {
		assertEquals(0.9565D, rc.calcRelatednessOfWords(CYCLONE, HURRICANE), 0.0001D);
		assertEquals(0.8571D, rc.calcRelatednessOfWords(MIGRATE, EMIGRATE), 0.0001D);
	}

	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(0.8750D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
				TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
				TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
				TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(0.8333D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
				TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + "other",
                TALK + WordSimilarityCalculator.SEPARATOR + "other"), 0.0001D);
	}

    @Test
    public void testHappyPathOnWordsWithPOSAndSense() {
        assertEquals(0.9565D, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 2,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(0.6957D, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(0.8571D, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(0.5714D, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 2,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
    }
}
