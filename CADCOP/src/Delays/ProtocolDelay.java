package Delays;
import java.util.Random;

public abstract class ProtocolDelay {
	
	protected boolean imperfectCommunicationScenario;
	protected boolean isTimeStamp;
	private double gamma;
	private Random rndGamma;
	

	public ProtocolDelay(boolean imperfectCommunicationScenario, boolean isTimeStamp, double gamma) {
		
		this.imperfectCommunicationScenario = imperfectCommunicationScenario;
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
		String pc;

		if (imperfectCommunicationScenario) {
			pc = "Imperfect Communication";
		}else {
			pc = "Perfect Communication";
		}
		
		
		String timeS;
		if (this.isTimeStamp) {
			timeS = "With Time Stamp";
		}else {
			timeS = "Without Time Stamp";
		}
		
		return pc+","+timeS+","+this.gamma+","+getStringParamets();
	}


	protected abstract String getStringParamets();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProtocolDelay) {
			ProtocolDelay other = (ProtocolDelay)obj;
			
			 boolean sameImperfectCommunicationScenario  = this.imperfectCommunicationScenario == other.getIsImperfectCommunication();
			 if (this.imperfectCommunicationScenario == false && sameImperfectCommunicationScenario) {
				return true;
			}
			 boolean sameIsTimeStamp = this.isTimeStamp == other.getIsTimeStamp();
			 boolean sameGamma= this.gamma == other.getGamma();
			 boolean sameOthers = checkSpecificEquals(other);
			
			
			return  sameImperfectCommunicationScenario && sameIsTimeStamp && sameGamma && sameOthers;
		}
		return false;
	}


	protected abstract boolean checkSpecificEquals(ProtocolDelay other);


	private double getGamma() {
		// TODO Auto-generated method stub
		return this.gamma;
	}


	private boolean getIsTimeStamp() {
		// TODO Auto-generated method stub
		return this.isTimeStamp;
	}


	private boolean getIsImperfectCommunication() {
		// TODO Auto-generated method stub
		return this.imperfectCommunicationScenario;
	}


	

	

}
