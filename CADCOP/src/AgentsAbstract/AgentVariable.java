package AgentsAbstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Messages.MsgAnyTimeDown;
import Messages.MsgAnyTimeUp;
import Messages.MsgReceive;

public abstract class AgentVariable extends Agent {

	public static String AlgorithmName;

	public static String algorithmHeader;
	public static String algorithmData;

	protected int valueAssignment;
	private Double valueAssignmentChangeCounter;
	protected int firstRandomVariable;
	protected TreeMap<NodeId, Integer[][]> neighborsConstraint; // id and matrix of constraints
	protected int[] domainArray;

	
	protected List<Context>anytimeUpToSend;
	protected List<Context>anytimeDownToSend;

	// ----------**Formations**----------
	// -----*DFS*-----
	protected NodeId dfsFather;
	protected Set<NodeId> dfsSons;
	// -----*Any time*-----
	protected NodeId anytimeFather;
	protected Set<NodeId> anytimeSons;
	

	public AgentVariable(int dcopId, int D, int id1) {
		super(dcopId, D);
		neighborsConstraint = new TreeMap<NodeId, Integer[][]>();
		this.id = id1;
		this.nodeId = new NodeId(id1);
		this.domainArray = new int[domainSize];
		createDomainArray();
		Random r = new Random(132 * id1 + 100 * dcopId);
		firstRandomVariable = r.nextInt(D);
		// resetAgent();
		valueAssignmentChangeCounter = 0.0;
		this.dfsSons = new HashSet<NodeId>();
		this.anytimeSons= new HashSet<NodeId>();
		anytimeUpToSend = new ArrayList<Context>();
		anytimeDownToSend= new ArrayList<Context>();
	}

	@Override
	public String toString() {
		return "A" + this.id;
	}
	
	
	@Override
	public void resetAgentGivenParameters() {
		valueAssignment = firstRandomVariable;
		valueAssignmentChangeCounter = 0.0;
		anytimeUpToSend = new ArrayList<Context>();
		anytimeDownToSend= new ArrayList<Context>();

		resetAgentGivenParametersV2();
	}

	/**
	 * update the Algorithm Header string
	 */
	public abstract void updateAlgorithmHeader();

	/**
	 * update the Algorithm Data string
	 */
	public abstract void updateAlgorithmData();

	/**
	 * update the Algorithm Name string
	 */
	public abstract void updateAlgorithmName();

	protected boolean setValueAssignmnet(int input) {
		if (this.valueAssignment != input) {
			this.valueAssignmentChangeCounter++;
			this.valueAssignment = input;
			return true;
		}
		return false;
	}

	public int getValueAssignment() {
		return this.valueAssignment;
	}

	private void createDomainArray() {
		for (int domainValue = 0; domainValue < domainSize; domainValue++) {
			domainArray[domainValue] = domainValue;
		}
	}

	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		this.neighborsConstraint.put(new NodeId(neighborId), constraint);
	}

	public int neighborSize() {
		return this.neighborsConstraint.size();
	}

	

	protected abstract void resetAgentGivenParametersV2();

	@Override
	public synchronized boolean reactionToAlgorithmicMsgs() {
		boolean isValueAssignmnetChange = super.reactionToAlgorithmicMsgs();
		if (MainSimulator.anyTime) {
			if (isValueAssignmnetChange ) {
				Context context_i = createContext();
				placeContextInMemory(context_i);
			}
		}
		return isValueAssignmnetChange;
		
	}
	
	public synchronized void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		Context context_i_beforeMsgUpdate = null;

		if (MainSimulator.anyTime) {
			if (!messages.isEmpty()) {
				context_i_beforeMsgUpdate = createContext();
			}
		}
		
		
		super.receiveAlgorithmicMsgs(messages);
		
		if (MainSimulator.anyTime) {
			if (isWithTimeStamp) {
				if (!messages.isEmpty()) {
					Context context_i_AfterMsgUpdate = createContext();
					if (!context_i_beforeMsgUpdate.equals(context_i_AfterMsgUpdate)) {
						placeContextInMemory(context_i_AfterMsgUpdate);
					}
				}
			}
		}
	
	}
	
	/*
	 * public synchronized void reactionToAlgorithmicMsgs() {
	 * 
	 * if (getDidComputeInThisIteration()) {
	 * 
	 * boolean isUpdate = compute(); if (isMsgGoingToBeSent(isUpdate)) {
	 * 
	 * computationCounter = computationCounter + 1; this.timeStampCounter =
	 * this.timeStampCounter + 1; this.time = this.time + 1;
	 * 
	 * if (MainSimulator.isWhatAgentDebug && this.id ==1) {
	 * System.out.println("before send msgs"); }
	 * 
	 * sendMsgs();
	 * 
	 * if (MainSimulator.isWhatAgentDebug && this.id ==1) {
	 * System.out.println("changing to false"); } changeRecieveFlagsToFalse(); } } }
	 */

	/*
	 * @Override public void run() { initialize(); while (terminationCondition()) {
	 * synchronized (this) { while (msgBoxAlgorithmic.isEmpty() &&
	 * msgBoxAnytime.isEmpty()) { try { wait(); } catch (InterruptedException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } } if
	 * (msgBoxAlgorithmic.isEmpty() == false) { reactionToAlgorithmicMsgs(); } if
	 * (msgBoxAnytime.isEmpty() == false) { reactionToAnytimeMsgs(); }
	 * 
	 * } // synch }
	 * 
	 * }
	 * 
	 */
	// --------------**TO-DO**--------------------
	public synchronized void recieveAnyTimeMsgs(List<? extends MsgAnyTime> messages) {
		
		for (MsgAnyTime msgAnyTime : messages) {
			if (msgAnyTime instanceof MsgAnyTimeUp) {
				placeContextInMemory((Context)msgAnyTime.getContext());
			}
			if (msgAnyTime instanceof MsgAnyTimeDown) {
				
			}
		}
		
		updateAgentTime(messages);

	}

	public synchronized void reactionToAnytimeMsgs() {
		// TODO Auto-generated method stub

	}

	private void sendAnytimeChangeContext() {
		// TODO Auto-generated method stub

	}

	// ------------- **TREE METHODS**-------------

	// ------------- **GENERAL USE**-------------

	public Set<NodeId> getNeigborSetId() {
		return this.neighborsConstraint.keySet();
	}

	public Double getChangeValueAssignmentCounter() {
		// TODO Auto-generated method stub
		return this.valueAssignmentChangeCounter;
	}

	public Integer[][] getMatrixWithAgent(int i) {
		if (this.neighborsConstraint.containsKey(new NodeId(i))) {
			return this.neighborsConstraint.get(new NodeId(i));
		}
		return null;
	}

	public int getTimestamp() {
		return this.timeStampCounter;
	}

	public void setDfsFather(NodeId input) {
		this.dfsFather = input;

	}

	public void addDfsSon(NodeId input) {
		this.dfsSons.add(input);
	}

	public NodeId getDfsFather() {
		// TODO Auto-generated method stub
		return this.dfsFather;
	}

}
