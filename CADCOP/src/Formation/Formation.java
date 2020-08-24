package Formation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;

public abstract class Formation {
	protected Collection<AgentVariable> agents;
	protected Map<AgentVariable, Boolean> visited;

	public Formation( AgentVariable[] input_a) {
		this.agents = createAfList(input_a);
		this.visited = initColorMap(); 
	}
	
	public abstract void setAboveBelow();
	
	protected AgentVariable getAgentByNodeId(NodeId nodeId ) {
		for (AgentVariable a : agents) {
			if (a.getNodeId().getId1() == nodeId.getId1()) {
				return a;
			}
		}
		return null;
	}
	
	private Map<AgentVariable, Boolean> initColorMap() {
		Map<AgentVariable, Boolean> ans = new HashMap<AgentVariable, Boolean>();
		for (AgentVariable agentField : agents) {
			ans.put(agentField, false);
		}

		return ans;
	}

	private Collection<AgentVariable> createAfList(AgentVariable[] aFieldInput) {
		Collection<AgentVariable> ans = new ArrayList<AgentVariable>();

		for (AgentVariable temp : aFieldInput) {
			ans.add(temp);
		}
		return ans;
	}
	
	public abstract void execute();
}
