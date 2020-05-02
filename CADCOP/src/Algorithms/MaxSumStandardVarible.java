package Algorithms;

import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariableInference;

public class MaxSumStandardVarible extends AgentVariableInference{

	private double dampingFactor=0.9 ; 
	///// ******* Control Variables ******* ////
	boolean dampingOn = true; 
	boolean storedMessageOn = true; 
	
	public MaxSumStandardVarible(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
	}
	
public void initialize() {
		
		for(NodeId i: neighbors) { //Start loop over the neighbors.

			double[] sentTable = new double[this.getD().length];
			sentTable = produceEmptyMessage(i, sentTable); //For each specific neighbor, produce an empty message.
			MaxSumMessage newMessage = new MaxSumMessage(this.nodeId, i, sentTable);
			storeNewMessage(newMessage);
			//Send newMessage.
			
		}
	
	}
	
	public void compute() {
		
		
	}
	
	///// ******* Sent Messages Methods ******* ////

	public void sendMessages() {
					
		for(NodeId i: neighbors) { //Start loop over the neighbors.
				
			double[] sentTable = new double[this.getD().length];
			sentTable = produceMessage(i, sentTable); //For each specific neighbor, sum all messages excluding the table of the receiving function node.
			sentTable = damping(i, sentTable);
			MaxSumMessage newMessage = new MaxSumMessage(this.nodeId, i, sentTable);
			
			if(areDifferentMessages(i, sentTable)) {
				
				storeNewMessage(newMessage);
				//Send newMessage.
				
			}

		}
			
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Stored Message Methods ******* ////

	protected void storeNewMessage(MaxSumMessage message) {
		
		if(storedMessageOn) {
			
			storedMessages.put(message.getReceiver(), message); 
			
		}
		
	}
		
	protected boolean areDifferentMessages(NodeId to, double[] table) {
		
		double[] lastStroedMessage = getLastSavedMessage(to);
		
		for(int i = 0 ; i < table.length ; i ++) {
			
			if(table[i] != lastStroedMessage[i]) {
				
				return true;
				
			}
			
		}
		
		return false; 
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Handle Messages Methods ******* ////

	//OmerP - When receive a message, puts the message in the messages map.
	public void handleMsgs(MaxSumMessage receivedMessage) {
		
		messages.put(receivedMessage.getSender(), receivedMessage);
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Choose Value Assignment Method ******* ////

	public void chooseValueAssignment() {
		
		double[] table = new double[this.getD().length];
		double bestValueAssignment = Double.MAX_VALUE; 
		int valueAssignment = 0; 
		
		for(NodeId i: messages.keySet()) { 		//OmerP - sum all the messages from the messages map. 

			table = sumMessages(table, messages.get(i).getTable());
			
		}
		
		for(int i = 0 ; i < table.length ; i++) { 	//OmerP - choose the best value assignment out of the table. 

			
			if(table[i] < bestValueAssignment) {
				
				bestValueAssignment = table[i]; 
				valueAssignment = i; 
				
			}
			
		}
		
		this.setValueAssignment(valueAssignment); //OmerP - submit the new value assignment of the variable node. 
		
	}
	
	///// ******* Arithmetic Messages ******* ////

	//OmerP - produce an empty message for the first iteration. 
	
	protected double[] produceEmptyMessage(NodeId to, double[] table) {
				
		for(int i = 0; i < table.length; i++) {
			
			table[i] = 0; 
			
		}
		
		return table;
		
	}
	
	//OmerP - produce message to a function node; 
	
	protected double[] produceMessage(NodeId to, double[] table) {
					
		for(NodeId i: messages.keySet()) {
			
			if(i.compareTo(to) == 0) {
				
				sumMessages(table, messages.get(i).getTable());
				
			}
			
		}
		
		return table; 
	
	}
	
	//OmerP - Sum two tables of doubles. 
	protected double[] sumMessages(double[] table1, double[] table2) {
		
		double[] sumTable = new double[table1.length];
		
		for(int i = 0; i < table1.length; i++) {
			
			sumTable[i] = table1[i] + table2[i];
			
		}

		return sumTable; 
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	///// ******* Damping Methods ******* ////

	protected double[] damping(NodeId to, double[] table) {
		
		if(dampingOn) {
			
			table = dampedMessage(table, getLastSavedMessage(to));
			return table; 
			
		}
		
		else {
			
			return table; 
			
		}
		
		
		
	}
	
	//OmerP - Multiplication of messages. 
	protected double[] messageMultiplication(double[] table, double multiplicationFactor) {
				
		for(int i = 0 ; i < table.length ; i++) {
			
			table[i] = table[i]*multiplicationFactor; 
			
		}
		
		return table; 
		
	}
	
	//OmerP - Loops over the messagesSent map and return the tableD that was saved. 
	protected double[] getLastSavedMessage(NodeId recevier) {
		
		for(NodeId i: messages.keySet()) {
			
			if(i.compareTo(recevier) == 0) {
				
				return messages.get(i).getTable(); 
				
			}
			
		}
		
		double[] emptyMessage = new double[this.getD().length];
	
		return emptyMessage;
		
	}
	
	//OmerP - gets two double[] and calculate the damping vector. 
	protected double[] dampedMessage(double[] currentMessage, double[] lastMessage) {
				
		double[] currentMessageAfterAlpha = messageMultiplication(currentMessage,  (1-dampingFactor)); //table after alpha.
		
		double[] lastMessageAfterAlpha = messageMultiplication(lastMessage,  dampingFactor); //table after one minus alpha.
		
		for(int i = 0 ; i < this.getD().length ; i++) {
			
			currentMessage[i] = currentMessageAfterAlpha[i] + lastMessageAfterAlpha[i];
			
		}
				
		return currentMessage; 
		
		
	}

	@Override
	protected void handleMsgs() {
		// TODO Auto-generated method stub
		
	}
	

}
