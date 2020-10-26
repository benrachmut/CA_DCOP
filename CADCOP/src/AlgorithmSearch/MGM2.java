package AlgorithmSearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgLR;
import Messages.MsgMgm2Phase1Friendship;
import Messages.MsgMgm2Phase2FriendshipReplay;
import Messages.MsgMgm2Phase2LR;
import Messages.MsgMgm2Phase3IsBestGain;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

abstract public class MGM2 extends AgentVariableSearch {

	public static int repsOfMsgSentInPhase3Phase4 = 1;

	// ##---------Phase 1---------##
	protected Random neighborRndPhase1;
	protected Random isOfferGiverRndPhase1;
	protected boolean isOfferGiverPhase1;
	protected Map<NodeId, MsgReceive<KOptInfo>> friendshipOffersPhase1;
	protected Map<NodeId, Boolean> friendshipOffersBooleanPhase1;
	protected NodeId selectedFriendNodeIdPhase1;
	// ##---------Phase 2---------##
	protected Random neighborRndPhase2;
	protected NodeId acceptedFriendPhase2;
	protected int LR_phase2;
	protected Set<NodeId> whoAskedToBeFriendWithMePhase2;
	protected Find2Opt find2OptPhase2;
	protected Integer myValueAssignmnetPotentialPhase2;
	protected Integer friendValueAssignmnetPotentialPhase2;
	protected Map<NodeId, MsgReceive<Integer>> neighborsLR_phase2; // id, variable
	protected Map<NodeId, Boolean> neighborsLR_booleanPhase2;
	// ##---------Phase 3---------##

	protected Find2Opt infoReplayFromFriendPhase3;

	// ##---------Phase 4---------##
	protected boolean isAcceptPhase4;

	public MGM2(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetFields();

	}

	@Override
	protected void resetAgentGivenParametersV3() {
		resetFields();
		resetAgentGivenParametersV4();
	}

	private void resetFields() {
		// ** ---phase 1--- **
		isOfferGiverRndPhase1 = new Random(id * 105 + dcopId * 10);
		neighborRndPhase1 = new Random(id * 132 + dcopId * 15);
		isOfferGiverPhase1 = false;
		selectedFriendNodeIdPhase1 = null;
		resetFriendshipOffersPhase1();

		// ** ---phase 2--- **
		neighborRndPhase2 = new Random(id * 145 + dcopId * 36);
		whoAskedToBeFriendWithMePhase2 = new HashSet<NodeId>();
		acceptedFriendPhase2 = null;
		LR_phase2 = Integer.MAX_VALUE;
		find2OptPhase2 = null;
		myValueAssignmnetPotentialPhase2 = null;
		friendValueAssignmnetPotentialPhase2 = null;
		resetNeighborsLR_phase2();

		// ** ---phase 3--- **
		infoReplayFromFriendPhase3 = null;

		// ** ---phase 4--- **
		isAcceptPhase4 = false;
	}

	private void resetNeighborsLR_phase2() {
		neighborsLR_phase2 = new HashMap<NodeId, MsgReceive<Integer>>(); // id, variable
		neighborsLR_booleanPhase2 = new HashMap<NodeId, Boolean>();
		for (NodeId nodeId : this.getNeigborSetId()) {
			neighborsLR_phase2.put(nodeId, null);
			neighborsLR_booleanPhase2.put(nodeId, false);
		}
	}

	private void resetFriendshipOffersPhase1() {

		friendshipOffersPhase1 = new HashMap<NodeId, MsgReceive<KOptInfo>>();
		friendshipOffersBooleanPhase1 = new HashMap<NodeId, Boolean>();
		for (NodeId nodeId : this.getNeigborSetId()) {
			friendshipOffersPhase1.put(nodeId, null);
			friendshipOffersBooleanPhase1.put(nodeId, false);
		}

	}

	protected abstract void resetAgentGivenParametersV4();

	private KOptInfo makeMyKOptInfo() {
		// TODO Auto-generated method stub
		return new KOptInfo(this.valueAssignment, nodeId, neighborsConstraint, domainArray,
				this.neighborsValueAssignmnet);
	}

	// ------------------** phase 1 **------------------
	protected void recieveMsgPhase1(MsgAlgorithm m) {
		updateMsgInContextValueAssignmnet(m);
	}

	// offer
	protected boolean computePhase1() {
		double rnd = isOfferGiverRndPhase1.nextDouble();
		if (rnd < 0.5) {
			this.isOfferGiverPhase1 = true;
		} else {
			this.isOfferGiverPhase1 = false;
		}
		return true;
	}

