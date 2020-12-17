package AgentsAbstract;

public class TimeObject {

	private int timeOfObject;

	public TimeObject(int timeOfObject) {
		super();
		this.timeOfObject = timeOfObject;
	}

	public int getTimeOfObject() {
		return timeOfObject;
	}

	public void setTimeOfObject(int timeOfObject) {
		this.timeOfObject = timeOfObject;
	}

	public void addToTime(int atomicActionCounter) {
		this.timeOfObject += atomicActionCounter;
		
	}
	
	
}
