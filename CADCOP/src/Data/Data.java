package Data;

import java.util.List;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import Main.Mailer;
import Main.MainSimulator;
import Problem.Dcop;
import Problem.Neighbor;

public class Data {
	private Double time;
	private Double globalCost;
	private Double globalAnytimeCost;
	private Double changeValueAssignmentCounter;
	// ------**measures from mailer**-----
	private Double algorithmMsgsCounter;
	// ------**measures from mailer: any time**-----
	private Double anytimeMsgsCounter;
	private Double monotonicy;
	private Double povCost;

	public Data(Double time, Dcop dcop, Mailer mailer) {
		this.time = time;
		this.globalCost = calcGlobalCost(dcop.getNeighbors());
		this.changeValueAssignmentCounter = calcChangeValueAssignmentCounter(dcop.getVariableAgents());
		this.algorithmMsgsCounter = mailer.getAlgorithmMsgsCounter();

		if (MainSimulator.anyTime) {
			this.anytimeMsgsCounter = mailer.getAnytimeMsgsCounter();
		}
		this.monotonicy = calcMonotonicy(mailer, globalCost);
		this.globalAnytimeCost = calcGlobalAnytimeCost(mailer);
		this.povCost = calcPovCost(dcop.getVariableAgents());
	
	}
	
	private static Double calcPovCost(AgentVariable[] variableAgents) {
		double ans = 0.0;
		for (AgentVariable a : variableAgents) {
			double aPOV = ((AgentVariableSearch)a).getCostPov();
			if (aPOV == -1) {
				return null;
			}else {
			ans += aPOV;
			}
		}
		return ans;
	}
	
	private  Double calcMonotonicy(Mailer mailer, Double globalCost2) {
		if (time == 0) {
			return 1.0;
		}
		Double lastGlobalCost = mailer.getLastGlobalCost();
		if (lastGlobalCost >= globalCost2) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	private Double calcGlobalAnytimeCost(Mailer mailer) {
		if (time == 0) {
			return this.globalCost;
		}
		Double lastAnytimeGlobal = mailer.getLastGlobalAnytimeCost();
		if (this.globalCost<lastAnytimeGlobal) {
			return  this.globalCost;
		}else {
			return lastAnytimeGlobal;
		}
	}
	
	private static Double calcChangeValueAssignmentCounter(AgentVariable[] variableAgents) {
		Double ans = 0.0;
		for (AgentVariable a : variableAgents) {
			ans = +a.getChangeValueAssignmentCounter();
		}
		return ans;
	}

	private static double calcGlobalCost(List<Neighbor> neighbors) {
		double ans = 0.0;
		for (Neighbor n : neighbors) {
			ans += n.getCurrentCost();
		}
		return ans;
	}



	public String getHeader() {
		return "Global Cost,Change Value Counter,Algorithm Message Counter,Anytime Message Counter,Cost Agent POV,";
	}

	@Override
	public String toString() {
		return this.globalCost + "," + this.changeValueAssignmentCounter + "," + this.algorithmMsgsCounter + ","
				+ this.anytimeMsgsCounter + "," + this.povCost+",";
	}

	public Double getGlobalCost() {
		return this.globalCost;
	}



	public Double getGlobalAnytimeCost() {
		return this.globalAnytimeCost;
	}

	
	
}
