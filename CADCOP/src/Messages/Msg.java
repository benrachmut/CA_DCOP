package Messages;

import AgentsAbstract.NodeId;

public abstract class Msg<Identity> {

	private NodeId sender;
	private NodeId reciever;
	protected Object context;
	protected int timeOfMsg;
	private int timestamp;
	
	boolean withDelay;
	private int delay;
	
	//private Integer delay;
	
	//private int mailerTime;
	public Msg(NodeId sender, NodeId reciever, Object context, int timeStamp, int agentTime) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timestamp = timeStamp;
		this.timeOfMsg = agentTime;
		this.withDelay = true;
	}
	
	public void setWithDelayToFalse() {
		this.withDelay = false;
	}
	/*
	public void setMailerTime(int mailerTime) {
		this.mailerTime = mailerTime;
	}
	public int getMailerTime() {
		return this.mailerTime;
	}

*/


/*
	public void setDelay(Integer d) {
		this.delay = d;
		
	}
	*/
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
	/*
	public Integer getDelay() {
		return this.delay;
	}
*/
	public Object getContext() {
		return context;
	}
	

	public int getTimeOfMsg() {
		return this.timeOfMsg;
	}

	public void setTimeOfMsg(int delay) {
		this.timeOfMsg = timeOfMsg+delay;
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "from "+this.sender.getId1()+" to "+ this.reciever.getId1()+ " time "+this.timeOfMsg;
	}


	public boolean isWithDelay() {
		return this.withDelay;
	}

	public int getDelay() {
		// TODO Auto-generated method stub
		return this.delay;
	}

	public void setDelay(int i) {
		this.delay =i;
		
	}

	
	
	
}
