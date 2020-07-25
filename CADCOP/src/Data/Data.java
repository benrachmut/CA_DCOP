package Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import Main.Mailer;
import Main.MainSimulator;
import Problem.Dcop;
import Problem.Neighbor;

public class Data {
	private int time;
	private Double globalCost;
	private Double globalAnytimeCost;
	private Double changeValueAssignmentCounter;
	// ------**measures from mailer**-----
	private Double algorithmMsgsCounter;
	// ------**measures from mailer: any time**-----
	private Double anytimeMsgsCounter;
	private Double monotonicy;
	private Double povCost;

	public Data(Entry<Integer, List<Data>> e) {
		this.time = e.getKey();

		List<List<Double>> colletionPerFields = createColletionsPerField(e.getValue());
			
			this.globalCost = Statistics.mean(colletionPerFields.get(0));
			this.monotonicy = Statistics.mean(colletionPerFields.get(1));
			this.povCost = Statistics.mean(colletionPerFields.get(2));
			this.globalAnytimeCost = Statistics.mean(colletionPerFields.get(3));
			this.changeValueAssignmentCounter = Statistics.mean(colletionPerFields.get(4));
			this.algorithmMsgsCounter = Statistics.mean(colletionPerFields.get(5));
	}

	
	
	
	private List<List<Double>> createColletionsPerField(List<Data> datas) {
		List<List<Double>> ans = new ArrayList<List<Double>>();
		for (int i = 0; i < 6; i++) {
			ans.add(new ArrayList<Double>());
		}
		
		for (Data d : datas) {
			ans.get(0).add(d.globalCost);
			ans.get(1).add(d.monotonicy);
			ans.get(2).add(d.povCost);
			ans.get(3).add(d.globalAnytimeCost);
			ans.get(4).add(d.changeValueAssignmentCounter);
			ans.get(5).add(d.algorithmMsgsCounter);
		}
		return ans;
	}

	public Data(int time, Dcop dcop, Mailer mailer) {
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
			double aPOV = ((AgentVariableSearch) a).getCostPov();
			if (aPOV == -1) {
				return null;
			} else {
				ans += aPOV;
			}
		}
		return ans/2.0;
	}

	private Double calcMonotonicy(Mailer mailer, Double globalCost2) {
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
		if (this.globalCost < lastAnytimeGlobal) {
			return this.globalCost;
		} else {
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

	
	
	public static String header() {
		String ans ="";
		if (!MainSimulator.anyTime) {
			ans = ans+ "Iteration"+","
					+"Global View Cost" + "," 
					+ "Monotonicy" + "," 
					+ "Agent View Cost" +","
					+"Global Anytime Cost" +  ","
					+ "Value Assignmnet Counter" + "," 
					+ "Algorithm Msgs Counter";
			
		} else {
			throw new RuntimeException();
		}
		return ans;
	}

	@Override
	public String toString() {
		return this.time+","
				+this.globalCost + "," 
				+this.monotonicy + "," 
				+ this.povCost+ "," 
				+ this.globalAnytimeCost + "," 
				+ this.changeValueAssignmentCounter + ","
				+ this.algorithmMsgsCounter;
	}

	public Double getGlobalCost() {
		return this.globalCost;
	}

	public Double getGlobalAnytimeCost() {
		return this.globalAnytimeCost;
	}

}
