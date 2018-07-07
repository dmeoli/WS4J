package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;

import java.util.*;

public class DepthFinder {

	private PathFinder pathFinder;
	
	public DepthFinder(ILexicalDatabase db) {
		this.pathFinder = new PathFinder(db);
	}
	
	public List<Depth> getRelatedness(Concept concept1, Concept concept2, StringBuilder tracer) {
		List<PathFinder.Subsumer> paths = pathFinder.getAllPaths(concept1, concept2, tracer);
		if (paths == null || paths.size() == 0) return null;
		List<Depth> depthList = new ArrayList<>(paths.size());
		for (PathFinder.Subsumer s : paths) {
			List<Depth> depths = getSynsetDepths(s.concept.getSynsetID());
			if (depths == null || depths.size() == 0) return null;
			Depth depth = depths.get(0);
			depthList.add(depth);
		}
		List<Depth> toBeDeleted = new ArrayList<>(depthList.size());
		for (Depth d : depthList) {
			if (depthList.get(0).depth != d.depth) toBeDeleted.add(d);
		}
		depthList.removeAll(toBeDeleted);
		Map<Integer, Depth> map = new LinkedHashMap<>(depthList.size());
		for (Depth d : depthList) {
			int key = d.toString().hashCode();
			map.put(key, d);
		}
		depthList = new ArrayList<>(map.values());
		return depthList;
	}
	
	private List<Depth> getSynsetDepths(String synset) {
		Set<String> history = new HashSet<>();
		List<List<String>> hyperTrees = pathFinder.getHypernymTrees(synset, history);
		if (hyperTrees == null) return null;
		List<Depth> depths = new ArrayList<>(hyperTrees.size());
		for (List<String> tree : hyperTrees) {
			Depth d = new Depth();
			d.depth = tree.size();
			d.root = tree.get(0);
			d.leaf = synset;
			depths.add(d);
		}
		depths.sort(Comparator.comparingInt(d -> d.depth));
		return depths;
	}
	
	public static class Depth {

		public String leaf;
		public int depth;
		public String root;

		@Override
		public String toString() {
			return "Depth{" +
					"leaf = " + leaf +
					", depth = " + depth +
					", root = " + root +
					'}';
		}
	}
		
	public int getShortestDepth(Concept concept) {
		return Objects.requireNonNull(getSynsetDepths(concept.getSynsetID())).get(0).depth;
	}
}
