package AlgorithmsInference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardFunctionDelay extends MaxSumStandardFunction {

	///// ******* Variables ******* ////

	protected HashMap<NodeId, MaxSumMemory> neighborsMemory;
	protected HashMap<NodeId, Boolean> messagesArrivedControl;
	protected int neighborsSize;
	protected int timeStampToLook;
	protected boolean isSync = false;
	private boolean print = false;

	// -----------------------------------------------------------------------------------------------------------//

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A_" + this.nodeId.toString();
	}

	///// ******* Constructor and initialize Methods******* ////

	public MaxSumStandardFunctionDelay(int dcopId, int D, int id1, int id2, Integer[][] constraints) {

		super(dcopId, D, id1, id2, constraints);
		this.neighborsMemory = new HashMap<NodeId, MaxSumMemory>();
		this.messagesArrivedControl = new HashMap<NodeId, Boolean>();
		this.neighborsSize = this.variableMsgs.size();
		this.timeStampToLook = 0;
		initializeNeighborsMemory();

	}

	public MaxSumStandardFunctionDelay(int dcopId, int D, int id1, int id2, double[][] constraints) {

		super(dcopId, D, id1, id2, constraints);
		this.neighborsMemory = new HashMap<NodeId, MaxSumMemory>();
		this.messagesArrivedControl = new HashMap<NodeId, Boolean>();
		this.neighborsSize = this.variableMsgs.size();
		this.timeStampToLook = 0;
		initializeNeighborsMemory();

	}

	// OmerP - A method to initialize the memory of the agent will add the
	// neighbors.
	public void initializeNeighborsMemory() {

		for (NodeId i : variableMsgs.keySet()) {

			MaxSumMemory newNeighborMemory = new MaxSumMemory(this.nodeId, i, this.domainSize);
			neighborsMemory.put(i, newNeighborMemory);

		}

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	// OmerP - Reset parameters at the end of the run.
	public void resetAgentGivenParametersV3() {

		this.neighborsSize = this.variableMsgs.size();
		this.neighborsMemory.clear();
		this.timeStampToLook = 0;
		this.computationCounter = 0;
		resetAgentGivenParametersV4();

	}

	public void resetAgentGivenParametersV4() {
	}

	// OmerP - Will send new messages for each one of the neighbors upon the
	public void initialize() {

		if (isSync) {

			// initializeNeighborsMemory();
			produceOnlyConstraintMessages();
			sendMsgs();

		}
	}

	// OmerP - new information has arrived and the variable node will update its
	// value assignment.
	public boolean compute() {

		produceNewMessageForAsyncVersion();
		this.timeStampToLook++;
		return true;

	}

	// OmerP - will loop over the neighbors and will send to each one of the a
	// message - Need to check during run.
	@Override
	public void sendMsgs() {

		for (NodeId i : messagesToBeSent.keySet()) {
			if (!variableNode.getNodeId().equals(i)) {
				mailer.sendMsg(messagesToBeSent.get(i)); // Get the message that need to be sent.
				if (print) {
					printSentMessage(messagesToBeSent.get(i));
				}

			}
			/*
			 * else {
			 * 
			 * List<MsgAlgorithm> messages = new ArrayList<MsgAlgorithm>();
			 * messages.add(messagesToBeSent.get(i)); MaxSumStandardVariableDelay
			 * VariableNode = (MaxSumStandardVariableDelay) variableNode; if (print)
			 * {printSentMessage(messagesToBeSent.get(i));}
			 * 
			 * if (Main.MainSimulator.isMaxSumThreadDebug) {
			 * System.err.println(this+" thread send message to "+messages.get(0).
			 * getRecieverId()); } variableNode.receiveAlgorithmicMsgs(messages);
			 * 
			 * 
			 * 
			 * }
			 */

		}

		this.computationCounter++;
		// messagesToBeSent.clear();
		clearMemoryFromAllNeighbors(this.timeStampToLook);

	}

	// OmerP - when a message received will update the context and flag that a
	// message was received.
	@Override
	public boolean updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		MsgAlgorithmFactor msgAlgorithmFactor = (MsgAlgorithmFactor) msgAlgorithm;

		double[] contextFix = (double[]) msgAlgorithmFactor.getContext(); // will cast the message object as a double[].

		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix,
				msgAlgorithmFactor.getTimeStamp()); //

		variableMsgs.put(msgAlgorithmFactor.getSenderId(), newMessageReceveid);

		messagesArrivedControl.put(msgAlgorithmFactor.getSenderId(), true);

		if (print) {
			printReceivedMessage(msgAlgorithmFactor);
		}

		return true;

	}

	// OmerP - Loop over the neighbors memory and operate createNewMessageFromMemory
	// method.
	protected void produceNewMessage() {

		for (NodeId i : neighborsMemory.keySet()) {

			createNewMessageFromMemory(this.timeStampToLook, i);

		}

	}

	// OmerP - Will produce a message from the constraint matrix without the
	// addition of messages - need to fix for memory.
	protected void produceOnlyConstraintMessages() {

		// for (NodeId i : neighborsMemory.keySet()) { // Looping over the neighbors
		// Memory.

		for (NodeId i : this.variableMsgs.keySet()) {

			double[] sentTable = new double[this.domainSize]; // Create a new table.
			double[][] constraintMatrix = new double[this.domainSize][this.domainSize]; // Create the constraint matrix.
			constraintMatrix = neighborsConstraintMatrix.get(i); // Get the constraint matrix of the relevant node id
																	// that will be sent to.
			sentTable = getBestValueTable(constraintMatrix); // Get the best value of each value of the domain.
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, this.timeStampCounter,
					this.time); // Create a new message factor.
			if (print) {
				printPreparedMessage(newMsg);
			}
			messagesToBeSent.put(i, newMsg);

		}
	}

	// OmerP - produce new messages.
	@Override
	protected void produceNewMessages() {

		for (NodeId i : neighborsMemory.keySet()) { // Looping over the neighbors Memory.

			createNewMessageFromMemory(this.timeStampToLook, i);

		}
	}

	///// ******* Methods for the Async Version ******* ////

	protected void produceNewMessageForAsyncVersion() {

		for (NodeId i : variableMsgs.keySet()) {

			double[] sentTable = new double[this.domainSize];
			sentTable = produceFunctionMessage(i);
			MsgAlgorithmFactor newMsg;
			newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, this.timeStampCounter, this.time); // Create
																												// a new
																												// message
																												// factor.
			messagesToBeSent.put(i, newMsg);
			if (print) {
				printPreparedMessage(newMsg);
			}

		}

	}

	protected double[] produceEmptyMessageForNullPointerExeption() {

		double[] emptyTable = new double[this.domainSize];

		for (int i = 0; i < emptyTable.length; i++) {

			emptyTable[i] = 0;

		}

		return emptyTable;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* MaxSumMemory Methods ******* ////

	// OmerP - A method that will check if all the message from the same iteration
	// was received.
	protected boolean didRecieveAllMessagesPerIteration(int iterationToLook) {

		int counter = 0;

		for (NodeId i : neighborsMemory.keySet()) {

			MaxSumMemory maxSumMemory = neighborsMemory.get(i);
			if (maxSumMemory.checkIfInMemory(iterationToLook)) {

				counter++;

			}

		}

		if (counter == this.neighborsSize) {

			return true;

		}

		else {

			return false;

		}

	}

	// OmerP - When all messages with the same iteration have been received this
	// method will create new messages to send.
	protected void createNewMessageFromMemory(int iterationToLook, NodeId receiverNode) {

		double[] sentTable = new double[this.domainSize]; // Create a new table for the new message.
		double[][] constraintMatrix = neighborsConstraintMatrix.get(receiverNode); // Get the relevant Constraint
																					// Matrix.
		double[] otherNodeIdMessage = getOtherNodeIdMessage(iterationToLook, receiverNode);
		constraintMatrix = addTableToMatrix(otherNodeIdMessage, constraintMatrix); // Will add the second variable node
																					// message to the constraint matrix.
		sentTable = getBestValueTable(constraintMatrix); // The best value will be chosen out of the matrix.
		MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), receiverNode, sentTable,
				this.computationCounter, this.time); // Prepare the message.
		messagesToBeSent.put(receiverNode, newMsg);

	}

	// OmerP - A method that will store new context in the memory according to the
	// iteration.
	protected void storeNewContextInMemory(MsgAlgorithmFactor receivedMessage) {

		MaxSumMemory maxSumMemory = neighborsMemory.get(receivedMessage.getSenderId());
		Integer timeStamp = receivedMessage.getTimeStamp();
		double[] context = receivedMessage.getContext();
		maxSumMemory.putContextInMemory(timeStamp, context);

	}

	protected double[] getOtherNodeIdMessage(int iterationToLook, NodeId receiverNode) {

		double[] context = new double[this.domainSize];

		for (NodeId i : neighborsMemory.keySet()) {

			if (receiverNode.compareTo(i) != 0) {

				MaxSumMemory maxSumMemory = neighborsMemory.get(i); // Will get the relevant memory.
				context = maxSumMemory.getMemory().get(iterationToLook); // Will return the double of the memory in the
																			// relevant iteration.

			}

		}

		return context;

	}

	protected void clearMemoryFromAllNeighbors(int iterationToLook) {

		for (NodeId i : neighborsMemory.keySet()) {

			MaxSumMemory maxSumMemory = neighborsMemory.get(i); // Will get the relevant memory.
			maxSumMemory.deleteContextFromMemory(iterationToLook - 1); // After the message was summed it will delete
																		// the message from the memory.

		}
	}

	// -----------------------------------------------------------------------------------------------------------//

	protected void printPreparedMessage(MsgAlgorithmFactor msg) {

		System.out.println(
				"Computation Counter:(" + this.computationCounter + "),FunctionNode:(" + msg.getSenderId().getId1()
						+ "," + msg.getSenderId().getId2() + ") PREPERED a message for VariableNode ("

						+ msg.getRecieverId().getId1() + "," + msg.getRecieverId().getId2() + ") with message context: "
						+ Arrays.toString(msg.getContext()) + " and timestamp:(" + msg.getTimeStamp() + ").\n");

	}

	///// ******* Getters ******* ////

	// OmerP - will return the message that is from the other variable node.
	protected double[] getOtherNodeIdMessage(NodeId to) {

		if (variableMsgs.size() == 1) { // Check the size of the neighbors, if it is 1, it is not full yet and will
										// return null.

			return null;

		}

		NodeId[] temp = new NodeId[2]; // Create an array of NodeId in the size of 2.

		variableMsgs.keySet().toArray(temp);

		double[] context = new double[this.domainSize];

		if (to.equals(temp[0])) {

			try {

				context = variableMsgs.get(temp[1]).getContext();

			}

			catch (NullPointerException e) {

				context = produceEmptyMessageForNullPointerExeption();

			}

			return context; // Will return the message that received from the other variable node that was
							// received.
		}

		else {

			try {

				context = variableMsgs.get(temp[0]).getContext(); // Will return the message that received from the
																	// other variable node that was received.

			}

			catch (NullPointerException e) {

				context = produceEmptyMessageForNullPointerExeption();

			}

			return context; // Will return the message that received from the other variable node that was
							// received.

		}

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Flags Methods ******* ////

	// Decide if to raise the flag of the agent.
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {

		if (isSync) { // If i am sync the flag will be raised only if all the messages have been
						// received.

			System.out.println("FunctionNode:(" + this.getNodeId().getId1() + "," + this.getNodeId().getId2()
					+ "), Flag Check.\n");

			if (checkIfReceivedAllMessages()) {

				if (print) {
					printFlag();
				}
				this.receiveMessageFlag = true;

			}

		} else { // If i am not sync the flag will be raised in each time that i will received a
					// message.

			this.receiveMessageFlag = true;

		}

	}

	@Override
	public void changeRecieveFlagsToFalse() {

		this.receiveMessageFlag = false;

	}

	// If this is a sync run check if the size of the messages that was received is
	// equal to the number of my neighbors.
	protected boolean checkIfReceivedAllMessages() {

		int numberOfReceivedMessages = this.messagesArrivedControl.size();
		int sizeOfMyNeigbors = this.variableMsgs.size();
		if (numberOfReceivedMessages == sizeOfMyNeigbors) {

			messagesArrivedControl.clear();
			return true;

		} else {

			return false;

		}

	}

	protected void printFlag() {

		System.out.println(
				"FunctionNode:(" + this.getNodeId().getId1() + "," + this.getNodeId().getId2() + "), Flag is UP.\n");

	}

	@Override
	protected void sendInsideMsgs() {
		for (NodeId i : messagesToBeSent.keySet()) {

			if (variableNode.getNodeId().equals(i)) {
				synchronized (variableNode) {
					
				
				List<MsgAlgorithm> messages = new ArrayList<MsgAlgorithm>();
				messages.add(messagesToBeSent.get(i));
				MaxSumStandardVariableDelay VariableNode = (MaxSumStandardVariableDelay) variableNode;
				if (print) {
					printSentMessage(messagesToBeSent.get(i));
				}

				if (Main.MainSimulator.isMaxSumThreadDebug) {
					System.err.println(this + " thread send message to " + messages.get(0).getRecieverId());
				}
				variableNode.receiveAlgorithmicMsgs(messages);
				}
			}

		}
	}

	// -----------------------------------------------------------------------------------------------------------//

}
