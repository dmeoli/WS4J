package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static edu.uniba.di.lacam.kdde.lexical_db.item.Link.*;

public class Traverser {

    private static ConcurrentMap<Concept, Set<Concept>> horizonCache;
    private static ConcurrentMap<Concept, Set<Concept>> upwardCache;
    private static ConcurrentMap<Concept, Set<Concept>> downwardCache;

    static {
        if (WS4JConfiguration.getInstance().useCache()) {
            horizonCache = new ConcurrentHashMap<>();
            upwardCache = new ConcurrentHashMap<>();
            downwardCache = new ConcurrentHashMap<>();
        }
    }

    private ILexicalDatabase db;

    public Traverser(ILexicalDatabase db) {
        this.db = db;
    }

    public boolean contained(Concept concept1, Concept concept2) {
        if (concept1 == null || concept2 == null) return false;
        List<String> wordsH = db.getWords(concept1);
        List<String> wordsN = db.getWords(concept2);
        for (String wordH : wordsH) {
            for (String wordN : wordsN) if (wordH.contains(wordN) || wordN.contains(wordH)) return true;
        }
        return false;
    }

    public Set<Concept> getHorizontalSynsets(Concept synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
            Set<Concept> cachedObj = horizonCache.get(synset);
            if (cachedObj != null) return cachedObj;
        }
        List<Link> points = new ArrayList<>();
        points.add(ANTONYM);
        points.add(ATTRIBUTE);
        points.add(SIMILAR_TO);
        Set<Concept> result = getGroupedSynsets(synset, points);
        if (WS4JConfiguration.getInstance().useCache()) if (result != null) horizonCache.put(synset, result);
        return result;
    }

    public Set<Concept> getUpwardSynsets(Concept synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
            Set<Concept> cachedObj = upwardCache.get(synset);
            if (cachedObj != null) return cachedObj;
        }
        List<Link> points = new ArrayList<>();
        points.add(HYPERNYM);
        points.add(MERONYM);
        Set<Concept> result = getGroupedSynsets(synset, points);
        if (WS4JConfiguration.getInstance().useCache()) if (result != null) upwardCache.put(synset, result);
        return result;
    }

    public Set<Concept> getDownwardSynsets(Concept synset) {
        if (WS4JConfiguration.getInstance().useCache()) {
            Set<Concept> cachedObj = downwardCache.get(synset);
            if (cachedObj != null) return cachedObj;
        }
        List<Link> points = new ArrayList<>();
        points.add(CAUSE);
        points.add(ENTAILMENT);
        points.add(HOLONYM);
        points.add(HYPONYM);
        Set<Concept> result = getGroupedSynsets(synset, points);
        if (WS4JConfiguration.getInstance().useCache()) if (result != null) downwardCache.put(synset, result);
        return result;
    }

    private Set<Concept> getGroupedSynsets(Concept synset, List<Link> points) {
        Set<Concept> synsets = new HashSet<>();
        points.forEach(point -> synsets.addAll(db.getLinkedSynsets(synset, point)));
        return synsets;
    }
}
