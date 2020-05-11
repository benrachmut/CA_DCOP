package Messages;

import AgentsAbstract.NodeId;

public class MsgAlgorithmFactor extends MsgAlgorithm{
    private NodeId recieverNode, senderNode;
	
    public MsgAlgorithmFactor(NodeId senderNode, NodeId recieverNode, Object context, 
    		double timeStamp, double timeCreated ) {
		super(-1, -1, context, timeStamp,  timeCreated);
		this.recieverNode = recieverNode;
		this.senderNode = senderNode;
	}

	public NodeId getReciverNodeId() {
		return this.recieverNode;
	}
	public NodeId getSenderNodeId() {
		return this.senderNode;
	}

	@Override
	public int getRecieverId() {
		// TODO Auto-generated method stub
		return -1;
	}
}
