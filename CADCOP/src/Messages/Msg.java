package Messages;

public abstract class Msg<Identity,Context> {

	private Identity sender;
	private Identity reciever;
	private Context context;
	private int timeStamp;
	
	private double delay;
	private double timeCreated;
	
	
	public Msg(Identity sender, Identity reciever, Context context, int timeStamp, double delay, double timeCreated) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timeStamp = timeStamp;
	}
	
	
	
}
