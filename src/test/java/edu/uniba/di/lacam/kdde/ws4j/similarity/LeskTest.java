package edu.uniba.di.lacam.kdde.ws4j.similarity;

import static org.junit.Assert.assertEquals;

import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculatorTest;
import edu.uniba.di.lacam.kdde.ws4j.util.WordSimilarityCalculator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link Lesk}.
 *
 * @author Hideki Shima
 */
public class LeskTest extends RelatednessCalculatorTest {

	@BeforeClass
	public static void oneTimeSetUp() {
		rc = new Lesk(db);
	}

	@Test
	public void testHappyPathOnSynsets() {
		assertEquals(184, rc.calcRelatednessOfSynsets(cycloneConcepts.get(1), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(3, rc.calcRelatednessOfSynsets(cycloneConcepts.get(0), hurricaneConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(73, rc.calcRelatednessOfSynsets(migrateConcepts.get(0), emigrateConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfSynsets(migrateConcepts.get(1), emigrateConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(36, rc.calcRelatednessOfSynsets(eventuallyConcepts.get(0), finallyConcepts.get(0)).getScore(), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfSynsets(manuscriptConcepts.get(1), writeDownConcepts.get(0)).getScore(), 0.0001D);
	}

    @Test
    public void testOnSameSynsets() {
        assertEquals(318, rc.calcRelatednessOfSynsets(cycloneConcepts.get(0), cycloneConcepts.get(0)).getScore(), 0.0001D);
    }

    @Test
    public void testOnUnknownSynsets() {
        assertEquals(rc.getMin(), rc.calcRelatednessOfSynsets(null, cycloneConcepts.get(0)).getScore(), 0.0001D);
        assertEquals(rc.getMin(), rc.calcRelatednessOfWords(null, CYCLONE), 0.0001D);
        assertEquals(rc.getMin(), rc.calcRelatednessOfWords("", CYCLONE), 0.0001D);
    }

	@Test
	public void testHappyPathOnWords() {
		assertEquals(184, rc.calcRelatednessOfWords(CYCLONE, HURRICANE), 0.0001D);
		assertEquals(73, rc.calcRelatednessOfWords(MIGRATE, EMIGRATE), 0.0001D);
		assertEquals(36, rc.calcRelatednessOfWords(EVENTUALLY, FINALLY), 0.0001D);
		assertEquals(4, rc.calcRelatednessOfWords(MANUSCRIPT, WRITE_DOWN), 0.0001D);
	}

	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(72, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
				TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(3, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.NOUN,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(5, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.NOUN), 0.0001D);
		assertEquals(8, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + POS.VERB,
                TALK + WordSimilarityCalculator.SEPARATOR + POS.VERB), 0.0001D);
		assertEquals(0, rc.calcRelatednessOfWords(CHAT + WordSimilarityCalculator.SEPARATOR + "other",
                TALK + WordSimilarityCalculator.SEPARATOR + "other"), 0.0001D);
	}

    @Test
    public void testHappyPathOnWordsWithPOSAndSense() {
        assertEquals(184, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 2,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(3, rc.calcRelatednessOfWords(
                CYCLONE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1,
                HURRICANE + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(73, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(4, rc.calcRelatednessOfWords(
                MIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 2,
                EMIGRATE + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(36, rc.calcRelatednessOfWords(
                EVENTUALLY + WordSimilarityCalculator.SEPARATOR + POS.ADVERB + WordSimilarityCalculator.SEPARATOR + 1,
                FINALLY + WordSimilarityCalculator.SEPARATOR + POS.ADVERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
        assertEquals(4, rc.calcRelatednessOfWords(
                MANUSCRIPT + WordSimilarityCalculator.SEPARATOR + POS.NOUN + WordSimilarityCalculator.SEPARATOR + 2,
                WRITE_DOWN + WordSimilarityCalculator.SEPARATOR + POS.VERB + WordSimilarityCalculator.SEPARATOR + 1), 0.0001D);
    }
}
