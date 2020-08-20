package Delays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Delays.ProtocolDelay;

public class CreatorDelaysUniform extends CreatorDelays {

	private double[] UBs = {2,5,10};//{5,10,25,50,100};

	@Override
	protected ProtocolDelay createDefultProtocol() {
		return new ProtocolDelayUniform();
	}

	@Override
	protected Collection<? extends ProtocolDelay> createCombinationsDelay(boolean timestampBoolean, double gamma) {
		List<ProtocolDelay> ans = new ArrayList<ProtocolDelay>();
		for (double ub : UBs) {
			ans.add(new ProtocolDelayUniform(timestampBoolean, gamma, ub));
		} // sigma
		return ans;
	}
	@Override

	protected String header() {
		return "UB";

	}

}
