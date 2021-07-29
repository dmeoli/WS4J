package edu.uniba.di.lacam.kdde.ws4j.similarity;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.ICFinder;
import edu.uniba.di.lacam.kdde.ws4j.util.PathFinder;
import edu.uniba.di.lacam.kdde.ws4j.Relatedness;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class calculates the Lin's similarity score between two synsets.
 * Following definition is cited from (Budanitsky & Hirst, 2001).
 * <blockquote>
 * Lin: Lin’s (1998) similarity measure follows from his
 * theory of similarity between arbitrary objects. It uses the
 * same elements as distJC, but in a different fashion:
 * <div style="padding:20px"><code>sim<sub>L</sub>(c<sub>1</sub>, c<sub>2</sub>) =
 * 2 * log p(lso(c<sub>1</sub>, c<sub>2</sub>)) / (log p(c<sub>1</sub>) + log p(c<sub>2</sub>)).</code></div>
 * </blockquote>
 *
 * @author Hideki Shima
 */
public class Lin extends RelatednessCalculator {

    private static final double min = 0.0D;
    private static final double max = 1.0D;

    private static final List<POS[]> POSPairs = new ArrayList<POS[]>() {{
        add(new POS[]{POS.NOUN, POS.NOUN});
        add(new POS[]{POS.VERB, POS.VERB});
    }};

    public Lin(ILexicalDatabase db) {
        super(db, min, max);
    }

    @Override
    protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
        StringBuilder tracer = new StringBuilder();
        if (concept1 == null || concept2 == null) return new Relatedness(min, null, illegalSynset);
        if (concept1.equals(concept2)) return new Relatedness(max, identicalSynset, null);
        StringBuilder subTracer = new StringBuilder();
        List<PathFinder.Subsumer> lcsList = ICFinder.getInstance().getLCSbyIC(pathFinder, concept1, concept2, subTracer);
        if (Objects.requireNonNull(lcsList).size() == 0) return new Relatedness(min, tracer.toString(), null);
        double ic1 = ICFinder.getInstance().IC(pathFinder, concept1);
        double ic2 = ICFinder.getInstance().IC(pathFinder, concept2);
        double score = (ic1 > 0 && ic2 > 0) ? (2D * lcsList.get(0).getIC() / (ic1 + ic2)) : 0D;
        if (WS4JConfiguration.getInstance().useTrace()) {
            tracer.append("LIN(").append(concept1).append(", ").append(concept2).append(")\n");
            tracer.append(Objects.requireNonNull(subTracer));
            lcsList.forEach(lcs -> {
                tracer.append("Lowest Common Subsumer(s): ");
                tracer.append(lcs.getSubsumer()).append(" (IC = ").append(lcs.getIC()).append(")\n");
            });
            tracer.append("Concept(").append(concept1).append(") = ").append(" (IC = ").append(ic1).append(")\n");
            tracer.append("Concept(").append(concept2).append(") = ").append(" (IC = ").append(ic2).append(")\n");
        }
        return new Relatedness(score, tracer.toString(), null);
    }

    @Override
    public List<POS[]> getPOSPairs() {
        return POSPairs;
    }
}
