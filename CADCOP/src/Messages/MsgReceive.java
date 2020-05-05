package Messages;

public class MsgReceive<Context> {

	private Context context;
	private Double timeStamp;
	
	public MsgReceive(Context context, Double timeStamp) {
		super();
		this.context = context;
		this.timeStamp = timeStamp;
	}
	
	public Context getContext() {
		return this.context;
	}
	public double getTimeStamp() {
		return this.timeStamp;
	}
	
	
}
