package edu.uniba.di.lacam.kdde.lexical_db.data;

public class Concept {

    private String synsetID;
    private POS pos;
    private String name;

    public Concept(String synsetID) {
        this.synsetID = synsetID;
    }

    public Concept(String synsetID, POS pos) {
        this.synsetID = synsetID;
        this.pos = pos;
    }

    public Concept(String synsetID, POS pos, String name) {
        this.synsetID = synsetID;
        this.pos = pos;
        this.name = name;
    }

    public String getSynsetID() {
        return synsetID;
    }

    public void setSynsetID(String synsetID) {
        this.synsetID = synsetID;
    }

    public POS getPOS() {
        return pos;
    }

    public void setPOS(POS pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Concept{" +
                "synsetID='" + synsetID + '\'' +
                ", pos=" + pos +
                ", name='" + name + '\'' +
                '}';
    }
}
