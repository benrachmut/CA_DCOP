package Messages;

import AgentsAbstract.NodeId;

public class MsgValueAssignmnet extends MsgAlgorithm {

	public MsgValueAssignmnet(NodeId sender, NodeId reciever, Object context, int timeStamp,int time) {
		super(sender, reciever, context, timeStamp,time);
	}

	public MsgValueAssignmnet(MsgAlgorithm m) {
	
		this(m.getSenderId(), m.getRecieverId(), m.getContext(), m.getTimeStamp(), m.getTime());
		/*
		if (this.getSenderId().getId1() == 2) {
			System.out.println("from MsgValueAssignmnet");
		}
		*/
	}

	public MsgValueAssignmnet(NodeId nodeId) {
		super(nodeId);
	}

}
