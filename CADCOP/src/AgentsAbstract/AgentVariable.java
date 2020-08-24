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

	// ----------**Formations**----------
	// -----*DFS*-----
	protected NodeId dfsFather;
	protected Set<NodeId> dfsSons;
	
	

	public AgentVariable(int dcopId, int D, int id1) {
		super(dcopId, D);
		neighborsConstraint = new TreeMap<NodeId, Integer[][]>();
		this.id = id1;
		this.nodeId = new NodeId(id1);
		this.domainArray = new int[domainSize];
		createDomainArray();
		Random r = new Random(132 * id1 + 100 * dcopId);
		firstRandomVariable = r.nextInt(D);
		//resetAgent();
		valueAssignmentChangeCounter = 0.0;
		this.dfsSons = new HashSet<NodeId>();	
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A"+this.id+"_|N|="+this.neighborSize();
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
		if (this.valueAssignment !=input) {
			this.valueAssignmentChangeCounter++;
			this.valueAssignment =input;
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

	@Override
	public void resetAgentGivenParameters() {
		valueAssignment = firstRandomVariable;
		valueAssignmentChangeCounter = 0.0;
		resetAgentGivenParametersV2();
	}

	

	
	protected abstract void resetAgentGivenParametersV2();

	public void reactionToAlgorithmicMsgs() {
		super.reactionToAlgorithmicMsgs();
		if (MainSimulator.anyTime) {
			sendAnytimeChangeContext();
		}
	}
	

/*
	@Override
	public void run() {
		initialize();
		while (terminationCondition()) {
			synchronized (this) {
				while (msgBoxAlgorithmic.isEmpty() && msgBoxAnytime.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (msgBoxAlgorithmic.isEmpty() == false) {
					reactionToAlgorithmicMsgs();
				}
				if (msgBoxAnytime.isEmpty() == false) {
					reactionToAnytimeMsgs();
				}

			} // synch
		}

	}

*/
	// --------------**TO-DO**--------------------
	public synchronized void recieveAnyTimeMsgs(List<? extends MsgAnyTime> messages) {
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
