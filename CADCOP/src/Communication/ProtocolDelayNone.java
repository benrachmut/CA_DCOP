package Communication;

public class ProtocolDelayNone extends ProtocolDelay{

	public ProtocolDelayNone() {
		super(true, true, 0);
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




}
