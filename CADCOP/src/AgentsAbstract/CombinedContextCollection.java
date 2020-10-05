package AgentsAbstract;

import java.util.HashSet;
import java.util.Set;

public class CombinedContextCollection {

	private Set<Integer>ids;
	private Set<Context>contexts;
	public CombinedContextCollection(Set<Integer> ids) {
		super();
		this.ids = ids;
		this.contexts = new HashSet<Context>();
	}
}
