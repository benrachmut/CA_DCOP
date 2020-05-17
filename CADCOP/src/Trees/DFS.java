package Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;

public class DFS extends Tree {

	public DFS(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		super(agentsArray, c);
	}
	
	//------------**create tree**----------

	public void createTree() {
		while (someOneIsNotColored()) {
			AgentVariable firstNotVisited = findFirstNotVisited();
			createTree(firstNotVisited);
		}
	
	}
	/**
	 * recursive call for dfs tree
	 * @param currentA
	 */
	private void createTree(AgentVariable currentA) {
		this.visited.put(currentA, true);
		List<AgentVariable> sons = getSons(currentA);
		for (AgentVariable agentFieldSon : sons) {
			if (!visited.get(agentFieldSon)) {
				agentFieldSon.setDfsFather(currentA.getNodeId());
				currentA.addDfsSon(agentFieldSon.getNodeId());
				createTree(agentFieldSon);
			}
		}
	}

	

	

	
	
	//------------**inform dfs above below and equals**----------
	@Override

	protected void informEqual(AgentVariable a, Set<NodeId> equalA) {
		if (equalA.isEmpty()== false) {
			System.err.println("the dfs is not pseudo tree thus you have a bug");
		}
		
	}

	@Override
	protected void informAbove(AgentVariable a, Set<NodeId> aboveA) {
		a.setAboveDFS(aboveA);
	}

	@Override
	protected void informBelow(AgentVariable a, Set<NodeId> belowA) {
		a.setBelowDFS(belowA);
	}

	
	//------------**level stuff**----------

	@Override
	protected void setLevelPerAgent(AgentVariable a, int level) {
		a.setDfsLevelInTree(level);
		
	}

	@Override
	protected Set<NodeId> getSonsIds(AgentVariable a) {
		return a.getDfsSonsIds();
	}

	@Override
	protected Set<AgentVariable> getHeads() {
		Set<AgentVariable> ans = new HashSet<AgentVariable>();
		for (AgentVariable a : agentsVector) {
			if (a.getDfsFather().getId1() == -1) {
				ans.add(a);
			}
		}
		return ans;
	}

	


}
