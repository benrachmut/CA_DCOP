package Trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;

public abstract class Tree {
	protected List<AgentVariable> agents;
	protected TreeMap<Integer, Integer> agentsLevelInTree;

	protected Map<AgentVariable, Boolean> visited;
	protected Comparator<AgentVariable> c;

	public Tree(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		this.c = c;
		this.agents = createAgentVariableList(agentsArray);
		this.visited = initColorMap();
		this.agentsLevelInTree = new  TreeMap<Integer,Integer>();
		this.createTree();
	}

	private List<AgentVariable> createAgentVariableList(AgentVariable[] agentsArray) {
		List<AgentVariable> ans = new ArrayList<AgentVariable>();
		for (AgentVariable temp : agentsArray) {
			ans.add(temp);
		}
		Collections.sort(ans, c); // sorts in a ascending order 1,2,3...
		Collections.reverse(ans); // reverse to descending order 3,2,1...
		return ans;
	}

	private Map<AgentVariable, Boolean> initColorMap() {
		Map<AgentVariable, Boolean> ans = new HashMap<AgentVariable, Boolean>();
		for (AgentVariable agentField : agents) {
			ans.put(agentField, false);
		}
		return ans;
	}
	
	public abstract void  createTree();

	protected abstract TreeMap<Integer, Integer>  setLevelInTree();
	/*
	public void setIsAboveBelow() {
		setAbove();
		setBelow();
	}
	
	protected abstract void setLevelInTree();
	protected abstract void  setBelow();
*/
}