package edu.uniba.di.lacam.kdde.ws4j.util;

import com.google.gson.Gson;
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
			List<Depth> depths = getSynsetDepths(s.getSubsumer());
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
	
	private List<Depth> getSynsetDepths(Concept concept) {
		Set<Concept> history = new HashSet<>();
		List<List<Concept>> hyperTrees = pathFinder.getHypernymTrees(concept, history);
		if (hyperTrees == null) return null;
		List<Depth> depths = new ArrayList<>(hyperTrees.size());
		hyperTrees.forEach(hyperTree -> depths.add(new Depth(concept, hyperTree.size(), hyperTree.get(0))));
		depths.sort(Comparator.comparingInt(d -> d.depth));
		return depths;
	}
	
	public static class Depth {

		private Concept leaf;
		private int depth;
		private Concept root;

		Depth(Concept leaf, int depth, Concept root) {
			this.leaf = leaf;
			this.depth = depth;
			this.root = root;
		}

        public Concept getLeaf() {
            return leaf;
        }

        public int getDepth() {
            return depth;
        }

        public Concept getRoot() {
            return root;
        }

        @Override
		public String toString() {
			return new Gson().toJson(this);
		}
	}
		
	public int getShortestDepth(Concept concept) {
		return Objects.requireNonNull(getSynsetDepths(concept)).get(0).depth;
	}
}
