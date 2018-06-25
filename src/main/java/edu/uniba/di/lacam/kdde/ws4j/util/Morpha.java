package edu.uniba.di.lacam.kdde.ws4j.util;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;

import java.util.Arrays;

public class Morpha {

    private static Stemmer stemmer;

    private static Morpha morpha = new Morpha();

    private Morpha() {
        stemmer = new PorterStemmer();
    }

    public Morpha getInstance() {
        return morpha;
    }

    public static String stemSentence(String sentence) {
        StringBuilder stem = new StringBuilder();
        Arrays.asList(sentence.split("\\s+")).forEach(token -> {
            if (!token.isEmpty()) stem.append(stemmer.stem(token)).append(" ");
        });
        return stem.toString();
    }
}
