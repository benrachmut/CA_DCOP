package Communication;

import java.util.Random;

public class ProtocolDelayNormal extends ProtocolDelay {

	private double sigma, mu;
	private Random rndUNormal;

	public ProtocolDelayNormal(boolean isTimeStamp, double gamma, double sigma, double mu) {
		super(false, isTimeStamp, gamma);

		this.sigma = sigma;
		this.mu = mu;
	}

	public ProtocolDelayNormal() {
		super(true, true, 0.0);
		this.sigma = 0;
		this.mu = 0;
	}

	@Override
	public Double createDelay() {
		double ans =  rndUNormal.nextGaussian() * sigma + mu;
		if (ans<0) {
			return 0.0;
		}
		else {
			return ans;
		}
	}

	@Override
	public void setSeeds(int dcopId) {
		rndUNormal = new Random(dcopId);

	}

}
