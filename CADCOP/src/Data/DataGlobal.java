package Data;

import java.util.List;

import AgentsAbstract.AgentVariable;
import Main.Mailer;
import Main.MainSimulator;
import Problem.Dcop;
import Problem.Neighbor;

public abstract class DataGlobal {
	//------**measures from DCOP**-----
	private Double globalCost;
	private Integer changeValueAssignmentCounter;
	
	//------**measures from mailer**-----
	private Integer algorithmMsgsCounter;
	
	//------**measures from mailer: any time**-----

	private int anytimeMsgsCounter;
	
	
	public DataGlobal(Dcop dcop, Mailer mailer) {

		this.globalCost = calcGlobalCost(dcop.getNeighbors());
		this.changeValueAssignmentCounter = calcChangeValueAssignmentCounter(dcop.getVariableAgents());
		this.algorithmMsgsCounter = mailer.getAlgorithmMsgsCounter();
		
		if (MainSimulator.anyTime) {
			this.anytimeMsgsCounter = mailer.getAnytimeMsgsCounter();
		}
	
	}
	
	




	

	private static Integer calcChangeValueAssignmentCounter(AgentVariable[] variableAgents) {
		Integer ans = 0;
		for (AgentVariable a : variableAgents) {
			ans =+a.getChangeValueAssignmentCounter();
		}
		return ans;
	}





	private static double calcGlobalCost(List<Neighbor> neighbors) {
		double ans = 0.0;
		for (Neighbor n : neighbors) {
			ans+=n.getCurrentCost();
		}
		return ans;
	}		

	
	protected abstract String getHeaderGivenParameters();

	protected abstract String getToStringGivenParameters();
	
	
	public String header() {
		return "Global Cost,Change Value Counter,Algorithm Message Counter,Anytime Message Counter,"+getHeaderGivenParameters();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.globalCost+","+this.changeValueAssignmentCounter+","+this.algorithmMsgsCounter+","+this.anytimeMsgsCounter+","+getToStringGivenParameters();
	}

}
