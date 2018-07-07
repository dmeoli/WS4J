package edu.uniba.di.lacam.kdde.ws4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

final public class StopWordRemover {

	private static final StopWordRemover stopWordRemover = new StopWordRemover();

	private Set<String> stopList;

	private StopWordRemover() {
		try {
			loadStopList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static StopWordRemover getInstance(){
		return StopWordRemover.stopWordRemover;
	}

	private synchronized void loadStopList() throws IOException {
		stopList = new HashSet<>();
		InputStream stream = StopWordRemover.class.getResourceAsStream("/" + WS4JConfiguration.getInstance().getStopList());
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
		Arrays.asList(words).forEach(word -> {
            if (!stopList.contains(word)) contents.add(word);
        });
		return contents.toArray(new String[0]);
	}
}
