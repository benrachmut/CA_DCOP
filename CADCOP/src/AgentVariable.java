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
	protected SortedMap<Integer,Integer>neighborsVariables; // id, variable  
	
	
	
	public AgentVariable( int D, int dcopId,int id) {
		super(id, D, dcopId);	
		Random r = new Random(132*id + 100*dcopId);
		firstRandomVariable = r.nextInt(D);
		this.id2=0;
		this.neighborsVariables = new TreeMap<Integer,Integer>();
		resetAgent();
	}
	
	public void meetNeighbor(int neighborId) {
		this.neighborsVariables.put(neighborId, defultMessageValue);
		
	}
	
	public  void resetAgent() {
		variableX=firstRandomVariable;
		this.neighborsVariables = Agent.turnAllValuesToDefult(this.neighborsVariables, this.defultMessageValue);
	}


	

	public int getVariableX() {
		// TODO Auto-generated method stub
		return variableX;
	}

	
	
	
}
