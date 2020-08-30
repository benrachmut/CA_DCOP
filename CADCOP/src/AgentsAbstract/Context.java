package AgentsAbstract;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Context {
	private TreeMap<Integer,Integer> valueAssignmentPerAgent;
	private TreeMap<Integer,Integer> costPerAgent;
	
	
	public Context(TreeMap<Integer,Integer>valueAssignmentPerAgent, int myId, int myCost) {
		this.valueAssignmentPerAgent = valueAssignmentPerAgent;
		this.costPerAgent = new TreeMap<Integer,Integer>();
		for (Entry<Integer, Integer> e : valueAssignmentPerAgent.entrySet()) {
			if (e.getKey() == myId) {
				costPerAgent.put(e.getKey(), myCost);
			}else {
				costPerAgent.put(e.getKey(), null);
			}
		}
	}
	
	
	public boolean isConsistentWith(Context other) {
		Set<Integer> sameIdsWithOther = sameIdsWithOther(other);
		for (Integer id : sameIdsWithOther) {
			int myValueOfId = this.valueAssignmentPerAgent.get(id);
			int otherValueOfId = other.getValueAssignmentPerAgent().get(id);
			if (myValueOfId != otherValueOfId) {
				return false;
			}
		}
		return true;
	}
	
	private Set<Integer> sameIdsWithOther(Context other) {
		Set<Integer> ans = new TreeSet<Integer>();
		for (Integer myKey : valueAssignmentPerAgent.keySet()) {
			if (other.getValueAssignmentPerAgent().containsKey(myKey)) {
				ans.add(myKey);
			}
		}
		return ans;
	}


	public TreeMap<Integer,Integer> getValueAssignmentPerAgent() {
		return this.valueAssignmentPerAgent;
	}


	public boolean isSameValueAssignmnets(Context context_i_AfterMsgUpdate) {
		Set<Integer> sameIdsWithOther = this.sameIdsWithOther(context_i_AfterMsgUpdate);
		if (sameIdsWithOther.size()  != this.size()) {
			return false;
		}
		for (Integer id : sameIdsWithOther) {
			int myValueOfId = this.valueAssignmentPerAgent.get(id);
			int otherValueOfId = context_i_AfterMsgUpdate.getValueAssignmentPerAgent().get(id);
			
			if (myValueOfId!=otherValueOfId) {
				return false;
			}
		}
		
		return true;
	}


	public Integer getValueAssignmnet(Integer id) {
		return this.valueAssignmentPerAgent.get(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Context) {
			Context other = (Context)obj;
			if (other.size()!= this.size()) {
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


	
}
