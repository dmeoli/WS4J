package edu.uniba.di.lacam.kdde.ws4j.util;

import java.io.*;
import java.util.*;

final public class StopWordRemover {

	private static final String STOP_WORDS = "stopWords";

	private Set<String> stopList;

	private static final StopWordRemover stopWordRemover = new StopWordRemover();

	private StopWordRemover() {
		try {
			loadStopWords();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static StopWordRemover getInstance(){
		return stopWordRemover;
	}

	synchronized private void loadStopWords() throws IOException {
		stopList = new HashSet<>();
		InputStream stream = getClass().getClassLoader().getResourceAsStream(STOP_WORDS);
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			String stopWord = line.trim();
			if (stopWord.length() > 0) stopList.add(stopWord);
		}
		br.close();
		isr.close();
	}

	String[] removeStopWords(String[] words) {
		List<String> contents = new ArrayList<>(words.length);
		Arrays.stream(words).filter(word -> !stopList.contains(word)).forEach(contents::add);
		return contents.toArray(new String[0]);
	}
}
