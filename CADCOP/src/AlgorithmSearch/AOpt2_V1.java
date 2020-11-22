package AlgorithmSearch;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAMDLS;
import Messages.MsgAMDLSColor;
import Messages.MsgAlgorithm;
import Messages.MsgMgm2Phase5IsBestLR;
import Messages.MsgOpt2FriendReplay;
import Messages.MsgOpt2FriendRequest;
import Messages.MsgValueAssignmnet;

public class AOpt2_V1 extends AgentVariableSearch {

	protected Set<MsgAlgorithm> future;
	protected Map<NodeId, Integer> neighborColors;
	protected Integer myCounter;
	protected Map<NodeId, Integer> neighborCounters;

	protected Map<NodeId, Integer> neighborBelowMeRequestCounter;

	protected Map<NodeId, KOptInfo> mapRecieveFriendRequest;
	protected NodeId nodeIdFriendIAskFor;

	protected Find2Opt find2OptFriendIAskFor;
	protected Integer myColor;

	protected Random rndFriendRequest;

	protected boolean waitingToSetColor;
	protected boolean waitForAnything;
	protected boolean waitForMoreColors;

	protected boolean flagCanSetColor;
	protected boolean flagAskForFriend;

	public AOpt2_V1(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		resetAgentGivenParametersV3();
	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "";
	}

	// done
	@Override
	public void updateAlgorithmData() {
		AgentVariable.algorithmData = "";
	}

