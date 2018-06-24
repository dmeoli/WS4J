package edu.uniba.di.lacam.kdde.lexical_db.item;

/**
 *
 * @author Donato Meoli
 */
public enum Link {

    ANTONYM("Antonym"),
    ATTRIBUTE("Attribute"),
    CAUSE("Cause"),
    ENTAILMENT("Entailment"),
    HYPERNYM("Hypernym"),
    HYPONYM("Hyponym"),
    HOLONYM("Holonym"),
    HOLONYM_MEMBER("Member holonym"),
    HOLONYM_SUBSTANCE("Substance holonym"),
    HOLONYM_PART("Part holonym"),
    MERONYM("Meronym"),
    MERONYM_MEMBER("Member meronym"),
    MERONYM_SUBSTANCE("Substance meronym"),
    MERONYM_PART("Part meronym"),
    SIMILAR_TO("Similar To"),
    SYNSET("Synset words");

    private final String name;

    Link(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
