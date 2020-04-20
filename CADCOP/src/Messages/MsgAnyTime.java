package Messages;

public abstract class MsgAnyTime extends Msg<Integer, Permutation> {

	public MsgAnyTime(Integer sender, Integer reciever, Permutation context, int timeStamp) {
		super(sender, reciever, context, timeStamp);
		// TODO Auto-generated constructor stub
	}

}
