package edu.uniba.di.lacam.kdde.ws4j.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CollectionUtil {

	static String join(String delimiter, List<String> list) {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (String element : list) {
			sb.append(counter++ > 0 ? delimiter : "");
			sb.append(element);
		}
		return sb.toString();
	}

	static List<String> reverse(List<String> list) {
		List<String> reversedList = new ArrayList<>(list);
		Collections.reverse(reversedList);
		return reversedList;
	}
}
