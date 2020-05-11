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
	protected TreeMap<Integer, Integer[][]> neighborsConstraint; // id and matrix of constraints
	protected int[] domainArray;

	// ----------**Trees**----------
	// -----*DFS*-----
	protected Integer dfsFather;
	protected Set<Integer> dfsSons;
	protected Set<Integer> dfsBelow;
	protected Set<Integer> dfsAbove;
	protected int dfsLevelInTree;
	// -----*BFS*-----

	protected Integer bfsFather;
	protected Set<Integer> bfsSons;
	protected Set<Integer> bfsBelow;
	protected Set<Integer> bfsAbove;
	protected int bfsLevelInTree;

	public AgentVariable(int dcopId, int D, int id1) {
		super(dcopId, D);
		this.domainArray = new int[domainSize];
		createDomainArray();
		Random r = new Random(132 * id1 + 100 * dcopId);
		firstRandomVariable = r.nextInt(D);
		//resetAgent();

		// -----*DFS*-----
		dfsSons = new HashSet<Integer>();
		dfsAbove = new HashSet<Integer>();
		dfsBelow = new HashSet<Integer>();
		dfsFather = -1;
		dfsLevelInTree = -1;

		// -----*BFS*-----
		bfsSons = new HashSet<Integer>();
		bfsAbove = new HashSet<Integer>();
		bfsBelow = new HashSet<Integer>();
		bfsFather = -1;
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
		// TODO Auto-generated method stub
		return this.valueAssignment;
	}

	private void createDomainArray() {
		for (int domainValue = 0; domainValue < domainSize; domainValue++) {
			domainArray[domainValue] = domainValue;
		}
	}

	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		this.neighborsConstraint.put(neighborId, constraint);
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

	private synchronized void reactionToAnytimeMsgs() {
		// TODO Auto-generated method stub

	}

	private void sendAnytimeChangeContext() {
		// TODO Auto-generated method stub

	}

	// ------------- **TREE METHODS**-------------

	// -- DFS
	public void setDfsFather(int currentA) {
		this.dfsFather = currentA;
	}

	public void addDfsSon(int inputId) {
		this.dfsSons.add(inputId);
	}

	public Integer getDfsFather() {
		return this.dfsFather;
	}

	public int dfsSonsSize() {
		return this.dfsSons.size();
	}

	public boolean isDfsHead() {
		return this.dfsFather == -1;
	}

	public void setAboveDFS(Set<Integer> aboveA) {
		this.dfsAbove = aboveA;
	}

	public void setBelowDFS(Set<Integer> belowA) {
		this.dfsBelow = belowA;
	}

	public Set<Integer> getDfsSonsIds() {
		return this.dfsSons;
	}

	public void setDfsLevelInTree(int input) {
		this.dfsLevelInTree = input;

	}

	// -- BFS
	public void setBfsFather(int currentA) {
		this.bfsFather = currentA;
	}

	public void addBfsSon(int inputId) {
		this.bfsSons.add(inputId);
	}

	public Integer getBfsFather() {
		return this.bfsFather;
	}

	public int bfsSonsSize() {
		return this.bfsSons.size();
	}

	public boolean isBfsHead() {
		return this.bfsFather == -1;
	}

	public void setAboveBFS(Set<Integer> aboveA) {
		this.bfsAbove = aboveA;
	}

	public void setBelowBFS(Set<Integer> belowA) {
		this.bfsBelow = belowA;
	}

	public Set<Integer> getBfsSonsIds() {
		return this.bfsSons;
	}

	public void setBfsLevelInTree(int input) {
		this.bfsLevelInTree = input;

	}

	// ------------- **GENERAL USE**-------------

	public Set<Integer> getNeigborSetId() {
		return this.neighborsConstraint.keySet();
	}
	public int getChangeValueAssignmentCounter() {
		// TODO Auto-generated method stub
		return this.valueAssignmentChangeCounterCounter;
	}


	

	


}
