package Messages;

import AgentsAbstract.NodeId;

public class MsgAnyTimeDown extends MsgAnyTime {

	public MsgAnyTimeDown(NodeId sender, NodeId reciever, Permutation context, double timeStamp,
			double timeCreated) {
		super(sender, reciever, context, timeStamp);
	}



}
