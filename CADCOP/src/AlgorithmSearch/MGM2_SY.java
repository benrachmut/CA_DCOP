package AlgorithmSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgMgm2Phase2FriendshipInformation;
import Messages.MsgMgm2Phase3FriendshipReplay;
import Messages.MsgMgm2Phase3LR;
import Messages.MsgMgm2Phase5IsBestLR;
import Messages.MsgValueAssignmnet;

public class MGM2_SY extends MGM2 {
	private Collection<MsgAlgorithm> future;
	private boolean flag;

	public MGM2_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetAgentGivenParametersV4();

	}

	@Override
	protected void resetAgentGivenParametersV4() {
		this.future = new ArrayList<MsgAlgorithm>();
		this.isWithTimeStamp = false;
		flag = false;

	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "MGM2_SY";
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm m) {
		boolean firstCondition = m instanceof MsgValueAssignmnet && flag;

		if (!flag && m instanceof MsgMgm2Phase2FriendshipInformation) {
			this.future.add(m);
		} else {
			super.updateMessageInContext(m);

		}
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm m) {
		if (m instanceof MsgValueAssignmnet && allMapBooleanMapIsTrue(this.phase1RecieveBooleanValueAssignmnet)) {
			this.flagComputeRecieveValueMsgsPhase1 = true;
			flag = true;

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this +  " recieve all values "+ " time " + this.time);
			}
		}

		if (m instanceof MsgMgm2Phase2FriendshipInformation
				&& allMapBooleanMapIsTrue(this.phase2RecieveBooleanFriendshipOffers)) {
			this.flagComputeFriendshipInformationPhase2 = true;

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " time " + this.time + " recieve all FriendshipOffers");
			}
		}

		if (gaveOfferAndRecieveNegetiveReplay(m)) {
			this.flagComputeOfferAndNegativeReplayPhase3 = true;
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " gave Offer And Recieve Negetive Replay from: A_" + m.getSenderId().getId1()
						+ " at time");
			}
		}

		if (m instanceof MsgMgm2Phase3FriendshipReplay && gaveOfferAndRecievePostivieReplay(m)) {
			this.flagComputeOfferAndPositiveReplayPhase3 = true;
			if (MainSimulator.isMGM2Debug) {
				if (m.getContext() == null) {
					System.out.println(this + " gave Offer And Recieve negative Replay from: A_"
							+ m.getSenderId().getId1() + " because she offers herself, at time " + this.time);
				} else {
					System.out.println(this + " gave Offer And Recieve positive Replay from: A_"
							+ m.getSenderId().getId1() + " at time " + this.time);
				}
			}
		}

		if (m instanceof MsgMgm2Phase3LR && allMapBooleanMapIsTrue(phase3RecieveBooleanLR)) {
			if (amIWithPartner()) {
				this.flagComputeAllLRandWithPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " with partner and recieve all LR msgs");
				}
			} else {
				this.flagComputeAllLRandWithNoPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " without partner and recieve all LR msgs");
				}
			}
		}
		if (amIWithPartner()) {
			NodeId myPartner = whoIsMyPartnerPhase4();
		
		if (phase5RecieveBooleanIsPartnerBestLR.get(myPartner) && allMapBooleanMapIsTrue(phase3RecieveBooleanLR)) {
			this.flagComputePartnerLRIsBestLRPhase5 = true;
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " recieve " + m.getContext() + " from A_" + m.getSenderId().getId1());
			}
		}
		}
	}

	private boolean gaveOfferAndRecieveNegetiveReplay(MsgAlgorithm m) {
		return this.phase1BooleanIsOfferGiver && m instanceof MsgMgm2Phase3LR
				&& this.phase1NodeIdSelectedFriend.getId1() == m.getSenderId().getId1();
	}

	private boolean gaveOfferAndRecievePostivieReplay(MsgAlgorithm m) {
		return m.getSenderId().getId1() == this.phase1NodeIdSelectedFriend.getId1() && m.getContext() != null;
	}


	/*
	 * @Override public boolean getDidComputeInThisIteration() { // TODO
	 * Auto-generated method stub return false; }
	 */

	@Override
	protected boolean compute() {
		if (this.flagComputeRecieveValueMsgsPhase1) {
			return computePhase1();
		}
		if (this.flagComputeFriendshipInformationPhase2) {
			if (this.phase1BooleanIsOfferGiver == false) {
				phase2SetNodeIdsAskedMeForFriendship();
				if (phase2SetNodeIdsAskedMeForFriendship.isEmpty()) {
					computeLikeMGM1();
				} else {
					computeGotRequestFromFriendsPhase2();
				}
			}

		}

		if (flagComputeOfferAndNegativeReplayPhase3) {
			computeLikeMGM1();
			return true;
		}

		if (flagComputeOfferAndPositiveReplayPhase3) {
			return computeOfferAndPositiveReplayPhase3();
		}

		if (this.flagComputeAllLRandWithPartnerPhase4) {
			computeAllLRandWithPartnerAmIBestPhase4();
		}

		if (this.flagComputeAllLRandWithNoPartnerPhase4) {
			computeAllLRandWithNoPartnerAmIBestPhase4();
		}
		if (flagComputePartnerLRIsBestLRPhase5) {
			computePartnerLRIsBestLRPhase5();
		}

		return false;
	}

	@Override
	public void sendMsgs() {
		if (this.flagComputeRecieveValueMsgsPhase1) {
			sendPhase1();
			resetPhase1RecieveBooleanValueAssignmnet();
			releaseFutureMsgs();
		}

		if (this.flagComputeFriendshipInformationPhase2) {
			if (this.phase1BooleanIsOfferGiver) {
				sendAllOffersThatIamAnOffererMySelfPhase2();
			}

			if (this.phase1BooleanIsOfferGiver == false) {
				phase2SetNodeIdsAskedMeForFriendship();
				if (phase2SetNodeIdsAskedMeForFriendship.isEmpty()) {
					sendSingleLRPhase2();
				} else {
					sendDoubleLRPhase2();
				}
			}
		}

		if (flagComputeOfferAndNegativeReplayPhase3) {
			sendSingleLRPhase2();
		}

		if (flagComputeOfferAndPositiveReplayPhase3) {
			sendAllLrExceptForPositiveFriendPhase3();
		}
		if (this.flagComputeAllLRandWithPartnerPhase4) {
			sendAllLRandWithPartnerAmIBestPhase4();

		}

		if (this.flagComputeAllLRandWithNoPartnerPhase4) {
			sendValueAssignmnetMsgs();
			resetPhases();
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " sent values and lonely");
			}
		}
		if (flagComputePartnerLRIsBestLRPhase5) {
			sendValueAssignmnetMsgsExceptPartnerPhase5();
			resetPhases();
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " sent values with partner");
			}
		}
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

	@Override
	public void changeRecieveFlagsToFalse() {
		flagComputeRecieveValueMsgsPhase1 = false;
		flagComputeFriendshipInformationPhase2 = false;
		flagComputeOfferAndNegativeReplayPhase3 = false;
		flagComputeOfferAndPositiveReplayPhase3 = false;
		flagComputeAllLRandWithPartnerPhase4 = false;
		flagComputeAllLRandWithNoPartnerPhase4 = false;
		flagComputePartnerLRIsBestLRPhase5 = false;
	}

	@Override
	public boolean getDidComputeInThisIteration() {
		return flagComputeRecieveValueMsgsPhase1 || flagComputeFriendshipInformationPhase2
				|| flagComputeOfferAndNegativeReplayPhase3 || flagComputeOfferAndPositiveReplayPhase3
				|| flagComputeAllLRandWithPartnerPhase4 || flagComputeAllLRandWithNoPartnerPhase4
				|| flagComputePartnerLRIsBestLRPhase5;
	}

}
