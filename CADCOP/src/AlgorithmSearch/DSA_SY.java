package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class DSA_SY extends DSA {
	private SortedMap<NodeId, Boolean> isNeighborInThisIteration;
	private Collection<MsgValueAssignmnet> futureMsgs;

	private int currentIteration;
	private boolean canComputeFlag;

	public DSA_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetAgentGivenParametersV4();	
	}

	public DSA_SY(int dcopId, int D, int id1, double stochastic) {
		super(dcopId, D, id1);
		this.stochastic = stochastic;
		resetAgentGivenParametersV4();
	}

	

	
	@Override
	protected void resetAgentGivenParametersV4() {
		resetNeighborRecieveInThisIteration();
		
		this.isWithTimeStamp = false;
		this.currentIteration = 0;
		resetNeighborRecieveInThisIteration();
		futureMsgs = new ArrayList<MsgValueAssignmnet>();
		canComputeFlag = false;

	}
	
	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_SY";
	}


	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		super.updateMessageInContext(msgAlgorithm);
		MsgValueAssignmnet mva = (MsgValueAssignmnet)msgAlgorithm;
		NodeId sender = mva.getSenderId();
		int msgTimestamp = mva.getTimeStamp();
		if (msgTimestamp == currentIteration) {
			this.isNeighborInThisIteration.put(sender, true);
			checkIfCanCompute();
		}else {
			if (msgTimestamp>1) {
				throw new RuntimeException();
			}
			this.futureMsgs.add(mva);
		}
	}
	
	@Override
	protected void updateRecieveMsgFlagTrue(NodeId senderId) {
		// TODO Auto-generated method stub	
	}
	
	private void checkIfCanCompute() {
		for (Boolean b : this.isNeighborInThisIteration.values()) {
			if (b == false) {
				return;
			}
		}
		canComputeFlag = true;
	}

	@Override
	protected boolean compute() {
		if (canComputeFlag) {
			return computeIfCan();
		}else {
			return false;
		}
		
	}
	
	@Override
	protected void sendMsgs() {
		if (canComputeFlag) {
			sendValueAssignmnetMsgs();
		}
	}

	
	protected void changeRecieveFlagsToFalse() {
		if (canComputeFlag) {
			canComputeFlag = false;
			resetNeighborRecieveInThisIteration();
			this.currentIteration = this.currentIteration+1;
			for (MsgValueAssignmnet mva : futureMsgs) {
				if (mva.getTimeStamp()>this.currentIteration) {
					throw new RuntimeException();
				}
				this.updateMessageInContext(mva);
			}
			futureMsgs = new ArrayList<MsgValueAssignmnet>();
		}
		
	}

	private void resetNeighborRecieveInThisIteration() {
		this.isNeighborInThisIteration = new TreeMap<NodeId, Boolean>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			this.isNeighborInThisIteration.put(nodeId, false);
		}
	}

	
	

}
