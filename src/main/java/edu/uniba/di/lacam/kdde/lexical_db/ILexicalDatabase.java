package edu.uniba.di.lacam.kdde.lexical_db;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.data.Link;
import edu.uniba.di.lacam.kdde.lexical_db.data.POS;

import java.util.List;

/**
 *
 * @author Donato Meoli
 */
public interface ILexicalDatabase {

    Concept getMostFrequentConcept(String lemma, POS pos);

    List<Concept> getAllConcepts(String lemma, POS pos);

    List<String> linkToSynsets(String synset, Link link);

    List<String> findWordsBySynset(String synset);

    List<String> getGloss(Concept synset, String link);
}