	// done
	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "AOPT2";
	}

	// ------------ **reset** ------------
	@Override
	protected void resetAgentGivenParametersV3() {
		future = new HashSet<MsgAlgorithm>();
		waitingToSetColor = true;
		myColor = null;
		myCounter = 1;
		resetMapNeighborCounters();
		resetMapNeighborColors();
		resetMapRecieveFriendRequest();
		nodeIdFriendIAskFor = null;

	}

	private void resetMapNeighborCounters() {
		this.neighborCounters = new HashMap<NodeId, Integer>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			neighborCounters.put(nodeId, 0);
		}
	}

	private void resetMapNeighborColors() {
		this.neighborColors = new HashMap<NodeId, Integer>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			neighborColors.put(nodeId, null);
		}

	}

	private void resetMapRecieveFriendRequest() {
		mapRecieveFriendRequest = new HashMap<NodeId, KOptInfo>();
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			mapRecieveFriendRequest.put(nodeId, null);
		}
	}

	// ------------ **initialize** ------------
	@Override
	public void initialize() {
		if (canSetColorInitilize()) { // if the lowest index out of my neighbors can change to different heuristics
			chooseColor(); // choose the smallest possible color out of my neighbors
			sendAMDLSColorMsgs(); // send msg with color and first value assignmnets
			waitingToSetColor = false;
		}
		// super.initialize();
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

	private boolean canSetColorInitilize() {
		for (NodeId nodeId : this.neighborsConstraint.keySet()) {
			if (this.id > nodeId.getId1()) {
				return false;
			}
		}
		return true;
	}

	// ------------ **updateMessageInContext** ------------

	@Override
	protected boolean updateMessageInContext(MsgAlgorithm m) {
		NodeId sendId = m.getSenderId();

		if (m instanceof MsgAMDLSColor) {
			Integer colorFromMsg = ((MsgAMDLSColor) m).getColor();
			this.neighborColors.put(sendId, colorFromMsg);
			updateCounterAndValue(m, sendId);
		}

		if (!(m instanceof MsgAMDLSColor) && waitingToSetColor) {
			future.add(m);
		}

		if (m instanceof MsgAMDLS && !(m instanceof MsgAMDLSColor) && waitForAnything) {
			updateCounterAndValue(m, sendId);
		}

		if (m instanceof MsgAMDLS) {
			Integer counterFromMsg = ((MsgAMDLS) m).getCounter();
			this.neighborCounters.put(sendId, counterFromMsg);

			MsgValueAssignmnet vam = new MsgValueAssignmnet(m);
			super.updateMsgInContextValueAssignmnet(vam);
		}

		if (m instanceof MsgOpt2FriendRequest) {
			this.mapRecieveFriendRequest.put(m.getSenderId(), (KOptInfo) m.getContext());
		}

		if (m instanceof MsgOpt2FriendReplay) {
			this.find2OptFriendIAskFor = (Find2Opt) m.getContext();

			if (m.getSenderId().getId1() != this.nodeIdFriendIAskFor.getId1()) {
				throw new RuntimeException("recieve friend replay from someone I didnt ask for");
			}
		}

	}

	private void updateCounterAndValue(MsgAlgorithm m, NodeId sendId) {
		Integer counterFromMsg = ((MsgAMDLS) m).getCounter();
		this.neighborCounters.put(sendId, counterFromMsg);

		MsgValueAssignmnet vam = new MsgValueAssignmnet(m);
		super.updateMsgInContextValueAssignmnet(vam);

	}

	// ------------ **changeRecieveFlagsToTrue** ------------

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm m) {
		if (m instanceof MsgAMDLSColor && waitingToSetColor && canSetColor() && this.myColor == null) {
			flagCanSetColor = true;
		}

		if (m instanceof MsgAMDLSColor && this.myColor != null && allNeighborsHaveColor()
				&& (allAboveOneMore() || (this.myColor == 1 || allBelowLikeMe()))) {
			flagAskForFriend = true;
		}

		if (m instanceof MsgAMDLS && !(m instanceof MsgAMDLSColor) && waitForAnything
				&& (allAboveOneMore() || (this.myColor == 1 || allBelowLikeMe()))) {
			flagAskForFriend = true;
		}
		/*
		 * if ((m instanceof MsgAMDLS || m instanceof MsgOpt2FriendRequest) && !(m
		 * instanceof MsgAMDLSColor)) { if (waitingToReplayUponRequest &&
		 * canReplayUponRequest()) { flagReplayUponRequest = true; } return; }
		 */

		if (m instanceof MsgOpt2FriendRequest) {
		}

		if (m instanceof MsgOpt2FriendReplay) {
		}

	}

	private boolean allBelowLikeMe() {
		Set<NodeId> belowMe = getAllBelowMe();
		for (NodeId nodeId : belowMe) {
			if (this.neighborCounters.get(nodeId) == myCounter) {
				return false;
			}

		}
		return true;
	}

	private Set<NodeId> getAllBelowMe() {
		Set<NodeId> ans = new HashSet<NodeId>();
		for (Entry<NodeId, Integer> e : this.neighborColors.entrySet()) {
			if (e.getValue() > this.myColor) {
				ans.add(e.getKey());

			}
			if (e.getValue() == this.myColor) {
				throw new RuntimeException("i have neighbors with similar color");
			}
		}
		return ans;
	}

	private boolean allAboveOneMore() {
		Set<NodeId> aboveMe = getAllAboveMe();
		for (NodeId nodeId : aboveMe) {
			if (this.neighborCounters.get(nodeId) + 1 != myCounter) {
				return false;
			}

		}
		return true;
	}

	private Set<NodeId> getAllAboveMe() {
		Set<NodeId> ans = new HashSet<NodeId>();
		for (Entry<NodeId, Integer> e : this.neighborColors.entrySet()) {
			if (e.getValue() < this.myColor) {
				ans.add(e.getKey());

			}
			if (e.getValue() == this.myColor) {
				throw new RuntimeException("i have neighbors with similar color");
			}
		}
		return ans;
	}

	protected boolean canSetColor() {

		Set<NodeId> neighborsThatHaveColor = getNeighborsThatHaveColor();
		Set<NodeId> neighborsIRequireToWait = neighborsWithSmallerIndexThenMe();

		for (NodeId nodeId : neighborsIRequireToWait) {
			if (!neighborsThatHaveColor.contains(nodeId)) {
				return false;
			}
		}
		return true;
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

	// ------------ **compute** ------------

	@Override
	protected boolean compute() {
		if (flagCanSetColor) {
			chooseColor();
		}
		if (flagAskForFriend) {
			selectFriend();
		}
	}

	private KOptInfo makeMyKOptInfo() {
		return new KOptInfo(this.valueAssignment, nodeId, neighborsConstraint, domainArray,
				this.neighborsValueAssignmnet);
	}

	private void selectFriend() {
		Integer minValIRequest = Collections.min(this.neighborBelowMeRequestCounter.values());
		Set<NodeId> potentialFriends = new HashSet<NodeId>();
		for (Entry<NodeId, Integer> e : neighborBelowMeRequestCounter.entrySet()) {
			if (e.getValue() == minValIRequest) {
				potentialFriends.add(e.getKey());
			}
		}
		int i = this.rndFriendRequest.nextInt(potentialFriends.size());
		this.nodeIdFriendIAskFor = (NodeId) potentialFriends.toArray()[i];

	}

	// ------------ **sendMsgs** ------------
	@Override
	public void sendMsgs() {
		if (flagCanSetColor) {
			sendAMDLSColorMsgs();
			this.waitingToSetColor = false;
			if (!allNeighborsHaveColor()) {
				this.waitForMoreColors = true;
			} else {
				waitForMoreColors = false;
				this.waitForAnything = true;
				resetNeighborBelowMeRequestCounter();
			}
		}
		if (flagAskForFriend) {
			sendFriendRequest();
			this.waitForAnything = false;
			this.waitforFriendReplay = true;
		}

	}

	private void sendFriendRequest() {
		MsgOpt2FriendRequest m = new MsgOpt2FriendRequest(this.nodeId, this.nodeIdFriendIAskFor, makeMyKOptInfo(),
				this.timeStampCounter, this.time);
		this.mailer.sendMsg(m);
	}

	private void resetNeighborBelowMeRequestCounter() {
		this.neighborBelowMeRequestCounter = new HashMap<NodeId, Integer>();
		for (NodeId nodeId : this.getAllBelowMe()) {
			this.neighborBelowMeRequestCounter.put(nodeId, 0);
		}

	}

	private boolean allNeighborsHaveColor() {
		for (Integer i : this.neighborColors.values()) {
			if (i == null) {
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

	// ------------ **getSenderCurrentTimeStampFromContext** ------------

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDidComputeInThisIteration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeRecieveFlagsToFalse() {
		// TODO Auto-generated method stub

	}

}
