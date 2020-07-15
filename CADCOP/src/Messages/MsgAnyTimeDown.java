package Messages;

import AgentsAbstract.NodeId;

public class MsgAnyTimeDown extends MsgAnyTime {

	public MsgAnyTimeDown(NodeId sender, NodeId reciever, Permutation context, int timeStamp,
			int timeCreated) {
		super(sender, reciever, context, timeStamp);
	}



}
