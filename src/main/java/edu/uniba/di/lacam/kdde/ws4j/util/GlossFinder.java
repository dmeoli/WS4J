package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;

import java.util.ArrayList;
import java.util.List;

import static edu.uniba.di.lacam.kdde.lexical_db.item.Link.*;

public class GlossFinder {

	private static List<Link[]> linkPairs = new ArrayList<Link[]>(){{
		add(new Link[]{null, null});
		add(new Link[]{null, HYPERNYM});
		add(new Link[]{null, HYPONYM});
		add(new Link[]{null, MERONYM});
		add(new Link[]{null, HOLONYM});

		add(new Link[]{HYPERNYM, null});
		add(new Link[]{HYPERNYM, HYPERNYM});
		add(new Link[]{HYPERNYM, HYPONYM});
		add(new Link[]{HYPERNYM, MERONYM});
		add(new Link[]{HYPERNYM, HOLONYM});

		add(new Link[]{HYPONYM, null});
		add(new Link[]{HYPONYM, HYPERNYM});
		add(new Link[]{HYPONYM, HYPONYM});
        add(new Link[]{HYPONYM, MERONYM});
        add(new Link[]{HYPONYM, HOLONYM});

        add(new Link[]{MERONYM, null});
        add(new Link[]{MERONYM, HYPERNYM});
        add(new Link[]{MERONYM, HYPONYM});
        add(new Link[]{MERONYM, MERONYM});
        add(new Link[]{MERONYM, HOLONYM});

        add(new Link[]{SYNSET, null});
        add(new Link[]{SYNSET, HYPERNYM});
        add(new Link[]{SYNSET, HYPONYM});
        add(new Link[]{SYNSET, MERONYM});
        add(new Link[]{SYNSET, HOLONYM});
	}};

	private ILexicalDatabase db;

	public GlossFinder(ILexicalDatabase db) {
		this.db = db;
	}

	public List<SuperGloss> getSuperGlosses(Concept concept1, Concept concept2) {
		List<SuperGloss> glosses = new ArrayList<>(linkPairs.size());
		linkPairs.forEach(links -> glosses.add(new SuperGloss(db.getGloss(concept1, links[0]), db.getGloss(concept2, links[1]),
				links[0] != null ? links[0].getName() : " ", links[1] != null ? links[1].getName() : " ",1.0D)));
		return glosses;
	}

	public static class SuperGloss {

		private List<String> gloss1;
		private List<String> gloss2;
		private String link1;
		private String link2;
		private double weight;

		SuperGloss(List<String> gloss1, List<String> gloss2, String link1, String link2, double weight) {
			this.gloss1 = gloss1;
			this.gloss2 = gloss2;
			this.link1 = link1;
			this.link2 = link2;
			this.weight = weight;
		}

		public List<String> getGloss1() {
			return gloss1;
		}

		public List<String> getGloss2() {
			return gloss2;
		}

		public String getLink1() {
			return link1;
		}

		public String getLink2() {
			return link2;
		}

		public double getWeight() {
			return weight;
		}
	}
}