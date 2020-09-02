package AgentsAbstract;

import java.util.Comparator;

public class contextSimilarityComparator implements Comparator<Context> {
	
	private Context relativeTo;
	public contextSimilarityComparator(Context createMyContext) {
		this.relativeTo = createMyContext;
	}

	@Override
	public int compare(Context c1, Context c2) {
		int similarityC1 = c1.similarScore(this.relativeTo);
		int similarityC2 = c2.similarScore(this.relativeTo);
		
		if (similarityC1>similarityC2) {
			return 1;
		}
		if (similarityC1<similarityC2) {
			return -1;
		}
		return 0;
	}

}
