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

public abstract class Tree {
	protected AgentVariable[] agentsArray;

	protected List<AgentVariable> agentsVector;
	protected SortedMap<Integer, Integer> agentsLevelInTree;
	protected SortedMap<Integer, Set<Integer>> belowNeighbors;
	protected SortedMap<Integer, Set<Integer>> aboveNeighbors;
	protected SortedMap<Integer, Set<Integer>> equalNeighbors;

	protected Map<AgentVariable, Boolean> visited;
	protected Comparator<AgentVariable> c;

	public Tree(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		this.c = c;
		this.agentsArray = agentsArray;
		this.agentsVector = createAgentVariableList(agentsArray);
		this.visited = initColorMap();
		this.agentsLevelInTree = new TreeMap<Integer, Integer>();

		this.belowNeighbors = new TreeMap<Integer, Set<Integer>>();
		this.aboveNeighbors = new TreeMap<Integer, Set<Integer>>();
		this.equalNeighbors = new TreeMap<Integer, Set<Integer>>();
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
			this.setLevel(head.getId(), 0); // initiate recursion
		}
	}

	/**
	 * recursion update agent and map, then stop condition = no more sons, recursion
	 * = call each son with level+1
	 * 
	 * @param agentId
	 * @param level
	 */
	private void setLevel(int agentId, int level) {

		updateLevelInfo(agentId, level);
		Set<Integer> sonsIds = getSonsIds(agentsArray[agentId]);
		if (sonsIds.isEmpty()) {
			return;
		} else {
			for (Integer son : sonsIds) {
				setLevel(son, level + 1);
			}
		}

	}

	private void updateLevelInfo(int agentId, int level) {
		setLevelPerAgent(agentsArray[agentId], level);
		this.agentsLevelInTree.put(agentId, level);

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
	protected abstract Set<Integer> getSonsIds(AgentVariable a);

	/**
	 * heads have father id of -1
	 * @return
	 */
	protected abstract Set<AgentVariable> getHeads();

	protected void informNeighborsRelativeToAgent() {
		for (AgentVariable a : agentsVector) {
			int aId = a.getId();
			Set<Integer> belowA = this.belowNeighbors.get(aId);
			informBelow(a, belowA);
			Set<Integer> aboveA = this.aboveNeighbors.get(aId);
			informAbove(a, aboveA);
			Set<Integer> equalA = this.equalNeighbors.get(aId);
			informEqual(a, equalA);
		}
	}

	protected abstract void informEqual(AgentVariable a, Set<Integer> equalA);

	protected abstract void informAbove(AgentVariable a, Set<Integer> aboveA);

	protected abstract void informBelow(AgentVariable a, Set<Integer> belowA);

	protected void createAboveBelowEqual() {
		for (AgentVariable a : agentsVector) {
			int aLevel = this.agentsLevelInTree.get(a.getId());
			Set<Integer> nIds = a.getNeigborSetId();

			Set<Integer> nAbove = new HashSet<Integer>();
			Set<Integer> nBelow = new HashSet<Integer>();
			Set<Integer> nEqual = new HashSet<Integer>();

			for (Integer nId : nIds) {
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

			this.aboveNeighbors.put(a.getId(), nAbove);
			this.belowNeighbors.put(a.getId(), nBelow);
			this.aboveNeighbors.put(a.getId(), nEqual);

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
		Set<Integer> nSetId = currntA.getNeigborSetId();
		List<AgentVariable> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, this.c);
		Collections.reverse(sons);
		return sons;
	}

	//-----**general methods used when creating a tree**-----
	protected List<AgentVariable> getNeighborsOfAgentField(Set<Integer> nSetId) {
		List<AgentVariable> aFNeighbors = new ArrayList<AgentVariable>();
		for (Integer i : nSetId) {
			for (AgentVariable neighbor : agentsVector) {
				if (i == neighbor.getId() && !this.visited.get(neighbor)) {
					aFNeighbors.add(neighbor);
					break;
				}
			}
		}
		return aFNeighbors;
	}


}