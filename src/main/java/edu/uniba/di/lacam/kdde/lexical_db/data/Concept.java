package edu.uniba.di.lacam.kdde.lexical_db.data;

import com.google.common.base.Objects;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;

public class Concept {

    private String synsetID;
    private String name;
    private POS pos;

    public Concept(String synsetID) {
        this.synsetID = synsetID;
    }

    public Concept(String synsetID, POS pos) {
        this.synsetID = synsetID;
        this.pos = pos;
    }

    public Concept(String synsetID, POS pos, String name) {
        this.synsetID = synsetID;
        this.name = name;
        this.pos = pos;
    }

    public String getSynsetID() {
        return synsetID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public POS getPOS() {
        return pos;
    }

    public void setPOS(POS pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return synsetID;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Concept && Objects.equal(((Concept) obj).getSynsetID(), synsetID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(synsetID);
    }
}
