package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.org.apache.bcel.internal.generic.LREM;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgLR;
import Messages.MsgValueAssignmnet;

public class MGM_SY extends MGM {
	private SortedMap<NodeId, Boolean> isFromNeighborLr;
	private SortedMap<NodeId, Boolean> isFromNeighborValueAssignmnent;
	// private int currentIteration;
	// private Collection<MsgLR> futureMsgs;
	private boolean currentPhaseWaitForVA;

	public MGM_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.isWithTimeStamp = false;
		resetAgentGivenParametersV4();
		updateAlgorithmName();
	}

	@Override
	protected void resetAgentGivenParametersV4() {
		this.isWithTimeStamp = false;

		resetFromNeighborLr();
		resetFromNeighborValueAssignmnent();
		currentPhaseWaitForVA = true;
		this.isWithTimeStamp = true;
		// this.currentIteration = 0;
		// futureMsgs = new ArrayList<MsgLR>();

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
	protected void updateMessageInContext(MsgAlgorithm m) {
		boolean shouldUpdateInContext1 = (m instanceof MsgLR && this.currentPhaseWaitForVA == false);
		boolean shouldUpdateInContext2 = (m instanceof MsgValueAssignmnet
				&& this.currentPhaseWaitForVA == true);
		boolean shouldUpdateInContext = shouldUpdateInContext1 || shouldUpdateInContext2;

		if (shouldUpdateInContext) {
			super.updateMessageInContext(m);
		}else {
			MsgAlgorithm copiedM = null;
			if (m instanceof MsgLR) {
				copiedM = new MsgLR(m);
			}else {
				copiedM = new MsgValueAssignmnet(m);

			}
			mailer.sendMsgWitoutDelay(copiedM);
			m.setContext(null);
		}

	}

	
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm.getContext() != null) {
			NodeId sender = msgAlgorithm.getSenderId();
			int msgTimestamp = msgAlgorithm.getTimeStamp();
			if (msgAlgorithm instanceof MsgLR) {
				this.isFromNeighborLr.put(sender, true);
				checkIfCanComputeVA();
			}
			if (msgAlgorithm instanceof MsgValueAssignmnet) {
				this.isFromNeighborValueAssignmnent.put(sender, true);
				checkIfCanComputeLR();
			}	
		}	
	}
	
	
	@Override
	protected void changeRecieveFlagsToFalse() {
		if (this.computeLr) {
			this.computeLr = false;
			resetFromNeighborValueAssignmnent();
			currentPhaseWaitForVA = false;
		}
		if (this.computeVA) {
			this.computeVA = false;
			resetFromNeighborLr();
			currentPhaseWaitForVA = true;

			
		}
/*
		this.currentIteration = this.currentIteration + 1;
		for (MsgLR msg : futureMsgs) {
			if (msg.getTimeStamp() > this.currentIteration) {
				throw new RuntimeException();
			}
			this.updateMessageInContextAndTreatFlag(msg);
		}
		futureMsgs = new ArrayList<MsgLR>();
*/
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
