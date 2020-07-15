package AlgorithmSearch;

import java.util.Random;


import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class DSA_ASY extends AgentVariableSearch {
	protected double stochastic;
	protected Random rndStochastic;
	private boolean receiveMsgFlag;

	public DSA_ASY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		stochastic = 0.7;
		this.rndStochastic = new Random(this.dcopId*10+this.id*100);
		this.receiveMsgFlag = false;
	}
	
	public DSA_ASY(int dcopId, int D, int id1, double stochastic) {
		this( dcopId, D,  id1);
		this.stochastic = stochastic;
	}
	
	/**
	 * reset the rndStochastic
	 */
	@Override
	protected void resetAgentSpecific() {
		this.rndStochastic = new Random(this.dcopId*10+this.id*100);
		this.receiveMsgFlag = false;
	}

	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Stochastic,";
	}

	@Override
	public void updateAlgorithmData() {
		AgentVariable.algorithmData = this.stochastic+",";
	}
		
	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_ASY";
	}
	
	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			return getTimestampOfValueAssignmnets(msgAlgorithm);
			
		}else {
			System.err.println("dsa agent was asked about timestamp upon wrong instance");
		}
		return 0;
	}



	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			updateMsgInContextValueAssignmnet(msgAlgorithm);
			this.receiveMsgFlag = true;
		}else {
			System.err.println("dsa agent was asked about timestamp upon wrong instance");
			throw new RuntimeException();
		}
	}

	@Override
	protected boolean compute() {
		if (receiveMsgFlag) {
			return computeIfCan();
		}
		return false;
	}

	protected boolean computeIfCan() {
		int candidate = getCandidateToChange();
		if (candidate == valueAssignment) {
			return false;
		}else {
			return stochasticChange(candidate);
		}
	}

	private boolean stochasticChange(int candidate) {
		double rnd = rndStochastic.nextDouble();
		if (rnd < stochastic) {
			this.valueAssignment = candidate;
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	protected void sendMsgs() {
		if (receiveMsgFlag) {
			sendValueAssignmnetMsgs();
		}
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		receiveMsgFlag = false;
	}

	







}
