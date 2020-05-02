package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;

public abstract class AgentFunction extends Agent {

	// List<AgentVariable> variableNeighbors;
	protected NodeId nodeId;
	protected SortedMap<NodeId, MsgReceive<double[]>> variableMsgs;

	public AgentFunction(int dcopId, int D, int id1, int id2) {
		super(dcopId, D);
		this.nodeId = new NodeId(id1, id2);
		this.variableMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();

	}

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

}
