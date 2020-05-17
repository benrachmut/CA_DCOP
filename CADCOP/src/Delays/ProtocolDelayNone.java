package Delays;

public class ProtocolDelayNone extends ProtocolDelay{

	public ProtocolDelayNone() {
		super(false, true, 0);
	}

	

	@Override
	protected void setSeedsGivenParameters(int dcopId) {
		// TODO Auto-generated method stub
	}


	@Override
	protected Double createDelayGivenParameters() {
		// TODO Auto-generated method stub
		return 0.0;
	}



	@Override
	protected String getStringParamets() {
		// TODO Auto-generated method stub
		return "";
	}



	@Override
	protected boolean checkSpecificEquals(ProtocolDelay other) {
		if (other instanceof ProtocolDelayNone) {
			return true;
		}
		return false;
	}




}
