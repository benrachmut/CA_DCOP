package Agents;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Messages.MsgAlgorithm;
import Messages.MsgReceive;

public abstract class AgentSearch extends AgentVariable<Integer, Integer> {

	protected SortedMap<Integer, MsgReceive<Integer>> neighborsVariables; // id, variable

	public AgentSearch(int dcopId, int id1, int D) {
		super(dcopId, id1, D);
		//this.neighborsVariables = new TreeMap<Integer, MsgReceive>();
	}
	
	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsVariables.put(neighborId, new MsgReceive<Integer>(-1,-1));
	}
	
	
	@Override
	public void resetAgent() {
		
		super.resetAgent();
		this.neighborsVariables = Agent.resetMapToValueNull(this.neighborsVariables);

	}

	@Override
	public void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm<Integer, Integer>> messages) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void sendContextMsgs(boolean changeContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleAlgorithmicMsgs() {
		// TODO Auto-generated method stub
		
	}


	
}
