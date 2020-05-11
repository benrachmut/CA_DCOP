package Data;

import java.util.List;

import AgentsAbstract.AgentVariable;
import Main.Mailer;
import Main.MainSimulator;
import Problem.Dcop;
import Problem.Neighbor;

public class DataGlobal {
	//------**measures from DCOP**-----
	private double globalCost;
	private double povCost;
	private int changeValueAssignmentCounter;
	
	//------**measures from mailer**-----
	private int valueAssignmentMsgsCreated;
	private int valueAssignmentMsgsRecieved;
	
	//------**measures from mailer: any time**-----

	private int anytimeMsgsCreated;
	private int anytimeMsgsRecieved;
	
	
	public DataGlobal(Dcop dcop, Mailer mailer) {

		this.globalCost = calcGlobalCost(dcop.getNeighbors());
		this.povCost = calcPovCost(dcop.getVariableAgents());
		this.valueAssignmentMsgsCreated = mailer.getValueAssignmentMsgsCreated();
		this.valueAssignmentMsgsRecieved = mailer.getValueAssignmentMsgsRecieved();
		
		if (MainSimulator.anyTime) {
			this.anytimeMsgsCreated = mailer.getAnytimeMsgsCreated();
			this.anytimeMsgsRecieved = mailer.getAnytimeMsgsRecieved();
		}
	
	}


	private double calcPovCost(AgentVariable[] variableAgents) {
		double ans = 0.0;

		for (AgentVariable a : variableAgents) {
			ans += a.getPOVcost();
		}
		return 0;
	}


	private static double calcGlobalCost(List<Neighbor> neighbors) {
		double ans = 0.0;
		for (Neighbor n : neighbors) {
			ans+=n.getCurrentCost();
		}
		return ans;
	}		


}
