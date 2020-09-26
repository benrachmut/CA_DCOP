package Messages;

import AgentsAbstract.NodeId;

public class MsgAMDLS extends MsgValueAssignmnet {

	private int counter;
	
	public MsgAMDLS(NodeId sender, NodeId reciever, Object context, int timeStamp, int time, int counter) {
		super(sender, reciever, context, timeStamp, time);
		this.counter = counter;
	}
	
	public MsgAMDLS(MsgAMDLSColor m) {
		super(m.getSenderId(), m.getRecieverId(), m.getContext(), m.getTimeStamp(),m.getTime());
		this.counter = m.getCounter();
	}

	public int getCounter() {
		return this.counter;
	}

}
