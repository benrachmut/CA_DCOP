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
	private boolean flagForDebug;

	public MGM2_SY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		resetAgentGivenParametersV4();
		flagForDebug = false;

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
			if (this.id == 7 && time == 23) {
				System.err.println(this+" recieve all values time "+this.time);
			}
			resetPhase1RecieveBooleanValueAssignmnet();
			this.flagComputeRecieveValueMsgsPhase1 = true;
			flag = true;
			
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " recieve all values " + " time " + this.time);
			}
		}

		if (m instanceof MsgMgm2Phase2FriendshipInformation
				&& allMapBooleanMapIsTrue(this.phase2RecieveBooleanFriendshipOffers)) {
			this.flagComputeFriendshipInformationPhase2 = true;

			if (this.id == 7 && time==25) {
				System.err.println("7 recieve all friendships at time 23");
			}
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

			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " gave Offer And Recieve positive Replay from: A_" + m.getSenderId().getId1()
						+ " at time " + this.time);
			}
		}

		if (m instanceof MsgMgm2Phase3LR && allMapBooleanMapIsTrue(phase3RecieveBooleanLR)) {

			if (amIWithPartner()) {
				this.flagComputeAllLRandWithPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " with partner and recieve all LR msgs, time "+this.time);
				}
				if (this.time == 22 && this.id == 7) {
					System.err.println(this+ " with partner and recieve all LR msgs, time "+time);
				}
			} else {
				this.flagComputeAllLRandWithNoPartnerPhase4 = true;
				if (MainSimulator.isMGM2Debug) {
					System.out.println(this + " without partner and recieve all LR msgs");
				}
				resetPhase3RecieveBooleanLR();

			}
			
			
		}

	
		if (m instanceof MsgMgm2Phase3LR && phase5RecieveBooleanIsPartnerBestLR.get(whoIsMyPartnerPhase4()) == null
				) {
			System.err.println("sdfgasdf");
		}
		
		
		if ((m instanceof MsgMgm2Phase5IsBestLR  &&
				phase5RecieveBooleanIsPartnerBestLR.get(whoIsMyPartnerPhase4())
				&& allMapBooleanMapIsTrue(phase3RecieveBooleanLR))
				
				|| 
				
				(m instanceof MsgMgm2Phase3LR && whoIsMyPartnerPhase4()!=null && phase5RecieveBooleanIsPartnerBestLR.get(whoIsMyPartnerPhase4())
				&& allMapBooleanMapIsTrue(phase3RecieveBooleanLR))
				
				
				
				) {

		
			
			if (this.id == 7 && m.getSenderId().getId1() == 0 ) {
				System.err.println(this.id+" recieve is best lr from A_"+m.getSenderId().getId1()+" time "+this.time);
			}

			this.flagComputePartnerLRIsBestLRPhase5 = true;
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " recieve " + m.getContext() + " from A_" + m.getSenderId().getId1());
			}
			resetPhase3RecieveBooleanLR();

		}

		checksomething();

	}

	private void checksomething() {
		int counter = 0;

		if (flagComputeRecieveValueMsgsPhase1) {
			counter = counter + 1;
		}

		if (flagComputeFriendshipInformationPhase2) {
			counter = counter + 1;
		}

		if (flagComputeOfferAndNegativeReplayPhase3) {
			counter = counter + 1;
		}

		if (flagComputeOfferAndPositiveReplayPhase3) {
			counter = counter + 1;
		}

		if (flagComputeAllLRandWithPartnerPhase4) {
			counter = counter + 1;
		}

		if (flagComputeAllLRandWithNoPartnerPhase4) {
			counter = counter + 1;
		}

		if (flagComputePartnerLRIsBestLRPhase5) {
			counter = counter + 1;
		}

	}

	private boolean gaveOfferAndRecieveNegetiveReplay(MsgAlgorithm m) {
		boolean firstCond = this.phase1BooleanIsOfferGiver && m instanceof MsgMgm2Phase3LR
				&& this.phase1NodeIdSelectedFriend.getId1() == m.getSenderId().getId1();

		boolean secondCond = m instanceof MsgMgm2Phase3FriendshipReplay && this.phase1BooleanIsOfferGiver
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
		try {

			ans = m.getSenderId().getId1() == this.phase1NodeIdSelectedFriend.getId1()
					&& phase3RecieveInfoReplayFromFriend.get(this.phase1NodeIdSelectedFriend) != null;
		} catch (Exception e) {
			System.err.println("AHHHHHHHHHHHHH");
		}
		return ans;

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
			// resetPhase2RecieveFriendshipOffers();
		}

		if (flagComputeOfferAndNegativeReplayPhase3) {
			sendSingleLRPhase2();
			// resetPhase3RecieveInfoReplayFromFriend();
		}

		if (flagComputeOfferAndPositiveReplayPhase3) {
			sendAllLrExceptForPositiveFriendPhase3();
			// resetPhase3RecieveInfoReplayFromFriend();
		}
		if (this.flagComputeAllLRandWithPartnerPhase4) {
			if (this.id == 0 && time == 18) {
				System.err.println(this.id+" here sends msg to a_7 at tune "+this.time);
			}
			sendAllLRandWithPartnerAmIBestPhase4();
			// resetPhase3RecieveLR();
			
		}

		if (this.flagComputeAllLRandWithNoPartnerPhase4) {
			sendValueAssignmnetMsgs();
			if (this.id==7&&time==25) {
				System.err.println("gaseg");
			}
			resetPhases(!didEarlyPhase1Flag);
			if (MainSimulator.isMGM2Debug) {
				System.out.println(this + " sent values and lonely, time "+this.time);
			}
			
			// resetPhase3RecieveLR();

		}
		if (flagComputePartnerLRIsBestLRPhase5) {
			sendValueAssignmnetMsgsExceptPartnerPhase5();
			
			if (allMapBooleanMapIsTrue(this.phase1RecieveBooleanValueAssignmnet)) {
				if (this.id == 0) {
					System.out.println("here did comp 1");
				}
				resetPhase1();
				doReactionAndSendPhase1();
				didEarlyPhase1Flag = true;
				resetPhase1RecieveBooleanValueAssignmnet();
			}

			if (!didEarlyPhase1Flag) {
				resetPhase1();
			}
			resetPhase2();
			resetPhase3();
			resetPhase4();
			resetPhase5();
			didEarlyPhase1Flag = false;
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
