package edu.uniba.di.lacam.kdde.ws4j.similarity;

import static org.junit.Assert.assertEquals;

import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import edu.uniba.di.lacam.kdde.ws4j.util.WordSimilarityCalculator;
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
		rc = new LeacockChodorow(db);
	}

	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(2.9957D, rc.calcRelatednessOfSynsets(cycloneConcepts.get(1), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(1.6094D, rc.calcRelatednessOfSynsets(cycloneConcepts.get(0), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(2.6391D, rc.calcRelatednessOfSynsets(migrateConcepts.get(0), emigrateConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(1.9459D, rc.calcRelatednessOfSynsets(migrateConcepts.get(1), emigrateConcepts.get(0)).getScore(), 0.0001D);
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
		assertEquals(2.9957D, rc.calcRelatednessOfWords(CYCLONE, HURRICANE), 0.0001D);
		assertEquals(2.6391D, rc.calcRelatednessOfWords(MIGRATE, EMIGRATE), 0.0001D);
	}

	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(2.5902D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(2.2335D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0.0000D, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + "other",
                TALK + WordSimilarityCalculator.SEPARATOR + "other"), 0.0001D);
	}

	@Test
	public void testHappyPathOnWordsWithPOSAndSense() {
        assertEquals(2.9957D, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 2,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(1.6094D, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(2.6391D, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(1.9459D, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 2,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
	}
}
