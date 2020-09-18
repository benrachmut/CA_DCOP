package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import AlgorithmsInference.MaxSumStandardFunction;
import Messages.MsgReceive;

public abstract class AgentVariableInference extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<double[]>> functionMsgs;

	public AgentVariableInference(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.functionMsgs = new TreeMap<NodeId, MsgReceive<double[]>> ();
	}

	public int getFunctionMsgsSize() {

		return functionMsgs.size();

	} 
	
	@Override
	public void resetAgentGivenParametersV2() {
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);
		resetAgentGivenParametersV3();
	}

	protected abstract void resetAgentGivenParametersV3();

	@Override
	public NodeId getNodeId() {
		// TODO Auto-generated method stub
		return this.nodeId;
	}
	
	//To add with Ben.
	public boolean checkIfNodeIsContained(NodeId nodeId) {
		
		if(functionMsgs.containsKey(nodeId)) {
			
			return true;
			
		}
		
		else {
			
			return false;
			
		}
		
	}
	
	///// ******* New methods ******* ////
	
	//OmerP - New meetFunction method. 
	public void meetFunction(List<NodeId> nodes) {
		
		for(int i = 0 ; i < nodes.size() ; i++) {
			
			this.functionMsgs.put(nodes.get(i), null);
			
		}
		
	}
		
	//OmerP - New meetFunction method. 
	public void meetFunction(NodeId nodeId) {
		
		functionMsgs.put(nodeId, null);
		
	}
	
	//OmerP - Will return the all the nodes. 
	public SortedMap<NodeId, MsgReceive<double[]>> getMyFunctionMessage() {
		
		return this.functionMsgs;
		
	}
	
	
	//-----------------------------------------------------------------------------------------------------------//

	
	
	
}
