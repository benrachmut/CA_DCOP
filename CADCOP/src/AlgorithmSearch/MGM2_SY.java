package AlgorithmSearch;

import java.util.HashSet;
import AgentsAbstract.AgentVariable;
import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgMgm2Phase2FriendshipInformation;
import Messages.MsgMgm2Phase3FriendshipReplay;
import Messages.MsgMgm2Phase3LR;
import Messages.MsgMgm2Phase5IsBestLR;
import Messages.MsgValueAssignmnet;

public class MGM2_SY extends MGM2 {
	private HashSet<MsgAlgorithm> future;
	protected boolean waitingForValueMsgs;
	protected boolean waitingForFirstFriendshipInformation;
	protected boolean waitingForOfferInformation;
	protected boolean waitingForAllLR;
	protected boolean waitingForPartnerIsBestLR;
	private boolean fromFuture;

	public MGM2_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetAgentGivenParametersV4();

	}

	@Override
	protected boolean updateMessageInContext(MsgAlgorithm m) {
		if (m instanceof MsgValueAssignmnet) {
			if (waitingForValueMsgs) {
				recieveMsgPhase1(m);
				return true;
			} else if (!fromFuture) {
				future.add(m);
			}
			return false;
		}
		if (m instanceof MsgMgm2Phase2FriendshipInformation) {
			if (waitingForFirstFriendshipInformation) {
				recieveMsgFriendshipInformationPhase2(m);
				return true;
			} else if (!fromFuture) {
				future.add(m);
			}
			return false;
		}

		if (m instanceof MsgMgm2Phase3FriendshipReplay) {
			if (waitingForOfferInformation) {
				recieveMsgFriendshipReplayPhase3(m);
				return true;
			} else if (!fromFuture) {
				future.add(m);
			}
			return false;
		}

		// || m instanceof MsgMgm2Phase3FriendshipReplay
		//
		if (m instanceof MsgMgm2Phase3LR) {

			if (waitingForAllLR || waitingForOfferInformation) {

				if (m instanceof MsgMgm2Phase3LR) {
					recieveMsgLRPhase3(m);
					return true;
				}
				// if (m instanceof MsgMgm2Phase3FriendshipReplay) {
				// recieveMsgFriendshipReplayPhase3(m);
				// }
			} else if (!fromFuture) {
				future.add(m);
			}
			return false;

		}

		if (m instanceof MsgMgm2Phase5IsBestLR) {

			if (waitingForPartnerIsBestLR) {
				recieveMsgPhase5(m);
				return true;
			} else if (!fromFuture) {
				future.add(m);
			}
			return false;
		}

		return true;
	}

	@Override
	protected void resetAgentGivenParametersV4() {
		this.future = new HashSet<MsgAlgorithm>();
		this.isWithTimeStamp = false;
		waitingForValueMsgs = true;
		waitingForFirstFriendshipInformation = true;
		waitingForOfferInformation = true;
		waitingForAllLR = true;
		waitingForPartnerIsBestLR = true;
		flagValueAssignmentAlready = false;

	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "MGM2_SY";
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm m) {

		if (this.id == 0 && time == 2135) {
			System.err.println("this.id == 0 && time == 2135");
		}
		if ((m instanceof MsgValueAssignmnet && allMapBooleanMapIsTrue(this.phase1RecieveBooleanValueAssignmnet)
				&& !flagValueAssignmentAlready)) {

			if (!flagValueAssignmentAlready) {
				flagValueAssignmentAlready = true;
			}

			resetPhase1RecieveBooleanValueAssignmnet();
			this.flagComputeRecieveValueMsgsPhase1 = true;

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " recieve all values " + " time " + this.time);
			}
		}

		if (m instanceof MsgMgm2Phase2FriendshipInformation
				&& allMapBooleanMapIsTrue(this.phase2RecieveBooleanFriendshipOffers)) {

			this.flagComputeFriendshipInformationPhase2 = true;
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " time " + this.time + " recieve all FriendshipOffers");
			}

			resetPhase2RecieveBooleanFriendshipOffers();
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
			this.phase3RecieveBooleanLR.put(whoIsMyPartnerPhase4(), true);

			if (this.id == 3 && time == 2847) {
				System.err.println("this.id == 3 && time == 2847");
			}

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " gave Offer And Recieve positive Replay from: A_" + m.getSenderId().getId1()
						+ " at time " + this.time);
			}

		}

		if (((m instanceof MsgMgm2Phase3FriendshipReplay || m instanceof MsgMgm2Phase3LR)
				&& allMapBooleanMapIsTrue(phase3RecieveBooleanLR))) {

			// waitingForAllLR = false;

			if (!flagIDidPhase4Already) {
				flagIDidPhase4Already = true;
			}

			if (amIWithPartner()) {
				this.flagComputeAllLRandWithPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " with partner and recieve all LR msgs, time " + this.time);
				}
				// waitingForPartnerIsBestLR = true;

			} else {
				this.flagComputeAllLRandWithNoPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " without partner and recieve all LR msgs");
				}
				resetPhase3RecieveBooleanLR();
				// waitingForValueMsgs = true;

			}

		}

		if ((m instanceof MsgMgm2Phase3LR && whoIsMyPartnerPhase4() != null
				&& phase5RecieveBooleanIsPartnerBestLR.get(whoIsMyPartnerPhase4())
				&& allMapBooleanMapIsTrue(phase3RecieveBooleanLR))
				|| (m instanceof MsgMgm2Phase5IsBestLR && whoIsMyPartnerPhase4() != null
						&& phase5RecieveBooleanIsPartnerBestLR.get(whoIsMyPartnerPhase4())
						&& allMapBooleanMapIsTrue(phase3RecieveBooleanLR))) {

			waitingForValueMsgs = true;
			waitingForPartnerIsBestLR = false;

			this.flagComputePartnerLRIsBestLRPhase5 = true;

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " recieve (" + m.getContext() + ") from A_" + m.getSenderId().getId1()
						+ " at time " + this.time);
			}

			resetPhase3RecieveBooleanLR();
		}

	}

	private boolean gaveOfferAndRecieveNegetiveReplay(MsgAlgorithm m) {

		boolean firstCond = this.phase1BooleanIsOfferGiver && m instanceof MsgMgm2Phase3LR
				&& this.phase1NodeIdSelectedFriend.getId1() == m.getSenderId().getId1();
		boolean secondCond = false;

		secondCond = m instanceof MsgMgm2Phase3FriendshipReplay && this.phase1BooleanIsOfferGiver
				&& m.getSenderId().getId1() == this.phase1NodeIdSelectedFriend.getId1()
				&& phase3RecieveInfoReplayFromFriend.get(this.phase1NodeIdSelectedFriend).getContext() == null;

		if (secondCond) {
			phase3RecieveInfoReplayFromFriend.put(this.phase1NodeIdSelectedFriend, null);
			phase3RecieveBooleanInfoReplayFromFriend.put(this.phase1NodeIdSelectedFriend, false);

		}

		if (!flagRecieveNegativaReplay && (firstCond || secondCond)) {
			flagRecieveNegativaReplay = true;
			return true;
		} else {
			return false;
		}

	}

	private boolean gaveOfferAndRecievePostivieReplay(MsgAlgorithm m) {
		boolean ans = false;
		ans = m.getSenderId().getId1() == this.phase1NodeIdSelectedFriend.getId1()
				&& phase3RecieveInfoReplayFromFriend.get(this.phase1NodeIdSelectedFriend) != null;

		return ans;

	}

	@Override
	protected boolean compute() {

		if (this.flagComputeRecieveValueMsgsPhase1 && flagComputePartnerLRIsBestLRPhase5) {
			computePartnerLRIsBestLRPhase5();
			computePhase1();
		} else {
			if (this.flagComputeRecieveValueMsgsPhase1) {
				computePhase1();
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
			}

			if (flagComputeOfferAndPositiveReplayPhase3) {

				computeOfferAndPositiveReplayPhase3();
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
		}
		return true;
	}

	@Override
	public void sendMsgs() {

		if (this.flagComputeRecieveValueMsgsPhase1) {
			sendPhase1();
			resetPhase1RecieveBooleanValueAssignmnet();
			waitingForValueMsgs = false;
			waitingForFirstFriendshipInformation = true;
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

			waitingForFirstFriendshipInformation = false;
			if (this.phase1BooleanIsOfferGiver) {
				waitingForOfferInformation = true;
			} else {
				waitingForAllLR = true;
			}

			// resetPhase2RecieveFriendshipOffers();
		}

		if (flagComputeOfferAndNegativeReplayPhase3) {
			sendSingleLRPhase2();
			waitingForOfferInformation = false;
			waitingForAllLR = true;
			// resetPhase3RecieveInfoReplayFromFriend();
		}

		if (flagComputeOfferAndPositiveReplayPhase3) {
			sendAllLrExceptForPositiveFriendPhase3();
			waitingForOfferInformation = false;
			waitingForAllLR = true;
			// resetPhase3RecieveInfoReplayFromFriend();
		}
		if (this.flagComputeAllLRandWithPartnerPhase4) {

			waitingForAllLR = false;
			waitingForPartnerIsBestLR = true;
			// waitingForValueMsgs = true;
			sendAllLRandWithPartnerAmIBestPhase4();
			System.out.println(this + " sent if it is best (" + this.phase4IsBestLR + ") at time: " + this.time);
		}

		if (this.flagComputeAllLRandWithNoPartnerPhase4) {
			waitingForAllLR = false;
			// waitingForPartnerIsBestLR = true;
			waitingForValueMsgs = true;
			sendFlagComputeAllLRandWithNoPartnerPhase4();
		}
		if (flagComputePartnerLRIsBestLRPhase5) {
			waitingForValueMsgs = true;
			waitingForPartnerIsBestLR = false;
			sendFlagComputePartnerLRIsBestLRPhase5();

		}

		releaseFutureMsgs();
	}

	private void sendFlagComputePartnerLRIsBestLRPhase5() {
		if (allMapBooleanMapIsTrue(this.phase1RecieveBooleanValueAssignmnet)) {
			resetPhase1();
			doReactionAndSendPhase1();
			didEarlyPhase1Flag = true;
			resetPhase1RecieveBooleanValueAssignmnet();
			waitingForValueMsgs = false;
			waitingForFirstFriendshipInformation = true;
		}

		if (!didEarlyPhase1Flag) {
			if (!this.phase1BooleanIsOfferGiver) {
				resetPhase1();

			}
		}
		flagValueAssignmentAlready = false;
		resetPhase2();
		resetPhase3();
		resetPhase4();
		resetPhase5();

		sendValueAssignmnetMsgs();

		didEarlyPhase1Flag = false;

		if (MainSimulator.isMGM2Debug) {
			System.out.println(this + " sent values with partner");
		}

	}

	private void sendFlagComputeAllLRandWithNoPartnerPhase4() {
		sendValueAssignmnetMsgs();
		resetPhases(!didEarlyPhase1Flag);
		/*
		 * resetPhase1(); resetPhase2(); resetPhase3(); resetPhase4(); resetPhase5();
		 */
		// resetPhase2RecieveBooleanFriendshipOffers();
		// resetPhase3RecieveBooleanLR();
		if (MainSimulator.isMGM2Debug) {
			System.out.println(this + " sent values and lonely, time " + this.time);
		}
	}

	private void releaseFutureMsgs() {
		this.fromFuture = true;
		HashSet<MsgAlgorithm> toRemove = new HashSet<MsgAlgorithm>();
		HashSet<MsgAlgorithm> toRelease = new HashSet<MsgAlgorithm>();

		for (MsgAlgorithm m : this.future) {

			boolean flag = false;
			if (m instanceof MsgValueAssignmnet
					&& (flagComputePartnerLRIsBestLRPhase5 || flagComputeAllLRandWithNoPartnerPhase4)) {
				flag = true;
			}

			if (m instanceof MsgMgm2Phase2FriendshipInformation && flagComputeRecieveValueMsgsPhase1) {
				flag = true;
			}

			if (m instanceof MsgMgm2Phase3FriendshipReplay && flagComputeFriendshipInformationPhase2) {
				flag = true;
			}

			if (m instanceof MsgMgm2Phase3LR && (flagComputeOfferAndNegativeReplayPhase3
					|| flagComputeOfferAndPositiveReplayPhase3 || flagComputeFriendshipInformationPhase2)) {
				flag = true;
			}

			if ((m instanceof MsgMgm2Phase5IsBestLR && flagComputeAllLRandWithPartnerPhase4)) {
				flag = true;
			}
			boolean anotherFlag = false;

			if (flag) {
				// if (m.getTimeStamp() == this.timeStampCounter) {

				boolean isupdated = updateMessageInContext(m);
				if (isupdated) {
					changeRecieveFlagsToTrue(m);
					if (m instanceof MsgValueAssignmnet && this.flagComputeRecieveValueMsgsPhase1) {
						anotherFlag = true;
						this.flagComputeAllLRandWithNoPartnerPhase4 = false;
						this.flagComputePartnerLRIsBestLRPhase5 = false;
					}

					if (m instanceof MsgMgm2Phase2FriendshipInformation
							&& this.flagComputeFriendshipInformationPhase2) {
						anotherFlag = true;
						this.flagComputeRecieveValueMsgsPhase1 = false;
					}

					if (m instanceof MsgMgm2Phase3FriendshipReplay && (this.flagComputeOfferAndPositiveReplayPhase3
							|| flagComputeOfferAndNegativeReplayPhase3)) {
						anotherFlag = true;
						if (!waitingForOfferInformation) {
							flagComputeFriendshipInformationPhase2 = false;
						}
					}

					if (m instanceof MsgMgm2Phase3LR && (this.flagComputeFriendshipInformationPhase2
							&& (this.flagComputeOfferAndNegativeReplayPhase3
									|| this.flagComputeOfferAndPositiveReplayPhase3))) {
						anotherFlag = true;
						if (!waitingForOfferInformation) {
							flagComputeFriendshipInformationPhase2 = false;
						}
					}

					else if (m instanceof MsgMgm2Phase3LR
							&& (this.flagComputeAllLRandWithNoPartnerPhase4 || flagComputeAllLRandWithPartnerPhase4)) {
						anotherFlag = true;
						flagComputeOfferAndPositiveReplayPhase3 = false;
						flagComputeOfferAndNegativeReplayPhase3 = false;
					}

					if (m instanceof MsgMgm2Phase5IsBestLR && this.flagComputePartnerLRIsBestLRPhase5) {
						anotherFlag = true;
						flagComputeAllLRandWithNoPartnerPhase4 = false;
						flagComputeAllLRandWithPartnerPhase4 = false;
					}
					if (anotherFlag) {
						toRemove.add(m);
					} else {
						toRelease.add(m);
					}
				}

			}

		}

		if (!toRemove.isEmpty()) {
			reactionToAlgorithmicMsgs();
			sendMsgs();
			changeRecieveFlagsToFalse();
		}

		this.future.removeAll(toRemove);
		if (!toRelease.isEmpty()) {
			changeRecieveFlagsToFalse();
			reactionToAlgorithmicMsgs();
		}
		this.future.removeAll(toRelease);
		this.fromFuture = false;
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