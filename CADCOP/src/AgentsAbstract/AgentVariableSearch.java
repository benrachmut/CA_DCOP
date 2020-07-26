package AgentsAbstract;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public abstract class AgentVariableSearch extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<Integer>> neighborsValueAssignmnet; // id, variable

	public AgentVariableSearch(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.neighborsValueAssignmnet = new TreeMap<NodeId, MsgReceive<Integer>>();
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
			Msg m = new MsgValueAssignmnet(this.nodeId, reciever, this.getValueAssignment(), this.timeStampCounter);
			this.mailer.sendMsg(m);
		}

	}

	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsValueAssignmnet.put(new NodeId(neighborId), null);
	}

	@Override
	public void resetAgentGivenParametersV2() {
		this.neighborsValueAssignmnet = Agent
				.<NodeId, MsgReceive<Integer>>resetMapToValueNull(this.neighborsValueAssignmnet);
		resetAgentGivenParametersV3();
	}

	protected abstract void resetAgentGivenParametersV3();

	public double getCostPov() {
		return getCostPerInput(this.valueAssignment);
	}
	
	public int getCostPerInput(int input) {
		int ans = 0;
		for (Entry<NodeId, MsgReceive<Integer>> e : this.neighborsValueAssignmnet.entrySet()) {
			 
			if (e.getValue() != null) {
				Object context = e.getValue().getContext();
				int nValueAssignmnet = e.getValue().getContext();
				
				Integer[][] nConst = this.neighborsConstraint.get(e.getKey());
				ans += nConst[input][nValueAssignmnet];
			}
		}
		return ans;
	}



	protected SortedMap<Integer, Integer> getCostPerDomain() {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();
		for (int domainCandidate : domainArray) {
			int sumCostPerAgent = this.getCostPerInput(domainCandidate);
			ans.put(domainCandidate, sumCostPerAgent);
		}
		return ans;
	}

	protected int getCandidateToChange() {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int minCost = Collections.min(costPerDomain.values());
		int costOfCurrentValue = costPerDomain.get(this.valueAssignment);
		if (minCost < costOfCurrentValue) {
			return getAlternativeCandidate(minCost, costPerDomain);
		}
		return this.valueAssignment;
	}

	private int getAlternativeCandidate(int minCost, SortedMap<Integer, Integer> costPerDomain) {
		for (Entry<Integer, Integer> e : costPerDomain.entrySet()) {
			if (e.getValue() == minCost && e.getKey() != this.valueAssignment) {
				return e.getKey();
			}
		}
		throw new RuntimeException();
	}

	protected void updateMsgInContextValueAssignmnet(MsgAlgorithm msgAlgorithm) {
		Integer context = (Integer) msgAlgorithm.getContext();
		int timestamp = msgAlgorithm.getTimeStamp();
		MsgReceive<Integer> msgReceive = new MsgReceive<Integer>(context, timestamp);
		this.neighborsValueAssignmnet.put(msgAlgorithm.getSenderId(), msgReceive);
	}

	protected int getTimestampOfValueAssignmnets(MsgAlgorithm msgAlgorithm) {
		NodeId senderNodeId = msgAlgorithm.getSenderId();
		MsgReceive<Integer> msgReceive = this.neighborsValueAssignmnet.get(senderNodeId);
		if (msgReceive == null) {
			return -1;
		}
		return msgReceive.getTimestamp();
	}

	protected void sendValueAssignmnetMsgs() {
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgValueAssignmnet mva = new MsgValueAssignmnet(this.nodeId, recieverNodeId, this.valueAssignment,
					this.timeStampCounter);
			this.mailer.sendMsg(mva);
		}

	}

}
