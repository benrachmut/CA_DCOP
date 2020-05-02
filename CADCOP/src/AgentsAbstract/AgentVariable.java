package AgentsAbstract;

import java.util.ArrayList;
import java.util.HashMap;
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

	protected int valueAssignment;
	protected int firstRandomVariable;
	protected TreeMap<Integer, Integer[][]> neighborsConstraint; // id and matrix of constraints
	protected int[] domainArray;
	private List<MsgAnyTime> msgBoxAnytime;

	// ----------**Trees**----------
	// -----*DFS*-----
	protected AgentVariable dfsFather;
	protected ArrayList<AgentVariable> dfsSons;
	protected TreeMap<Integer, Integer> dfsAbove;
	protected TreeMap<Integer, Integer> dfsBelow;
	protected int dfsLevelInTree;
	// -----*BFS*-----

	public AgentVariable(int dcopId, int D, int id1) {
		super(dcopId, D);
		this.domainArray = new int[domainSize];
		createDomainArray();
		Random r = new Random(132 * id1 + 100 * dcopId);
		firstRandomVariable = r.nextInt(D);
		resetAgent();
		dfsAbove = new TreeMap<Integer, Integer>();
		dfsBelow = new TreeMap<Integer, Integer>();

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

	public void resetAgent() {
		super.resetAgent();
		valueAssignment = firstRandomVariable;
		msgBoxAnytime = new ArrayList<MsgAnyTime>();
		neighborsConstraint = new TreeMap<Integer, Integer[][]>();
	}

	public int getValueAssignment() {
		return valueAssignment;
	}

	@Override
	public void initialize() {
		resetAgent();
		sendContextMsgs(true);
	}
	// public void receiveAnytimeMessage (List<? extends MsgAnytime> messages) {---}

	public boolean reactionToAlgorithmicMsgs() {
		boolean isUpdate = super.reactionToMsgs();

		if (isUpdate && MainSimulator.anyTime) {
			sendAnytimeChangeContext();
		}
		return isUpdate;
	}

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
	public void setDfsFather(AgentVariable currentA) {
		this.dfsFather = currentA;
	}

	public void addDfsSon(AgentVariable input) {
		if (this.dfsSons == null) {
			this.dfsSons = new ArrayList<AgentVariable>();
		}
		this.dfsSons.add(input);

	}

	public AgentVariable getDfsFather() {
		return this.dfsFather;
	}

	public int dfsSonsSize() {
		return this.dfsSons.size();
	}

	/**
	 * called by tree creator while creating the dfs tree
	 * @param id of agent above
	 * @param i counter set to zero 
	 */
	public void putInDfsAboveMap(int id, int i) {
		this.dfsAbove.put(id, i);

	}
	
	/**
	 * called by tree creator after all above map is set
	 */
	/*
	public void addBelowDFS() {
		List<Integer> temp = new ArrayList<Integer>();
		for (int n : this.neighborsConstraint.keySet()) {
			Set<Integer> isAbove = this.dfsAbove.keySet();

			boolean isAlreadyInMap = isAbove.contains(n);
			if (!isAlreadyInMap) {
				temp.add(n);
			}

		}
		
		for (Integer idTemp : temp) {
			this.putInDfsBelowMap(idTemp, 0);
		}

	}
	*/
	/*
	private void putInDfsBelowMap(int id, int i) {
		this.dfsBelow.put(id, i);
	}
	*/
	
	public boolean isDfsHead() {
		// TODO Auto-generated method stub
		return this.dfsFather == null;
	}
	
	

	public Integer getLevelInDfs() {
		// TODO Auto-generated method stub
		return this.dfsLevelInTree;
	}
	// ------------- **GENERAL USE**-------------

	public Set<Integer> getNeigborSetId() {
		return this.neighborsConstraint.keySet();
	}

	public void setDfsLevelInTree(int i) {
		this.dfsLevelInTree = i;
		for (AgentVariable dfsSon : dfsSons) {
			dfsSon.setDfsLevelInTree(i+1);
		}
		
	}


	

	

}
