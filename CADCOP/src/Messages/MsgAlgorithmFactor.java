package Messages;

import AgentsAbstract.NodeId;

public class MsgAlgorithmFactor extends MsgAlgorithm{
    private NodeId recieverNode, senderNode;
	
    public MsgAlgorithmFactor(NodeId senderNode, NodeId recieverNode, Object context, 
    		int timeStamp, double delay,double timeCreated ) {
		super(-1, -1, context, timeStamp, delay, timeCreated);
		this.recieverNode = recieverNode;
		this.senderNode = senderNode;
	}

	public NodeId getReciver() {
		return this.recieverNode;
	}
	public NodeId getSender() {
		return this.senderNode;
	}
}
