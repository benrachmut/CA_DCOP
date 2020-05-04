package Communication;
import java.util.Random;

public abstract class ProtocolDelay {
	
	protected boolean perfectCommunication;
	protected boolean isTimeStamp;
	private double gamma;
	

	public ProtocolDelay(boolean perfectCommunication, boolean isTimeStamp, double gamma) {
		
		this.perfectCommunication = perfectCommunication;
		this.isTimeStamp = isTimeStamp;
		this.gamma = gamma;
	}
	
	public abstract Double createDelay();
	abstract public void setSeeds(int dcopId);

	public boolean isWithTimeStamp() {
		// TODO Auto-generated method stub
		return isTimeStamp;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.perfectCommunication+","+this.isTimeStamp+","+this.gamma;
	}

	

}
