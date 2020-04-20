import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public abstract class AgentVariable extends Agent{
	
	protected int variableX;
	protected int firstRandomVariable;
	protected SortedMap<Integer,Integer> neighborsVariables; // id, variable  
	protected TreeMap<Integer, Integer[][]> neighborsConstraint;
	protected int [] D;
	
	
	public AgentVariable(int dcopId, int agentId, int D ) {
		super(dcopId,agentId);	
		this.D = new int[D];
		createDomainArray();

		Random r = new Random(132*id + 100*dcopId);
		firstRandomVariable = r.nextInt(D);
		this.id2=0;
		this.neighborsVariables = new TreeMap<Integer,Integer>();
		neighborsConstraint = new TreeMap<Integer,Integer[][]>();

		resetAgent();
	}
	private void createDomainArray() {
		for (int domainValue = 0; domainValue < D.length; domainValue++) {
			D[domainValue] = domainValue;
		}
	}
	public void meetNeighbor(int neighborId, Integer[][]constraint) {
		this.neighborsVariables.put(neighborId, defultMessageValue);
		this.neighborsConstraint.put(neighborId, constraint);

	}
	
	public int neighborSize() {
		return this.neighborsVariables.size();
	}
	
	public  void resetAgent() {
		variableX=firstRandomVariable;
		this.neighborsVariables = Agent.turnAllValuesToDefult(this.neighborsVariables, this.defultMessageValue);
	}

	public int getVariableX() {
		// TODO Auto-generated method stub
		return variableX;
	}

	//public void receiveAnytimeMessage (List<? extends MsgAnytime> messages) {---}

	
	
}
