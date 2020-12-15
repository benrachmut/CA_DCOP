package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import AlgorithmsInference.MaxSumStandardFunction;
import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;

public abstract class AgentVariableInference extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<double[]>> functionMsgs;
	protected HashMap<NodeId, AgentFunction> functionNodes;
	protected Object timeSynchKey;

	public AgentVariableInference(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.functionMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.functionNodes = new HashMap<NodeId, AgentFunction>();
		this.timeSynchKey = new Object();

	}

	public Object getTimeSynchKey() {
		return this.timeSynchKey;
	}
	public int getFunctionMsgsSize() {

		return functionMsgs.size();

	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A_"+this.nodeId;
	}
	@Override
	public void resetAgentGivenParametersV2() {
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);
		this.time = Integer.valueOf(1);

		for (AgentFunction af : functionNodes.values()) {
			af.updateTimeObject(this.time);
		}

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

	public int getFunctionNodesSize() {

		return this.functionNodes.size();
	}

	public void holdTheFunctionNode(AgentFunction input) {
		this.functionNodes.put(input.getNodeId(), input);
		input.updateTimeObject(this.timeSynchKey);
	}

	public boolean reactionToAlgorithmicMsgs() {
		synchronized (this.timeSynchKey) {
			this.atomicActionCounter = 0;
			if (getDidComputeInThisIteration()) {
				boolean isUpdate = compute();
				if (isMsgGoingToBeSent(isUpdate)) {
					computationCounter = computationCounter + 1;
					this.timeStampCounter = this.timeStampCounter + 1;
					if (MainSimulator.isAtomicTime) {
						this.time = this.time + this.atomicActionCounter;
						this.atomicActionCounter = 0;
					} else {
						this.time = this.time + 1;
					}
				}
				this.sendMsgs();
				this.changeRecieveFlagsToFalse();
				return isUpdate;
			}
			return false;
		}

	}

	public void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {

		Collection<String> aaa = new ArrayList<String>();

		for (MsgAlgorithm msgAlgorithm : messages) {
			aaa.add(msgAlgorithm.getSenderId().toString());
		}

		if (MainSimulator.isMaxSumThreadDebug) {
			System.out.println(this.nodeId + " needs key to recieve msgs from " + aaa);
		}
		synchronized (this.timeSynchKey) {
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this.nodeId + " recieve msgs from " + aaa);
			}

			for (MsgAlgorithm msgAlgorithm : messages) {
				if (this.isWithTimeStamp) {
					int currentDateInContext;
					try {
						currentDateInContext = getSenderCurrentTimeStampFromContext(msgAlgorithm);
					} catch (NullPointerException e) {
						currentDateInContext = -1;
					}
					if (msgAlgorithm.getTimeStamp() > currentDateInContext) {
						updateMessageInContextAndTreatFlag(msgAlgorithm);
					}
				} else {
					updateMessageInContextAndTreatFlag(msgAlgorithm);
				}
			}
			
		
			updateAgentTime(messages);

			
			isIdle = false;
			if (!messages.isEmpty()) {
				if (MainSimulator.isThreadDebug) {
					System.out
							.println("mailer update " + this + " context, msg time_" + messages.get(0).getAgentTime());
					System.out.println(this + " is NOT idle");
				}
			}
			
			
			this.timeSynchKey.notifyAll();
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this.nodeId + " finish recieve msgs so wakes up all");
			}
		}
	}

	protected void waitUntilMsgsRecieved() {
		synchronized (this.timeSynchKey) {
			while (getDidComputeInThisIteration() == false) {
				waitingMethodology();
				if (stopThreadCondition == true) {
					return;
				}
			}
			this.reactionToAlgorithmicMsgs();
		}
	}

	protected void waitingMethodology() {
		try {
			isIdle = true;
			if (MainSimulator.isThreadDebug) {
				System.out.println(this + " is idle");
			}
			mailer.wakeUp();

			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this.nodeId + " went to sleep");
			}
			this.timeSynchKey.wait();
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this.nodeId + " woke up");
			}
			mailer.wakeUp();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	// -----------------------------------------------------------------------------------------------------------//

}
