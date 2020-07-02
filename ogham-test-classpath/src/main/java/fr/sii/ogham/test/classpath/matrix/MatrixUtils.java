package fr.sii.ogham.test.classpath.matrix;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class MatrixUtils {
	public static <T> List<List<T>> expand(List<T> values) {
		int size = values.size();
		int combinations = (int) Math.pow(2, size) - 1;
		List<List<T>> expandedValues = new ArrayList<>();
		for (int i=1 ; i<combinations+1 ; i++) {
			ArrayList<T> vals = new ArrayList<>();
			expandedValues.add(vals);
			for(int j=0 ; j<size ; j++) {
				if ((i >> j) % 2 != 0) {
					vals.add(values.get(j));
				}
			}
		}
		return expandedValues;
	}
	
	public static List<String> expand(String dependenciesString) {
		return asList(dependenciesString.split("\\s*\\+\\s*"));
	}

	private MatrixUtils() {
		super();
	}

}
