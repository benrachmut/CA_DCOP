package Communication;

import java.util.Collection;

public class CreatorDelaysNone extends CreatorDelays {

	@Override
	protected ProtocolDelay createDefultProtocol() {
		// TODO Auto-generated method stub
		return new ProtocolDelayNone();
	}

	@Override
	protected Collection<? extends ProtocolDelay> createCombinationsDelay(boolean isTimeStamp, double gamma) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String header() {
		return ",";
	}

}
