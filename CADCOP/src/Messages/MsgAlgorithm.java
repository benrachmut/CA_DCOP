package Messages;

import javax.management.RuntimeErrorException;

import AgentsAbstract.NodeId;

public class MsgAlgorithm extends Msg{

	private boolean isArtificialMsg;
	public MsgAlgorithm(NodeId sender, NodeId reciever, Object context, int timeStamp, int time) {
		super(sender, reciever, context, timeStamp,time);
		isArtificialMsg = false;
	}
	

	public void setContext(Object input) {
		this.context = input;
	}
	public void setArtificialMsg(boolean shouldBeTrue) {
		if (shouldBeTrue == false) {
			throw new RuntimeException();
		}
	}


	public boolean getArtificialMsg() {
		// TODO Auto-generated method stub
		return this.isArtificialMsg;
	}



	




	
}
