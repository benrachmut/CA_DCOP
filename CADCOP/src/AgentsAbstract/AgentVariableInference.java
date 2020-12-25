package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import AlgorithmsInference.MaxSumStandardFunction;
import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgsMailerTimeComparator;

public abstract class AgentVariableInference extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<double[]>> functionMsgs;
	protected Map<NodeId, AgentFunction> functionNodes;
	protected boolean flagOfInferenceForKey;

	public AgentVariableInference(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.functionMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.functionNodes = new TreeMap<NodeId, AgentFunction>();
		flagOfInferenceForKey = false;
	}

	public int getFunctionMsgsSize() {

		return functionMsgs.size();

	}

	@Override
	public void resetAgentGivenParametersV2() {
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);
		resetAgentGivenParametersV3();
		flagOfInferenceForKey = false;
	}

	protected abstract void resetAgentGivenParametersV3();

	@Override
	public NodeId getNodeId() {
		// TODO Auto-generated method stub
		return this.nodeId;
	}

	// To add with Ben.
	public boolean checkIfNodeIsContained(NodeId nodeId) {

		if (functionMsgs.containsKey(nodeId)) {

			return true;

		}

		else {

			return false;

		}

	}

	///// ******* New methods ******* ////

	// OmerP - New meetFunction method.
	public void meetFunction(List<NodeId> nodes) {

		for (int i = 0; i < nodes.size(); i++) {

			this.functionMsgs.put(nodes.get(i), null);

		}

	}

	// OmerP - New meetFunction method.
	public void meetFunction(NodeId nodeId) {

		functionMsgs.put(nodeId, null);

	}

	// OmerP - Will return the all the nodes.
	public SortedMap<NodeId, MsgReceive<double[]>> getMyFunctionMessage() {

		return this.functionMsgs;

	}

	public void holdTheFunctionNode(AgentFunction af) {
		this.functionNodes.put(af.nodeId, af);
		af.informFunctionNodeAboutItsVariableNode(this);

	}

	public int getFunctionNodesSize() {
		// TODO Auto-generated method stub
		return this.functionNodes.size();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A_" + this.nodeId;
	}

	// -----------------------------------------------------------------------------------------------------------//






	
	
	
	
	
	
	

}
