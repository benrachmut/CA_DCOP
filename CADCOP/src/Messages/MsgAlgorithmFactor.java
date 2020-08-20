package Messages;

import AgentsAbstract.NodeId;

public class MsgAlgorithmFactor extends MsgAlgorithm{
	
    public MsgAlgorithmFactor(NodeId senderNode, NodeId recieverNode, Object context, 
    		int timeStamp) {
		super(senderNode, recieverNode, context, timeStamp);
		
	}

	public double[] getContext() {
		
		return (double[]) this.context;
		
	}
}
