package Messages;

import AgentsAbstract.NodeId;

public abstract class MsgAnyTime extends Msg<Integer> {

	public MsgAnyTime(NodeId sender, NodeId reciever, Permutation context, double timeStamp) {
		super(sender, reciever, context, timeStamp);
		// TODO Auto-generated constructor stub
	}


}
