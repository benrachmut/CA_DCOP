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

public class AMDLS_distributed extends AMDLS {
	public static int structureHeuristic = 1; // 1:by index, 2:delta_max, 3:delta_min
	private boolean isWaitingToSetColor;
	private Integer myColor;
	private TreeMap<NodeId, Integer> neighborColors;
	private boolean canSetColorFlag;

	public AMDLS_distributed(int dcopId, int D, int agentId) {
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
		AgentVariable.AlgorithmName = "AMDLS_d";
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

	private void chooseColor() {
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

	private boolean canSetColorInitilize() {
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

	private void sendAMDLSColorMsgs() {
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgAMDLSColor mva = new MsgAMDLSColor(this.nodeId, recieverNodeId, this.valueAssignment,
					this.timeStampCounter, this.time, this.myCounter, this.myColor);
			this.mailer.sendMsg(mva);
		}
	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Structure Heuristic";
	}

	// done
	@Override
	public void updateAlgorithmData() {
		if (structureHeuristic == 1) {
			AgentVariable.algorithmData = "Index";
		}
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		if (this.id == 8 && MainSimulator.isAMDLSDistributedDebug) {
			printAMDLSstatus();
		}

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

			if (MainSimulator.isAMDLSDistributedDebug) {
				System.out.println(this + " got color msg from A_" + msgAlgorithm.getSenderId().getId1());
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
				if (MainSimulator.isAMDLSDistributedDebug) {
					System.out.println(this + " will be set color");
				}
				chooseColor();
				setAboveAndBelow();
				isWaitingToSetColor = false;
				// releaseFutureMsgs();
			}
		}

		boolean firstCondition = !this.isWaitingToSetColor && allNeighborsHaveColor();
		if (firstCondition || canSetColorFlag) {
			super.changeRecieveFlagsToTrue(msgAlgorithm);
		}

	}

	protected boolean compute() {
		if (MainSimulator.isAMDLSDistributedDebug && this.id == 8) {
			System.out.println();
		}

		if (consistentFlag && !canSetColorFlag) {
			this.myCounter = this.myCounter + 1;
			this.valueAssignment = getCandidateToChange_A();
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

	private void setAboveAndBelow() {
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
		if (this.consistentFlag && !canSetColorFlag) {
			sendAMDLSmsgs();
			
		} else if (this.canSetColorFlag) {
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
