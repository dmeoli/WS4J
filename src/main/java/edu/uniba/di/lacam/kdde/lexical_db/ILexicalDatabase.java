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

    Concept getConcept(String lemma, POS pos, int sense);

    List<Concept> getAllConcepts(String lemma, POS pos);

    List<String> getSynsets(String synsetID, Link link);

    List<String> getWords(String synsetID);

    List<String> getGloss(Concept concept, Link link);
}
