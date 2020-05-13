package Messages;

public abstract class Msg<Identity> {

	private int sender;
	private int reciever;
	private Object context;
	private Double timeStamp;
	
	private Double delay;
	
	
	
	public Msg(int sender, int reciever, Object context, double timeStamp) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timeStamp = timeStamp;
	}





	public void setDelay(double d) {
		this.delay = d;
		
	}
	
	public Double getTimeStamp() {
		// TODO Auto-generated method stub
		return this.getTimeStamp();
	}


	public int getRecieverId() {
		return reciever;
	}
	public Double getDelay() {
		return this.delay;
	}





	public Object getContext() {
		return context;
	}
	
	
	
	
	
}
