package Algorithms;

import java.util.HashMap;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardFunctionSync extends MaxSumStandardFunction {

	///// ******* Variables ******* ////

	protected HashMap<NodeId, Double> neighborsMessageIteration; 
	protected int currentIteration;

	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor ******* ////

	public MaxSumStandardFunctionSync(int dcopId, int D, int id1, int id2, Double[][] constraints, Double[][] constraintsTranspose) {
		
		super(dcopId, D, id1, id2, constraints, constraintsTranspose);
		initiatNeighborsMessageIteration();
		
		
	}

	//-----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	//OmerP - To reset the agent if this is a new run. 
	@Override
	public void resetAgent() {
		super.resetAgent();
		this.variableMsgs = Agent.resetMapToValueNull(this.variableMsgs);
		this.neighborsMessageIteration.clear();
		
	}
	
	@Override
	protected void updateMessageInContext(MsgAlgorithm msgAlgorithm) {

		double[] contextFix = (double[]) msgAlgorithm.getContext(); //will cast the message object as a double[].
		
		MsgReceive<double[]> newMessageReceveid = new MsgReceive<double[]>(contextFix, msgAlgorithm.getTimeStamp()); //
				
		MsgAlgorithmFactor newMessage = (MsgAlgorithmFactor) msgAlgorithm; // Will do casting for the msgAlgorithm.

		variableMsgs.put(newMessage.getSenderId(), newMessageReceveid);
		
		neighborsMessageIteration.put(newMessage.getSenderId(), msgAlgorithm.getTimeStamp());

	}
	
	//OmerP - will loop over the neighbors and will send to each one of the a message. - BUG !!!
	@Override
	protected void sendMsg() {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = produceFunctionMessage(i);
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0);
			mailer.sendMsg(newMsg);
			
		}
			
	}
	
	//OmerP - will send new messages for each one of the neighbors upon the initiation of the algorithm (iteration = 0) - BUG!!!
	@Override
	public void initialize() {
		
		for(NodeId i: variableMsgs.keySet()) {
			
			double[] sentTable = new double[this.domainSize];
			sentTable = getBestValueTable(getConstraintMatrix());
			storeNewMessage(i, sentTable);
			MsgAlgorithmFactor newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0);
			mailer.sendMsg(newMsg);
			
						
		}
		
	}
	
	//-----------------------------------------------------------------------------------------------------------//
	
	///// ******* Methods to initialize a new run  ******* ////

	//OmerP - Will initiate the list at the constructor for synchronous run. 
	public void initiatNeighborsMessageIteration() {
		
		this.neighborsMessageIteration = new HashMap<NodeId, Double>();
		
		for(NodeId i: variableMsgs.keySet()) {
			
			neighborsMessageIteration.put(i, null);
			
		}
		
	}
	
	//OmerP - To check if all the messages at the same iteration was received. 
	protected boolean allMsgsForIterationReceived() {
		
		int msgsForIterationReceived = 0;
		
		for(NodeId i: neighborsMessageIteration.keySet()) {
			
			if(neighborsMessageIteration.get(i) == currentIteration) {
				
				msgsForIterationReceived++; 
				
			}
			
		}
		
		if(msgsForIterationReceived == this.neighborsMessageIteration.size()) {
		
			return true;
			
		}
		
		return false; 
	}
	
	
	
}
