package AlgorithmSearch;

import java.util.Random;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

abstract public class DSA extends AgentVariableSearch {
	protected double stochastic;
	protected Random rndStochastic;

	public DSA(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		stochastic = 0.7;
		this.rndStochastic = new Random(this.dcopId*10+this.id*100);
	}
	
	public DSA(int dcopId, int D, int id1, double stochastic) {
		this( dcopId, D,  id1);
		this.stochastic = stochastic;
	}
	
	@Override
	protected void resetAgentGivenParametersV3() {
		this.rndStochastic = new Random(this.dcopId*10+this.id*100);
		resetAgentGivenParametersV4();
	}
	
	protected abstract void resetAgentGivenParametersV4();

	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Stochastic";
	}

	@Override
	public void updateAlgorithmData() {
		AgentVariable.algorithmData = this.stochastic+"";
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
	
}
