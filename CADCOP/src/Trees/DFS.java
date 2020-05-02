package Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;

public class DFS extends Tree {

	public DFS(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		super(agentsArray, c);
	}

	public void createTree() {
		while (someOneIsNotColored()) {
			AgentVariable firstNotVisited = findFirstNotVisited();
			createTree(firstNotVisited);
		}
		TreeMap<Integer, Integer> levelsMap = setLevelInTree();
		stop here
	}

	protected TreeMap<Integer, Integer> setLevelInTree() {

		TreeMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();// id,level
		Collection<AgentVariable> heads = getAllHeads();
		for (AgentVariable head : heads) {
			head.setDfsLevelInTree(0);
		}

		for (AgentVariable a : agents) {
			ans.put(a.getId(), a.getLevelInDfs());
		}
		return ans;
	}

	/**
	 * get all the head root of trees. If an agent has no dfs father then it is a
	 * head
	 * 
	 * @return
	 */
	private Collection<AgentVariable> getAllHeads() {
		Collection<AgentVariable> ans = new ArrayList<AgentVariable>();
		for (AgentVariable a : agents) {
			if (a.isDfsHead()) {
				ans.add(a);
			}
		}
		return ans;
	}

	private AgentVariable findFirstNotVisited() {

		for (AgentVariable agentField : agents) {
			if (!visited.get(agentField)) {
				return agentField;
			}
		}
		return null;
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
				agentFieldSon.setDfsFather(currentA);
				currentA.addDfsSon(agentFieldSon);
				createTree(agentFieldSon);
			}
		}
	}

	private boolean someOneIsNotColored() {
		Collection<Boolean> colors = this.visited.values();
		for (Boolean c : colors) {
			if (!c) {
				return true;
			}
		}
		return false;
	}

	private List<AgentVariable> getSons(AgentVariable currntA) {
		Set<Integer> nSetId = currntA.getNeigborSetId();
		List<AgentVariable> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, this.c);
		Collections.reverse(sons);
		return sons;
	}

	private List<AgentVariable> getNeighborsOfAgentField(Set<Integer> nSetId) {
		List<AgentVariable> aFNeighbors = new ArrayList<AgentVariable>();
		for (Integer i : nSetId) {
			for (AgentVariable neighbor : agents) {
				if (i == neighbor.getId() && !this.visited.get(neighbor)) {
					aFNeighbors.add(neighbor);
					break;
				}
			}
		}
		return aFNeighbors;
	}

	/*
	 * public void setIsAboveBelow() { setAbove(); setBelow(); }
	 */
	/*
	 * protected void setBelow() { for (AgentVariable a : agents) { a.addBelowDFS();
	 * }
	 * 
	 */

	/*
	 * protected void setAbove() { Map<AgentVariable, Boolean> color = new
	 * HashMap<AgentVariable, Boolean>(); for (AgentVariable agentField : agents) {
	 * color.put(agentField, false); }
	 * 
	 * List<AgentVariable> breathingArray = getAllLeaves();
	 * 
	 * while (nonColored(color)) { breathingArray =
	 * setIsAboveBelowPerBreathing(breathingArray, color); }
	 * 
	 * }
	 */
	/*
	 * private List<AgentVariable> setIsAboveBelowPerBreathing(List<AgentVariable>
	 * breathingArray, Map<AgentVariable, Boolean> color) { List<AgentVariable> temp
	 * = new ArrayList<AgentVariable>();
	 * 
	 * for (AgentVariable a : breathingArray) { AgentVariable father =
	 * a.getDfsFather(); if (father != null) { if (!temp.contains(father) &&
	 * !color.get(father)) { temp.add(father); } } color.put(a, true); while (father
	 * != null) { if (a.getNeigborSetId().contains(father.getId())) {
	 * a.putInDfsAboveMap(father.getId(), 0); }
	 * 
	 * father = father.getDfsFather(); } }
	 * 
	 * return temp; }
	 * 
	 */
	/*
	 * private List<AgentVariable> getAllLeaves() { List<AgentVariable> ans = new
	 * ArrayList<AgentVariable>(); for (AgentVariable a : agents) { if
	 * (a.dfsSonsSize() == 0) { ans.add(a); } } return ans; }
	 * 
	 */
	/*
	 * private boolean nonColored(Map<AgentVariable, Boolean> color) { for (Boolean
	 * colored : color.values()) { if (!colored) { return true; } } return false; }
	 * 
	 */

}
