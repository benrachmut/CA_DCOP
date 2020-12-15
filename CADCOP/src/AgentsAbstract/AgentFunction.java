package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgsMailerTimeComparator;
import Problem.Neighbor;

public abstract class AgentFunction extends Agent {

	// List<AgentVariable> variableNeighbors;
	protected SortedMap<NodeId, MsgReceive<double[]>> variableMsgs;
	protected List<NodeId> nodes;
	protected AgentVariableInference variableNode;

	///// ******* Constructor ******* ////

	public AgentFunction(int dcopId, int D, int id1, int id2) {
		super(dcopId, D);
		this.nodeId = new NodeId(id1, id2);
		this.variableMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.nodes = new ArrayList<NodeId>();

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Initialize Methods ******* ////

	public void meetVariables(NodeId VariableOneNodeId, NodeId VariableTwoNodeId) {

		this.variableMsgs.put(VariableOneNodeId, null);
		this.variableMsgs.put(VariableTwoNodeId, null);

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Reset Methods ******* ////

	@Override
	protected void resetAgentGivenParameters() {
		this.variableMsgs = Agent.resetMapToValueNull(this.variableMsgs);
		resetAgentGivenParametersV2();
	}

	protected abstract void resetAgentGivenParametersV2();

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constraint Methods ******* ////

	public static double[][] turnIntegerToDoubleMatrix(Integer[][] input) {

		double[][] ans = new double[input.length][input[0].length];

		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans[i].length; j++) {
				ans[i][j] = (double) input[i][j];
			}
		}

		return ans;
	}

	public static double[][] transposeConstraintMatrix(double[][] input) {

		double[][] ans = new double[input.length][input.length];

		for (int i = 0; i < ans.length; i++) {

			for (int j = 0; j < ans.length; j++) {

				ans[j][i] = input[i][j];

			}

		}

		return ans;

	}

	public static long[][] turnIntegerToLongMatrix(Integer[][] input) {

		long[][] ans = new long[input.length][input[0].length];

		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans[i].length; j++) {
				ans[i][j] = (long) input[i][j];
			}
		}

		return ans;
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Getters ******* ////

	public int getVariableMsgsSize() {
		return variableMsgs.size();
	}

	@Override
	public NodeId getNodeId() {
		return this.nodeId;
	}

	public boolean checkIfNodeIsContained(NodeId nodeId) {

		if (variableMsgs.containsKey(nodeId)) {

			return true;

		}

		else {

			return false;

		}

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* New methods ******* ////

	// OmerP - Will return the all the nodes.
	public List<NodeId> getMyNodes() {

		return nodes;

	}

	// OmerP - Will add a new nodeId to the updated list.
	public void updataNodes(NodeId nodeId) {

		this.nodes.add(nodeId);

	}

	public SortedMap<NodeId, MsgReceive<double[]>> getVariableMsgs() {

		return variableMsgs;

	}

	public void informFunctionNodeAboutItsVariableNode(AgentVariableInference agentVariableInference) {
		this.variableNode = agentVariableInference;
		this.timeKey = agentVariableInference.timeKey;

	}

	@Override
	public synchronized boolean reactionToAlgorithmicMsgs() {
		this.atomicActionCounter = 0;

		if (getDidComputeInThisIteration()) {
			boolean isUpdate = compute();
			if (isMsgGoingToBeSent(isUpdate)) {
				synchronized (this.timeKey) {
					if (MainSimulator.isMaxSumThreadDebug) {
						System.out.println(this + "time is " + this.time + " BEFORE because computation");
					}
					computationCounter = computationCounter + 1;
					this.timeStampCounter = this.timeStampCounter + 1;
					if (MainSimulator.isAtomicTime) {
						this.time = this.time + this.atomicActionCounter;
						infromMyVaribleAboutTime();
						this.atomicActionCounter = 0;
					} else {
						this.time = this.time + 1;
					}

					if (MainSimulator.isMaxSumThreadDebug) {
						System.out.println(this + "time is " + this.time + " AFTER because computation");
					}
				}
			}
			return isUpdate;

		}
		return false;
	}
	// -----------------------------------------------------------------------------------------------------------//

	private void infromMyVaribleAboutTime() {
		this.variableNode.time = this.time;
		this.variableNode.infromMyFunctionsAboutTime();

	}

	@Override
	protected void updateAgentTime(List<? extends Msg> messages) {
		Msg msgWithMaxTime = Collections.max(messages, new MsgsMailerTimeComparator());
		int maxAgentTime = msgWithMaxTime.getMailerTime();
		synchronized (this.timeKey) {
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + "time is " + this.time + " BEFORE because recieve Msg");
			}
			if (this.time <= maxAgentTime) {
				int oldTime = this.time;
				this.time = maxAgentTime;
				infromMyVaribleAboutTime();
			}
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + "time is " + this.time + " AFTER because recieve Msg");
			}
		}
	}

	

	

}
