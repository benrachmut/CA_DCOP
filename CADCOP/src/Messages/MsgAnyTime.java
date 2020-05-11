package Messages;

public abstract class MsgAnyTime extends Msg<Integer> {

	public MsgAnyTime(Integer sender, Integer reciever, Permutation context, double timeStamp, 
			double timeCreated) {
		super(sender, reciever, context, timeStamp, timeCreated);
		// TODO Auto-generated constructor stub
	}


}
