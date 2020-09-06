package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;

public class DSA_SDP_SY extends DSA_SDP {

	private List<MsgAlgorithm> future;

	public DSA_SDP_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		updateAlgorithmName();
		resetAgentGivenParametersV5();
	}
	
	@Override
	protected void resetAgentGivenParametersV5() {
		this.isWithTimeStamp = false;
		this.future = new ArrayList<MsgAlgorithm>();
	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_SDP_SY";
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		
		if (this.timeStampCounter == msgAlgorithm.getTimeStamp()) {
			
			super.updateMessageInContext(msgAlgorithm);
		} else {
			
			this.future.add(msgAlgorithm);
		}
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		for (MsgReceive<Integer> m : this.neighborsValueAssignmnet.values()) {
			int msgTimestamp = 0;
			if (m == null) {
				return;
			} else {
				msgTimestamp = m.getTimestamp();
			}
			if (msgTimestamp != this.timeStampCounter) {
				return;
			}
		}
		canCompute = true;
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
			canCompute = false;
		
	}

	@Override
	protected void sendMsgs() {
			sendValueAssignmnetMsgs();
			releaseFutureMsgs();	
	}
	private void releaseFutureMsgs() {
		Collection<MsgAlgorithm> toRelease = new HashSet<MsgAlgorithm>();
		for (MsgAlgorithm m : this.future) {
			if (m.getTimeStamp() == this.timeStampCounter) {
				toRelease.add(m);
				updateMessageInContext(m);

			}
		}
		this.future.removeAll(toRelease);
	}
}
