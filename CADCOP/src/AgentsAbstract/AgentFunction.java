package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Problem.Neighbor;

public abstract class AgentFunction extends Agent {

	// List<AgentVariable> variableNeighbors;
	protected SortedMap<NodeId, MsgReceive<double[]>> variableMsgs;
	protected List<NodeId> nodes; 
	
	public AgentFunction(int dcopId, int D, int id1, int id2) {
		super(dcopId, D);
		this.nodeId = new NodeId(id1, id2);
		this.variableMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.nodes = new ArrayList<NodeId>();
	
	}

	public int getVariableMsgsSize() {
		return variableMsgs.size();
	}

	@Override
	public NodeId getNodeId() {
		return this.nodeId;
	}

	@Override
	public void resetAgent() {
		super.resetAgent();
		this.variableMsgs = Agent.resetMapToValueNull(this.variableMsgs);
	}

	public static Double[][] turnIntegerToDoubleMatrix(Integer[][] input) {

		Double[][] ans = new Double[input.length][input[0].length];

		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans[i].length; j++) {
				ans[i][j] = (double) input[i][j];
			}
		}

		return ans;
	}
	
	public void meetVariables(NodeId VariableOneNodeId, NodeId VariableTwoNodeId) {
		
		this.variableMsgs.put(VariableOneNodeId, null);
		this.variableMsgs.put(VariableTwoNodeId, null);

	}

	public boolean checkIfNodeIsContained(NodeId nodeId) {
		
		if(variableMsgs.containsKey(nodeId)) {
			
			return true;
			
		}
		
		else {
			
			return false;
			
		}
		
	}
	
	
	///// ******* New methods ******* ////
	
	//OmerP - Will return the all the nodes. 
	public List<NodeId> getMyNodes() {
		
		return nodes;
		
	}
	
	//OmerP - Will add a new nodeId to the updated list. 
	public void updataNodes(NodeId nodeId){
		
		this.nodes.add(nodeId);

	}
	
	//-----------------------------------------------------------------------------------------------------------//

	
}
