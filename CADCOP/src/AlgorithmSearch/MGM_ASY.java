package AlgorithmSearch;

import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgLR;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public class MGM_ASY extends AgentVariableSearch{
	protected SortedMap<NodeId, MsgReceive<Integer>> neighborsLR; // id, variable
	protected boolean recieveLRFlag;
	protected boolean receiveMsgVAFlag;

	public MGM_ASY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		neighborsLR = new TreeMap<NodeId, MsgReceive<Integer>>();
		recieveLRFlag = false;
		receiveMsgVAFlag = false; 
	}
	
	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsLR.put(new NodeId(neighborId), null);
	}
	
	@Override
	protected void resetAgentSpecific() {
		this.neighborsLR = Agent.<NodeId,MsgReceive<Integer>>resetMapToValueNull(this.neighborsLR);
	
	}

	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "";
	}

	@Override
	public void updateAlgorithmData() {
		AgentVariable.algorithmData = "";
	}

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "MGM_ASY";	
	}

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			return this.getTimestampOfValueAssignmnets(msgAlgorithm);
		}
		if (msgAlgorithm instanceof MsgLR) {
			return this.getTimestampOfLR(msgAlgorithm);

		}else {
			throw new RuntimeException();
		}
	}

	private int getTimestampOfLR(MsgAlgorithm msgAlgorithm) {
		NodeId senderNodeId = msgAlgorithm.getSenderId();
		MsgReceive<Integer> msgReceive= this.neighborsLR.get(senderNodeId);
		return msgReceive.getTimestamp();
	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			updateMsgInContextValueAssignmnet(msgAlgorithm);
			receiveMsgVAFlag = true; 
			}
		if (msgAlgorithm instanceof MsgLR) {
			updateMsgInContextLR(msgAlgorithm);
			recieveLRFlag = true;
		}else {
			throw new RuntimeException();
		}
		
	}
	
	protected void updateMsgInContextLR(MsgAlgorithm msgAlgorithm) {
		Integer context = (Integer)msgAlgorithm.getContext();
		int timestamp = msgAlgorithm.getTimeStamp();
		MsgReceive<Integer> msgReceive = new MsgReceive<Integer>(context, timestamp);
		this.neighborsLR.put(msgAlgorithm.getSenderId(), msgReceive);
	}

	@Override
	protected boolean compute() {
		if (receiveMsgVAFlag) {
			return computeMyLR();
		}
		if (recieveLRFlag) {
			return computeChangeInValueAssignment();
		}
		return false;
	}

	@Override
	protected void sendMsgs() {
		if (receiveMsgVAFlag) {
			sendLRmsgs();
		}
		if (recieveLRFlag) {
			sendValueAssignmnetMsgs();
		}
		
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		receiveMsgVAFlag = false;
		recieveLRFlag = false;
	}
}
