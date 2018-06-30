package edu.uniba.di.lacam.kdde.lexical_db.item;

public enum Link {

    ANTONYM("Antonym", "!"),
    ATTRIBUTE("Attribute", "="),
    CAUSE("Cause", ">"),
    ENTAILMENT("Entailment", "*"),
    HYPERNYM("Hypernym", "@"),
    HYPONYM("Hyponym", "~"),
    HOLONYM("Holonym"),
    HOLONYM_MEMBER("Member Holonym", "#m"),
    HOLONYM_SUBSTANCE("Substance Holonym", "#s"),
    HOLONYM_PART("Part Holonym", "#p"),
    MERONYM("Meronym"),
    MERONYM_MEMBER("Member Meronym", "%m"),
    MERONYM_SUBSTANCE("Substance Meronym", "%s"),
    MERONYM_PART("Part Meronym", "%p"),
    SIMILAR_TO("Similar To", "&"),
    SYNSET("Synset Words");

    private String name;
    private String symbol;

    Link(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    Link(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return name;
    }
}
