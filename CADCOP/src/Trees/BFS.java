package Trees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;

public class BFS extends Tree{

	public BFS(AgentVariable[] agentsArray, Comparator<AgentVariable> c) {
		super(agentsArray, c);
		// TODO Auto-generated constructor stub
	}
	
	
	public void createTree() {
		while (someOneIsNotColored()) {
			AgentVariable firstNotVisited = findFirstNotVisited();
			createTree(firstNotVisited);
		}

	}

	private void createTree(AgentVariable firstNotVisited) {

		AgentVariable current = firstNotVisited;
		List<AgentVariable> q = getSons(current);
	
		for (AgentVariable a : q) {
			current.addBfsSon(a.getId());
			a.setBfsFather(current.getNodeId());
		}
		
		this.visited.put(current, true);
		Iterator<AgentVariable> it = q.iterator();
		while (it.hasNext()) {
			current = it.next();
			List<AgentVariable> temp = getSons(current);
			List<AgentVariable> toAdd = getSonsToQueue(temp, current, q);
			this.visited.put(current, true);
			it.remove();
			q.addAll(toAdd);
			it = q.iterator();
		}
	}

	private List<AgentVariable> getSonsToQueue(List<AgentVariable> temp, AgentVariable current, List<AgentVariable> q) {
		List<AgentVariable> ans = new ArrayList<AgentVariable>();
		for (AgentVariable a : temp) {
			if (!this.visited.get(a) && !q.contains(a)) {
				ans.add(a);
				current.addBfsSon(a.getId());
				a.setBfsFather(current.getNodeId());
			}
		}
		return ans;

	}


	@Override
	protected void setLevelPerAgent(AgentVariable a, int level) {
		a.setBfsLevelInTree(level);
		
	}


	@Override
	protected Set<NodeId> getSonsIds(AgentVariable a) {
		// TODO Auto-generated method stub
		return a.getBfsSonsIds();
	}


	@Override
	protected Set<AgentVariable> getHeads() {
		Set<AgentVariable> ans = new HashSet<AgentVariable>();
		for (AgentVariable a : agentsVector) {
			if (a.getBfsFather().getId1() == -1) {
				ans.add(a);
			}
		}
		return ans;
	}


	@Override
	protected void informEqual(AgentVariable a, Set<NodeId> equalA) {
		
		a.setAboveBFS(equalA);
		
	}

	@Override
	protected void informAbove(AgentVariable a, Set<NodeId> aboveA) {
		a.setAboveBFS(aboveA);
	}

	@Override
	protected void informBelow(AgentVariable a, Set<NodeId> belowA) {
		a.setBelowBFS(belowA);
	}



}
