package Delays;

import java.util.Random;

public class ProtocolDelayNormal extends ProtocolDelay {

	private double sigma, mu;
	private Random rndUNormal;

	public ProtocolDelayNormal(boolean isTimeStamp, double gamma, double sigma, double mu) {
		super(true, isTimeStamp, gamma);

		this.sigma = sigma;
		this.mu = mu;
	}

	public ProtocolDelayNormal() {
		super(false, true, 0.0);
		this.sigma = 0;
		this.mu = 0;
	}
	
	@Override
	protected  Double createDelayGivenParameters() {
		double ans =  rndUNormal.nextGaussian() * sigma + mu;
		if (ans<0) {
			return 0.0;
		}
		else {
			return ans;
		}
	}

	@Override
	protected void setSeedsGivenParameters(int dcopId) {
		rndUNormal = new Random(dcopId);
	}

	@Override
	protected String getStringParamets() {
		// TODO Auto-generated method stub
		return this.sigma+","+this.mu+",";
	}

	@Override
	protected boolean checkSpecificEquals(ProtocolDelay other) {
		if (other instanceof ProtocolDelayNormal) {
			ProtocolDelayNormal otherNormal = (ProtocolDelayNormal)other;
			boolean sameMu = otherNormal.getMu() == this.mu;
			boolean sameSigma = otherNormal.getSigma() == this.sigma;
			return sameMu && sameSigma;
		}
		return false;
	}

	private double getSigma() {
		// TODO Auto-generated method stub
		return this.sigma;
	}

	private double getMu() {
		// TODO Auto-generated method stub
		return this.mu;
	}

}
