package AlgorithmSearch;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
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
			
			if (MainSimulator.isAMDLSDistributedDebug && this.id==0) {
				System.out.println(this.toString()+" color is:"+this.myColor+" and sends its color");
			}
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

		super.updateMessageInContext(msgAlgorithm);

		
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
				System.out.println(this+" got color msg from A_"+msgAlgorithm.getSenderId().getId1());
			}
			
		}
	}

	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgAMDLS && !this.isWaitingToSetColor && allNeighborsHaveColor()) {
			super.changeRecieveFlagsToTrue(msgAlgorithm);
		}
		if (msgAlgorithm instanceof MsgAMDLSColor) {
			if (canSetColor() && this.isWaitingToSetColor) {
				canSetColorFlag = true;
				if (MainSimulator.isAMDLSDistributedDebug) {
					System.out.println(this+" will be set color");
				}
			}
		}
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
			if(!neighborsThatHaveColor.contains(nodeId)) {
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
			if (nodeId.getId1()<this.id ) {
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
		return canSetColorFlag || consistentFlag;
	}

	protected boolean compute() {
		if (canSetColorFlag && isWaitingToSetColor) {
			chooseColor();
			setAboveAndBelow();
			isWaitingToSetColor = false;
			if (MainSimulator.isAMDLSDistributedDebug) {
				System.out.println(this+" color is: "+this.myColor);
			}
		}

		if (consistentFlag) {
			if (MainSimulator.isAMDLSDistributedDebug) {
				System.out.println(this+" has a consistent stage");
			}
			this.myCounter = this.myCounter + 1;
			this.valueAssignment = getCandidateToChange_A();
		}
		return true;
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

	@Override
	protected void sendMsgs() {
		if (this.consistentFlag) {
			sendAMDLSmsgs();
		} else if (this.canSetColorFlag) {
			sendAMDLSColorMsgs();
		}
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		this.consistentFlag = false;
		this.canSetColorFlag = false;
	}

}
