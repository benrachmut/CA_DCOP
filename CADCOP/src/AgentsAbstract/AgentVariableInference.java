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
import Messages.MsgReceive;
import Messages.MsgsMailerTimeComparator;

public abstract class AgentVariableInference extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<double[]>> functionMsgs;
	protected Map<NodeId, AgentFunction> functionNodes;

	public AgentVariableInference(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.functionMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.functionNodes = new TreeMap<NodeId, AgentFunction>();
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

	// -----------------------------------------------------------------------------------------------------------//

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
						infromMyFunctionsAboutTime();
						this.atomicActionCounter = 0;

					} else {
						this.time = this.time + 1;
					}
				}
			}
			return isUpdate;

		}
		return false;
	}

	public void infromMyFunctionsAboutTime() {
		for (AgentFunction af : this.functionNodes.values()) {
			af.time = time;
		}

	}

	protected void updateAgentTime(List<? extends Msg> messages) {
		Msg msgWithMaxTime = Collections.max(messages, new MsgsMailerTimeComparator());

		if (MainSimulator.isThreadDebug && messages.size() > 1) {
			System.out.println(this.toString() + " update time upon msg recieve");
		}

		int maxAgentTime = msgWithMaxTime.getMailerTime();

		synchronized (this.timeKey) {
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + "time is " + this.time + " BEFORE because recieve Msg");
			}
			if (this.time <= maxAgentTime) {
				int oldTime = this.time;
				this.time = maxAgentTime;
				infromMyFunctionsAboutTime();

			}
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + "time is " + this.time + " AFTER because recieve Msg");
			}

		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A_" + this.nodeId;
	}

}
