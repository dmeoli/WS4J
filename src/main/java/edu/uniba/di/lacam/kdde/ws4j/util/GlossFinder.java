package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;

import java.util.ArrayList;
import java.util.List;

public class GlossFinder {

	private static String[] pairs = {
			"    :    ", "    :hype", "    :hypo", "    :mero", "    :holo",
			"hype:    ", "hype:hype", "hype:hypo", "hype:mero", "hype:holo",
			"hypo:    ", "hypo:hype", "hypo:hypo", "hypo:mero", "hypo:holo",
			"mero:    ", "mero:hype", "mero:hypo", "mero:mero", "mero:holo",
			"syns:    ", "syns:hype", "syns:hypo", "syns:mero", "syns:holo"
	};

	private ILexicalDatabase db;

	public GlossFinder(ILexicalDatabase db) {
		this.db = db;
	}

	public List<SuperGloss> getSuperGlosses(Concept synset1, Concept synset2) {
		List<SuperGloss> glosses = new ArrayList<>(pairs.length);
		for (String pair : pairs) {
			String[] links = pair.split(":");
			SuperGloss sg = new SuperGloss();
			sg.gloss1 = db.getGloss(synset1, links[0]);
			sg.gloss2 = db.getGloss(synset2, links[1]);
			sg.link1  = links[0];
			sg.link2  = links[1];
			sg.weight = 1.0D;
			glosses.add(sg);
		}
		return glosses;
	}

	public static class SuperGloss {

		private List<String> gloss1;
		private List<String> gloss2;
		private String link1;
		private String link2;
		private double weight;

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