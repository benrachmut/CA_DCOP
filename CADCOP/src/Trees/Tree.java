package Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;

public abstract class Tree {
	protected AgentVariable[] agentsArray;

	protected List<AgentVariable> agentsVector;
	protected SortedMap<NodeId, Integer> agentsLevelInTree;
	protected SortedMap<NodeId, Set<NodeId>> belowNeighbors;
	protected SortedMap<NodeId, Set<NodeId>> aboveNeighbors;
	protected SortedMap<NodeId, Set<NodeId>> equalNeighbors;

	protected Map<AgentVariable, Boolean> visited;
	protected Comparator<AgentVariable> c;

	public Tree(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		this.c = c;
		this.agentsArray = agentsArray;
		this.agentsVector = createAgentVariableList(agentsArray);
		this.visited = initColorMap();
		this.agentsLevelInTree = new TreeMap<NodeId, Integer>();

		this.belowNeighbors = new TreeMap<NodeId, Set<NodeId>>();
		this.aboveNeighbors = new TreeMap<NodeId, Set<NodeId>>();
		this.equalNeighbors = new TreeMap<NodeId, Set<NodeId>>();
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
		for (AgentVariable agentField : agentsVector) {
			ans.put(agentField, false);
		}
		return ans;
	}

	public void initiateTree() {
		createTree(); // inform agents upon who is their father and sons
		setLevelInTree(); // find level of each agent for this.agentsLevelInTree
		createAboveBelowEqual(); // create maps belowNeighbors, aboveNeighbors, equalNeighbors
		informNeighborsRelativeToAgent();
	}

	protected abstract void createTree();

	/**
	 * set level of each agent after tree is creates
	 */
	protected void setLevelInTree() {
		Set<AgentVariable> heads = getHeads();
		for (AgentVariable head : heads) {
			this.setLevel(head.getNodeId(), 0); // initiate recursion
		}
	}

	/**
	 * recursion update agent and map, then stop condition = no more sons, recursion
	 * = call each son with level+1
	 * 
	 * @param agentId
	 * @param level
	 */
	private void setLevel(NodeId agentId, int level) {

		updateLevelInfo(agentId, level);
		Set<NodeId> sonsIds = getSonsIds(agentsArray[agentId.getId1()]);
		if (sonsIds.isEmpty()) {
			return;
		} else {
			for (NodeId son : sonsIds) {
				setLevel(son, level + 1);
			}
		}
	}

	private void updateLevelInfo(NodeId agentId, int level) {
		setLevelPerAgent(agentsArray[agentId.getId1()], level);
		this.agentsLevelInTree.put(new NodeId(agentId.getId1()), level);

	}

	/**
	 * update the agent upon its level according to relevant field
	 * @param a
	 * @param level
	 */
	protected abstract void setLevelPerAgent(AgentVariable a, int level);


	/**
	 * 
	 * @param a
	 * @return set of sons from relevent tree field
	 */
	protected abstract Set<NodeId> getSonsIds(AgentVariable a);

	/**
	 * heads have father id of -1
	 * @return
	 */
	protected abstract Set<AgentVariable> getHeads();

	protected void informNeighborsRelativeToAgent() {
		for (AgentVariable a : agentsVector) {
			NodeId aId = a.getNodeId();
			Set<NodeId> belowA = this.belowNeighbors.get(aId);
			informBelow(a, belowA);
			Set<NodeId> aboveA = this.aboveNeighbors.get(aId);
			informAbove(a, aboveA);
			Set<NodeId> equalA = this.equalNeighbors.get(aId);
			informEqual(a, equalA);
		}
	}

	protected abstract void informEqual(AgentVariable a, Set<NodeId> equalA);

	protected abstract void informAbove(AgentVariable a, Set<NodeId> aboveA);

	protected abstract void informBelow(AgentVariable a, Set<NodeId> belowA);

	protected void createAboveBelowEqual() {
		for (AgentVariable a : agentsVector) {
			int aLevel = this.agentsLevelInTree.get(a.getNodeId());
			Set<NodeId> nIds = a.getNeigborSetId();

			Set<NodeId> nAbove = new HashSet<NodeId>();
			Set<NodeId> nBelow = new HashSet<NodeId>();
			Set<NodeId> nEqual = new HashSet<NodeId>();

			for (NodeId nId : nIds) {
				int nLevel = this.agentsLevelInTree.get(nId);

				if (aLevel < nLevel) {
					nBelow.add(nId);
				}
				if (aLevel > nLevel) {
					nAbove.add(nId);
				} else {
					nEqual.add(nId);
				}

			} // for neighbors of agent

			this.aboveNeighbors.put(a.getNodeId(), nAbove);
			this.belowNeighbors.put(a.getNodeId(), nBelow);
			this.aboveNeighbors.put(a.getNodeId(), nEqual);

		} // for agents

	}
	
	//-----**general methods used when creating a tree**-----
	protected boolean someOneIsNotColored() {
		Collection<Boolean> colors = this.visited.values();
		for (Boolean c : colors) {
			if (!c) {
				return true;
			}
		}
		return false;
	}
	
	//-----**general methods used when creating a tree**-----
	protected AgentVariable findFirstNotVisited() {

		for (AgentVariable agentField : agentsVector) {
			if (!visited.get(agentField)) {
				return agentField;
			}
		}
		return null;
	}
	
	//-----**general methods used when creating a tree**-----
	protected List<AgentVariable> getSons(AgentVariable currntA) {
		Set<NodeId> nSetId = currntA.getNeigborSetId();
		List<AgentVariable> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, this.c);
		Collections.reverse(sons);
		return sons;
	}

	//-----**general methods used when creating a tree**-----
	protected List<AgentVariable> getNeighborsOfAgentField(Set<NodeId> nSetId) {
		List<AgentVariable> aFNeighbors = new ArrayList<AgentVariable>();
		for (NodeId i : nSetId) {
			for (AgentVariable neighbor : agentsVector) {
				if (i.equals(neighbor.getNodeId()) && !this.visited.get(neighbor)) {
					aFNeighbors.add(neighbor);
					break;
				}
			}
		}
		return aFNeighbors;
	}


}