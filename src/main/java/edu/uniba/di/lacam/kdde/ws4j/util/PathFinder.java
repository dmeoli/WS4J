package edu.uniba.di.lacam.kdde.ws4j.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.Link;

public class PathFinder {

	private ILexicalDatabase db;
	
	public PathFinder(ILexicalDatabase db) {
		this.db = db;
	}

	private static ConcurrentMap<String, List<List<String>>> cache;

	static {
		if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
	}

	List<Subsumer> getAllPaths(Concept concept1, Concept concept2, StringBuilder tracer) {
		List<Subsumer> paths = new ArrayList<>();
		Set<String> history = new HashSet<>();
		List<List<String>> lTrees = getHypernymTrees(concept1.getSynsetID(), history);
		history = new HashSet<>();
		List<List<String>> rTrees = getHypernymTrees(concept2.getSynsetID(), history);
		if (lTrees == null || rTrees == null) return null;
		for (List<String> lTree : lTrees) {
			for (List<String> rTree : rTrees) {
				String subsumer = getSubsumerFromTrees(lTree, rTree);
				if (subsumer == null) continue;
				int lCount = 0;
				List<String> lPath = new ArrayList<>(lTree.size());
				List<String> reversedLTree = Lists.reverse(lTree);
				for (String synset : reversedLTree) {
					lCount++;
					if (synset.equals(subsumer)) break;
					lPath.add(synset);
				}
				int rCount = 0;
				List<String> rPath = new ArrayList<>(rTree.size());
				List<String> reversedRTree = Lists.reverse(rTree);
				for (String synset : reversedRTree) {
					rCount++;
					if (synset.equals(subsumer)) break;
					rPath.add(synset);
				}
				paths.add(new Subsumer(new Concept(subsumer, concept1.getPOS()), rCount + lCount - 1, lPath, rPath));
				if (tracer != null) {
					tracer.append("HyperTree1: ").append(lTree).append("\n");
					tracer.append("HyperTree2: ").append(rTree).append("\n");
				}
			}
		}
		paths.sort(Comparator.comparingInt(s -> s.length));
		return paths;
	}

	public List<Subsumer> getShortestPaths(Concept concept1, Concept concept2, StringBuilder tracer) {
		List<Subsumer> returnList = new ArrayList<>();
		List<Subsumer> paths = getAllPaths(concept1, concept2, tracer);
		if (paths == null || paths.size() == 0) return returnList;
		int bestLength = paths.get(0).length;
		returnList.add(paths.get(0));
		for (int i = 1; i < paths.size(); i++) {
			if (paths.get(i).length > bestLength) break;
			returnList.add(paths.get(i));
		}
		return returnList;
	}

	private static String getSubsumerFromTrees(List<String> list1, List<String> list2) {
		List<String> tree1 = Lists.reverse(list1);
		List<String> tree2 = Lists.reverse(list2);
		String tree1Joined = " " + String.join(" ", tree1) + " ";
		for (String synset2 : tree2) {
			if (tree1Joined.contains(synset2)) return synset2;
		}
		return null;
	}

	List<List<String>> getHypernymTrees(String synset, Set<String> history) {
        if (WS4JConfiguration.getInstance().useCache()) {
			List<List<String>> cachedObj = cache.get(synset);
			if (cachedObj != null) return clone(cachedObj);
		}
		if (synset.equals("0")) {
			List<String> tree = new ArrayList<>();
			tree.add("0");
			List<List<String>> trees = new ArrayList<>();
			trees.add(tree);
			if (WS4JConfiguration.getInstance().useCache()) cache.put(synset, clone(trees));
			return trees;
		}
		List<String> synLinks = db.getLinkedSynsets(synset, Link.HYPERNYM);
		List<List<String>> returnList = new ArrayList<>();
		if (synLinks.size() == 0) {
			List<String> tree = new ArrayList<>();
			tree.add(synset);
			tree.add(0, "0");
			returnList.add(tree);
		} else {
			for (String hypernym : synLinks) {
				if (history.contains(hypernym)) continue;
				history.add(hypernym);
				List<List<String>> hypernymTrees = getHypernymTrees(hypernym, history);
				if (hypernymTrees != null) {
					for (List<String> hypernymTree : hypernymTrees) {
						hypernymTree.add(synset);
						returnList.add(hypernymTree);
					}
				}
				if (returnList.size() == 0) {
					List<String> newList = new ArrayList<>();
					newList.add(synset);
					newList.add(0, "0");
					returnList.add(newList);
				}
			}
		}
		if (WS4JConfiguration.getInstance().useCache()) cache.put(synset, clone(returnList));
		return returnList;
	}

	public static class Subsumer {

		private Concept concept;
		private int length;
		private double ic;
		private List<String> lPath;
		private List<String> rPath;

		Subsumer(Concept concept, int length, List<String> lPath, List<String> rPath) {
			this.concept = concept;
			this.length = length;
			this.lPath = lPath;
			this.rPath = rPath;
		}

        void setIC(double ic) {
            this.ic = ic;
        }

		public Concept getConcept() {
			return concept;
		}

		public int getLength() {
			return length;
		}

		public double getIC() {
			return ic;
		}

		public List<String> getlPath() {
			return lPath;
		}

		public List<String> getrPath() {
			return rPath;
		}

		@Override
		public String toString() {
			return new Gson().toJson(this);
		}
    }

	public Concept getRoot(String synset) {
		Set<String> history = new HashSet<>();
		List<List<String>> paths = getHypernymTrees(synset,history);
		if (paths != null && paths.size() > 0 && paths.get(0).size() > 1) {
			return new Concept(paths.get(0).get(1));
		} else if (paths != null && paths.size() > 0) {
			return new Concept(paths.get(0).get(0));
		}
		return null;
	}

	public List<Subsumer> getLCSByPath(Concept concept1, Concept concept2, StringBuilder tracer) {
		List<Subsumer> paths = getAllPaths(concept1, concept2, tracer);
		List<Subsumer> returnPaths = new ArrayList<>(paths.size());
		paths.forEach(path -> {
            if (path.length <= paths.get(0).length) returnPaths.add(path);
        });
		return returnPaths;
	}

	private static List<List<String>> clone(List<List<String>> originals) {
		List<List<String>> clone = new ArrayList<>(originals.size());
		originals.forEach(original -> {
            List<String> cStrings = new ArrayList<>(original.size());
            cStrings.addAll(original);
            clone.add(cStrings);
        });
		return clone;
	}
}
