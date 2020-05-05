package Messages;

public abstract class Msg<Identity> {

	private int sender;
	private int reciever;
	private Object context;
	private double timeStamp;
	
	private double delay;
	private double timeCreated;
	
	
	public Msg(int sender, int reciever, Object context, double timeStamp, double delay, double timeCreated) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timeStamp = timeStamp;
	}


	public double getDelay() {
		// TODO Auto-generated method stub
		return this.delay;
	}


	public void setDelay(double d) {
		this.delay = d;
		
	}
	
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return this.getTimeStamp();
	}
	
	
	
}
