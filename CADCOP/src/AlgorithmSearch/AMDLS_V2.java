package AlgorithmSearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Main.MailerIterations;
import Main.MainSimulator;
import Messages.MsgAMDLS;
import Messages.MsgAMDLSColor;
import Messages.MsgAlgorithm;

public class AMDLS_V2 extends AMDLS_V1 {
	public static int structureHeuristic = 1; // 1:by index, 2:delta_max, 3:delta_min
	
	protected boolean isWaitingToSetColor;
	private Integer myColor;
	private TreeMap<NodeId, Integer> neighborColors;
	protected boolean canSetColorFlag;

	public AMDLS_V2(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		myColor = null;
		isWaitingToSetColor = true;
		canSetColorFlag = false;
		resetNeighborColors();
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
	protected void resetAgentGivenParametersV3() {
		super.resetAgentGivenParametersV3();
		myColor = null;
		isWaitingToSetColor = true;
		resetNeighborColors();
		canSetColorFlag = false;
	}

	// done
	@Override
	public void initialize() {
		if (canSetColorInitilize()) {
			chooseColor();
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
		} else {
			throw new RuntimeException();
		}
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
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgAMDLSColor mva = new MsgAMDLSColor(this.nodeId, recieverNodeId, this.valueAssignment,
					this.timeStampCounter, this.time, this.myCounter, this.myColor);
			this.mailer.sendMsg(mva);
		}
	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Structure Heuristic"+","+"Message Frequency"+','+"Decision";;
	}

	// done
	@Override
	public void updateAlgorithmData() {
		String heuristic="";
		if (structureHeuristic == 1) {
			heuristic= "Index";
		}
		//-------------------------
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
		
		//-------------------------

		AgentVariable.algorithmData = heuristic+","+freq+","+t; 
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgAMDLSColor) {
			Integer colorN = ((MsgAMDLSColor) msgAlgorithm).getColor();
			neighborColors.put(msgAlgorithm.getSenderId(), colorN);
			if (this.myColor != null) {
				if (this.myColor > colorN) {
					this.above.add(msgAlgorithm.getSenderId());
				} else {
					this.below.add(msgAlgorithm.getSenderId());
				}
			}
		}

		if (!canSetColor() && this.isWaitingToSetColor) {
			MsgAMDLS m = new MsgAMDLS((MsgAMDLSColor) msgAlgorithm);
			future.add(m);
		} else {
			super.updateMessageInContext(msgAlgorithm);
		}
	}

	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {

		if (msgAlgorithm instanceof MsgAMDLSColor) {
			if (canSetColor() && this.isWaitingToSetColor) {
				canSetColorFlag = true;
				
				isWaitingToSetColor = false;
			}
		}

		boolean firstCondition = !this.isWaitingToSetColor && allNeighborsHaveColor();
		if (firstCondition || canSetColorFlag) {
			super.changeRecieveFlagsToTrue(msgAlgorithm);
		}
		
		this.gotMsgFlag = true;

	}

	protected boolean compute() {
		if (canSetColorFlag) {
			chooseColor();
			setAboveAndBelow();
		}
		if (consistentFlag && !canSetColorFlag) {
			decideAndChange();
		}

		return true;
	}

	private boolean releaseFutureMsgs_distributed() {

		Collection<MsgAlgorithm> toRelease = new HashSet<MsgAlgorithm>();
		for (MsgAlgorithm m : this.future) {

			int currentCounterInContext = this.counters.get(m.getSenderId());
			int msgCounter = ((MsgAMDLS) m).getCounter();

			if (currentCounterInContext + 1 == msgCounter) {
				toRelease.add(m);
				updateMessageInContext(m);
				changeRecieveFlagsToTrue(m);
			}
		}
		boolean ans = false;
		if (toRelease.size() != 0) {
			ans = true;
		}
		this.future.removeAll(toRelease);

		return true;
	}

	private boolean allNeighborsHaveColor() {
		Set<NodeId> allNeighbors = this.neighborsConstraint.keySet();
		for (NodeId nodeId : allNeighbors) {
			if (this.neighborColors.get(nodeId) == null) {
				return false;
			}
		}
		return true;
	}

	private boolean canSetColor() {
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
		} else {
			throw new RuntimeException();
		}
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

		if (MainSimulator.isAMDLSDistributedDebug && MailerIterations.m_iteration == 50) {
			printAMDLSstatus();
		}
		
		if (sendWhenMsgReceive) {
			return gotMsgFlag;
		}
		return canSetColorFlag || consistentFlag;
	}

	private void printAMDLSstatus() {
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

	/*
	 * if (consistentFlag && !canSetColorFlag) { private void
	 * releaseFutureMsgs_distributed() {
	 */
	@Override
	protected void sendMsgs() {
		boolean sendAllTheTime = AMDLS_V1.sendWhenMsgReceive && this.gotMsgFlag;
		
		if (sendAllTheTime || (this.consistentFlag && !canSetColorFlag)) {
			sendAMDLSmsgs();
			
		} else if (sendAllTheTime || this.canSetColorFlag) {
			sendAMDLSColorMsgs();
			this.consistentFlag = false;
			this.canSetColorFlag = false;
			if (releaseFutureMsgs_distributed()) {
				reactionToAlgorithmicMsgs();
			}
		}
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		this.consistentFlag = false;
		this.canSetColorFlag = false;
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