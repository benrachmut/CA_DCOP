package AgentsAbstract;

import java.util.List;
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
		resetAgent();
		this.createVariableAssignmentMsg(0);
		// sendMsg(true);
	}
	// public void receiveAnytimeMessage (List<? extends MsgAnytime> messages) {---}

	private void createVariableAssignmentMsg(int timeCreated) {
		for (Integer reciever : this.getNeigborSetId()) {
			Msg m = new MsgValueAssignmnet(this.id, reciever, this.valueAssignment, this.timeStampCounter, timeCreated);
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

}
