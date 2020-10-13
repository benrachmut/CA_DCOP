package AlgorithmsInference;

import java.util.HashMap;

import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardFunctionDelay extends MaxSumStandardFunction  {

	///// ******* Variables ******* ////

	protected HashMap<NodeId, MaxSumMemory> neighborsMemory; 
	protected int neighborsSize; 
	protected int timeStampToLook; 
	protected boolean isSync = false; 
	private boolean print = false;
	
	// -----------------------------------------------------------------------------------------------------------//
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A_"+this.nodeId.toString();
	}
	///// ******* Constructor and initialize Methods******* ////

	public MaxSumStandardFunctionDelay(int dcopId, int D, int id1, int id2, Integer[][] constraints) {
		
		super(dcopId, D, id1, id2, constraints);
		this.neighborsMemory = new HashMap<NodeId, MaxSumMemory>();
		this.neighborsSize = this.variableMsgs.size();
		this.timeStampToLook = 0; 
		
	}

	public MaxSumStandardFunctionDelay(int dcopId, int D, int id1, int id2, double[][] constraints) {
		
		super(dcopId, D, id1, id2, constraints);
		this.neighborsMemory = new HashMap<NodeId, MaxSumMemory>();
		this.neighborsSize = this.variableMsgs.size();
		this.timeStampToLook = 0; 
		
	}
	
	//OmerP - A method to initialize the memory of the agent will add the neighbors. 
	protected void initializeNeighborsMemory() {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			MaxSumMemory newNeighborMemory = new MaxSumMemory(this.nodeId, i, this.domainSize);
			neighborsMemory.put(i, newNeighborMemory);
			
		}
		
		
	}
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	//OmerP - Reset parameters at the end of the run.
	public void resetAgentGivenParametersV3() {
		
		this.neighborsSize = this.variableMsgs.size();
		this.neighborsMemory.clear();
		this.timeStampToLook = 0; 
		this.computationCounter = 0; 
		resetAgentGivenParametersV4();
		
	}
	
	public void resetAgentGivenParametersV4() {}	
	
	//OmerP - Will send new messages for each one of the neighbors upon the
	public void initialize() {

		initializeNeighborsMemory();
		
		if(isSync) {
		
			produceOnlyConstraintMessages();
			sendMsgs();
			
		}
	}
	
	//OmerP - new information has arrived and the variable node will update its value assignment.
	public boolean compute() {

		if(isSync) {
		
			if(didRecieveAllMessagesPerIteration(this.timeStampToLook)) { //Only if all the message from the same iteration were received the agent will start to perform computation.  
			
				produceNewMessage();
				this.timeStampToLook++; 
			
			}
		
		}else{
			
			//if(this.receiveMessageFlag == true) {
		
				//produceNewMessage();
				produceNewMessageForAsyncVersion();
				this.timeStampToLook++; 
				
			//}
			
		}
		
		
		return true;
	}
	
	//OmerP - will loop over the neighbors and will send to each one of the a message - Need to check during run. 
	@Override
	public void sendMsgs() {
		
		for(NodeId i: messagesToBeSent.keySet()) {
			
			mailer.sendMsg(messagesToBeSent.get(i));
			
			//if(print) {printSentMessage(messagesToBeSent.get(i));}
			
			if(storedMessageOn) {
				
				storedMessgesTable.put(i, messagesToBeSent.get(i).getContext());
				
			}
				
		}
		
		
		this.computationCounter++; 
		
		if(isSync == false) {
		
			changeRecieveFlagsToFalse();
			if(print) {printFlag();}
		
		}
		messagesToBeSent.clear();
		clearMemoryFromAllNeighbors(this.timeStampToLook);
		
	}
	
	//OmerP - when a message received will update the context and flag that a message was received.
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		MsgAlgorithmFactor msgAlgorithmFactor = (MsgAlgorithmFactor) msgAlgorithm;
		
		double[] contextFix = (double[]) msgAlgorithmFactor.getContext(); // will cast the message object as a double[].

		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithmFactor.getTimeStamp()); //

		if(isSync) {
			
			storeNewContextInMemory(msgAlgorithmFactor);
			
		} 
		
		else {
			
			variableMsgs.put(msgAlgorithmFactor.getSenderId(), newMessageReceveid);
			if(print) {printReceivedMessage(msgAlgorithmFactor);}
			//changeRecieveFlagsToTrue(msgAlgorithm);
			//if(print) {printFlag();}
				
		}
		
	}
	
	//OmerP - Loop over the neighbors memory and operate createNewMessageFromMemory method.
	protected void produceNewMessage() {
		
		for(NodeId i : neighborsMemory.keySet()) {
			
			createNewMessageFromMemory(this.timeStampToLook, i);
			
		}
		
		
	}
	
	//OmerP - Will produce a message from the constraint matrix without the addition of messages - need to fix for memory.
	protected void produceOnlyConstraintMessages() {
		
		for(NodeId i: neighborsMemory.keySet()) { //Looping over the neighbors Memory. 
			
			double[] sentTable = new double[this.domainSize]; //Create a new table.
			double[][] constraintMatrix = new double[this.domainSize][this.domainSize]; //Create the constraint matrix. 
			constraintMatrix = neighborsConstraintMatrix.get(i); //Get the constraint matrix of the relevant node id that will be sent to. 
			sentTable = getBestValueTable(constraintMatrix); //Get the best value of each value of the domain.
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, this.timeStampCounter, this.time); //Create a new message factor. 
			messagesToBeSent.put(i, newMsg);
					
		}
	}
	
	//OmerP - produce new messages. 
	@Override
	protected void produceNewMessages() {
		
		for(NodeId i: neighborsMemory.keySet()) { //Looping over the neighbors Memory. 
		
			createNewMessageFromMemory(this.timeStampToLook, i);
			
		}
	}
	
	///// ******* Methods for the Async Version ******* ////

	protected void produceNewMessageForAsyncVersion() {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = produceFunctionMessage(i);
			MsgAlgorithmFactor newMsg;
			newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, this.timeStampCounter, this.time); //Create a new message factor. 
			messagesToBeSent.put(i, newMsg);
			//printStoredMessage(newMsg);
			
		}
		
	}
	
	protected double[] produceEmptyMessageForNullPointerExeption() {
		
		double[] emptyTable = new double[this.domainSize];
		
		for(int i=0 ; i < emptyTable.length; i++) {
			
			emptyTable[i] = 0;
		
		}
		
		return emptyTable;
		
	}
	
	// -----------------------------------------------------------------------------------------------------------//
	
	///// ******* MaxSumMemory Methods ******* ////
	
	//OmerP - A method that will check if all the message from the same iteration was received. 
	protected boolean didRecieveAllMessagesPerIteration(int iterationToLook) {
		
		int counter = 0; 
		
		for(NodeId i: neighborsMemory.keySet()) {
			
			MaxSumMemory maxSumMemory = neighborsMemory.get(i);
			if(maxSumMemory.checkIfInMemory(iterationToLook)) {
				
				counter++; 
				
			}
		
		}
		
		if(counter == this.neighborsSize) {
			
			return true;
			
		}
		
		else {
			
			return false; 
			
		}
			
	}
	
	//OmerP - When all messages with the same iteration have been received this method will create new messages to send. 
	protected void createNewMessageFromMemory(int iterationToLook, NodeId receiverNode) {
		
		double[] sentTable = new double[this.domainSize]; //Create a new table for the new message. 
		double[][] constraintMatrix = neighborsConstraintMatrix.get(receiverNode); //Get the relevant Constraint Matrix.	
		double [] otherNodeIdMessage = getOtherNodeIdMessage(iterationToLook, receiverNode);
		constraintMatrix = addTableToMatrix(otherNodeIdMessage, constraintMatrix); //Will add the second variable node message to the constraint matrix.
		sentTable = getBestValueTable(constraintMatrix);  //The best value will be chosen out of the matrix. 
		MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), receiverNode , sentTable, this.computationCounter, this.time); //Prepare the message.
		messagesToBeSent.put(receiverNode, newMsg);
		
		
	}
		
	//OmerP - A method that will store new context in the memory according to the iteration. 
	protected void storeNewContextInMemory(MsgAlgorithmFactor receivedMessage) {
		
		MaxSumMemory maxSumMemory = neighborsMemory.get(receivedMessage.getSenderId());
		Integer timeStamp = receivedMessage.getTimeStamp();
		double[] context = receivedMessage.getContext(); 
		maxSumMemory.putContextInMemory(timeStamp, context);
		
	}
	
	protected double[] getOtherNodeIdMessage(int iterationToLook, NodeId receiverNode) {
		
		double[] context = new double[this.domainSize];

		for(NodeId i: neighborsMemory.keySet()) {
			
			if(receiverNode.compareTo(i) != 0) {
				
				MaxSumMemory maxSumMemory = neighborsMemory.get(i); //Will get the relevant memory. 
				context = maxSumMemory.getMemory().get(iterationToLook); //Will return the double of the memory in the relevant iteration. 
				
			}
			
		}
		
		return context; 
		
	}
	
	protected void clearMemoryFromAllNeighbors(int iterationToLook) {
		
		for(NodeId i: neighborsMemory.keySet()) {
			
			MaxSumMemory maxSumMemory = neighborsMemory.get(i); //Will get the relevant memory. 
			maxSumMemory.deleteContextFromMemory(iterationToLook-1); //After the message was summed it will delete the message from the memory. 

		}
	}
	
	// -----------------------------------------------------------------------------------------------------------//

    ///// ******* Getters ******* ////
	
	//OmerP - will return the message that is from the other variable node. 
	protected double[] getOtherNodeIdMessage(NodeId to) {
		
		
		if(variableMsgs.size() == 1) { //Check the size of the neighbors, if it is 1, it is not full yet and will return null. 
			
			return null; 
			
		}
			
		NodeId[] temp = new NodeId[2]; //Create an array of NodeId in the size of 2. 
		
		variableMsgs.keySet().toArray(temp); 
				
		double[] context = new double[this.domainSize]; 
		
		if(to.equals(temp[0])) {
			
			try {
				
				context = variableMsgs.get(temp[1]).getContext();
				
				}
			
			catch(NullPointerException e) {
				
				context	= produceEmptyMessageForNullPointerExeption();
				
			}
			
			return context; //Will return the message that received from the other variable node that was received. 
		}
		
		else {
			
			try {
				
				context = variableMsgs.get(temp[0]).getContext(); //Will return the message that received from the other variable node that was received. 
				
				}
			
			catch(NullPointerException e) {
				
				context	= produceEmptyMessageForNullPointerExeption();
				
			}
			
			return context; //Will return the message that received from the other variable node that was received. 
			
		}
		
	}
	
	
	
	
	
}
