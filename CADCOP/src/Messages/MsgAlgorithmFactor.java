package Messages;

import AgentsAbstract.NodeId;

public class MsgAlgorithmFactor extends MsgAlgorithm{
	
    public MsgAlgorithmFactor(NodeId senderNode, NodeId recieverNode, Object context, 
    		double timeStamp) {
		super(senderNode, recieverNode, context, timeStamp);
		
	}


	

	public double[] getContext() {
		// TODO Auto-generated method stub
		return null;
	}
}
