package edu.uniba.di.lacam.kdde.ws4j.util;

import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final public class ICFinder {

	private static final ICFinder IC = new ICFinder();

	private ConcurrentMap<Integer, Integer> freqV;
	private ConcurrentMap<Integer, Integer> freqN;

	private final static int rootFreqN = 128767; // sum of all root freq of n in IC-semcor.dat
	private final static int rootFreqV = 95935;  // sum of all root freq of v in IC-semcor.dat

	private ICFinder(){
		try {
			loadIC();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ICFinder getIC(){
		return ICFinder.IC;
	}
	
	private synchronized void loadIC() throws IOException {
		freqV = new ConcurrentHashMap<>();
		freqN = new ConcurrentHashMap<>();
		InputStream stream = getClass().getResourceAsStream(File.separator + WS4JConfiguration.getInstance().getInfoContent());
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			String[] elements = line.split(" ");
			if (elements.length >= 2) {
				String e = elements[0];
				POS pos = POS.getPOS(e.charAt(e.length()-1));
				int id = Integer.parseInt(e.substring(0, e.length()-1));
				int freq = Integer.parseInt(elements[1]);
				if (Objects.equals(pos, POS.NOUN)) freqN.put(id, freq);
				else if (Objects.equals(pos, POS.VERB)) freqV.put(id, freq);
			}
		}
		br.close();
		isr.close();
	}
	
	public List<PathFinder.Subsumer> getLCSbyIC(PathFinder pathFinder, Concept concept1, Concept concept2,
												StringBuilder tracer) {
		List<PathFinder.Subsumer> paths = pathFinder.getAllPaths(concept1, concept2, tracer);
		if (paths == null || paths.size() == 0) return null;
		for (PathFinder.Subsumer path : paths) path.ic = IC(pathFinder, path.concept);
		paths.sort((s1, s2) -> Double.compare(s2.ic, s1.ic));
		List<PathFinder.Subsumer> results = new ArrayList<>(paths.size());
		for (PathFinder.Subsumer path : paths) {
			if (path.ic == paths.get(0).ic) results.add(path);
		}
		return results;
	}

	public double IC(PathFinder pathFinder, Concept concept) {
		POS pos = concept.getPOS();
		if (pos.equals(POS.NOUN) || pos.equals(POS.VERB)) {
			double prob = probability(pathFinder, concept);
			return prob > 0.0D ? - Math.log(prob) : 0.0D;
		} else return 0.0D;
	}

	private double probability(PathFinder pathFinder, Concept concept) {
		Concept rootConcept = pathFinder.getRoot(concept.getSynsetID());
		int rootFreq = 0;
		if (RelatednessCalculator.useRootNode) {
			if (concept.getPOS().equals(POS.NOUN)) rootFreq = rootFreqN;
			else if (concept.getPOS().equals(POS.VERB)) rootFreq = rootFreqV;
		} else rootFreq = getFrequency(rootConcept);
		int offFreq = getFrequency(concept);
		if (offFreq <= rootFreq) return (double) offFreq / (double) rootFreq;
		return 0.0D;
	}
	
	public int getFrequency(Concept concept) {
		if (concept.getSynsetID().equals("0")) {
			if (concept.getPOS().equals(POS.NOUN)) return rootFreqN;
			else if (concept.getPOS().equals(POS.VERB)) return rootFreqV;
		}
		int synsetID = Integer.parseInt(concept.getSynsetID().replaceAll("[^\\d]", ""));
		int freq = 0;
		if (concept.getPOS().equals(POS.NOUN)) {
			Integer freqObj = freqN.get(synsetID);
			freq = freqObj != null ? freqObj : 0;
		} else if (concept.getPOS().equals(POS.VERB)) {
			Integer freqObj = freqV.get(synsetID);
			freq = freqObj != null ? freqObj : 0;
		}
		return freq;
	}
}
