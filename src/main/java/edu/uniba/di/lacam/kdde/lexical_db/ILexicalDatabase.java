package edu.uniba.di.lacam.kdde.lexical_db;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;

import java.util.List;

/**
 *
 * @author Donato Meoli
 */
public interface ILexicalDatabase {

    Concept getMostFrequentConcept(String lemma, POS pos);

    List<Concept> getAllConcepts(String lemma, POS pos);

    List<String> linkToSynsets(String synset, Link point);

    List<String> findWordsBySynset(String synset);

    List<String> getGloss(Concept synset, Link link);
}
