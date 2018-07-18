package edu.uniba.di.lacam.kdde.lexical_db.item;

public enum POS {

    NOUN("noun", 'n'),
    VERB("verb", 'v'),
    ADJECTIVE("adjective", 'a'),
    ADVERB("adverb", 'r');

    private static final char NOUN_TAG = 'n';
    private static final char VERB_TAG = 'v';
    private static final char ADJECTIVE_TAG = 'a';
    private static final char ADVERB_TAG = 'r';

    private final String name;
    private final char tag;

    POS(String name, char tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public char getTag() {
        return tag;
    }

    public static POS getPOS(char tag) {
        switch (tag) {
            case (NOUN_TAG): return NOUN;
            case (VERB_TAG): return VERB;
            case (ADJECTIVE_TAG): return ADJECTIVE;
            case (ADVERB_TAG): return ADVERB;
            default: return null;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(tag);
    }
}
