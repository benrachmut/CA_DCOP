package AgentsAbstract;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Messages.MsgAlgorithm;
import Messages.MsgReceive;

public abstract class AgentVariableSearch extends AgentVariable {

	protected SortedMap<Integer, MsgReceive<Integer>> neighborsVariables; // id, variable

	public AgentVariableSearch(int dcopId, int id1, int D) {
		super(dcopId, D, id1);
		//this.neighborsVariables = new TreeMap<Integer, MsgReceive>();
	}
	
	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsVariables.put(neighborId, null);
	}
	
	
	@Override
	public void resetAgent() {
		
		super.resetAgent();
		this.neighborsVariables = Agent.resetMapToValueNull(this.neighborsVariables);

	}



	
}
