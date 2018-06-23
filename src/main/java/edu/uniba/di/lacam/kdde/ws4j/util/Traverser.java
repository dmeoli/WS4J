package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.data.Link;

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

	public static boolean contained(Concept synset1, Concept synset2) {
		if (synset1 == null || synset2 == null) return false;
		List<String> wordsH = db.findWordsBySynset(synset1.getSynsetID());
		List<String> wordsN = db.findWordsBySynset(synset2.getSynsetID());
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
		List<Link> links = new ArrayList<>();
		links.add(Link.ants);
		links.add(Link.attr);
		links.add(Link.sim);
		Set<String> result = getGroupedSynsets(synset, links);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) horizonCache.put(synset, result);
		return result;
	}

	public static Set<String> getUpwardSynsets(String synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
			Set<String> cachedObj = upwardCache.get(synset);
			if (cachedObj != null) return cachedObj;
		}
		List<Link> links = new ArrayList<>();
		links.add(Link.hype);
		links.add(Link.mero);
		links.add(Link.mmem);
		links.add(Link.mprt);
		links.add(Link.msub);
		Set<String> result = getGroupedSynsets(synset, links);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) upwardCache.put(synset, result);
		return result;
	}

	public static Set<String> getDownwardSynsets(String synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
			Set<String> cachedObj = downwardCache.get(synset);
			if (cachedObj != null) return cachedObj;
		}
		List<Link> links = new ArrayList<>();
		links.add(Link.caus);
		links.add(Link.enta);
		links.add(Link.holo);
		links.add(Link.hmem);
		links.add(Link.hsub);
		links.add(Link.hprt);
		links.add(Link.hypo);
		Set<String> result = getGroupedSynsets(synset, links);
		if (WS4JConfiguration.getInstance().useCache()) if (result != null) downwardCache.put(synset, result);
		return result;
	}

	private static Set<String> getGroupedSynsets(String synset, List<Link> links) {
		Set<String> synsets = new HashSet<>();
		links.forEach(link -> synsets.addAll(db.linkToSynsets(synset, link)));
		return synsets;
	}
}
