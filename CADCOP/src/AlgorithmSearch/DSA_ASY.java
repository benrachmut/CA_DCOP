package AlgorithmSearch;

import java.util.Random;


import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class DSA_ASY extends DSA {


	public DSA_ASY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		updateAlgorithmName();

	}
	
	public DSA_ASY(int dcopId, int D, int id1, double stochastic) {
		super( dcopId, D,  id1, stochastic);
		updateAlgorithmName();
	}
	

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_ASY";
	}
	
	
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		this.canCompute = true;
	}
	

	@Override
	protected void changeRecieveFlagsToFalse() {
		canCompute = false;
	}

	@Override
	protected void resetAgentGivenParametersV4() {
		// no special fields....
	}



}
