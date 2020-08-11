package AlgorithmsInference;

import java.util.HashMap;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardVaribleSync extends MaxSumStandardVarible{

	///// ******* Variables ******* ////

	protected HashMap<NodeId, Integer> neighborsMessageIteration; 
	protected int currentIteration;

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor ******* ////

	public MaxSumStandardVaribleSync(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.neighborsMessageIteration = new HashMap<NodeId, Integer>();
		initiatNeighborsMessageIteration();
		this.currentIteration = 0;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	// OmerP - new information has arrived and the variable node will update its value assignment.
	@Override
	public boolean compute() {

		if(allMsgsForIterationReceived()) {
			
			produceNewMessages();
			chooseValueAssignment();
			
		}
		
		return true;

	}
	
	//OmerP - to reset after each run.
	@Override
	public void resetAgentGivenParametersV4() {
		
		clearHashMapIntValues(neighborsMessageIteration);
		this.currentIteration = 0;
		
	}
		
	//OmerP - update messages. 
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		MsgAlgorithmFactor newMessage = (MsgAlgorithmFactor) msgAlgorithm; // Will do casting for the msgAlgorithm.

		double[] contextFix = (double[]) newMessage.getContext(); // will cast the message object as a double[].

		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithm.getTimeStamp()); //

		functionMsgs.put(newMessage.getSenderId(), newMessageReceveid);
		
		neighborsMessageIteration.put(newMessage.getSenderId(), msgAlgorithm.getTimeStamp());

	}
	
	// OmerP - will loop over the neighbors and will send to each one of the a
	@Override
	protected void sendMsgs() {

		for (NodeId i : functionMsgs.keySet()) {
			
			mailer.sendMsg(messagesToBeSent.get(i));
			
			if(storedMessageOn) {
				
				storeNewMessage(i, messagesToBeSent.get(i).getContext());
				
			}
			
		}

		messagesToBeSent.clear();
		currentIteration++; 
		
	}
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Methods to initialize a new run  ******* ////

	//OmerP - Will initiate the list at the constructor for synchronous run. 
	public void initiatNeighborsMessageIteration() {
		
		for(NodeId i: functionMsgs.keySet()) {
			
			neighborsMessageIteration.put(i, null);
			
		}
		
	}
	
	//OmerP - To check if all the messages at the same iteration was received. 
	protected boolean allMsgsForIterationReceived() {
		
		for(NodeId i: neighborsMessageIteration.keySet()) {
			
			if(neighborsMessageIteration.get(i) != currentIteration) {
				
				return false; 
				
			}
			
		}
				
		return true; 
	}
	
}
