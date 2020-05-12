package Data;

import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableSearch;
import Main.Mailer;
import Problem.Dcop;

public class DataGlobalSearch extends DataGlobal {
	private Double povCost;

	public DataGlobalSearch(Dcop dcop, Mailer mailer) {
		super(dcop, mailer);
		this.povCost = calcPovCost(dcop.getVariableAgents());

	}
	private static Double calcPovCost(AgentVariable[] variableAgents) {
		double ans = 0.0;

		for (AgentVariable a : variableAgents) {
			double aPOV = ((AgentVariableSearch)a).getPOVcost();
			if (aPOV == -1) {
				return null;
			}else {
			ans += aPOV;
			}
		}
		return ans;
	}
	@Override
	protected String header() {
		// TODO Auto-generated method stub
		return "Cost Agent POV,";
	}
	@Override
	protected String getToStringGivenParameters() {
		// TODO Auto-generated method stub
		return this.povCost+",";
	}
}
