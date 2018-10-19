package edu.uniba.di.lacam.kdde.ws4j.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;

import static edu.uniba.di.lacam.kdde.lexical_db.item.Link.*;

public class PathFinder {

    private ILexicalDatabase db;

    public PathFinder(ILexicalDatabase db) {
        this.db = db;
    }

    private static ConcurrentMap<Concept, List<List<Concept>>> cache;

    static {
        if (WS4JConfiguration.getInstance().useCache()) cache = new ConcurrentHashMap<>();
    }

    List<Subsumer> getAllPaths(Concept concept1, Concept concept2, StringBuilder tracer) {
        List<Subsumer> paths = new ArrayList<>();
        Set<Concept> history = new HashSet<>();
        List<List<Concept>> lTrees = getHypernymTrees(concept1, history);
        history = new HashSet<>();
        List<List<Concept>> rTrees = getHypernymTrees(concept2, history);
        if (lTrees == null || rTrees == null) return null;
        for (List<Concept> lTree : lTrees) {
            for (List<Concept> rTree : rTrees) {
                Concept subsumer = getSubsumerFromTrees(lTree, rTree);
                if (subsumer == null) continue;
                int lCount = 0;
                List<Concept> lPath = new ArrayList<>(lTree.size());
                List<Concept> reversedLTree = Lists.reverse(lTree);
                for (Concept synset : reversedLTree) {
                    lCount++;
                    if (synset.equals(subsumer)) break;
                    lPath.add(synset);
                }
                int rCount = 0;
                List<Concept> rPath = new ArrayList<>(rTree.size());
                List<Concept> reversedRTree = Lists.reverse(rTree);
                for (Concept synset : reversedRTree) {
                    rCount++;
                    if (synset.equals(subsumer)) break;
                    rPath.add(synset);
                }
                paths.add(new Subsumer(new Concept(subsumer.getSynsetID(), concept1.getPOS()),
                        rCount + lCount - 1, lPath, rPath));
                if (tracer != null) {
                    tracer.append("HyperTree(").append(concept1).append(") = ").append(lTree).append("\n");
                    tracer.append("HyperTree(").append(concept2).append(") = ").append(rTree).append("\n");
                }
            }
        }
        paths.sort(Comparator.comparingInt(s -> s.pathLength));
        return paths;
    }

    public List<Subsumer> getShortestPaths(Concept concept1, Concept concept2, StringBuilder tracer) {
        List<Subsumer> returnList = new ArrayList<>();
        List<Subsumer> paths = getAllPaths(concept1, concept2, tracer);
        if (paths == null || paths.size() == 0) return returnList;
        int bestLength = paths.get(0).pathLength;
        returnList.add(paths.get(0));
        for (int i = 1; i < paths.size(); i++) {
            if (paths.get(i).pathLength > bestLength) break;
            returnList.add(paths.get(i));
        }
        return returnList;
    }

    private static Concept getSubsumerFromTrees(List<Concept> concepts1, List<Concept> concepts2) {
        List<String> tree1 = Lists.reverse(concepts1.stream().map(Concept::getSynsetID).collect(Collectors.toList()));
        List<String> tree2 = Lists.reverse(concepts2.stream().map(Concept::getSynsetID).collect(Collectors.toList()));
        String tree1Joined = " " + String.join(" ", tree1) + " ";
        for (String synset : tree2) if (tree1Joined.contains(synset)) return new Concept(synset);
        return null;
    }

    List<List<Concept>> getHypernymTrees(Concept synset, Set<Concept> history) {
        /* if (WS4JConfiguration.getInstance().useCache()) {
			List<List<Concept>> cachedObj = cache.get(synset);
			if (cachedObj != null) return clone(cachedObj);
		} */
        if (synset.getSynsetID().equals("0")) {
            List<Concept> tree = new ArrayList<>();
            tree.add(new Concept("0"));
            List<List<Concept>> trees = new ArrayList<>();
            trees.add(tree);
            // if (WS4JConfiguration.getInstance().useCache()) cache.put(synset, clone(trees));
            return trees;
        }
        List<Concept> synLinks = db.getLinkedSynsets(synset, HYPERNYM);
        List<List<Concept>> returnList = new ArrayList<>();
        if (synLinks.size() == 0) {
            List<Concept> tree = new ArrayList<>();
            tree.add(synset);
            tree.add(0, new Concept("0"));
            returnList.add(tree);
        } else {
            for (Concept hypernym : synLinks) {
                if (history.contains(hypernym)) continue;
                history.add(hypernym);
                List<List<Concept>> hypernymTrees = getHypernymTrees(hypernym, history);
                if (hypernymTrees != null) {
                    for (List<Concept> hypernymTree : hypernymTrees) {
                        hypernymTree.add(synset);
                        returnList.add(hypernymTree);
                    }
                }
                if (returnList.size() == 0) {
                    List<Concept> newList = new ArrayList<>();
                    newList.add(synset);
                    newList.add(0, new Concept("0"));
                    returnList.add(newList);
                }
            }
        }
        // if (WS4JConfiguration.getInstance().useCache()) cache.put(synset, clone(returnList));
        return returnList;
    }

    public static class Subsumer {

        private Concept subsumer;
        private int pathLength;
        private double ic;
        private List<Concept> lPath;
        private List<Concept> rPath;

        Subsumer(Concept subsumer, int pathLength, List<Concept> lPath, List<Concept> rPath) {
            this.subsumer = subsumer;
            this.pathLength = pathLength;
            this.lPath = lPath;
            this.rPath = rPath;
        }

        void setIC(double ic) {
            this.ic = ic;
        }

        public Concept getSubsumer() {
            return subsumer;
        }

        public int getPathLength() {
            return pathLength;
        }

        public double getIC() {
            return ic;
        }

        public List<Concept> getlPath() {
            return lPath;
        }

        public List<Concept> getrPath() {
            return rPath;
        }

        @Override
        public String toString() {
            return "{" +
                    "subsumer=" + subsumer +
                    ", pathLength=" + pathLength +
                    ", ic=" + ic +
                    ", lPath=" + lPath +
                    ", rPath=" + rPath +
                    '}';
        }
    }

    public Concept getRoot(Concept synset) {
        Set<Concept> history = new HashSet<>();
        List<List<Concept>> paths = getHypernymTrees(synset, history);
        if (paths != null && paths.size() > 0 && paths.get(0).size() > 1) return paths.get(0).get(1);
        else if (paths != null && paths.size() > 0) return paths.get(0).get(0);
        return null;
    }

    public List<Subsumer> getLCSByPath(Concept concept1, Concept concept2, StringBuilder tracer) {
        List<Subsumer> paths = getAllPaths(concept1, concept2, tracer);
        List<Subsumer> returnPaths = new ArrayList<>(paths.size());
        paths.forEach(path -> {
            if (path.pathLength <= paths.get(0).pathLength) returnPaths.add(path);
        });
        return returnPaths;
    }

    private static List<List<Concept>> clone(List<List<Concept>> originals) {
        List<List<Concept>> clone = new ArrayList<>(originals.size());
        originals.forEach(original -> {
            List<Concept> cStrings = new ArrayList<>(original.size());
            cStrings.addAll(original);
            clone.add(cStrings);
        });
        return clone;
    }
}
