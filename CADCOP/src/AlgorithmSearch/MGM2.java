package AlgorithmSearch;

import java.util.Random;

import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class MGM2 extends AgentVariableSearch {
	Random neighborRnd;

	Random isOfferGiverRnd;
	private boolean isOfferGiver;
	
	public MGM2(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		isOfferGiverRnd = new Random(id1*105+dcopId*10);
		isOfferGiver = false;
		neighborRnd = new Random(id1*132+dcopId*15);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void resetAgentGivenParametersV3() {
		neighborRnd = new Random(id*132+dcopId*15);

		isOfferGiverRnd = new Random(this.id*105+dcopId*10);
		isOfferGiver = false;

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
			KOptInfo myKoptInfo = new KOptInfo(this.valueAssignment, nodeId, neighborsConstraint, domainArray, this.neighborsValueAssignmnet);
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
