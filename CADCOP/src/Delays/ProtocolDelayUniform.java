package Delays;

import java.util.Random;

public class ProtocolDelayUniform extends ProtocolDelay {

	private double ub;
	private Random rndUniform;
	public ProtocolDelayUniform( boolean isTimeStamp, double gamma, double ub) {
		super(true, isTimeStamp,gamma);

		this.ub = ub;
		
	}


	
	public ProtocolDelayUniform() {
		super(false, true,0.0);
		ub = 0;
	}
	@Override
	protected Double createDelayGivenParameters() {
		return rndUniform.nextDouble()*ub;
	}

	@Override
	protected void setSeedsGivenParameters(int dcopId) {
		rndUniform = new Random(dcopId);
		
	}
	
	protected String getStringParamets() {
		// TODO Auto-generated method stub
		return this.ub+",";
	}

	@Override
	protected boolean checkSpecificEquals(ProtocolDelay other) {
		if (other instanceof ProtocolDelayUniform) {
			ProtocolDelayUniform otherUniform = (ProtocolDelayUniform)other;
			boolean sameUB = otherUniform.getUb() == this.ub;
			return sameUB;
		}
		return false;
	}



	private double getUb() {
		// TODO Auto-generated method stub
		return this.ub;
	}


	

}
