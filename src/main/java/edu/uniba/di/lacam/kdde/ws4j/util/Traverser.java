package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Traverser {

    private static ILexicalDatabase db;

	private static ConcurrentMap<String, Set<String>> horizonCache;
	private static ConcurrentMap<String, Set<String>> upwardCache;
	private static ConcurrentMap<String, Set<String>> downwardCache;

	static {
	    db = new MITWordNet();
		if (WS4JConfiguration.getInstance().useCache()) {
			horizonCache = new ConcurrentHashMap<>();
			upwardCache = new ConcurrentHashMap<>();
			downwardCache = new ConcurrentHashMap<>();
		}
	}

	public static boolean contained(Concept concept1, Concept concept2) {
		if (concept1 == null || concept2 == null) return false;
		List<String> wordsH = db.getWords(concept1.getSynsetID());
		List<String> wordsN = db.getWords(concept2.getSynsetID());
		for (String wordH : wordsH) {
			for (String wordN : wordsN) {
				if (wordH.contains(wordN) || wordN.contains(wordH)) return true;
			}
		}
		return false;
	}

	public static Set<String> getHorizontalSynsets(String synset) {
		if (WS4JConfiguration.getInstance().useCache()) {
			Set<String> cachedObj = horizonCache.get(synset);
			if (cachedObj != null) return cachedObj;
		}
		List<Link> points = new ArrayList<>();
		points.add(Link.ANTONYM);
		points.add(Link.ATTRIBUTE);
		points.add(Link.SIMILAR_TO);
		Set<String> result = getGroupedSynsets(synset, points);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) horizonCache.put(synset, result);
		return result;
	}

	public static Set<String> getUpwardSynsets(String synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
			Set<String> cachedObj = upwardCache.get(synset);
			if (cachedObj != null) return cachedObj;
		}
		List<Link> points = new ArrayList<>();
		points.add(Link.HYPERNYM);
		points.add(Link.MERONYM);
		Set<String> result = getGroupedSynsets(synset, points);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) upwardCache.put(synset, result);
		return result;
	}

	public static Set<String> getDownwardSynsets(String synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
			Set<String> cachedObj = downwardCache.get(synset);
			if (cachedObj != null) return cachedObj;
		}
		List<Link> points = new ArrayList<>();
		points.add(Link.CAUSE);
		points.add(Link.ENTAILMENT);
		points.add(Link.HOLONYM);
		points.add(Link.HYPONYM);
		Set<String> result = getGroupedSynsets(synset, points);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) downwardCache.put(synset, result);
		return result;
	}

	private static Set<String> getGroupedSynsets(String synset, List<Link> points) {
		Set<String> synsets = new HashSet<>();
		points.forEach(point -> synsets.addAll(db.getSynsets(synset, point)));
		return synsets;
	}
}
