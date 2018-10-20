package edu.uniba.di.lacam.kdde.lexical_db;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.Morpha;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static edu.uniba.di.lacam.kdde.lexical_db.item.Link.*;

final public class MITWordNet implements ILexicalDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MITWordNet.class);

    private static final String WORDNET_FILE = "wn30.dict";

    private static IRAMDictionary dict;
    private static ConcurrentMap<String, List<String>> glosses;

    public MITWordNet(IRAMDictionary dict) {
        MITWordNet.dict = dict;
        if (WS4JConfiguration.getInstance().useCache()) glosses = new ConcurrentHashMap<>();
    }

    public MITWordNet() {
        try {
            loadWordNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (WS4JConfiguration.getInstance().useCache()) glosses = new ConcurrentHashMap<>();
    }

    synchronized private void loadWordNet() throws IOException {
        if (WS4JConfiguration.getInstance().useMemoryDB()) {
            LOGGER.info("Loading WordNet into memory");
            long t = System.currentTimeMillis();
            dict = new RAMDictionary(Objects.requireNonNull(
                    MITWordNet.class.getClassLoader().getResource(WORDNET_FILE)), ILoadPolicy.IMMEDIATE_LOAD);
            dict.open();
            LOGGER.info("WordNet loaded into memory in {} sec.", (System.currentTimeMillis() - t) / 1000L);
        } else {
            dict = new RAMDictionary(Objects.requireNonNull(
                    MITWordNet.class.getClassLoader().getResource(WORDNET_FILE)), ILoadPolicy.NO_LOAD);
            dict.open();
        }
    }

    public IRAMDictionary getDictionary() {
        return dict;
    }

    @Override
    public Concept getConcept(String lemma, POS pos, int sense) {
        IIndexWord indexWord = dict.getIndexWord(lemma, edu.mit.jwi.item.POS.getPartOfSpeech(pos.getTag()));
        return Objects.nonNull(indexWord) ?
                new Concept(indexWord.getWordIDs().get(sense - 1).getSynsetID().toString(), pos, lemma) : null;
    }

    @Override
    public List<Concept> getAllConcepts(String lemma, POS pos) {
        IIndexWord indexWord = dict.getIndexWord(lemma, edu.mit.jwi.item.POS.getPartOfSpeech(pos.getTag()));
        return Objects.nonNull(indexWord) ? indexWord.getWordIDs().stream().map(wordID -> new Concept(
                wordID.getSynsetID().toString(), pos, lemma)).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public List<Concept> getLinkedSynsets(Concept concept, Link link) {
        List<Concept> linkedSynsets = new ArrayList<>();
        if (Objects.isNull(link) || link.equals(SYNSET)) linkedSynsets.add(concept);
        else {
            ISynsetID synsetID = SynsetID.parseSynsetID(concept.getSynsetID());
            if (link.equals(MERONYM)) {
                linkedSynsets.addAll(getRelatedSynsets(synsetID, MERONYM_MEMBER));
                linkedSynsets.addAll(getRelatedSynsets(synsetID, MERONYM_SUBSTANCE));
                linkedSynsets.addAll(getRelatedSynsets(synsetID, MERONYM_PART));
            } else if (link.equals(HOLONYM)) {
                linkedSynsets.addAll(getRelatedSynsets(synsetID, HOLONYM_MEMBER));
                linkedSynsets.addAll(getRelatedSynsets(synsetID, HOLONYM_SUBSTANCE));
                linkedSynsets.addAll(getRelatedSynsets(synsetID, HOLONYM_PART));
            } else linkedSynsets.addAll(getRelatedSynsets(synsetID, link));
        }
        return linkedSynsets;
    }

    private List<Concept> getRelatedSynsets(ISynsetID synsetID, Link link) {
        return dict.getSynset(synsetID).getRelatedSynsets(Pointer.getPointerType(link.getSymbol(), null))
                .stream().map(synset -> new Concept(synset.toString())).collect(Collectors.toList());
    }

    @Override
    public List<String> getWords(Concept concept) {
        return dict.getSynset(SynsetID.parseSynsetID(concept.getSynsetID())).getWords().stream().map(IWord::getLemma)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getGloss(Concept concept, Link link) {
        String key = concept + " " + link;
        if (WS4JConfiguration.getInstance().useCache()) {
            List<String> gloss = glosses.get(key);
            if (Objects.nonNull(gloss)) return new ArrayList<>(gloss);
        }
        List<Concept> linkedSynsets = getLinkedSynsets(concept, link);
        List<String> glosses = new ArrayList<>(linkedSynsets.size());
        for (Concept linkedSynset : linkedSynsets) {
            String gloss;
            if (SYNSET.equals(link)) gloss = concept.getName();
            else gloss = dict.getSynset(SynsetID.parseSynsetID(linkedSynset.getSynsetID())).getGloss()
                    .replaceFirst("; \".+", "");
            if (Objects.isNull(gloss)) continue;
            gloss = gloss.replaceAll("[.;:,?!(){}\"`$%@<>]", " ")
                    .replaceAll("&", " and ")
                    .replaceAll("_", " ")
                    .replaceAll("[ ]+", " ")
                    .replaceAll("(?<!\\w)'", " ")
                    .replaceAll("'(?!\\w)", " ")
                    .replaceAll("--", " ").toLowerCase();
            if (WS4JConfiguration.getInstance().useStem()) gloss = Morpha.stemSentence(gloss);
            glosses.add(gloss);
        }
        if (WS4JConfiguration.getInstance().useCache()) MITWordNet.glosses.put(key, new ArrayList<>(glosses));
        return glosses;
    }
}
