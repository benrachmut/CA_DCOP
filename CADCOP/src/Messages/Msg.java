package Messages;

public abstract class Msg<Identity> {

	private int sender;
	private int reciever;
	private Object context;
	private int timeStamp;
	
	private double delay;
	private double timeCreated;
	
	
	public Msg(int sender, int reciever, Object context, int timeStamp, double delay, double timeCreated) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timeStamp = timeStamp;
	}
	
	
	
}
