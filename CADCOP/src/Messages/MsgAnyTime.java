package Messages;

import AgentsAbstract.NodeId;

public abstract class MsgAnyTime extends Msg<Integer> {

	public MsgAnyTime(NodeId sender, NodeId reciever, Permutation context, int timeStamp, int time) {
		super(sender, reciever, context, timeStamp,  time);
		// TODO Auto-generated constructor stub
	}


}
