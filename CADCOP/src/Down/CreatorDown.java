package Down;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Delays.ProtocolDelay;

public abstract class CreatorDown {

	protected boolean[] agentDownScenarios = { false };
	protected double[] probPerMsgApproch = { 0 };

	public  List<ProtocolDown> createProtocolDelays() {
		List<ProtocolDown> ans = new ArrayList<ProtocolDown>();
		for (boolean agentDownScenario : agentDownScenarios) {
			if (agentDownScenario == false) {
				ans.add(createDefultProtocol());
			}
			else {
				for (double prob : probPerMsgApproch) {
					ans.addAll(createCombinationsDelay(prob));
				}
			}
		}
		return ans;
	}
	
	public String getHeader() {
		return "Agent Down Scenario, Prob Down Per Approch,"+header();
	}


	protected abstract String header();
		
	protected abstract Collection<? extends ProtocolDown> createCombinationsDelay(double prob);

	protected abstract ProtocolDown createDefultProtocol();
}