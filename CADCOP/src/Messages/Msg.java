package Messages;

import AgentsAbstract.NodeId;

public abstract class Msg<Identity> {

	private NodeId sender;
	private NodeId reciever;
	protected Object context;
	protected int agentTime;
	private int timestamp;
	
	boolean withDelay;
	
	private Integer delay;
	
	private int mailerTime;
	public Msg(NodeId sender, NodeId reciever, Object context, int timeStamp, int agentTime) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.context = context;
		this.timestamp = timeStamp;
		this.agentTime = agentTime;
		this.withDelay = true;
	}
	
	public void setWithDelayToFalse() {
		this.withDelay = false;
	}
	
	public void setMailerTime(int mailerTime) {
		this.mailerTime = mailerTime;
	}
	public int getMailerTime() {
		return this.mailerTime;
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
	

	public int getAgentTime() {
		// TODO Auto-generated method stub
		return this.agentTime;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "from "+this.sender.getId1()+" to "+ this.reciever.getId1()+ " time "+this.agentTime;
	}



	public void setAgentTime(int timeToSendByMailer) {
		this.agentTime = timeToSendByMailer;
		
	}

	public boolean isWithDelay() {
		return this.withDelay;
	}

	
	
	
}
