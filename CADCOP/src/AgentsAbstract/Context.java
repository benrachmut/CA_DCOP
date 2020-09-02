package AgentsAbstract;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Context {
	private TreeMap<Integer, Integer> valueAssignmentPerAgent;
	private TreeMap<Integer, Integer> costPerAgent;

	public Context(TreeMap<Integer, Integer> valueAssignmentPerAgent, int myId, int myCost) {
		this.valueAssignmentPerAgent = valueAssignmentPerAgent;
		this.costPerAgent = new TreeMap<Integer, Integer>();
		for (Entry<Integer, Integer> e : valueAssignmentPerAgent.entrySet()) {
			if (e.getKey() == myId) {
				costPerAgent.put(e.getKey(), myCost);
			} else {
				costPerAgent.put(e.getKey(), null);
			}
		}
	}

	public Context(TreeMap<Integer, Integer> valueAssignmentPerAgent, TreeMap<Integer, Integer> costs) {
		this.valueAssignmentPerAgent = valueAssignmentPerAgent;
		this.costPerAgent = costs;
	}

	public boolean isConsistentWith(Context other) {
		Set<Integer> sameIdsWithOther = sameIdsWithOther(other);
		for (Integer id : sameIdsWithOther) {
			int myValueOfId = this.valueAssignmentPerAgent.get(id);
			int otherValueOfId = other.getValueAssignmentPerAgent(id);
			if (myValueOfId != otherValueOfId) {
				return false;
			}
		}
		return true;
	}

	private Set<Integer> sameIdsWithOther(Context other) {
		Set<Integer> ans = new TreeSet<Integer>();
		for (Integer myKey : valueAssignmentPerAgent.keySet()) {
			if (other.isValueAssignmentPerAgentContaintsKey(myKey)) {
				ans.add(myKey);
			}
		}
		return ans;
	}

	private boolean isValueAssignmentPerAgentContaintsKey(Integer myKey) {
		// TODO Auto-generated method stub
		return this.valueAssignmentPerAgent.containsKey(myKey);
	}

	/*
	 * public TreeMap<Integer,Integer> getValueAssignmentPerAgent() { return
	 * this.valueAssignmentPerAgent; }
	 * 
	 */
	public boolean isSameValueAssignmnets(Context context_i_AfterMsgUpdate) {
		Set<Integer> sameIdsWithOther = this.sameIdsWithOther(context_i_AfterMsgUpdate);
		if (sameIdsWithOther.size() != this.size()) {
			return false;
		}
		for (Integer id : sameIdsWithOther) {
			int myValueOfId = this.valueAssignmentPerAgent.get(id);
			int otherValueOfId = context_i_AfterMsgUpdate.getValueAssignmentPerAgent(id);

			if (myValueOfId != otherValueOfId) {
				return false;
			}
		}

		return true;
	}

	public Integer getValueAssignmentPerAgent(Integer id) {
		return this.valueAssignmentPerAgent.get(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Context) {
			Context other = (Context) obj;
			if (other.size() != this.size()) {
				return false;
			}
			if (!isSameVariables(other)) {
				return false;
			}
			if (!isSameValueAssignmnets(other)) {
				return false;
			}
			
			return true;
		}
		return false;
	}


	private boolean isSameVariables(Context other) {
		for (Integer key : this.valueAssignmentPerAgent.keySet()) {
			if (!other.isVariableInContext(key)) {
				return false;
			}
		}
		return true;
	}

	private boolean isVariableInContext(Integer key) {
		return this.valueAssignmentPerAgent.containsKey(key);

	}

	private int size() {
		// TODO Auto-generated method stub
		return this.valueAssignmentPerAgent.size();
	}

	public Context combineWith(Context input) {
		if (!this.isConsistentWith(input)) {
			return null;
		}
		if (this.equals(input)) {
			return null;
		} else {
			TreeMap<Integer, Integer> vam = createValueAssignmnetMapCombineWith(input);
			TreeMap<Integer, Integer> costs = createCostsMapCombineWith(input);
			return new Context(vam, costs);
		}
	}

	private TreeMap<Integer, Integer> createCostsMapCombineWith(Context input) {
		TreeMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();
		ans.putAll(this.costPerAgent);
		ans.putAll(input.getCostsPerAgent());

		Set<Integer> sameKeys = this.sameIdsWithOther(input);
		for (Integer agentId : sameKeys) {
			Integer cost = null;

			Integer myCost = this.costPerAgent.get(agentId);
			if (myCost != null) {
				cost = myCost;
			}
			Integer otherCost = input.getCostsPerAgent().get(agentId);
			if (otherCost != null) {
				cost = otherCost;
			}
			if (myCost != null && otherCost != null) {
				if (myCost != otherCost) {
					throw new RuntimeException("something doesnt make sense when aggergating costs");
				}
			}
			ans.put(agentId, cost);
		}

		return ans;
	}

	private TreeMap<Integer, Integer> getCostsPerAgent() {
		return this.costPerAgent;
	}

	private TreeMap<Integer, Integer> createValueAssignmnetMapCombineWith(Context input) {
		TreeMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();
		ans.putAll(this.valueAssignmentPerAgent);
		ans.putAll(input.getValueAssignmentPerAgent());
		return ans;
	}

	private Map<? extends Integer, ? extends Integer> getValueAssignmentPerAgent() {
		return this.valueAssignmentPerAgent;
	}

	public int similarScore(Context relativeTo) {
		int counter = 0;
		Set<Integer>sameIds=this.sameIdsWithOther(relativeTo);
		for (Integer id : sameIds) {
			int myValue = this.valueAssignmentPerAgent.get(id);
			int otherValue = relativeTo.getValueAssignmentPerAgent(id);
			if (myValue == otherValue) {
				counter = counter+1;
			}
		}
		return counter;
	}

	public Integer getCost(Integer id) {
		if (this.costPerAgent.containsKey(id)) {
			return this.costPerAgent.get(id);
		}
		return null;
	}

}
