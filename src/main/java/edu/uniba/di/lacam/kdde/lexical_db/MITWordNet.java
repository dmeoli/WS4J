package edu.uniba.di.lacam.kdde.lexical_db;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.Log;
import edu.uniba.di.lacam.kdde.ws4j.util.PorterStemmer;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 *
 * @author Donato Meoli
 */
public class MITWordNet implements ILexicalDatabase {

    private static IDictionary dict;
    private static HashMap<Link, Pointer> mapLinkToPointer;
    private static PorterStemmer stemmer;
    private static ConcurrentMap<String, List<String>> cache;

    private static File WORDNET_DICT = new File(System.getProperty("user.dir") + File.separator + "dict");

    static {
        try {
            if (WS4JConfiguration.getInstance().useMemoryDB()) {
                Log.info("Loading WordNet into memory...");
                long t0 = System.currentTimeMillis();
                dict = new RAMDictionary(WORDNET_DICT, ILoadPolicy.IMMEDIATE_LOAD);
                dict.open();
                long t1 = System.currentTimeMillis();
                Log.info("WordNet loaded into memory in %d msec.", (t1-t0));
            } else {
                dict = new Dictionary(WORDNET_DICT);
                dict.open();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
        if (WS4JConfiguration.getInstance().useStem()) stemmer = new PorterStemmer();
        mapLinkToPointer = new HashMap<>();
        mapLinkToPointer.put(Link.HYPERNYM, Pointer.HYPERNYM);
        mapLinkToPointer.put(Link.HYPONYM, Pointer.HYPONYM);
        mapLinkToPointer.put(Link.HOLONYM_MEMBER, Pointer.HOLONYM_MEMBER);
        mapLinkToPointer.put(Link.HOLONYM_SUBSTANCE, Pointer.HOLONYM_SUBSTANCE);
        mapLinkToPointer.put(Link.HOLONYM_PART, Pointer.HOLONYM_PART);
        mapLinkToPointer.put(Link.MERONYM_MEMBER, Pointer.MERONYM_MEMBER);
        mapLinkToPointer.put(Link.MERONYM_SUBSTANCE, Pointer.MERONYM_SUBSTANCE);
        mapLinkToPointer.put(Link.MERONYM_PART, Pointer.MERONYM_PART);
        mapLinkToPointer.put(Link.CAUSE, Pointer.CAUSE);
        mapLinkToPointer.put(Link.ENTAILMENT, Pointer.ENTAILMENT);
        mapLinkToPointer.put(Link.ANTONYM, Pointer.ANTONYM);
        mapLinkToPointer.put(Link.ATTRIBUTE, Pointer.ATTRIBUTE);
        mapLinkToPointer.put(Link.SIMILAR_TO, Pointer.SIMILAR_TO);
    }

    @Override
    public Concept getMostFrequentConcept(String lemma, POS pos) {
        IIndexWord iIndexWord = dict.getIndexWord(lemma, edu.mit.jwi.item.POS.getPartOfSpeech(pos.getTag()));
        return iIndexWord != null ? new Concept(iIndexWord.getWordIDs().get(0).getSynsetID().toString(), pos, lemma) : null;
    }

    @Override
    public List<Concept> getAllConcepts(String lemma, POS pos) {
        IIndexWord iIndexWord = dict.getIndexWord(lemma, edu.mit.jwi.item.POS.getPartOfSpeech(pos.getTag()));
        return iIndexWord != null ? iIndexWord.getWordIDs().stream().map(iWordID -> new Concept(
                iWordID.getSynsetID().toString(), pos, lemma)).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public List<String> linkToSynsets(String synset, Link point) {
       return dict.getSynset(SynsetID.parseSynsetID(synset)).getRelatedSynsets(mapLinkToPointer.get(point))
               .stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> findWordsBySynset(String synset) {
        return dict.getSynset(SynsetID.parseSynsetID(synset)).getWords()
                .stream().map(IWord::getLemma).collect(Collectors.toList());
    }

    @Override
    public List<String> getGloss(Concept synset, Link link) {
        String key = synset + " " + link;
        if (WS4JConfiguration.getInstance().useCache()) {
            List<String> cachedObj = cache.get(key);
            if (cachedObj != null) return new ArrayList<>(cachedObj);
        }
        List<String> linkedSynsets = new ArrayList<>();
        if (link == null || link.equals(Link.SYNSET)) {
            linkedSynsets.add(synset.getSynsetID());
        } else if (link.equals(Link.MERONYM)) {
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.MERONYM_MEMBER));
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.MERONYM_SUBSTANCE));
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.MERONYM_PART));
        } else if (link.equals(Link.HOLONYM)) {
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.HOLONYM_MEMBER));
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.HOLONYM_SUBSTANCE));
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), Link.HOLONYM_PART));
        } else {
            linkedSynsets.addAll(linkToSynsets(synset.getSynsetID(), link));
        }
        List<String> glosses = new ArrayList<>(linkedSynsets.size());
        for (String linkedSynset : linkedSynsets) {
            String gloss;
            if (Link.SYNSET.equals(link)) gloss = synset.getName();
            else gloss = dict.getSynset(SynsetID.parseSynsetID(linkedSynset)).getGloss()
                    .replaceFirst("; \".+", "");
            if (gloss == null) continue;
            gloss = gloss.replaceAll("[.;:,?!(){}\"`$%@<>]", " ")
                    .replaceAll("&", " and ")
                    .replaceAll("_", " ")
                    .replaceAll("[ ]+", " ")
                    .replaceAll("(?<!\\w)'", " ")
                    .replaceAll("'(?!\\w)", " ")
                    .replaceAll("--", " ").toLowerCase();
            if (WS4JConfiguration.getInstance().useStem()) gloss = stemmer.stemSentence(gloss);
            glosses.add(gloss);
        }
        if (WS4JConfiguration.getInstance().useCache()) cache.put(key, new ArrayList<>(glosses));
        return glosses;
    }
}
