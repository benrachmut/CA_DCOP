package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Main.MailerIterations;
import Main.MainSimulator;
import Messages.MsgAMDLS;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class AMDLS extends AgentVariableSearch {

	public static boolean structureColor = true;
	public static boolean sendWhenMsgReceive = false;

	private Set<NodeId> below;
	private Set<NodeId> above;
	private Map<NodeId, Integer> counters;
	private int myCounter;

	private List<MsgAMDLS> future;
	protected boolean consistentFlag;
	protected boolean gotMsgFlag;

	public AMDLS(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);

		this.below = new HashSet<NodeId>();
		this.above = new HashSet<NodeId>();
		this.myCounter = 1;
		future = new ArrayList<MsgAMDLS>();
		consistentFlag = false;
		gotMsgFlag = false;
		resetCounters();
		updateAlgorithmHeader();
		updateAlgorithmData();
		updateAlgorithmName();
	}

	// done
	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "AMDLS";
	}

	// done
	@Override
	public void initialize() {
		sendAMDLSmsgs();

	}

	private void sendAMDLSmsgs() {

		

		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgAMDLS mva = new MsgAMDLS(this.nodeId, recieverNodeId, this.valueAssignment, this.timeStampCounter,
					this.time, this.myCounter);
			this.mailer.sendMsg(mva);

		}

	}

	// done
	@Override
	protected void resetAgentGivenParametersV3() {
		myCounter = 1;
		consistentFlag = false;
		future = new ArrayList<MsgAMDLS>();
		gotMsgFlag = false;
		resetCounters();
	}

	// done
	private void resetCounters() {
		counters = new HashMap<NodeId, Integer>();
		for (NodeId nodeId : neighborsConstraint.keySet()) {
			counters.put(nodeId, 0);
		}

	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Version" + "," + "Structure";
	}

	// done
	@Override
	public void updateAlgorithmData() {

		if (structureColor) {
			if (sendWhenMsgReceive) {
				AgentVariable.algorithmData = "Msg Receive" + "," + "Color";

			} else {
				AgentVariable.algorithmData = "Consistent" + "," + "Color";

			}
		} else {
			if (sendWhenMsgReceive) {
				AgentVariable.algorithmData = "Msg Receive" + "," + "Pseudo Tree";

			} else {
				AgentVariable.algorithmData = "Consistent" + "," + "Pseudo Tree";
			}
		}

	}

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			return getTimestampOfValueAssignmnets(msgAlgorithm);

		} else {
			throw new RuntimeException();
		}
	}

	// 1
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
/*
		if (MainSimulator.isAMDLSdebug && this.id==19) {
			System.out.println();
		}
		*/
		NodeId sender = msgAlgorithm.getSenderId();
		int currentCounterInContext = this.counters.get(sender);
		int msgCounter = ((MsgAMDLS) msgAlgorithm).getCounter();

		if (currentCounterInContext + 1 == msgCounter) {
			updateMsgInContextValueAssignmnet(msgAlgorithm);
			this.counters.put(sender, msgCounter);
		} else {
			this.future.add((MsgAMDLS) msgAlgorithm);
		}
		/*
		 * if (this.id == 0 && this.myCounter == 1 && MainSimulator.isAMDLSdebug) {
		 * System.out.println("--------------"); System.out.println(this.toString() +
		 * " counter is " + this.myCounter); if (!this.above.isEmpty()) {
		 * System.out.println("above:"); for (NodeId nodeId : above) {
		 * System.out.print("A" + nodeId.getId1() + ":" + this.counters.get(nodeId) +
		 * ", "); } System.out.println();
		 * 
		 * }
		 * 
		 * 
		 * if (!this.below.isEmpty()) { System.out.println("below:"); for (NodeId nodeId
		 * : below) { System.out.print("A" + nodeId.getId1() + ":" +
		 * this.counters.get(nodeId) + ","); } System.out.println(); }
		 * System.out.println("--------------");
		 * 
		 * }
		 */

	}

	// 2
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {

		
		boolean allNotZero = checkAllNotZero();

		if (allNotZero) {
			releaseFutureMsgs();

		}
		boolean aboveConsistent = isAboveConsistent();
		boolean belowConsistent = isBelowConsistent();
		if (aboveConsistent && belowConsistent && allNotZero) {
			this.consistentFlag = true;
		} else {
			this.consistentFlag = false;
		}
		this.gotMsgFlag = true;
	}

	private boolean checkAllNotZero() {
		for (Integer i : this.counters.values()) {
			if (i == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean getDidComputeInThisIteration() {
		/*
		if (MainSimulator.isAMDLSdebug && MailerIterations.m_iteration == 50) {
			
			System.out.println("--------------");
			System.out.println(this.toString() + " counter is " + this.myCounter);
			if (!this.above.isEmpty()) {
				System.out.println("above:");
				for (NodeId nodeId : above) {
					System.out.print("A" + nodeId.getId1() + ":" + this.counters.get(nodeId) + ", ");
				}
				System.out.println();

			}

			if (!this.below.isEmpty()) {
				System.out.println("below:");
				for (NodeId nodeId : below) {
					System.out.print("A" + nodeId.getId1() + ":" + this.counters.get(nodeId) + ",");
				}
				System.out.println();

			}
			System.out.println();

		}
		*/
		
		if (sendWhenMsgReceive) {
			return gotMsgFlag;
		} else {
			return consistentFlag;
		}
	}

	// 4
	@Override
	protected boolean compute() {
		if (consistentFlag) {
			this.myCounter = this.myCounter + 1;
			this.valueAssignment = getCandidateToChange_A();
		}
		return true;
	}

	// 5
	@Override
	protected void sendMsgs() {

		if ((sendWhenMsgReceive && this.gotMsgFlag) || (!sendWhenMsgReceive && this.consistentFlag)) {
			sendAMDLSmsgs();
		}

	}

	private void releaseFutureMsgs() {

		Collection<MsgAlgorithm> toRelease = new HashSet<MsgAlgorithm>();
		for (MsgAlgorithm m : this.future) {

			int currentCounterInContext = this.counters.get(m.getSenderId());
			int msgCounter = ((MsgAMDLS) m).getCounter();

			if (currentCounterInContext + 1 == msgCounter) {
				toRelease.add(m);
				updateMessageInContext(m);

			}
		}
		this.future.removeAll(toRelease);

	}

	private boolean isBelowConsistent() {
		for (NodeId nodeId : this.below) {
			if (this.counters.get(nodeId) != this.myCounter) {
				return false;
			}
		}
		return true;
	}

	private boolean isAboveConsistent() {
		for (NodeId nodeId : this.above) {		
				if (this.counters.get(nodeId) != this.myCounter + 1) {
					return false;
				} 
		}
		return true;
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		consistentFlag = false;
		gotMsgFlag = false;
	}

	public void setBelow(Set<NodeId> below) {
		this.below.addAll(below);

	}

	public void setAbove(Set<NodeId> above) {
		this.above.addAll(above);

	}

}
