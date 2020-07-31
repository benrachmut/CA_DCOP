package AlgorithmSearch;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public class DSA_SY extends DSA {
	private SortedMap<NodeId, Boolean> isNeighborInThisIteration;

	public DSA_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetAgentGivenParametersV4();
		updateAlgorithmName();

	}

	public DSA_SY(int dcopId, int D, int id1, double stochastic) {
		super(dcopId, D, id1);
		this.stochastic = stochastic;
		resetAgentGivenParametersV4();
		updateAlgorithmName();

	}

	@Override
	protected void resetAgentGivenParametersV4() {
		resetNeighborRecieveInThisIteration();
		this.isWithTimeStamp = true;
		// futureMsgs = new ArrayList<MsgAlgorithm>();
	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_SY";
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		int currentDateInContext = getSenderCurrentTimeStampFromContext(msgAlgorithm);
		if (msgAlgorithm.getTimeStamp() == currentDateInContext + 1 && 
				this.timeStampCounter == msgAlgorithm.getTimeStamp()) {
			super.updateMessageInContext(msgAlgorithm);
		} else {
			MsgAlgorithm copiedMsg = new MsgValueAssignmnet(msgAlgorithm);
			this.mailer.sendMsgWitoutDelay(copiedMsg);
			msgAlgorithm.setContext(null) ;
		}
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm.getContext() != null) {
			MsgValueAssignmnet mva = (MsgValueAssignmnet) msgAlgorithm;
			NodeId sender = mva.getSenderId();
			this.isNeighborInThisIteration.put(sender, true);
			checkIfCanCompute();
		}
	}

	private void checkIfCanCompute() {
		/*
		 * if (this.timeStampCounter == 1 && this.id == 3) {
		 * System.out.println(this.timeStampCounter); }
		 */
		for (Boolean b : this.isNeighborInThisIteration.values()) {
			if (b == false) {
				return;
			}
		}
		canCompute = true;
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		if (this.canCompute) {
			canCompute = false;
			resetNeighborRecieveInThisIteration();
/*
			if (!futureMsgs.isEmpty()) {
				for (MsgAlgorithm mva : futureMsgs) {
					if (mva.getTimeStamp() != this.timeStampCounter) {
						throw new RuntimeException();
					}
					super.updateMessageInContext(mva);
				}

				for (MsgAlgorithm mva : futureMsgs) {
					changeRecieveFlagsToTrue(mva);
				}

				reactionToAlgorithmicMsgs();
			}

			futureMsgs = new ArrayList<MsgAlgorithm>();
		*/
		}
	}

	private void resetNeighborRecieveInThisIteration() {
		this.isNeighborInThisIteration = new TreeMap<NodeId, Boolean>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			this.isNeighborInThisIteration.put(nodeId, false);
		}
	}

	public String getStringForDebug() {
		String ans = "";
		ans = ans + this.rndForDebug + ",";
		ans = ans + this.valueAssignment + ",";

		for (Entry<NodeId, MsgReceive<Integer>> e : this.neighborsValueAssignmnet.entrySet()) {
			int context;
			int timeStamp;
			MsgReceive<Integer> mva = e.getValue();
			if (mva == null) {
				context = -1;
				timeStamp = -1;
			} else {
				Object context1 = e.getValue().getContext();
				context = (int) context1;
				timeStamp = e.getValue().getTimestamp();
			}
			ans = ans + context + "," + timeStamp + ",";
		}
		return ans;
	}

}
