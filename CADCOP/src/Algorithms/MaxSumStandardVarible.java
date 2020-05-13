package Algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardVarible extends AgentVariableInference {

	///// ******* Variables ******* ////

	private double dampingFactor = 0.9;
	HashMap<NodeId, double[]> storedMessges = new HashMap<NodeId, double[]>();

	///// ******* Control Variables ******* ////

	boolean dampingOn = true;
	boolean storedMessageOn = true;

	///// ******* Constructor ******* ////

	public MaxSumStandardVarible(int dcopId, int D, int id1) {

		super(dcopId, D, id1);

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	// OmerP - will send new messages for each one of the neighbors upon the
	// initiation of the algorithm (iteration = 0). - BUG !!!!
	public void initialize() {

		chooseFirstValueAssignment();

		for (NodeId i : functionMsgs.keySet()) { // Start loop over the neighbors.

			double[] sentTable = new double[this.domainSize];
			sentTable = produceEmptyMessage(i, sentTable); // For each specific neighbor, produce an empty message.
			storedMessges.put(i, sentTable);
			// Produce new message.
			// MaxSumMessage newMessage = new MaxSumMessage(this.nodeId, i, sentTable);
			// Send newMessage.

		}

	}

	// OmerP - new information has arrived and the variable node will update its
	// value assignment.
	public boolean compute() {

		chooseValueAssignment();
		return true;

	}

	// OmerP - will loop over the neighbors and will send to each one of the a
	// message. - BUG !!!
	@Override
	protected void sendMsg() {

		for (NodeId i : functionMsgs.keySet()) { // Start loop over the neighbors.

			double[] sentTable = new double[this.domainSize];
			sentTable = produceMessage(i, sentTable); // For each specific neighbor, sum all messages excluding the
														// table of the receiving function node.

			if (dampingOn) { // If damping is on will generate a damped message.

				sentTable = damping(i, sentTable); // Will produce a damped message.

			}

			if (storedMessageOn) { // If stored message is on.

				if (areDifferentMessages(i, sentTable)) { // Will check if the new message and the last stored message
															// are the same, if not will send messege.

					storeNewMessage(i, sentTable);
					// Produce new message.
					// Send newMessage.

				}

			} else { // If stored message is off than the new message will be sent.

				// Produce new message.
				// Send newMessage.

			}

		}

	}

	// OmerP - saved for multi-threading.
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	/*
	 * @Override public void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm>
	 * messages) {
	 * 
	 * chooseValueAssignment();
	 * 
	 * }
	 */

	@Override
	protected double getSenderCurrentTimeStampFromContext(MsgAlgorithm MsgAlgorithm) {

		return MsgAlgorithm.getTimeStamp();

	}

	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		MsgAlgorithmFactor newMessage = (MsgAlgorithmFactor) msgAlgorithm; // Will do casting for the msgAlgorithm.

		double[] contextFix = (double[]) newMessage.getContext(); // will cast the message object as a double[].

		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithm.getTimeStamp()); //

		functionMsgs.put(newMessage.getSenderNodeId(), newMessageReceveid);

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

	public void chooseFirstValueAssignment() {

		Random rnd = new Random();
		setValueAssignmnet(rnd.nextInt(this.domainSize)); 

	}

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

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Arithmetic Messages ******* ////

	// OmerP - produce an empty message for the first iteration.
	protected double[] produceEmptyMessage(NodeId to, double[] table) {

		for (int i = 0; i < table.length; i++) {

			table[i] = 0;

		}

		return table;

	}

	// OmerP - produce message to a function node;

	protected double[] produceMessage(NodeId to, double[] table) {

		for (NodeId i : functionMsgs.keySet()) {

			if (!(i.compareTo(to) == 0)) {

				sumMessages(table, functionMsgs.get(i).getContext());

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

}
