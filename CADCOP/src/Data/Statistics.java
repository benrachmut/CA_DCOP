package Data;

import java.util.Collection;
import java.util.List;

public class Statistics {

	public static Double mean(Collection<Double> list) {
		Double sum = sum(list);
		if (sum == null) {
			return null;
		}
		return sum/list.size();
	}

	public static Double sum(Collection<Double> list) {
		Double sum = 0.0;
		for (Double d : list) {
			if (d== null) {
				return null;
			}
			sum+=d;
		}
		return sum;
	}
}