	protected void sendPhase1() {
		if (this.isOfferGiverPhase1) {
			KOptInfo myKoptInfo = makeMyKOptInfo();
			int i = this.neighborRndPhase1.nextInt(this.neighborSize());
			this.selectedFriendNodeIdPhase1 = (NodeId) this.getNeigborSetId().toArray()[i];

			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
				MsgMgm2Phase1Friendship mva = null;
				if (recieverNodeId.getId1() == selectedFriendNodeIdPhase1.getId1()) {
					mva = new MsgMgm2Phase1Friendship(this.nodeId, recieverNodeId, myKoptInfo, this.timeStampCounter,
							this.time);
				} else {
					mva = new MsgMgm2Phase1Friendship(this.nodeId, recieverNodeId, null, this.timeStampCounter,
							this.time);
				}
				this.mailer.sendMsg(mva);
			}

		} else {
			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {

				MsgMgm2Phase1Friendship mva = new MsgMgm2Phase1Friendship(this.nodeId, recieverNodeId, null,
						this.timeStampCounter, this.time);

				this.mailer.sendMsg(mva);
			}
		}

	}

	private void checkRightAddress(MsgAlgorithm m) {
		NodeId recieverId = m.getRecieverId();
		if (!recieverId.equals(this.nodeId)) {
			throw new RuntimeException("mailer sent wrong agent msg");
		}

	}

	// ------------------** phase 2 **------------------
	protected void recieveMsgPhase2(MsgAlgorithm m) {
		if (m instanceof MsgMgm2Phase1Friendship) {
			checkRightAddress(m);
			NodeId senderId = m.getSenderId();
			MsgReceive<KOptInfo> msgRecieve = new MsgReceive<KOptInfo>((KOptInfo) m.getContext(), m.getTimeStamp());
			this.friendshipOffersPhase1.put(senderId, msgRecieve);
			this.friendshipOffersBooleanPhase1.put(senderId, true);
		} else {
			throw new RuntimeException("called recieveMsgPhase1 with wrong timing");
		}
	}

	// accept/reject
	protected boolean computePhase2() {
		this.resetNeighborsLR_phase2();

		if (!this.isOfferGiverPhase1) {
			whoAskedToBeFriendWithMePhase2();
			if (whoAskedToBeFriendWithMePhase2.isEmpty()) {
				computeLonelyInformationPhase2();
			} else {
				computeGotRequestFromFriendsPhase2();
			}
			return true;
		} else {
			return false;
		}

	}

	private void computeLonelyInformationPhase2() {
		computeLikeMGM1();
	}

	private void computeGotRequestFromFriendsPhase2() {
		int i = this.neighborRndPhase2.nextInt(whoAskedToBeFriendWithMePhase2.size());
		this.acceptedFriendPhase2 = (NodeId) whoAskedToBeFriendWithMePhase2.toArray()[i];
		MsgReceive<KOptInfo> msgRec = this.friendshipOffersPhase1.get(this.acceptedFriendPhase2);
		KOptInfo infoOfMyFriendPhase2 = msgRec.getContext();
		resetFriendshipOffersPhase1();
		this.find2OptPhase2 = new Find2Opt(makeMyKOptInfo(), infoOfMyFriendPhase2);
		this.atomicActionCounter = find2OptPhase2.getAtomicActionCounter();
		this.LR_phase2 = find2OptPhase2.getLR();
		this.myValueAssignmnetPotentialPhase2 = find2OptPhase2.getValueAssignmnet1();
		this.friendValueAssignmnetPotentialPhase2 = find2OptPhase2.getValueAssignmnet2();

		this.neighborsLR_booleanPhase2.put(acceptedFriendPhase2, true);
		this.neighborsLR_phase2.remove(acceptedFriendPhase2);

	}

	private void whoAskedToBeFriendWithMePhase2() {
		whoAskedToBeFriendWithMePhase2 = new HashSet<NodeId>();
		for (Entry<NodeId, MsgReceive<KOptInfo>> e : friendshipOffersPhase1.entrySet()) {
			if (e.getValue() != null) {
				whoAskedToBeFriendWithMePhase2.add(e.getKey());
			}
		}
	}

	protected void sendPhase2() {
		if (!this.isOfferGiverPhase1) {
			whoAskedToBeFriendWithMePhase2();
			if (whoAskedToBeFriendWithMePhase2.isEmpty()) {
				sendSingleLRPhase2();
			} else {
				sendDoubleLRPhase2();
			}
		}
	}

	private void sendDoubleLRPhase2() {
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			if (this.acceptedFriendPhase2.getId1() == recieverNodeId.getId1()) {
				MsgMgm2Phase2FriendshipReplay mlr = new MsgMgm2Phase2FriendshipReplay(this.nodeId, recieverNodeId,
						this.find2OptPhase2, this.timeStampCounter, this.time);

				this.mailer.sendMsg(mlr);
			} else {
				MsgMgm2Phase2LR mlr = new MsgMgm2Phase2LR(this.nodeId, recieverNodeId, this.LR_phase2,
						this.timeStampCounter, this.time);

				this.mailer.sendMsg(mlr);
			}
		}

	}

	private void sendSingleLRPhase2() {
		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgMgm2Phase2LR mlr = new MsgMgm2Phase2LR(this.nodeId, recieverNodeId, this.LR_phase2,
					this.timeStampCounter, this.time);
			this.mailer.sendMsg(mlr);
		}

	}

	// ------------------** phase 3 **------------------
	protected void recieveMsgPhase3(MsgAlgorithm m) {
		if (m instanceof MsgMgm2Phase2FriendshipReplay) {
			recieveMsgFriendshipReplayPhase3(m);
		}

		if (m instanceof MsgMgm2Phase2LR) {
			recieveMsgLRPhase3(m);
		}

		throw new RuntimeException("wrong type of msg sent in phase 3");
	}

	private void recieveMsgFriendshipReplayPhase3(MsgAlgorithm m) {
		neighborsLR_phase2.remove(m.getSenderId()); // id, variable
		neighborsLR_booleanPhase2.put(m.getSenderId(), true);
		this.infoReplayFromFriendPhase3 = (Find2Opt) m.getContext();
		this.LR_phase2 = this.infoReplayFromFriendPhase3.getLR();
		this.myValueAssignmnetPotentialPhase2 = find2OptPhase2.getValueAssignmnet2();
		if (m.getSenderId().getId1() != this.selectedFriendNodeIdPhase1.getId1()) {
			throw new RuntimeException("logical mistake, a different friend that i asked in phase 1 replayed");
		}
	}

	private void recieveMsgLRPhase3(MsgAlgorithm m) {
		Integer context = (Integer) m.getContext();
		int timestamp = m.getTimeStamp();
		MsgReceive<Integer> msgReceive = new MsgReceive<Integer>(context, timestamp);
		neighborsLR_phase2.put(m.getSenderId(), msgReceive); // id, variable
		neighborsLR_booleanPhase2.put(m.getSenderId(), true);
	}

	// is best gain
	protected boolean computePhase3() {
		if (isOfferAndRejectPhase3()) {
			computeLikeMGM1();
		}
		return true;
	}

	private void computeLikeMGM1() {
		int candidate = getCandidateToChange_A();
		if (candidate != this.valueAssignment) {
			this.myValueAssignmnetPotentialPhase2 = candidate;
			this.friendValueAssignmnetPotentialPhase2 = null;
			this.LR_phase2 = findLr(candidate);
		}
	}

	private boolean isOfferAndRejectPhase3() {
		return selectedFriendNodeIdPhase1 != null && infoReplayFromFriendPhase3 == null;
	}

	protected void sendPhase3() {

		if (isOfferAndReplayPositivePhase3()) {

			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
				if (recieverNodeId.getId1() != selectedFriendNodeIdPhase1.getId1()) {
					MsgMgm2Phase2LR mlr = new MsgMgm2Phase2LR(this.nodeId, recieverNodeId, this.LR_phase2,
							this.timeStampCounter, this.time);
					this.mailer.sendMsg(mlr);
				}
			}
		}

		if (isOfferAndRejectPhase3()) {
			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
				MsgMgm2Phase2LR mlr = new MsgMgm2Phase2LR(this.nodeId, recieverNodeId, this.LR_phase2,
						this.timeStampCounter, this.time);
				this.mailer.sendMsg(mlr);
			}
		}

	}

	private boolean isOfferAndReplayPositivePhase3() {
		return selectedFriendNodeIdPhase1 != null && infoReplayFromFriendPhase3 != null;
	}

	// ------------------** phase 4 **------------------
	protected void recieveMsgPhase4(MsgAlgorithm m) {
		if (m instanceof MsgMgm2Phase2LR) {
			recieveMsgLRPhase3(m);
		}
	}

	protected boolean computePhase4() {
		this.isAcceptPhase4 = amIBestLR_phase4();
		if im best and im alone change value 
		return true;
	}

	private boolean amIBestLR_phase4() {
		for (Entry<NodeId, MsgReceive<Integer>> e : neighborsLR_phase2.entrySet()) {
			Integer lrOfNeighbors = e.getValue().getContext();
			if (lrOfNeighbors > this.LR_phase2) {
				return false;
			}
		}
		return true;
	}

	protected void sendPhase4() {
		if (isOfferAndReplayPositivePhase3() || isRecieverAndReplayPositivePhase2()) {
			sendToPartenerPhase4();
		}
		
		if im best and im alone send new value

	}

	private void sendToPartenerPhase4() {
		NodeId myPartner = whoIsMyPartnerPhase4();
		
		
	}

	private NodeId whoIsMyPartnerPhase4() {
		if (isOfferAndReplayPositivePhase3()) {
			return selectedFriendNodeIdPhase1;
		}
		if (isRecieverAndReplayPositivePhase2()) {
			return acceptedFriendPhase2;
		}
		return null;
	}

	private boolean isRecieverAndReplayPositivePhase2() {
		
		return acceptedFriendPhase2 != null;
	}

// ------------------** phase 5 **------------------
// protected void recieveMsgPhase5(MsgAlgorithm m) {}
// protected boolean computePhase5() {}
// protected void sendPhase5() {}
}
