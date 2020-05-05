package Messages;

public abstract class MsgAnyTime extends Msg<Integer> {

	public MsgAnyTime(Integer sender, Integer reciever, Permutation context, double timeStamp, double delay,
			double timeCreated) {
		super(sender, reciever, context, timeStamp, delay, timeCreated);
		// TODO Auto-generated constructor stub
	}


}
