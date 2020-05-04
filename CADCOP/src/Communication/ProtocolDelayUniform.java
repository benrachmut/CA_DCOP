package Communication;

import java.util.Random;

public class ProtocolDelayUniform extends ProtocolDelay {

	private double ub;
	private Random rndUniform;
	public ProtocolDelayUniform( boolean isTimeStamp, double gamma, double ub) {
		super(false, isTimeStamp,gamma);

		this.ub = ub;
		
	}


	
	public ProtocolDelayUniform() {
		super(true, true,0.0);
		ub = 0;
	}
	@Override
	public Double createDelay() {
		return rndUniform.nextDouble()*ub;
	}

	@Override
	public void setSeeds(int dcopId) {
		rndUniform = new Random(dcopId);
		
	}

	

}
