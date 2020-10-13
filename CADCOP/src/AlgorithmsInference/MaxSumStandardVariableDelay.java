package AlgorithmsInference;
import java.util.HashMap;

import AgentsAbstract.NodeId;
import AlgorithmsInference.MaxSumMemory;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive; 

public class MaxSumStandardVariableDelay extends MaxSumStandardVarible {

	///// ******* Variables ******* ////

	protected HashMap<NodeId, MaxSumMemory> neighborsMemory; 
	protected int neighborsSize; 
	protected int timeStampToLook; 
	protected boolean isSync = false; 
	private boolean print = false;
	private boolean damping = true; 
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor and initialize Methods******* ////

	public MaxSumStandardVariableDelay(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.neighborsMemory = new HashMap<NodeId, MaxSumMemory>();
		this.neighborsSize = this.functionMsgs.size();
		this.timeStampToLook = 0; 
		
	}
	
	public void updateNodeId() {
		this.nodeId = new NodeId(id+1);
	}

	//OmerP - A method to initialize the memory of the agent will add the neighbors. 
	protected void initializeNeighborsMemory() {
		
		for(NodeId i: functionMsgs.keySet()) {
			
			MaxSumMemory newNeighborMemory = new MaxSumMemory(this.nodeId, i, this.domainSize);
			neighborsMemory.put(i, newNeighborMemory);
			
		}
		
		
	}
	
	///// ******* Main Methods ******* ////
	
	public void resetAgentGivenParametersV4() {
		
		this.neighborsSize = this.functionMsgs.size();
		this.neighborsMemory.clear();
		this.timeStampToLook = 0; 
		this.computationCounter = 0;
		resetAgentGivenParametersV5();
		
	}
	
	public void resetAgentGivenParametersV5() {}	
	
	// OmerP - Will send new messages for each one of the neighbors upon the
	public void initialize() {

		initializeNeighborsMemory();
		produceEmptyMessage();
		sendMsgs();
		
	}
	
