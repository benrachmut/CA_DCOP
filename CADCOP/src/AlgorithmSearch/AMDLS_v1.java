package AlgorithmSearch;

import java.util.HashSet;
import java.util.Set;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;

public class AMDLS_v1 extends AgentVariableSearch {
	private Set<NodeId> below;
	private Set<NodeId> above;


	public AMDLS_v1(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		below = new HashSet<NodeId>();
		above = new HashSet<NodeId>();

		updateAlgorithmHeader();
		updateAlgorithmData();
	}

	
	@Override
	protected void resetAgentGivenParametersV3() {
		// TODO Auto-generated method stub
		
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
		AgentVariable.AlgorithmName = "AMDLS_v1";
		
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
	protected boolean getDidComputeInThisIteration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void sendMsgs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		// TODO Auto-generated method stub
		
	}


	public void setBelow(Set<NodeId> below) {
		this.below.addAll(below);
		
	}
	
	public void setAbove(Set<NodeId> above) {
		this.above.addAll(above);
		
	}

	
}
