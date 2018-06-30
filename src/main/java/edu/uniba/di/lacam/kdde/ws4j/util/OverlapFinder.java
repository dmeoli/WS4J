package edu.uniba.di.lacam.kdde.ws4j.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OverlapFinder {

	private final static String MARKER = "###";

	public static Overlaps getOverlaps(String gloss1, String gloss2) {
		Overlaps overlaps = new Overlaps();
		String[] words0 = gloss1.split("\\s+");
		String[] words1 = gloss2.split("\\s+");
		words0 = StopWordRemover.getInstance().removeStopWords(words0);
		words1 = StopWordRemover.getInstance().removeStopWords(words1);
		overlaps.length1 = words0.length;
		overlaps.length2 = words1.length;
		Map<Integer,Integer> overlapsLengths = new HashMap<>();
		int matchStartIndex = 0;
		int currIndex = -1;
		while (currIndex < words0.length-1) {
			currIndex++;
			if (!contains(words1, words0, matchStartIndex, currIndex)) {
				overlapsLengths.put(matchStartIndex, currIndex - matchStartIndex);
				if (overlapsLengths.get(matchStartIndex) != null && overlapsLengths.get(matchStartIndex) > 0) currIndex--;
				matchStartIndex++;
			}
		}
		for (int i = matchStartIndex; i <= currIndex; i++) overlapsLengths.put(i, currIndex - i+1);
		int longestOverlap = -1;
		for (int length : overlapsLengths.values()) {
			if (longestOverlap < length) longestOverlap = length;
		}
		overlaps.setOverlapsHash(new ConcurrentHashMap<>(overlapsLengths.size()));
		while (longestOverlap > 0) {
			for (int i = 0; i <= overlapsLengths.size()-1; i++) {
				if (overlapsLengths.get(i) < longestOverlap) continue;
				int stringEnd = i + longestOverlap-1;
				if (containsReplace(words1, words0, i, stringEnd)) {
					List<String> words0Sub = new ArrayList<>(stringEnd - i+1);
                    words0Sub.addAll(Arrays.asList(words0).subList(i, stringEnd+1));
					String temp = String.join(" ", words0Sub);
					synchronized (overlaps.getOverlapsHash()) {
						int v = overlaps.getOverlapsHash().get(temp) != null ? overlaps.getOverlapsHash().get(temp) : 0;
						overlaps.getOverlapsHash().put(temp, v+1);
					}
					for (int j = i; j < i+longestOverlap; j++) overlapsLengths.put(j, 0);
					for (int j = i-1; j >= 0; j--) {
						if (overlapsLengths.get(j) <= i-j) break;
						overlapsLengths.put(j, i-j);
					}
				} else {
					int k = longestOverlap-1;
					while (k > 0) {
						int stringEndNew = i + k-1;
						if (contains(words1, words0, i, stringEndNew)) break;
						k--;
					}
					overlapsLengths.put(i, k);
				}
			}
			longestOverlap = -1;
			for (int length : overlapsLengths.values()) if (longestOverlap < length) longestOverlap = length;
		}
		return overlaps;
	}

	private static boolean contains(String[] words1, String[] words2, int begin, int end) {
		return contains(words1, words2, begin, end, false);
	}

	private static boolean containsReplace(String[] words1, String[] words2, int begin, int end) {
		return contains(words1, words2, begin, end, true);
	}

	private static boolean contains(String[] words1, String[] words2, int begin, int end, boolean doReplace) {
		String[] words2Sub = new String[end-begin+1];
		System.arraycopy(words2, begin, words2Sub, 0, end - begin+1);
		words2 = words2Sub;
		if (words1.length < words2.length) return false;
		for (int j = 0; j <= words1.length - words2.length; j++) {
			if (words1[j].equals(MARKER)) continue;
			if (words2[0].equals(words1[j])) {
				boolean match = true;
				for (int i = 1; i < words2.length; i++) {
					if (words1[j+i].equals(MARKER) || !words2[i].equals(words1[j+i])) {
						match = false;
						break;
					}
				}
				if (match) {
					if (doReplace) {
						for (int k = j; k < j + words2.length; k++) {
							words1[k] = MARKER;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public static class Overlaps {

		private ConcurrentMap<String,Integer> overlapsHash;
		public int length1;
		public int length2;

        public ConcurrentMap<String, Integer> getOverlapsHash() {
            return overlapsHash;
        }

        void setOverlapsHash(ConcurrentMap<String, Integer> overlapsHash) {
            this.overlapsHash = overlapsHash;
        }
    }
}
