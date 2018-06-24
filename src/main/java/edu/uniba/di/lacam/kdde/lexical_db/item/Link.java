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
    HOLONYM_MEMBER("Member Holonym"),
    HOLONYM_SUBSTANCE("Substance Holonym"),
    HOLONYM_PART("Part Holonym"),
    MERONYM("Meronym"),
    MERONYM_MEMBER("Member Meronym"),
    MERONYM_SUBSTANCE("Substance Meronym"),
    MERONYM_PART("Part Meronym"),
    SIMILAR_TO("Similar To"),
    SYNSET("Synset Words");

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
