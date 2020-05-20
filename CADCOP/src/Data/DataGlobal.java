package Data;

import java.util.List;

import AgentsAbstract.AgentVariable;
import Main.Mailer;
import Main.MainSimulator;
import Problem.Dcop;
import Problem.Neighbor;

public abstract class DataGlobal {
	// ------**measures from DCOP**-----
	private Double globalCost;
	private Double changeValueAssignmentCounter;

	// ------**measures from mailer**-----
	private Double algorithmMsgsCounter;

	// ------**measures from mailer: any time**-----

	private Double anytimeMsgsCounter;
	private Double monotonicy;

	public DataGlobal(Dcop dcop, Mailer mailer) {

		this.globalCost = calcGlobalCost(dcop.getNeighbors());
		this.changeValueAssignmentCounter = calcChangeValueAssignmentCounter(dcop.getVariableAgents());
		this.algorithmMsgsCounter = mailer.getAlgorithmMsgsCounter();

		if (MainSimulator.anyTime) {
			this.anytimeMsgsCounter = mailer.getAnytimeMsgsCounter();
		}
		this.monotonicy = calcMonotonicy(mailer, globalCost);
	}

	private static Double calcMonotonicy(Mailer mailer, Double globalCost2) {
		Double lastGlobalCost = mailer.getLastGlobalCost();
		if (lastGlobalCost >= globalCost2) {
			return 1.0;
		} else {
			return 0.0;
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

	protected abstract String header();

	protected abstract String getToStringGivenParameters();

	public String getHeader() {
		return "Global Cost,Change Value Counter,Algorithm Message Counter,Anytime Message Counter," + header();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.globalCost + "," + this.changeValueAssignmentCounter + "," + this.algorithmMsgsCounter + ","
				+ this.anytimeMsgsCounter + "," + getToStringGivenParameters();
	}

	public Double getGlobalCost() {
		// TODO Auto-generated method stub
		return this.globalCost;
	}

}
