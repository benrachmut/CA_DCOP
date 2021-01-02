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
import Messages.Msg;
import Messages.MsgAMDLS;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class AMDLS_V1 extends AgentVariableSearch {

	public static boolean structureColor = true;
	public static boolean sendWhenMsgReceive = false;
	public static char typeDecision = 'a';

	protected Set<NodeId> below;
	protected Set<NodeId> above;
	protected Map<NodeId, Integer> counters;
	protected int myCounter;

	protected List<MsgAMDLS> future;
	protected boolean consistentFlag;
	protected boolean gotMsgFlag;

	public AMDLS_V1(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);

		this.below = new HashSet<NodeId>();
		this.above = new HashSet<NodeId>();
		this.myCounter = 1;
		future = new ArrayList<MsgAMDLS>();
		consistentFlag = false;
		gotMsgFlag = false;
		this.isWithTimeStamp = false;
		resetCounters();
		updateAlgorithmHeader();
		updateAlgorithmData();
		updateAlgorithmName();
	}

	// done
	@Override
	public void updateAlgorithmName() {
		
		String a = "AMDLS";
		String b = "V1";
		String c = "";
		if (AMDLS_V1.typeDecision=='A' || AMDLS_V1.typeDecision=='a') {
			c = "a";
		}
		
		if (AMDLS_V1.typeDecision=='B' || AMDLS_V1.typeDecision=='b') {
			c = "b";
		}
		
		if (AMDLS_V1.typeDecision=='C' || AMDLS_V1.typeDecision=='c') {
			c = "c";
		}
		AgentVariable.AlgorithmName = a+"_"+b+"_"+c;
	}

	// done
	@Override
	public void initialize() {
		sendAMDLSmsgs();
	}

	protected void sendAMDLSmsgs() {
		
		List<Msg>msgsToOutbox = new ArrayList<Msg>();
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgAMDLS mva = new MsgAMDLS(this.nodeId, recieverNodeId, this.valueAssignment, this.timeStampCounter,
					this.time, this.myCounter);
			msgsToOutbox.add(mva);
		}
		this.outbox.insert(msgsToOutbox);
	}

	// done
	@Override
	protected void resetAgentGivenParametersV3() {
		myCounter = 1;
		consistentFlag = false;
		future = new ArrayList<MsgAMDLS>();
		gotMsgFlag = false;
		this.isWithTimeStamp = false;

		resetCounters();
	}

	// done
	protected void resetCounters() {
		counters = new HashMap<NodeId, Integer>();
		for (NodeId nodeId : neighborsConstraint.keySet()) {
			counters.put(nodeId, 0);
		}

	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Message Frequency"+','+"Decision";
	}

	// done
	@Override
	public void updateAlgorithmData() {
		String freq = "";
		if (AMDLS_V1.sendWhenMsgReceive) {
			freq = "high";
		}else {
			freq = "low";
		}
		//-------------------------
		String t = "";
		if (AMDLS_V1.typeDecision=='A' || AMDLS_V1.typeDecision=='a') {
			t = "a";
		}
		
		if (AMDLS_V1.typeDecision=='B' || AMDLS_V1.typeDecision=='b') {
			t = "b";
		}
		
		if (AMDLS_V1.typeDecision=='C' || AMDLS_V1.typeDecision=='c') {
			t = "c";
		}
		AgentVariable.algorithmData = freq+","+t; 

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
	protected boolean updateMessageInContext(MsgAlgorithm msgAlgorithm) {
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
		return true;

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

	protected boolean checkAllNotZero() {
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
		if (consistentFlag ) {
			
			decideAndChange();
			
		}
		return true;
	}

	protected void decideAndChange() {
		this.myCounter = this.myCounter + 1;
		
		if (typeDecision == 'a'|| typeDecision == 'A') {
			this.valueAssignment = getCandidateToChange_A();
		}
		if (typeDecision == 'b'|| typeDecision == 'B') {
			this.valueAssignment = getCandidateToChange_B();
		}
		if (typeDecision == 'c' || typeDecision == 'C') {
			this.valueAssignment = getCandidateToChange_C();
		}
	}
	

	// 5
	@Override
	public void sendMsgs() {

		if ((sendWhenMsgReceive && this.gotMsgFlag) || (!sendWhenMsgReceive && this.consistentFlag)) {
		//if ((!sendWhenMsgReceive && this.consistentFlag)) {
			sendAMDLSmsgs();
		}

	}

	protected void releaseFutureMsgs() {

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

	protected boolean isBelowConsistent() {
		for (NodeId nodeId : this.below) {
			if (this.counters.get(nodeId) != this.myCounter) {
				return false;
			}
		}
		return true;
	}

	protected boolean isAboveConsistent() {
		for (NodeId nodeId : this.above) {		
				if (this.counters.get(nodeId) != this.myCounter + 1) {
					return false;
				} 
		}
		return true;
	}

	@Override
	public void changeRecieveFlagsToFalse() {
		consistentFlag = false;
		gotMsgFlag = false;
	}

	public void setBelow(Set<NodeId> below) {
		this.below.addAll(below);

	}

	public void setAbove(Set<NodeId> above) {
		this.above.addAll(above);

	}
	
	public void printAMDLSstatus() {
		System.out.println("--------------");
		System.out.println(this.toString() + " counter is " + this.myCounter + " Value is: "+this.valueAssignment);
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
/*
	@Override
	protected int numberOfAtomicActionsInComputation() {
		return this.neighborSize()+this.domainSize;
	}
*/
}
