package Data;

import java.util.Collection;
import java.util.List;

public class Statistics {

	public static Double mean(Collection<Double> list) {
		return sum(list)/list.size();
	}

	public static Double sum(Collection<Double> list) {
		Double sum = 0.0;
		for (Double d : list) {
			sum+=d;
		}
		return sum;
	}
}
