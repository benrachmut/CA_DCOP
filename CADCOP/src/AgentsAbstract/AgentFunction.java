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
	private boolean flagOfFunctionForKey;

	///// ******* Constructor ******* ////

	public AgentFunction(int dcopId, int D, int id1, int id2) {
		super(dcopId, D);
		this.nodeId = new NodeId(id1, id2);
		this.variableMsgs = new TreeMap<NodeId, MsgReceive<double[]>>();
		this.nodes = new ArrayList<NodeId>();
		flagOfFunctionForKey = false;

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
		flagOfFunctionForKey = false;

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
		this.timeObject = agentVariableInference.getTimeObject();

	}
//----------------------------------------
	

	public boolean reactionToAlgorithmicMsgs() {
		synchronized (this) {

			this.atomicActionCounter = 0;

			if (getDidComputeInThisIteration()) {
				boolean isUpdate = compute();
				if (isMsgGoingToBeSent(isUpdate)) {
					if (MainSimulator.isMaxSumThreadDebug) {
						System.out.println(this + "time is " + this.time + " BEFORE because computation");
					}
					computationCounter = computationCounter + 1;
					this.timeStampCounter = this.timeStampCounter + 1;
					if (MainSimulator.isAtomicTime) {
						this.timeObject.addToTime(this.atomicActionCounter);
						this.time = this.timeObject.getTimeOfObject();
						this.atomicActionCounter = 0;

					} else {
						this.time = this.time + 1;
					}
					if (MainSimulator.isMaxSumThreadDebug) {
						System.out.println(this + "time is " + this.time + " After because computation");
					}
					this.sendMsgs();
					mailer.wakeUp();

					this.changeRecieveFlagsToFalse();

				}
				return isUpdate;

			}

			if (MainSimulator.isMaxSumThreadDebug) {
				System.err.println(this + " release key and finish reactionToAlgorithmicMsgs");
			}
			return false;
		}
	}

	protected void waitUntilMsgsRecieved() {
		synchronized (this) {

			if (getDidComputeInThisIteration() == false) {
				waitingMethodology();
				if (stopThreadCondition == true) {
					return;
				}
			}
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + " about to react to message");
			}
			this.reactionToAlgorithmicMsgs();
			flagOfFunctionForKey = true; 
			// mailer.wakeUp();
		}
		if (flagOfFunctionForKey) {
			flagOfFunctionForKey = false;
			sendInsideMsgs();
		}
	}

	protected abstract void sendInsideMsgs();

	protected void waitingMethodology() {
		try {
			isIdle = true;
			if (MainSimulator.isThreadDebug) {
				System.out.println(this + " is idle");
			}
			mailer.wakeUp();
			this.wait();
			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + " woke up after wait");
			}
			mailer.wakeUp();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		if (MainSimulator.isMaxSumThreadDebug && this.nodeId.getId2()==2 ) {
			System.err.println("AHHHHHHHHHHHH");
		}
		synchronized (this) {

			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + " recieve msgs");
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

			if (MainSimulator.isMaxSumThreadDebug) {
				System.out.println(this + " is about to notifyAll");
			}
			this.notifyAll();
			if (MainSimulator.isMaxSumThreadDebug) {
				System.err.println(this + " release key and finish receiveToAlgorithmicMsgs");
			}
		}

	}

	protected void updateAgentTime(List<? extends Msg> messages) {
		Msg msgWithMaxTime = Collections.max(messages, new MsgsMailerTimeComparator());

		if (MainSimulator.isThreadDebug && messages.size() > 1) {
			System.out.println(this.toString() + " update time upon msg recieve");
		}

		int maxAgentTime = msgWithMaxTime.getMailerTime();

		if (this.time <= maxAgentTime) {
			int oldTime = this.time;
			this.timeObject.addToTime(maxAgentTime);
			this.time = this.timeObject.getTimeOfObject();
		}

	}

}
