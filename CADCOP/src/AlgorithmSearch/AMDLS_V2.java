package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAMDLS;
import Messages.MsgAMDLSColor;
import Messages.MsgAlgorithm;

public class AMDLS_V2 extends AMDLS_V1 {
	public static int structureHeuristic = 1; // 1:by index, 2:delta_max, 3:delta_min
	protected boolean isWaitingToSetColor;
	protected Integer myColor;
	private TreeMap<NodeId, Integer> neighborColors;
	protected boolean canSetColorFlag;

	private Collection<MsgAlgorithm>future;
	public AMDLS_V2(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		myColor = null;
		isWaitingToSetColor = true;
		resetNeighborColors();
		future = new ArrayList<MsgAlgorithm>();
	}

	private void resetNeighborColors() {
		neighborColors = new TreeMap<NodeId, Integer>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			neighborColors.put(nodeId, null);
		}
	}

	@Override
	public void updateAlgorithmName() {
		String a = "AMDLS";
		String b = "V2";
		String c = "";
		if (AMDLS_V1.typeDecision == 'A' || AMDLS_V1.typeDecision == 'a') {
			c = "a";
		}

		if (AMDLS_V1.typeDecision == 'B' || AMDLS_V1.typeDecision == 'b') {
			c = "b";
		}

		if (AMDLS_V1.typeDecision == 'C' || AMDLS_V1.typeDecision == 'c') {
			c = "c";
		}
		AgentVariable.AlgorithmName = a + "_" + b + "_" + c;

	}

	// done
	@Override
	protected void resetAgentGivenParametersV3() {
		super.resetAgentGivenParametersV3();
		myColor = null;
		isWaitingToSetColor = true;
		resetNeighborColors();
		canSetColorFlag = false;
		future = new ArrayList<MsgAlgorithm>();
	}

	// done
	@Override
	public void initialize() {

		if (canSetColorInitilize()) {
			chooseColor();
			this.myCounter = 1;
			sendAMDLSColorMsgs();
			isWaitingToSetColor = false;
		}
	}

	protected void chooseColor() {
		Integer currentColor = 1;
		while (true) {
			if (isColorValid(currentColor)) {
				break;
			}
			currentColor = currentColor + 1;
		}
		this.myColor = currentColor;
	}

	private boolean isColorValid(Integer currentColor) {
		for (Integer nColor : neighborColors.values()) {
			if (nColor != null) {
				if (nColor.equals(currentColor)) {
					return false;
				}
			}
		}
		return true;
	}

	protected boolean canSetColorInitilize() {
		if (structureHeuristic == 1) {
			return determineByIndexInit();
		}
		if (structureHeuristic == 2) {
			return determineByMaxNeighborInit();
		}
		if (structureHeuristic == 3) {
			return determineByMinNeighborInit();
		} else {
			throw new RuntimeException();
		}
	}

	private boolean determineByMinNeighborInit() {
		return mailer.isMinOfItsNeighbors(this);
	}

	private boolean determineByMaxNeighborInit() {
		return mailer.isMaxOfItsNeighbors(this);
	}

	private boolean determineByIndexInit() {
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			if (this.id > nodeId.getId1()) {
				return false;
			}
		}
		return true;
	}

	protected void sendAMDLSColorMsgs() {
		List<Msg> msgsToOutbox = new ArrayList<Msg>();

		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgAMDLSColor mva = new MsgAMDLSColor(this.nodeId, recieverNodeId, this.valueAssignment,
					this.timeStampCounter, this.time, this.myCounter, this.myColor);

			msgsToOutbox.add(mva);

		}
		outbox.insert(msgsToOutbox);

	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Structure Heuristic" + "," + "Message Frequency" + ',' + "Decision";
		;
	}

	// done
	@Override
	public void updateAlgorithmData() {
		String heuristic = "";
		if (structureHeuristic == 1) {
			heuristic = "Index";
		}

		if (structureHeuristic == 2) {
			heuristic = "Max Neighbor";
		}

		if (structureHeuristic == 3) {
			heuristic = "Min Neighbor";
		}
		// -------------------------
		String freq = "";
		if (AMDLS_V1.sendWhenMsgReceive) {
			freq = "high";
		} else {
			freq = "low";
		}
		// -------------------------
		String t = "";
		if (AMDLS_V1.typeDecision == 'A' || AMDLS_V1.typeDecision == 'a') {
			t = "a";
		}

		if (AMDLS_V1.typeDecision == 'B' || AMDLS_V1.typeDecision == 'b') {
			t = "b";
		}

		if (AMDLS_V1.typeDecision == 'C' || AMDLS_V1.typeDecision == 'c') {
			t = "c";
		}

		// -------------------------

		AgentVariable.algorithmData = heuristic + "," + freq + "," + t;
	}

	@Override
	protected boolean updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		NodeId sender = msgAlgorithm.getSenderId();
		if (msgAlgorithm instanceof MsgAMDLSColor) {
			Integer colorN = ((MsgAMDLSColor) msgAlgorithm).getColor();
			neighborColors.put(sender, colorN);
			if (this.myColor != null) {
				if (this.myColor > colorN) {
					this.above.add(msgAlgorithm.getSenderId());
				} else {
					this.below.add(msgAlgorithm.getSenderId());
				}
			}
		}

		if (!(msgAlgorithm instanceof MsgAMDLSColor) && (this.myColor == null && neighborColors.get(msgAlgorithm.getSenderId())!=null)) {
			this.future.add(msgAlgorithm);
			return true;
		}

		if (!(msgAlgorithm instanceof MsgAMDLSColor) && this.myColor != null || (
				msgAlgorithm instanceof MsgAMDLSColor
				)) {
			int currentCounterInContext = this.counters.get(sender);
			int msgCounter = ((MsgAMDLS) msgAlgorithm).getCounter();
			if (currentCounterInContext + 1 == msgCounter) {
				updateMsgInContextValueAssignmnet(msgAlgorithm);
				this.counters.put(sender, msgCounter);
			}
			return true;
		}
		else {
			throw new RuntimeException ();
		}

	}

	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {

		if (this.myColor == null && msgAlgorithm instanceof MsgAMDLSColor && canSetColor()) {
			canSetColorFlag = true;
		}

		boolean allHaveColor = checkAllHaveColor();
		boolean aboveConsistent = isAboveConsistent();
		boolean belowConsistent = isBelowConsistent();
		if (aboveConsistent && belowConsistent && allHaveColor) {
			this.consistentFlag = true;
		}

	}

	protected boolean checkAllHaveColor() {

		for (Integer i : this.neighborColors.values()) {
			if (i == null) {
				return false;
			}
		}
		return true;
	}
	/*
	 * protected void changeRecieveFlagsToTrueMsgAMDLSColor() { if (canSetColor() &&
	 * this.isWaitingToSetColor) { canSetColorFlag = true; this.gotMsgFlag = true;
	 * isWaitingToSetColor = false; } }
	 */

	protected boolean compute() {
		boolean flag= false;
		if (this.myColor == null && canSetColorFlag) {
			chooseColor();
			setAboveAndBelow();
			decideAndChange();
			//boolean allHaveColor = checkAllHaveColor();
			//boolean aboveConsistent = isAboveConsistent();
			//boolean belowConsistent = isBelowConsistent();
			//if (aboveConsistent && belowConsistent && allHaveColor) {
				// this.myCounter = this.myCounter + 1;
				//this.consistentFlag = true;
			//}
			flag = true;
		}

		if (consistentFlag && !flag) {
			decideAndChange();
		}

		return true;
	}

	private boolean haveCounter2() {
		for (Integer i : this.counters.values()) {
			if (i == 2) {
				return true;
			}
		}
		return false;
	}

	/*
	 * protected boolean releaseFutureMsgs_distributed() {
	 * 
	 * Collection<MsgAlgorithm> toRelease = new HashSet<MsgAlgorithm>(); for
	 * (MsgAlgorithm m : this.future) {
	 * 
	 * int currentCounterInContext = this.counters.get(m.getSenderId()); int
	 * msgCounter = ((MsgAMDLS) m).getCounter();
	 * 
	 * if (currentCounterInContext + 1 == msgCounter) { toRelease.add(m);
	 * updateMessageInContext(m); changeRecieveFlagsToTrue(m); } } boolean ans =
	 * false; if (toRelease.size() != 0) { ans = true; }
	 * this.future.removeAll(toRelease);
	 * 
	 * return true; }
	 */

	/*
	 * protected boolean allNeighborsHaveColor() { Set<NodeId> allNeighbors =
	 * this.neighborsConstraint.keySet(); for (NodeId nodeId : allNeighbors) { if
	 * (this.neighborColors.get(nodeId) == null) { return false; } } return true; }
	 */

	protected boolean canSetColor() {

		Set<NodeId> neighborsThatHaveColor = getNeighborsThatHaveColor();
		Set<NodeId> neighborsIRequireToWait = getNeighborsIRequireToWait();

		for (NodeId nodeId : neighborsIRequireToWait) {
			if (!neighborsThatHaveColor.contains(nodeId)) {
				return false;
			}
		}
		return true;
	}

	private Set<NodeId> getNeighborsIRequireToWait() {
		if (structureHeuristic == 1) {
			return neighborsWithSmallerIndexThenMe();
		}
		if (structureHeuristic == 2) {
			return neighborsWithMoreNeighborsThenMe();
		}
		if (structureHeuristic == 3) {
			return neighborsWithLessNeighborsThenMe();
		}

		else {
			throw new RuntimeException();
		}
	}

	private Set<NodeId> neighborsWithLessNeighborsThenMe() {
		return mailer.getNeighborsWithLessNeighborsThenMe(this);
	}

	private Set<NodeId> neighborsWithMoreNeighborsThenMe() {
		return mailer.getNeighborsWithMoreNeighborsThenMe(this);
	}

	private Set<NodeId> neighborsWithSmallerIndexThenMe() {
		Set<NodeId> ans = new HashSet<NodeId>();
		for (NodeId nodeId : neighborsConstraint.keySet()) {
			if (nodeId.getId1() < this.id) {
				ans.add(nodeId);
			}
		}
		return ans;
	}

	private Set<NodeId> getNeighborsThatHaveColor() {
		Set<NodeId> ans = new HashSet<NodeId>();
		for (Entry<NodeId, Integer> e : this.neighborColors.entrySet()) {
			if (e.getValue() != null) {
				ans.add(e.getKey());
			}
		}
		return ans;
	}

	public boolean getDidComputeInThisIteration() {

		if (MainSimulator.isAMDLSDistributedDebug && this.id == 5) {
			printAMDLSstatus();
			System.out.println(this + " compute in this iteration: " + (canSetColorFlag || consistentFlag));
		}
		/*
		 * if (sendWhenMsgReceive && canSetColor()) { return gotMsgFlag; }
		 */
		return canSetColorFlag || consistentFlag;
	}

	protected void setAboveAndBelow() {
		for (Entry<NodeId, Integer> e : this.neighborColors.entrySet()) {
			if (e.getValue() != null) {
				if (this.myColor > e.getValue()) {
					this.above.add(e.getKey());
				} else {
					this.below.add(e.getKey());
				}
			}
		}
	}

	public void sendMsgs() {
		boolean sendAllTheTime = AMDLS_V1.sendWhenMsgReceive && this.gotMsgFlag;
		boolean flag = false;
		if (this.canSetColorFlag) {
			sendAMDLSColorMsgs();
			releaseFutureMsgs();
			if (consistentFlag) {
				reactionToAlgorithmicMsgsWithoutSendingMsgs();
			}
		}
		
		if (this.consistentFlag && canSetColorFlag) {
			//myCounter= myCounter+1;
			sendAMDLSmsgs();
		}
		
		
		if (this.consistentFlag && !canSetColorFlag) {
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
				updateMessageInContextAndTreatFlag(m);
			}
		}
		this.future.removeAll(toRelease);
	}
	@Override
	public void changeRecieveFlagsToFalse() {
		this.consistentFlag = false;
		this.canSetColorFlag = false;
		gotMsgFlag = false;
	}

	public double getIfColor() {
		if (isWaitingToSetColor) {
			return 0.0;
		} else {
			return 1.0;
		}
	}

	public Integer getColorNumber() {
		return this.myColor;
	}

	public Integer getColor() {
		// TODO Auto-generated method stub
		return myColor;
	}

}
