package AlgorithmSearch;

import java.util.Random;


import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgValueAssignmnet;

public class DSA_ASY extends DSA {

	private boolean receiveMsgFlag;

	public DSA_ASY(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.receiveMsgFlag = false;
	}
	
	public DSA_ASY(int dcopId, int D, int id1, double stochastic) {
		super( dcopId, D,  id1, stochastic);
		this.receiveMsgFlag = false;
	}
	

	@Override
	public void updateAlgorithmName() {
		AgentVariable.AlgorithmName = "DSA_ASY";
	}
	
	
	@Override
	protected void updateRecieveMsgFlagTrue(MsgAlgorithm msgAlgorithm) {
		this.receiveMsgFlag = true;
	}
	

	@Override
	protected boolean compute() {
		if (receiveMsgFlag) {
			return computeIfCan();
		}
		return false;
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

	@Override
	protected void resetAgentGivenParametersV4() {
		receiveMsgFlag = false;		
	}

	


}
