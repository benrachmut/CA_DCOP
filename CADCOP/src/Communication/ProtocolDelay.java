package Communication;
import java.util.Random;

public abstract class ProtocolDelay {
	
	protected boolean perfectCommunication;
	protected boolean isTimeStamp;
	private double gamma;
	private Random rndGamma;
	

	public ProtocolDelay(boolean perfectCommunication, boolean isTimeStamp, double gamma) {
		
		this.perfectCommunication = perfectCommunication;
		this.isTimeStamp = isTimeStamp;
		this.gamma = gamma;
	}
	
	
	public Double createDelay() {
		double rnd = rndGamma.nextDouble();
		if (rnd<gamma) {
			return null;
		}
		else {
			return createDelayGivenParameters();
		}
	}
	protected abstract Double createDelayGivenParameters();
	protected abstract void setSeedsGivenParameters(int dcopId) ;

	public void setSeeds(int dcopId) {
		this.rndGamma = new Random(dcopId*145);
		this.setSeedsGivenParameters(dcopId);
	}

	public boolean isWithTimeStamp() {
		// TODO Auto-generated method stub
		return isTimeStamp;
	}

	@Override
	public String toString() {
		String timeS;
		if (this.isTimeStamp) {
			timeS = "With Time Stamp";
		}else {
			timeS = "Without Time Stamp";
		}
		
		return this.perfectCommunication+","+timeS+","+this.gamma+","+getStringParamets();
	}


	protected abstract String getStringParamets();


	

	

}
