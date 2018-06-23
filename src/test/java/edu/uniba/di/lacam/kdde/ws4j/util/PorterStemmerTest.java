package edu.uniba.di.lacam.kdde.ws4j.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hideki Shima
 */
public class PorterStemmerTest {

	/**
	 * Test method for {@link PorterStemmer#stemWord(String)}.
	 */
	@Test
	public void testStemWord() {

		PorterStemmer stemmer = new PorterStemmer();
		
		// plural
		assertEquals("caress", stemmer.stemWord("caresses"));
		assertEquals("poni", stemmer.stemWord("ponies"));
		assertEquals("ti", stemmer.stemWord("ties"));
		assertEquals("caress", stemmer.stemWord("caress"));
		assertEquals("cat", stemmer.stemWord("cats"));
		
		// past/pp
		assertEquals("feed", stemmer.stemWord("feed"));
		assertEquals("agree", stemmer.stemWord("agreed"));
		assertEquals("disabl", stemmer.stemWord("disabled"));
		
		// gerund
		assertEquals("mat", stemmer.stemWord("matting"));
		assertEquals("mat", stemmer.stemWord("mating"));
		assertEquals("meet", stemmer.stemWord("meeting"));
		assertEquals("mill", stemmer.stemWord("milling"));
		assertEquals("mess", stemmer.stemWord("messing"));
				
		// misc: famous example of stemming's weakness
		assertEquals("univers", stemmer.stemWord("universal"));
		assertEquals("univers", stemmer.stemWord("universe"));
		assertEquals("univers", stemmer.stemWord("university"));
	}
}
