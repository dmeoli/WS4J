package edu.uniba.di.lacam.kdde.lexical_db.item;

/**
 *
 * @author Donato Meoli
 */
public enum Link {

    ANTONYM("!", "Antonym"),
    ATTRIBUTE("=", "Attribute"),
    CAUSE(">", "Cause"),
    ENTAILMENT("*", "Entailment"),
    HYPERNYM("@", "Hypernym"),
    HYPONYM("~", "Hyponym"),
    HOLONYM("Holonym"),
    HOLONYM_MEMBER("#m", "Member Holonym"),
    HOLONYM_SUBSTANCE("#s", "Substance Holonym"),
    HOLONYM_PART("#p", "Part Holonym"),
    MERONYM("Meronym"),
    MERONYM_MEMBER("%m", "Member Meronym"),
    MERONYM_SUBSTANCE("%s", "Substance Meronym"),
    MERONYM_PART("%p", "Part Meronym"),
    SIMILAR_TO("&", "Similar To"),
    SYNSET("Synset Words");

    private String symbol;
    private String name;

    Link(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    Link(String name) {
        this(null, name);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