	// OmerP - new information has arrived and the variable node will update its value assignment.
	public boolean compute() {

		if(isSync) {
		
			if(didRecieveAllMessagesPerIteration(this.timeStampToLook)) { //Only if all the message from the same iteration were received the agent will start to perform computation.  
				
				chooseValueLongAssignment(); 
				produceNewMessages(); 
				this.timeStampToLook++; 
				
			}
		
		}else {
			
			//if(this.receiveMessageFlag == true) {
				
				//chooseValueLongAssignment(); 
				//produceNewMessages(); 
				chooseValueLongAssignmentForAsyncVersion();
				produceNewMessageForAsyncVersion();
				this.timeStampToLook++; 
				
			//}
			
			
		}
		
		return true;
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
			
			functionMsgs.put(msgAlgorithmFactor.getSenderId(), newMessageReceveid);
			if(print) {printReceivedMessage(msgAlgorithmFactor);}
			//changeRecieveFlagsToTrue(msgAlgorithm);
			//if(print) {printFlag();}
			
		}
		
		


	}
	
	// OmerP - will loop over the neighbors and will send to each one of the neighbors. //Need to modify !!!!!
	@Override
	public void sendMsgs() {

		for(NodeId i: neighborsMemory.keySet()) { //Loop over the neighborsMemory. 
			
			mailer.sendMsg(messagesToBeSent.get(i)); //Get the message that need to be sent. 
			
			//if(print) {printSentdMessage(messagesToBeSent.get(i));} //A print for debug.
			
			if(storedMessageOn) {
				
				storeNewMessage(i, messagesToBeSent.get(i).getContext());
				
			}
			
		}
		
		this.computationCounter++; 
		messagesToBeSent.clear();  //When finish sending all the messages will clear the messages to be sent. 
		clearMemoryFromAllNeighbors(this.timeStampToLook);
		
		if(isSync == false) {
			
			changeRecieveFlagsToFalse(); //Lower the flag down. 
			if(print) {printFlag();} //A print for debug.
			
		}
						
	}
	
	@Override
	protected void produceNewMessages() {
		
		for(NodeId i: neighborsMemory.keySet()) {
			
			createNewMessageFromMemory(this.timeStampToLook, i);
			
		}
		
		
		
	}
	
	@Override
	public void chooseValueLongAssignment() {
		
		long[] table = new long[this.domainSize];
		long bestValueAssignment = Long.MAX_VALUE; //Best value that is initialized to a big number. 
		int valueAssignment = 0; //What that will assigned. 
		
		for(NodeId i: neighborsMemory.keySet()) {
			
			MaxSumMemory maxSumMemory = neighborsMemory.get(i);
			double[] context = maxSumMemory.findContextByIteration(this.timeStampToLook);
			table = sumMessageAsLong(table, context);
			
		}
		

		if(dust) {
			table = addLongDust(table);
		}
		
		for (int i = 0; i < table.length; i++) { // OmerP - choose the best value assignment out of the table.

			if (table[i] < bestValueAssignment) {

				bestValueAssignment = table[i];
				valueAssignment = i;

			}

		}

		setValueAssignmnet(valueAssignment); 
		if(print) {printValueAssignment(valueAssignment, table);}
		
		
	}
	
	
	///// ******* Methods for the Async Version  ******* ////

	protected void produceNewMessageForAsyncVersion() {
		
		for (NodeId i : functionMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = produceMessage(i, sentTable); // For each specific neighbor, sum all messages excluding the table of the receiving function node.
			sentTable = subtractMinimumValueD(sentTable);
			MsgAlgorithmFactor newMsg; 
			
			if(damping) {
				
				sentTable = damping(i, sentTable); // Will produce a damped message.
				storedMessges.put(i, sentTable); //Will store the new message
				
			}
			

			newMsg = new MsgAlgorithmFactor(this.getNodeId(), i , sentTable, this.computationCounter, this.time);	
			messagesToBeSent.put(i, newMsg);
			
		}
		
	}
	
	public void chooseValueLongAssignmentForAsyncVersion() {
		
		long[] table = new long[this.domainSize];
		long bestValueAssignment = Long.MAX_VALUE; //Best value that is initialized to a big number. 
		int valueAssignment = 0; //What that will assigned. 
		
		for (NodeId i : functionMsgs.keySet()) { //Create the belief of the agent. 

			double[] context = new double[this.domainSize];
			
			try {
				
				context = functionMsgs.get(i).getContext();
				
				}
			
			catch(NullPointerException e) {
				
				context	= produceEmptyMessageForNullPointerExeption();
				
				}
			
			
			table = sumMessageAsLong(table, context);

		}

		if(dust) {
			table = addLongDust(table);
		}
		
		for (int i = 0; i < table.length; i++) { // OmerP - choose the best value assignment out of the table.

			if (table[i] < bestValueAssignment) {

				bestValueAssignment = table[i];
				valueAssignment = i;

			}

		}

		setValueAssignmnet(valueAssignment); 
		if(print) {printValueAssignment(valueAssignment, table);}
		
		
	}

	protected double[] produceEmptyMessageForNullPointerExeption() {
		
		double[] emptyTable = new double[this.domainSize];
		
		for(int i=0 ; i < emptyTable.length; i++) {
			
			emptyTable[i] = 0;
		
		}
		
		return emptyTable;
		
	}
	
	// OmerP - produce message to a function node;
	protected double[] produceMessage(NodeId to, double[] table) {

		for (NodeId i : functionMsgs.keySet()) {

			if (!(i.compareTo(to) == 0)) {

				double[] context = new double[this.domainSize]; 
				
				try {
					
					context = functionMsgs.get(i).getContext();
					
					}
				
				catch(NullPointerException e) {
					
					context	= produceEmptyMessageForNullPointerExeption();
					
					}
				
				table = sumMessages(table, context);
			}

		}

		return table;

	}
	

	// -----------------------------------------------------------------------------------------------------------//
	
	///// ******* MaxSumMemory Methods ******* ////

	//OmerP - A method that will store new context in the memory according to the iteration. 
	protected void storeNewContextInMemory(MsgAlgorithmFactor receivedMessage) {
		
		MaxSumMemory maxSumMemory = neighborsMemory.get(receivedMessage.getSenderId());
		Integer timeStamp = receivedMessage.getTimeStamp();
		double[] context = receivedMessage.getContext(); 
		//Print what i got to store...
		maxSumMemory.putContextInMemory(timeStamp, context);
		
	}
	
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

		for(NodeId i: neighborsMemory.keySet()) {
				
			if(!(i.compareTo(receiverNode) == 0)) { //If i is not the same as the receiver node. 
					
				MaxSumMemory maxSumMemory = neighborsMemory.get(i); //Will get the relevant memory. 
				double[] context = maxSumMemory.getMemory().get(iterationToLook); //Will return the double of the memory in the relevant iteration. 
				sentTable = sumMessages(sentTable, context); //Will sum the messages. 
				
				
				}
		
		}
		
		sentTable = subtractMinimumValueD(sentTable);
		
		if(damping) {
			
			sentTable = damping(receiverNode, sentTable); // Will produce a damped message.
			storedMessges.put(receiverNode, sentTable); //Will store the new message
		
		}
		
		
		MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), receiverNode , sentTable, this.computationCounter, this.time);
		messagesToBeSent.put(receiverNode, newMsg);
		
	}
	
	//OmerP - will produce empty messages. 
	@Override
	protected void produceEmptyMessage() {
		
		for(NodeId i: neighborsMemory.keySet()) { //Looping over the neighborsMemory. 
			
			double[] sentTable = new double[this.domainSize]; //Create a new table. 
			sentTable = produceEmptyTable(i, sentTable); // For each specific neighbor, produce an empty message.
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, this.computationCounter, this.time); //Create new factor message.
			messagesToBeSent.put(i, newMsg); //Store the message in the message to by sent HashMap. 
			
			if(damping) { //If damping is on it will store the new message so it could restore it for the next message that will be created. 
				
				storedMessges.put(i, newMsg.getContext());
				
			}
			
		}
		
	}
	
	protected void clearMemoryFromAllNeighbors(int iterationToLook) {
		
		for(NodeId i: neighborsMemory.keySet()) {
			
			MaxSumMemory maxSumMemory = neighborsMemory.get(i); //Will get the relevant memory. 
			maxSumMemory.deleteContextFromMemory(iterationToLook-1); //After the message was summed it will delete the message from the memory. 

		}
		
		
		
		
	}
	
	///// ******* Print Methods ******* ////

	protected void printMessageUsedForCalculations() {
		
		
		
		
	}
	
	
	
	
}
