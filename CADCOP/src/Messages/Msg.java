package Messages;

import AgentsAbstract.NodeId;

public abstract class Msg<Identity> {

	private NodeId sender;
	private NodeId reciever;
	protected Object context;
	private int timestamp;
	
	private Integer delay;
	
	
	
	public Msg(NodeId sender, NodeId reciever, Object context, int timeStamp) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timestamp = timeStamp;
	}





	public void setDelay(Integer d) {
		this.delay = d;
		
	}
	
	public Integer getTimeStamp() {
		// TODO Auto-generated method stub
		return this.timestamp;
	}


	public NodeId getRecieverId() {
		return reciever;
	}
	
	public NodeId getSenderId() {
		return sender;
	}
	public Integer getDelay() {
		return this.delay;
	}





	public Object getContext() {
		return context;
	}
	
	
	
	
	
}
