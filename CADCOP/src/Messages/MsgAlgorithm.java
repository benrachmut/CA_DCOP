package Messages;

public class MsgAlgorithm<Identity,Context>extends Msg<Identity,Context> {

	public MsgAlgorithm(Identity sender, Identity reciever, Context context, int timeStamp) {
		super(sender, reciever, context, timeStamp);
		// TODO Auto-generated constructor stub
	}

}
