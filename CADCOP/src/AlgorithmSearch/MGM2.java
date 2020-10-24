package AlgorithmSearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgMgm2Phase1FriendShip;
import Messages.MsgValueAssignmnet;

public class MGM2 extends AgentVariableSearch {
	protected Random neighborRnd;
	protected Random isOfferGiverRnd;
	protected boolean isOfferGiver;
	protected Map<NodeId, KOptInfo> friendshipOffers;
	protected Map<NodeId, Boolean> friendshipOffersBoolean;

	
	public MGM2(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		isOfferGiverRnd = new Random(id1*105+dcopId*10);
		isOfferGiver = false;
		neighborRnd = new Random(id1*132+dcopId*15);
		resetFriendshipOffers();
	}

	@Override
	protected void resetAgentGivenParametersV3() {
		neighborRnd = new Random(id*132+dcopId*15);

		isOfferGiverRnd = new Random(this.id*105+dcopId*10);
		isOfferGiver = false;
		resetFriendshipOffers();

	}

	


	
	
	private void resetFriendshipOffers() {
		
		friendshipOffers= new HashMap<NodeId,KOptInfo>();
		friendshipOffersBoolean = new HashMap<NodeId,Boolean>();
		for (NodeId nodeId : this.getNeigborSetId()) {
			friendshipOffers.put(nodeId,null);
			friendshipOffersBoolean.put(nodeId,false);
		}
		
	}

	@Override
	public void updateAlgorithmHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAlgorithmData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAlgorithmName() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDidComputeInThisIteration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}
	protected boolean computePhase1() {
		double rnd = isOfferGiverRnd.nextDouble();
		if (rnd<0.5) {
			this.isOfferGiver = true;
		}else {
			this.isOfferGiver = false;
		}
		return true; 
	}
	
	protected void sendPhase1() {
		if (this. isOfferGiver) {
			KOptInfo myKoptInfo = makeMyKOptInfo();
			int i = this.neighborRnd.nextInt(this.neighborSize());
			NodeId selectedFriendNodeId =  (NodeId)this.getNeigborSetId().toArray()[i];
			
			
			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
				MsgMgm2Phase1FriendShip mva = null;
				if (recieverNodeId.getId1() == selectedFriendNodeId.getId1()) {
					mva = new MsgMgm2Phase1FriendShip(this.nodeId, recieverNodeId, myKoptInfo,
							this.timeStampCounter, this.time);
				}else {
					mva = new MsgMgm2Phase1FriendShip(this.nodeId, recieverNodeId, null,
							this.timeStampCounter, this.time);
				}
				this.mailer.sendMsg(mva);
			}
			
		}else {
			for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			
				MsgMgm2Phase1FriendShip mva = new MsgMgm2Phase1FriendShip(this.nodeId, recieverNodeId, null,
							this.timeStampCounter, this.time);
				
				this.mailer.sendMsg(mva);
			}
		}
		
	}

	private KOptInfo makeMyKOptInfo() {
		// TODO Auto-generated method stub
		return new KOptInfo(this.valueAssignment, nodeId, neighborsConstraint, domainArray, this.neighborsValueAssignmnet);;
	}

	protected void recieveMsgPhase1(MsgAlgorithm m) {
		if (m instanceof MsgMgm2Phase1FriendShip) {
			checkRightAddress(m);	
			NodeId senderId = m.getSenderId();
			this.friendshipOffers.put(senderId,(KOptInfo)m.getContext());
			this.friendshipOffersBoolean.put(senderId, true);
		}else {
			throw new RuntimeException("called recieveMsgPhase1 with wrong timing");
		}
	}

	protected boolean computePhase2() {
		if (!this.isOfferGiver) {
			selectWhoIWantToBeFriendsWith
			KOptInfo myKOptInfo = makeMyKOptInfo();
			Find2Opt find2Opt = new Find2Opt
		}
	}
	//protected void sendPhase2()
	//protected void recieveMsgPhase2(MsgAlgorithm m)
	private void checkRightAddress(MsgAlgorithm m) {
		NodeId recieverId = m.getRecieverId();
		if (!recieverId.equals(this.nodeId)) {
			throw new RuntimeException("mailer sent wrong agent msg");
		}
		
	}

	@Override
	public void sendMsgs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeRecieveFlagsToFalse() {
		// TODO Auto-generated method stub
		
	}

}
