package AlgorithmsInference;

import java.util.HashMap;
import java.util.List;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardFunction extends AgentFunction {

	///// ******* Variables ******* ////

	private boolean receiveMessageFlag;
	protected Double[][] constraints; 
	protected Double[][] constraintsTranspose; 
	HashMap<NodeId, double[]> storedMessgesTable = new HashMap<NodeId, double[]>();
	HashMap<NodeId, MsgAlgorithmFactor> messagesToBeSent = new HashMap<NodeId, MsgAlgorithmFactor>(); 

	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Control Variables ******* ////
	
	boolean storedMessageOn = true;
	 
	///// ******* Constructor ******* ////

	//OmerP - Constructor for regular factor graph. 
	public MaxSumStandardFunction(int dcopId, int D, int id1, int id2, Integer[][] constraints, Integer[][] constraintsTranspose) {
		
		super(dcopId, D, id1, id2);
		this.constraints = AgentFunction.turnIntegerToDoubleMatrix(constraints);
		this.constraintsTranspose = AgentFunction.turnIntegerToDoubleMatrix(constraintsTranspose);
		updataNodes(getNodeId());
		this.receiveMessageFlag = false;
		
	}
	
	public MaxSumStandardFunction(int dcopId, int D, int id1, int id2, Double[][] constraints, Double[][] constraintsTranspose) {
		
		super(dcopId, D, id1, id2);
		this.constraints = constraints; 
		this.constraintsTranspose = constraintsTranspose;

		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	//OmerP - will send new messages for each one of the neighbors upon the initiation of the algorithm (iteration = 0).
	public void initialize() {
			
	}
		
	//OmerP - function node don't need to update anything so he will return false. 
	@Override
	protected boolean compute() {
		
		if(receiveMessageFlag) {
			
			produceNewMessages(); 
			
			return true; 
			
		}
		
		return false; 
		
	}
	
	//OmerP - To reset the agent if this is a new run. 
	@Override
	public void resetAgentGivenParametersV2() {	
		this.storedMessgesTable.clear();
		messagesToBeSent.clear();
		resetAgentGivenParametersV3();
		
	}
	
	public void resetAgentGivenParametersV3() {}
	
	//OmerP - produce new messages. 
	protected void produceNewMessages() {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = produceFunctionMessage(i);
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0);
			messagesToBeSent.put(i, newMsg);
			
		}
			
	}
	
	//OmerP - will loop over the neighbors and will send to each one of the a message.
	@Override
	protected void sendMsgs() {
		
		for(NodeId i: messagesToBeSent.keySet()) {
			
			mailer.sendMsg(messagesToBeSent.get(i));
			
			if(storedMessageOn) {
				
				storedMessgesTable.put(i, messagesToBeSent.get(i).getContext());
				
			}
				
		}
		
		changeRecieveFlagsToFalse();
		messagesToBeSent.clear();
		
		
	}
	
	@Override
	protected int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm) {
		
		return msgAlgorithm.getTimeStamp();

	}

	//OmerP - will get the message and update context in HashMap.
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {
		
		double[] contextFix = (double[]) msgAlgorithm.getContext(); //will cast the message object as a double[].
		
		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithm.getTimeStamp()); //
		
		variableMsgs.put(msgAlgorithm.getSenderId(), newMessageReceveid);
		
		changeRecieveFlagsToTrue(msgAlgorithm);

	}
	
	//-----------------------------------------------------------------------------------------------------------//
	
	///// ******* ConstraintMatrix ******* //// 
			
	//OmerP - Will initialize a matrix to zero.  
	protected void initializeMatrixToZero(double[][] matrix) {
		
		for(int i = 0; i < matrix.length ; i++) {
			
			for(int j = 0; j < matrix.length ; j++) {
				
				matrix[i][j] = 0; 
				
			}
			
			
		}
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

    ///// ******* Arithmetic Methods ******* ////
	
	//OmerP - Multiplication of table - FIXED.
	protected Double[][] tableMultiplication(Double[][] table, double multiplicationFactor) {
		
		Double[][] tableDAfterMultiplication = new Double[this.domainSize][this.domainSize]; 
		
		for(int i = 0 ; i < tableDAfterMultiplication.length ; i++) {
			
			for(int j = 0; j < tableDAfterMultiplication.length; j++) {
				
				tableDAfterMultiplication[i][j] = table[i][j]*multiplicationFactor; 
				
			}
			
		}
		
		return tableDAfterMultiplication; 
		
	}
	
	//OmerP - Get the best value of the matrix - FIXED.
	protected double[] getBestValueTable(Double[][] constraintMatrix) {
		
		double[] table = new double[this.domainSize];
		
		for(int i = 0 ; i < constraintMatrix.length ; i++) {
			
			double bestCurrentValue = Double.MAX_VALUE; 

			for(int j = 0 ; j < constraintMatrix.length ; j++) {
				
				if(constraintMatrix[i][j] < bestCurrentValue) {
					
					bestCurrentValue = constraintMatrix[i][j]; 
					
				}
								
			}

			table[i] = bestCurrentValue; 
			
		}
		
		return table; 
		
	}
	
	//OmerP - Add an table to the matrix - FIXED. 
	protected void addTableToMatrix(double[] receivedTable, Double[][] constraintMatrix){
		
		for(int i = 0; i < this.domainSize ; i++) {
			
			for(int j = 0; j < this.domainSize ; j++) {
				
				constraintMatrix[i][j] = constraintMatrix[i][j] + receivedTable[j];
				
			}
			
		}
	
	}
	
	//-----------------------------------------------------------------------------------------------------------//

    ///// ******* Send Messages ******* ////
	
	//OmerP - Will produce function message
	protected double[] produceFunctionMessage(NodeId to) {
		
		double[] sentMessage = new double[this.domainSize]; //The message that will be sent.
		Double[][] constraintMatrix = getConstraintMatrix(); //The constraint matrix. 
		
		
		if(getOtherNodeIdMessage(to) != null) { //If the message that the other variable node sent is not Null.
			
			addTableToMatrix(getOtherNodeIdMessage(to), constraintMatrix); //Will add the second variable node message to the constraint matrix.
			
		}
		
		sentMessage = getBestValueTable(constraintMatrix);  //The best value will be chosen out of the matrix. 
		return sentMessage; //Sending the message. 
		
	}
				
	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Stored Message Methods ******* ////

	protected void storeNewMessage(NodeId nodeId, double[] table) {
		
		if(storedMessageOn) {
			
			storedMessgesTable.put(nodeId, table);
			
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
	
	//OmerP - Loops over the messagesSent map and return the tableD that was saved. 
	protected double[] getLastSavedMessage(NodeId recevier) {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			if(i.compareTo(recevier) == 0) {
				
				return variableMsgs.get(i).getContext(); 
				
			}
			
		}
		
		double[] emptyMessage = new double[this.domainSize];
	
		return emptyMessage;
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//
	
    ///// ******* Getters ******* ////

	//OmerP - will return the constraint matrix - FIXED. 
	public Double[][] getConstraintMatrix(){
		
		return constraints; 
		
	}
	
	//OmerP - will return the message that is from the other variable node. 
	protected double[] getOtherNodeIdMessage(NodeId to) {
		
		
		if(variableMsgs.size() == 1) { //Check the size of the neighbors, if it is 1, it is not full yet and will return null. 
			
			return null; 
			
		}
			
		NodeId[] temp = new NodeId[2]; //Create an array of NodeId in the size of 2. 
		
		variableMsgs.keySet().toArray(temp); 
				
		if(to.equals(temp[0])) {
						
			return variableMsgs.get(temp[1]).getContext(); //Will return the message that received from the other variable node that was received. 
		}
		
		else {
			
			return variableMsgs.get(temp[0]).getContext(); //Will return the message that received from the other variable node that was received. 
			
		}
		
	}

	//OmerP - Parameter that are needed to be initialized every time a new problem is initialized. 
	@Override
	protected void resetAgentGivenParameters() {

		for(NodeId i : variableMsgs.keySet()) {
			
			variableMsgs.replace(i, variableMsgs.get(i), null); //OmerP - will put null instead of the value that was stored. 
			
		}
		
		for(NodeId i : storedMessgesTable.keySet()) {
			
			storedMessgesTable.replace(i, storedMessgesTable.get(i), null); //OmerP - will put null instead of the value that was stored. 
			
		}
		
		
	}

    ///// ******* Flags Methods ******* ////

	//OmerP - Flag that should be down after the all the messages were sent. 
	@Override
	protected void changeRecieveFlagsToFalse() {
		
		this.receiveMessageFlag = false;
		
	}

	//OmerP - Flag that should be raised when a message was received. 
	@Override
	protected void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm) {

		this.receiveMessageFlag = true;
		
	}

	@Override
	protected boolean getDidComputeInThisIteration() {
		// TODO Auto-generated method stub
		return false;
	}

	//OmerP - will clear the HashMap from values double. 
	protected void clearHashMapIntValues(HashMap<NodeId, Integer> hashMapToClear) {
		
		for(NodeId i: hashMapToClear.keySet()) {
			
			hashMapToClear.put(i, null);
			
		}
		
	}
	

	//-----------------------------------------------------------------------------------------------------------//


}
