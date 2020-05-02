package Algorithms;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.NodeId;

public class MaxSumStandardFunction extends AgentFunction {

	
	double SCFGRatio = 0.6;
	///// ******* Control Variables ******* ////
	boolean storedMessageOn = true; 
	
	protected Double[][] constraints; 
	protected Double[][] constraintsTranspose; 

	public MaxSumStandardFunction(int dcopId, int D, int id1, int id2, Integer[][] constraints, Integer[][] constraintsTranspose) {
		super(dcopId, D, id1, id2);
		this.constraints = AgentFunction.turnIntegerToDoubleMatrix(constraints);
		this.constraintsTranspose = AgentFunction.turnIntegerToDoubleMatrix(constraintsTranspose);

		// TODO Auto-generated constructor stub
	}
	
///// ******* ConstraintMatrix ******* ////
	
	//OmerP - Will initialize to constraint matrix according to the cost of the problem
	protected void initializeConstraintMatrix(double[][] constraintMatrix, int multiplicationFactor) {
		
		Random rnd = new Random();
		
		for(int i = 0; i < constraintMatrix.length ; i++) {
			
			for(int j = 0; j < constraintMatrix.length ; j++) {
				
				constraintMatrix[i][j] = rnd.nextDouble();
				constraintMatrix[i][j] = constraintMatrix[i][j]*multiplicationFactor; 
				
			}
			
		}
		
	}
	
	//OmerP - Will transpose the constraint matrix. 
	protected double[][] transposeConstraintMatrix(double[][] constraintMatrix) {
		
		double[][] transposedConstraintMatrix = new double [this.getD().length][this.getD().length]; 
		
		for(int i = 0; i < constraintMatrix.length ; i++) {
			
			for(int j = 0; j < constraintMatrix.length ; j++) {
				
				transposedConstraintMatrix[j][i] = constraintMatrix[i][j];
				
			}
			
			
		}
		
		return transposedConstraintMatrix; 
		
	}
	
	//OmerP - Will initialize a matrix to zero.  
	protected void initializeMatrixToZero(double[][] matrix) {
		
		for(int i = 0; i < matrix.length ; i++) {
			
			for(int j = 0; j < matrix.length ; j++) {
				
				matrix[i][j] = 0; 
				
			}
			
			
		}
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	
    ///// ******* Split Constraint Factor Graph Methods ******* ////

	protected double[][] splitConstraintFactorGraph(){
		
		double[][] splitConstraintTable = getConstraintMatrix();
		
		if(nodeId.getId1() > nodeId.getId2()) {
			
			
			tableMultiplication(splitConstraintTable, SCFGRatio);
			
			
		}
		
		else {
			
			tableMultiplication(splitConstraintTable, 1-SCFGRatio);
			
		}
		

		return splitConstraintTable; 
		
	}
	
    ///// ******* Arithmetic Methods ******* ////

	//OmerP - Multiplication of table. 
	protected double[][] tableMultiplication(double[][] table, double multiplicationFactor) {
		
		double[][] tableDAfterMultiplication = new double[this.getD().length][this.getD().length]; 
		
		for(int i = 0 ; i < tableDAfterMultiplication.length ; i++) {
			
			for(int j = 0; j < tableDAfterMultiplication.length; j++) {
				
				tableDAfterMultiplication[i][j] = table[i][j]*multiplicationFactor; 
				
			}
			
		}
		
		return tableDAfterMultiplication; 
		
	}
	
	//OmerP - Get the best value of the matrix.
	protected double[] getBestValueTable(double[][] constraintMatrix) {
		
		double[] table = new double[this.getD().length];
		
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
	
	//OmerP - Add an table to the matrix.
	protected void addTableToMatrix(double[] receivedTable, double[][] constraintMatrix){
		
		for(int i = 0; i < this.getD().length ; i++) {
			
			for(int j = 0; j < this.getD().length ; j++) {
				
				constraintMatrix[i][j] = constraintMatrix[i][j] + receivedTable[j];
				
			}
			
		}
	
	}
	
	//-----------------------------------------------------------------------------------------------------------//

    ///// ******* Send Messages ******* ////

	protected void produceOnlyConstraint() {
		
		double[] onlyConstraint = new double[this.getD().length];
		
		onlyConstraint = getBestValueTable(getConstraintMatrix());
		
		
	
		//Send only this message. 
				
	}
	
	protected double[] produceFunctionMessage(NodeId to, double[] table) {
		
		double[][] constraintMatrix = getConstraintMatrix();
		MaxSumMessage addedMessage = getOtherNodeIdMessage(to);
		
		if(addedMessage != null) {
			
			addTableToMatrix(addedMessage.getTable(), constraintMatrix); 
			
		}
		
		table = getBestValueTable(constraintMatrix);  
		return table; 
		
	}
		
	@Override
	public void initialize() {
		
		for(NodeId i: neighbors) {
			
			double[] sentTable = new double[this.getD().length];
			sentTable = getBestValueTable(constraintMatrix);
			MaxSumMessage newMessage = new MaxSumMessage(this.nodeId, i, sentTable);
			storeNewMessage(newMessage);
			//Send newMessage.
						
		}
	
	}
	
	@Override
	protected void sendMessages() {
		
		for(NodeId i: neighbors) {
			
			double[] sentTable = new double[this.getD().length];
			produceFunctionMessage(i, sentTable);
			MaxSumMessage newMessage = new MaxSumMessage(this.nodeId, i, sentTable);
			
			if(areDifferentMessages(i, sentTable)) {
				
				storeNewMessage(newMessage);
				//Send newMessage.
				
			}
			
			
		}

	}
	
    ///// ******* Methods to handle messages ******* ////

	protected void handleMsgs(MaxSumMessage receivedMessage) {
		
		messages.put(receivedMessage.getSender(), receivedMessage);
		
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
	
	//-----------------------------------------------------------------------------------------------------------//
	
	
    ///// ******* Setters ******* ////

	
	//-----------------------------------------------------------------------------------------------------------//
	

    ///// ******* Getters ******* ////

	public double[][] getConstraintMatrix(){
		
		return constraintMatrix; 
		
	}
	
	protected MaxSumMessage getOtherNodeIdMessage(NodeId to) {
		
		if(neighbors.size() == 1) { //Check the size of the neighbors, if it is 1, it is not full yet and will return null. 
			
			return null; 
			
		}
		
		NodeId [] a = new NodeId[2]; //Create an array of NodeId in the size of 2. 
		neighbors.toArray(a); //Returns an array containing all of the elements in this list in proper sequence (from first to last element).
		
		if(to.equals(a[0])) {
			
			return messages.get(a[1]); //Will return the message that received from the other variable node that was received. 
		}
		
		else {
			
			return messages.get(a[0]); //Will return the message that received from the other variable node that was received. 
			
		}
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAgent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleMsgs() {
		// TODO Auto-generated method stub
		
	}
	
	

}
