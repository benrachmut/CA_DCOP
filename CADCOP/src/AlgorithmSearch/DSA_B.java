package AlgorithmSearch;

import java.util.Random;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

abstract public class DSA_B extends AgentVariableSearch {
	protected static double stochastic = 0.7;
	protected double rndForDebug; // for debug
	protected Random rndStochastic;
	protected boolean canCompute;

	public DSA_B(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.rndStochastic = new Random(this.dcopId * 10 + this.id * 100);
		canCompute = false;
		updateAlgorithmHeader();
		updateAlgorithmData();
	}

	@Override
	protected void resetAgentGivenParametersV3() {
		this.rndStochastic = new Random(this.dcopId * 10 + this.id * 100);
		canCompute = false;
		rndForDebug = 0;

		resetAgentGivenParametersV4();
	}

	protected abstract void resetAgentGivenParametersV4();

	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Stochastic";
	}

	@Override
	public void updateAlgorithmData() {
		AgentVariable.algorithmData = this.stochastic + "";
	}

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		if (msgAlgorithm instanceof MsgValueAssignmnet) {
			return getTimestampOfValueAssignmnets(msgAlgorithm);

		} else {
			throw new RuntimeException();
		}

	}

	@Override
	protected boolean compute() {
		// if (canCompute) {
		int candidate = getCandidateToChange_B();
		if (candidate == valueAssignment) {
			return false;
		} else {
			return stochasticChange(candidate);
		}
		// }
		// return false;
	}

	protected boolean stochasticChange(int candidate) {
		double rnd = rndStochastic.nextDouble();
		this.rndForDebug = rnd;
		if (rnd < stochastic) {
			this.valueAssignment = candidate;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void sendMsgs() {
		sendValueAssignmnetMsgs();
	}

	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		updateMsgInContextValueAssignmnet(msgAlgorithm);
	}

	@Override
	public boolean getDidComputeInThisIteration() {
		return canCompute;
	}

}
