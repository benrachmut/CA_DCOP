package AgentsAbstract;

import java.util.SortedMap;

import Messages.MsgReceive;

public abstract class AgentVariableInference extends AgentVariable{


	protected  NodeId nodeId;
	protected SortedMap <NodeId, MsgReceive<double[]>> functionMsgs; 
	
	
	public AgentVariableInference(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.nodeId = new NodeId(id1);
	}


	public void meetFunction(NodeId nodeId) {
		this.functionMsgs.put(nodeId, null);
		
	}
	
	@Override
	public void resetAgent() {
		super.resetAgent();
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);

	}
	
	



	
	

	
}
