package Messages;

import AgentsAbstract.NodeId;

public class MsgAlgorithmMaxSum extends MsgAlgorithm{
    private NodeId recieverNode, senderNode;
	
    public MsgAlgorithmMaxSum(int sender, int reciever, Object context, int timeStamp, double delay,
			double timeCreated, NodeId senderNode, NodeId recieverNode) {
		super(sender, reciever, context, timeStamp, delay, timeCreated);
		this.recieverNode = recieverNode;
		this.senderNode = senderNode;
	}

}
