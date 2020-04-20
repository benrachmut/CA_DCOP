package Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Problem.MainSimulator;

public abstract class AgentVariable<Identity, Context> extends Agent<Identity, Context> {

	protected int variableX;
	protected int firstRandomVariable;
	protected TreeMap<Integer, Integer[][]> neighborsConstraint;
	protected int[] D;
	private int defultMessageValue;
	private List<MsgAnyTime> msgBoxAnytime;

	public AgentVariable(int dcopId, int id1, int D) {
		super(dcopId, id1);
		this.D = new int[D];
		createDomainArray();
		Random r = new Random(132 * id1 + 100 * dcopId);
		firstRandomVariable = r.nextInt(D);
		defultMessageValue = -1;
		resetAgent();

	}

	private void createDomainArray() {
		for (int domainValue = 0; domainValue < D.length; domainValue++) {
			D[domainValue] = domainValue;
		}
	}

	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		//this.neighborsVariables.put(neighborId, defultMessageValue);
		this.neighborsConstraint.put(neighborId, constraint);

	}

	public int neighborSize() {
		return this.neighborsConstraint.size();
	}

	public void resetAgent() {
		super.resetAgent();
		variableX = firstRandomVariable;
		msgBoxAnytime = new ArrayList<MsgAnyTime>();
		neighborsConstraint = new TreeMap<Integer, Integer[][]>();
	}

	
	public int getVariableX() {
		return variableX;
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
			//sendAnytimeChangeContext();
		}
		return isUpdate;
	}
	
	
	

	
	@Override
	public void run() {
		initialize();
		while (terminationCondition()) {
			synchronized (this) {
				while (msgBoxAlgorithmic.isEmpty() && msgBoxAnytime.isEmpty()) {
					wait();
				}
				if (msgBoxAlgorithmic.isEmpty() == false) {
					reactionToAlgorithmicMsgs();
				}
				if (msgBoxAnytime.isEmpty() == false) {
					reactionToAnytimeMsgs();
				}

			}
		}

	}
	
	
	

	//--------------**TO-DO**--------------------
	public void recieveAnyTimeMsgs(List<? extends MsgAnyTime> messages) {
		// TODO Auto-generated method stub
	}
	
	private void reactionToAnytimeMsgs() {
		// TODO Auto-generated method stub
		
	}
}
