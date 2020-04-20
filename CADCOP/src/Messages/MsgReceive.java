package Messages;

public class MsgReceive<Context> {

	private Context context;
	private Integer timeStamp;
	
	public MsgReceive(Context context, Integer timeStamp) {
		super();
		this.context = context;
		this.timeStamp = timeStamp;
	}
	
	
}
