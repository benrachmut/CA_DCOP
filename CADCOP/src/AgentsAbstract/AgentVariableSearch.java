package AgentsAbstract;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public abstract class AgentVariableSearch extends AgentVariable {

	protected SortedMap<Integer, MsgReceive<Integer>> neighborsVariables; // id, variable

	public AgentVariableSearch(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		// this.neighborsVariables = new TreeMap<Integer, MsgReceive>();
	}

	@Override
	public void initialize() {
		// resetAgent();
		this.createVariableAssignmentMsg();
		// sendMsg(true);
	}
	// public void receiveAnytimeMessage (List<? extends MsgAnytime> messages) {---}

	private void createVariableAssignmentMsg() {
		for (NodeId reciever : this.getNeigborSetId()) {
			Msg m = new MsgValueAssignmnet(this.nodeId, reciever, this.getValueAssignmnet(), this.timeStampCounter);
			this.mailer.sendMsg(m);
		}

	}



	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsVariables.put(neighborId, null);
	}

	@Override
	public void resetAgent() {
		this.neighborsVariables = Agent.resetMapToValueNull(this.neighborsVariables);

	}

	public double getPOVcost() {
		double ans=0;
	
		for (Entry<Integer, MsgReceive<Integer>> e : this.neighborsVariables.entrySet()) {
			int nId = e.getKey();
			if (e.getValue().getContext() == null) {
				return -1;
			}
			int nValueAssignmnet = e.getValue().getContext();

			Integer[][] nConst= this.neighborsConstraint.get(nId);
			ans+=nConst[this.getValueAssignment()][nValueAssignmnet];
		}
		return ans;
	}



}
