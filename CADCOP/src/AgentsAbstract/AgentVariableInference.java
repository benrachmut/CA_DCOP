package AgentsAbstract;

import java.util.SortedMap;
import java.util.TreeMap;

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

	public void meetFunction(NodeId nodeId) {
		this.functionMsgs.put(nodeId, null);

	}

	@Override
	public void resetAgent() {
		super.resetAgent();
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);

	}

	@Override
	public NodeId getNodeId() {
		// TODO Auto-generated method stub
		return this.nodeId;
	}
	


}
