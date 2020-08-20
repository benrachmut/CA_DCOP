package AlgorithmsInference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardVarible extends AgentVariableInference {

	///// ******* Variables ******* ////

	private boolean receiveMessageFlag; 
	private double dampingFactor = 0.9;
	protected HashMap<NodeId, double[]> storedMessges; 
	HashMap<NodeId, MsgAlgorithmFactor> messagesToBeSent;

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Control Variables ******* ////

	boolean dampingOn = false;
	boolean storedMessageOn = false;
	
	///// ******* Constructor ******* ////

	public MaxSumStandardVarible(int dcopId, int D, int id1) {

		super(dcopId, D, id1);
		this.storedMessges = new HashMap<NodeId, double[]>();
		this.messagesToBeSent = new HashMap<NodeId, MsgAlgorithmFactor>();
		this.receiveMessageFlag  = false; 
		updateAlgorithmHeader();
		updateAlgorithmData();
		updateAlgorithmName();
		
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Methods to initialize a new run  ******* ////

	//OmerP - To reset the agent if this is a new run. 
	@Override
	public void resetAgentGivenParametersV3() {
		
		this.storedMessges.clear();
		this.messagesToBeSent.clear();
		resetAgentGivenParametersV4();
	}
	
	public void resetAgentGivenParametersV4() {}
	
	// OmerP - Will send new messages for each one of the neighbors upon the
	public void initialize() {

		produceEmptyMessage();
		sendMsgs();
		
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	// OmerP - new information has arrived and the variable node will update its value assignment.
	public boolean compute() {

		if(receiveMessageFlag) {
			
			produceNewMessages();
			chooseValueAssignment();
			
		}
		
		return true;

	}

	// OmerP - will loop over the neighbors and will send to each one of the a
	@Override
	protected void sendMsgs() {

		for (NodeId i : functionMsgs.keySet()) {
			
			mailer.sendMsg(messagesToBeSent.get(i));
			
			printSentdMessage(messagesToBeSent.get(i));
			
			if(storedMessageOn) {
				
				storeNewMessage(i, messagesToBeSent.get(i).getContext());
				
			}
			
		}
		
		messagesToBeSent.clear();
		changeRecieveFlagsToFalse();
		
	}
	
	//OmerP - when a message received will update the context and flag that a message was received.
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		MsgAlgorithmFactor msgAlgorithmFactor = (MsgAlgorithmFactor) msgAlgorithm;
		
		double[] contextFix = (double[]) msgAlgorithmFactor.getContext(); // will cast the message object as a double[].

		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithmFactor.getTimeStamp()); //

		functionMsgs.put(msgAlgorithmFactor.getSenderId(), newMessageReceveid);
		
		changeRecieveFlagsToTrue(msgAlgorithm);

	}
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Produce Messages Methods ******* ////

	//OmerP - will produce new messages. 
	protected void produceNewMessages() {
		
		for (NodeId i : functionMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = produceMessage(i, sentTable); // For each specific neighbor, sum all messages excluding the table of the receiving function node.
			
			if(dampingOn) {
				
				sentTable = damping(i, sentTable); // Will produce a damped message.
				
			}
			
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0);
			messagesToBeSent.put(i, newMsg);
			printStoredMessage(newMsg);
			
		}
		
	}
	
	//OmerP - will produce empty messages. 
	protected void produceEmptyMessage() {
		
		for (NodeId i : functionMsgs.keySet()) { // Start loop over the neighbors.
		
			double[] sentTable = new double[this.domainSize];
			sentTable = produceEmptyTable(i, sentTable); // For each specific neighbor, produce an empty message.
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0); //Create new factor message.
			messagesToBeSent.put(i, newMsg); //Store the message in the message to by sent HashMap. 
				
			printStoredMessage(newMsg);
			
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Stored Message Methods ******* ////

	protected void storeNewMessage(NodeId nodeid, double[] table) {

		if (storedMessageOn) {

			storedMessges.put(nodeid, table);

		}

	}

	protected boolean areDifferentMessages(NodeId to, double[] table) {

		double[] lastStroedMessage = getLastSavedMessage(to);

		for (int i = 0; i < table.length; i++) {

			if (table[i] != lastStroedMessage[i]) {

				return true;

			}

		}

		return false;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Choose Value Assignment Method ******* ////

	//OmerP - Will choose the first value assignment.
	public void chooseFirstValueAssignment() {

		Random rnd = new Random();
		setValueAssignmnet(rnd.nextInt(this.domainSize)); 

	}

	//OmerP - Will update the value assignment.
	public void chooseValueAssignment() {

		double[] table = new double[this.domainSize];
		double bestValueAssignment = Double.MAX_VALUE;
		int valueAssignment = 0;

		for (NodeId i : functionMsgs.keySet()) { // OmerP - sum all the messages from the messages map.

			table = sumMessages(table, functionMsgs.get(i).getContext());

		}

		for (int i = 0; i < table.length; i++) { // OmerP - choose the best value assignment out of the table.

			if (table[i] < bestValueAssignment) {

				bestValueAssignment = table[i];
				valueAssignment = i;

			}

		}

		setValueAssignmnet(valueAssignment); 
		printValueAssignment(valueAssignment);

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Arithmetic Messages ******* ////

	// OmerP - produce an empty message for the first iteration.
	protected double[] produceEmptyTable(NodeId to, double[] table) {

		for (int i = 0; i < table.length; i++) {

			table[i] = 0;

		}

		return table;

	}

	// OmerP - produce message to a function node;
	protected double[] produceMessage(NodeId to, double[] table) {

		for (NodeId i : functionMsgs.keySet()) {

			if (!(i.compareTo(to) == 0)) {

				table = sumMessages(table, functionMsgs.get(i).getContext());
			}

		}

		return table;

	}

	// OmerP - Sum two tables of doubles.
	protected double[] sumMessages(double[] table1, double[] table2) {

		double[] sumTable = new double[table1.length];

		for (int i = 0; i < table1.length; i++) {

			sumTable[i] = table1[i] + table2[i];

		}

		return sumTable;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Damping Methods ******* ////

	protected double[] damping(NodeId to, double[] table) {

		if (dampingOn) {

			table = dampedMessage(table, getLastSavedMessage(to));
			return table;

		}

		else {

			return table;

		}

	}

	// OmerP - Multiplication of messages.
	protected double[] messageMultiplication(double[] table, double multiplicationFactor) {

		for (int i = 0; i < table.length; i++) {

			table[i] = table[i] * multiplicationFactor;

		}

		return table;

	}

	// OmerP - Loops over the messagesSent map and return the tableD that was saved.
	protected double[] getLastSavedMessage(NodeId recevier) {

		for (NodeId i : functionMsgs.keySet()) {

			if (i.compareTo(recevier) == 0) {

				return functionMsgs.get(i).getContext();

			}

		}

		double[] emptyMessage = new double[this.domainSize];

		return emptyMessage;

	}

	// OmerP - gets two double[] and calculate the damping vector.
	protected double[] dampedMessage(double[] currentMessage, double[] lastMessage) {

		double[] currentMessageAfterAlpha = messageMultiplication(currentMessage, (1 - dampingFactor)); // table after
																										// alpha.

		double[] lastMessageAfterAlpha = messageMultiplication(lastMessage, dampingFactor); // table after one minus
																							// alpha.

		for (int i = 0; i < this.domainSize; i++) {

			currentMessage[i] = currentMessageAfterAlpha[i] + lastMessageAfterAlpha[i];

		}

		return currentMessage;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Printing Data ******* ////

	@Override
	public void updateAlgorithmHeader() {
		
		AgentVariable.algorithmHeader = "MaxSum";
		
	}

	@Override
	public void updateAlgorithmData() {
		
		AgentVariable.algorithmData = this.dampingFactor + "";
		
	}

	@Override
	public void updateAlgorithmName() {
		
		AgentVariable.AlgorithmName = "MaxSum";
		
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Flags Methods ******* ////
	
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {
		
		this.receiveMessageFlag = true;

		
	}

	@Override
	protected void changeRecieveFlagsToFalse() {
		
		this.receiveMessageFlag = false;
		
		
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Getters ******* ////

	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm MsgAlgorithm) {

		int timestamp = functionMsgs.get(MsgAlgorithm.getSenderId()).getTimestamp(); //OmerP - will get the timestamp of the messages. 
		
		return timestamp; 

	}

	// -----------------------------------------------------------------------------------------------------------//
	
	///// ******* Clear HashMap without loosing ket ******* ////
	
	//OmerP - will clear the HashMap from values double. 
	protected void clearHashMapDoubleValues(HashMap<NodeId, double[]> hashMapToClear) {
		
		for(NodeId i: hashMapToClear.keySet()) {
			
			hashMapToClear.put(i, null);
			
		}
		
	}
	
	//OmerP - will clear the HashMap from values double. 
	protected void clearHashMapIntValues(HashMap<NodeId, Integer> hashMapToClear) {
		
		for(NodeId i: hashMapToClear.keySet()) {
			
			hashMapToClear.put(i, null);
			
		}
		
	}

	@Override
	protected boolean getDidComputeInThisIteration() {
		// TODO Auto-generated method stub
		return receiveMessageFlag;
	}
	
	// -----------------------------------------------------------------------------------------------------------//
	
	///// ******* Print Messages ******* ////

	protected void printStoredMessage(MsgAlgorithmFactor msg) {
		
		System.out.println("VariableNode:(" + msg.getSenderId().getId1() + "," + msg.getSenderId().getId2() + ") STORED a message for FunctionNode ("
				
				+ msg.getRecieverId().getId1() + "," + msg.getRecieverId().getId2() + ") with message context: " + Arrays.toString(msg.getContext()) + ".\n");
		
	}
	
	protected void printSentdMessage(MsgAlgorithmFactor msg) {
		
		System.out.println("VariableNode:(" + msg.getSenderId().getId1() + "," + msg.getSenderId().getId2() + ") SENT a message for FunctionNode ("
				
				+ msg.getRecieverId().getId1() + "," + msg.getRecieverId().getId2() + ") with message context: " + Arrays.toString(msg.getContext()) + ".\n");
		
	}
	
	protected void printReceivedMessage(MsgAlgorithmFactor msg) {
		
		System.out.println("VariableNode:(" + msg.getRecieverId().getId1() + "," + msg.getRecieverId().getId2() + ") RECEIVED a message from FunctionNode ("
				
				+ msg.getSenderId().getId1() + "," + msg.getSenderId().getId2() + ") with message context: " + Arrays.toString(msg.getContext()) + ".\n");
		
	}
	
	protected void printValueAssignment(int valueAssignment) {
		
		System.out.println("VariableNode:(" + this.getNodeId().getId1() + "," + this.getNodeId().getId2() + ") value assignment is:" + valueAssignment +".\n");
		
	}
	
	
	
	
	
	
}
