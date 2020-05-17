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

import com.sun.swing.internal.plaf.synth.resources.synth;

import Main.MainSimulator;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;

public abstract class AgentVariable extends Agent {

	private int valueAssignment;
	private int valueAssignmentChangeCounterCounter;
	protected int firstRandomVariable;
	protected TreeMap<NodeId, Integer[][]> neighborsConstraint; // id and matrix of constraints
	protected int[] domainArray;

	// ----------**Trees**----------
	// -----*DFS*-----
	protected NodeId dfsFather;
	protected Set<NodeId> dfsSons;
	protected Set<NodeId> dfsBelow;
	protected Set<NodeId> dfsAbove;
	protected int dfsLevelInTree;
	// -----*BFS*-----

	protected NodeId bfsFather;
	protected Set<NodeId> bfsSons;
	protected Set<NodeId> bfsBelow;
	protected Set<NodeId> bfsAbove;
	protected int bfsLevelInTree;

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

		// -----*DFS*-----
		dfsSons = new HashSet<NodeId>();
		dfsAbove = new HashSet<NodeId>();
		dfsBelow = new HashSet<NodeId>();
		dfsFather = new NodeId(-1);
		dfsLevelInTree = -1;

		// -----*BFS*-----
		bfsSons = new HashSet<NodeId>();
		bfsAbove = new HashSet<NodeId>();
		bfsBelow = new HashSet<NodeId>();
		bfsFather = new NodeId(-1);
		bfsLevelInTree = -1;

	}
	protected boolean setValueAssignmnet(int input) {
		if (this.valueAssignment !=input) {
			this.valueAssignmentChangeCounterCounter++;
			this.valueAssignment =input;
			return true;
		}
		return false;
	}
	
	protected int getValueAssignmnet() {
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

	public void resetAgentGivenParameters() {
		super.resetAgent();
		valueAssignment = firstRandomVariable;
		valueAssignmentChangeCounterCounter = 0;
	}

	public int getValueAssignment() {
		return valueAssignment;
	}

	
	public boolean reactionToAlgorithmicMsgs() {
		boolean isUpdate = super.reactionToAlgorithmicMsgs();

		if (isUpdate && MainSimulator.anyTime) {
			sendAnytimeChangeContext();
		}
		return isUpdate;
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
		// TODO Auto-generated method stub
	}

	public synchronized void reactionToAnytimeMsgs() {
		// TODO Auto-generated method stub

	}

	private void sendAnytimeChangeContext() {
		// TODO Auto-generated method stub

	}

	// ------------- **TREE METHODS**-------------

	// -- DFS
	public void setDfsFather(NodeId currentA) {
		this.dfsFather = currentA;
	}

	public void addDfsSon(NodeId inputId) {
		this.dfsSons.add(inputId);
	}

	public NodeId getDfsFather() {
		return this.dfsFather;
	}

	public int dfsSonsSize() {
		return this.dfsSons.size();
	}

	public boolean isDfsHead() {
		return this.dfsFather.getId1() == -1;
	}

	public void setAboveDFS(Set<NodeId> aboveA) {
		this.dfsAbove = aboveA;
	}

	public void setBelowDFS(Set<NodeId> belowA) {
		this.dfsBelow = belowA;
	}

	public Set<NodeId> getDfsSonsIds() {
		return this.dfsSons;
	}

	public void setDfsLevelInTree(int input) {
		this.dfsLevelInTree = input;

	}

	// -- BFS
	public void setBfsFather(NodeId currentA) {
		this.bfsFather = currentA;
	}

	public void addBfsSon(int inputId) {
		this.bfsSons.add(new NodeId(inputId));
	}

	public NodeId getBfsFather() {
		return this.bfsFather;
	}

	public int bfsSonsSize() {
		return this.bfsSons.size();
	}

	public boolean isBfsHead() {
		return this.bfsFather.getId1() == -1;
	}

	public void setAboveBFS(Set<NodeId> aboveA) {
		this.bfsAbove = aboveA;
	}

	public void setBelowBFS(Set<NodeId> belowA) {
		this.bfsBelow = belowA;
	}

	public Set<NodeId> getBfsSonsIds() {
		return this.bfsSons;
	}

	public void setBfsLevelInTree(int input) {
		this.bfsLevelInTree = input;

	}

	// ------------- **GENERAL USE**-------------

	public Set<NodeId> getNeigborSetId() {
		return this.neighborsConstraint.keySet();
	}
	public int getChangeValueAssignmentCounter() {
		// TODO Auto-generated method stub
		return this.valueAssignmentChangeCounterCounter;
	}


	

	


}
