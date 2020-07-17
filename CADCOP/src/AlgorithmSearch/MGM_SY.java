package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgLR;
import Messages.MsgValueAssignmnet;

public class MGM_SY extends MGM {
	private SortedMap<NodeId, Boolean> isFromNeighborLr;
	private SortedMap<NodeId, Boolean> isFromNeighborValueAssignmnent;
	private int currentIteration;
	private Collection<MsgLR> futureMsgs;

	public MGM_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.isWithTimeStamp = false;
		resetAgentGivenParametersV4();

	}

	@Override
	protected void resetAgentGivenParametersV4() {
		this.isWithTimeStamp = false;

		resetFromNeighborLr();
		resetFromNeighborValueAssignmnent();

		this.isWithTimeStamp = false;
		this.currentIteration = 0;
		futureMsgs = new ArrayList<MsgLR>();

	}

	private void resetFromNeighborValueAssignmnent() {
		this.isFromNeighborValueAssignmnent = new TreeMap<NodeId, Boolean>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			this.isFromNeighborValueAssignmnent.put(nodeId, false);
		}

	}

	private void resetFromNeighborLr() {
		this.isFromNeighborLr = new TreeMap<NodeId, Boolean>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			this.isFromNeighborLr.put(nodeId, false);
		}
	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "MGM_SY";

	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		if (this.computeLr) {
			this.computeLr = false;
			resetFromNeighborValueAssignmnent();
		}
		if (this.computeVA) {
			this.computeVA = false;
			resetFromNeighborLr();
		}
		
		this.currentIteration = this.currentIteration + 1;
		for (MsgLR msg : futureMsgs) {
			if (msg.getTimeStamp() > this.currentIteration) {
				throw new RuntimeException();
			}
			this.updateMessageInContextAndTreatFlag(msg);
		}
		futureMsgs = new ArrayList<MsgLR>();

	}
	


	@Override
	protected void updateRecieveMsgFlagTrue(MsgAlgorithm msgAlgorithm) {
		NodeId sender = msgAlgorithm.getSenderId();
		int msgTimestamp = msgAlgorithm.getTimeStamp();

		if (msgTimestamp == currentIteration) {
			if (msgAlgorithm instanceof MsgLR) {
				this.isFromNeighborLr.put(sender, true);
				checkIfCanComputeVA();
			}
			if (msgAlgorithm instanceof MsgValueAssignmnet) {
				this.isFromNeighborValueAssignmnent.put(sender, true);
				checkIfCanComputeLR();
			}
		}else {
			if (msgTimestamp > 1 || msgAlgorithm instanceof MsgLR == false) {
				throw new RuntimeException();
			}
			this.futureMsgs.add((MsgLR)msgAlgorithm);
		}
	}

	private void checkIfCanComputeLR() {
		for (Boolean b : this.isFromNeighborValueAssignmnent.values()) {
			if (b == false) {
				return;
			}
		}
		computeLr = true;
		
	}

	private void checkIfCanComputeVA() {
		for (Boolean b : this.isFromNeighborLr.values()) {
			if (b == false) {
				return;
			}
		}
		computeVA = true;
	}
	
	


}
