package AlgorithmSearch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
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

public class MGM_ASY extends AgentVariableSearch {
	protected SortedMap<NodeId, MsgReceive<Integer>> neighborsLR; // id, variable
	protected boolean recieveLRFlag;
	protected boolean receiveMsgVAFlag;
	protected int lr;
	protected  int candidateValueAssignment;

	public MGM_ASY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		neighborsLR = new TreeMap<NodeId, MsgReceive<Integer>>();
		recieveLRFlag = false;
		receiveMsgVAFlag = false;
		lr = -1;
		candidateValueAssignment = -1;
	}

	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsLR.put(new NodeId(neighborId), null);
	}

	@Override
	protected void resetAgentGivenParametersV4() {
		this.neighborsLR = Agent.<NodeId, MsgReceive<Integer>>resetMapToValueNull(this.neighborsLR);
		lr = -1;
		candidateValueAssignment = -1;
		receiveMsgVAFlag = false;
		recieveLRFlag = false;
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

		} else {
			throw new RuntimeException();
		}
	}

	private int getTimestampOfLR(MsgAlgorithm msgAlgorithm) {
		NodeId senderNodeId = msgAlgorithm.getSenderId();
		MsgReceive<Integer> msgReceive = this.neighborsLR.get(senderNodeId);
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
		} else {
			throw new RuntimeException();
		}

	}

	protected void updateMsgInContextLR(MsgAlgorithm msgAlgorithm) {
		Integer context = (Integer) msgAlgorithm.getContext();
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

	private boolean computeChangeInValueAssignment() {
		SortedMap<NodeId, Integer> lrInfoPerNeighbor = Agent.<Integer>turnMapWithMsgRecieveToContextValues(this.neighborsLR);
		int maxLrOfNeighbors = Collections.max(lrInfoPerNeighbor.values());
		if (this.lr>maxLrOfNeighbors) {
			this.valueAssignment = this.candidateValueAssignment;
			return true;
		}
		if (this.lr == maxLrOfNeighbors) {
			Set<NodeId> competitors = getCompetitors(maxLrOfNeighbors, lrInfoPerNeighbor);
			NodeId bestCompetitor = Collections.min(competitors);
			if (this.nodeId.getId1()<bestCompetitor.getId1()) {
				this.valueAssignment = this.candidateValueAssignment;
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	private static Set<NodeId> getCompetitors(int maxLrOfNeighbors, SortedMap<NodeId, Integer> lrInfoPerNeighbor) {
		Set<NodeId> ans = new HashSet<NodeId>();
		for (Entry<NodeId, Integer> e : lrInfoPerNeighbor.entrySet()) {
			if (e.getValue() == maxLrOfNeighbors) {
				ans.add(e.getKey());
			}
		}
		return ans;
	}

	private boolean computeMyLR() {
		int candidate = getCandidateToChange();
		if (candidate != this.valueAssignment) {
			this.candidateValueAssignment = candidate;
			int lrToCheck = findLr(candidate);
			return changeLr(lrToCheck);
		}
		return changeLrToZero();
	}

	private boolean changeLrToZero() {
		if (lr == 0) {
			return false;
		} else {
			this.lr = 0;
			return true;
		}
	}

	private boolean changeLr(int lrToCheck) {
		if (lrToCheck <= 0) {
			throw new RuntimeException();
		}
		if (this.lr != lrToCheck) {
			this.lr = lrToCheck;
			return true;
		} else {
			return false;
		}
	}

	private int findLr(int candidate) {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int costOfCandidate = costPerDomain.get(candidate);
		int costOfCurrentValueAssignment = costPerDomain.get(this.valueAssignment);
		return costOfCurrentValueAssignment - costOfCandidate;
	}

	@Override
	protected void sendMsgs() {
		if (receiveMsgVAFlag) {
			if (lr == -1) {
				throw new RuntimeException();
			}
			sendLRmsgs();
		}
		if (recieveLRFlag) {
			sendValueAssignmnetMsgs();
		}

	}

	protected void sendLRmsgs() {
		
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		receiveMsgVAFlag = false;
		recieveLRFlag = false;
	}
}
