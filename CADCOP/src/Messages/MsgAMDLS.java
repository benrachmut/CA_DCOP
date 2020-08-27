package Messages;

import AgentsAbstract.NodeId;

public class MsgAMDLS extends MsgValueAssignmnet {

	private int counter;
	
	public MsgAMDLS(NodeId sender, NodeId reciever, Object context, int timeStamp, int time, int counter) {
		super(sender, reciever, context, timeStamp, time);
		this.counter = counter;
	}
	
	public int getCounter() {
		return this.counter;
	}

}
