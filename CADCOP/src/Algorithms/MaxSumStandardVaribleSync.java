package Algorithms;

import java.util.HashMap;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgReceive;

public class MaxSumStandardVaribleSync extends MaxSumStandardVarible{

	///// ******* Variables ******* ////

	protected HashMap<NodeId, Double> neighborsMessageIteration; 
	protected int currentIteration;

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor ******* ////

	public MaxSumStandardVaribleSync(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		initiatNeighborsMessageIteration();

		
		
		// TODO Auto-generated constructor stub
	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Main Methods ******* ////

	//OmerP - To reset the agent if this is a new run. 
	@Override
	public void resetAgent() {
		super.resetAgent();
		this.functionMsgs = Agent.resetMapToValueNull(this.functionMsgs);
		this.neighborsMessageIteration.clear();
		
	}
	
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
	protected void sendMsg() {

		if(allMsgsForIterationReceived()){
		
			for (NodeId i : functionMsgs.keySet()) { // Start loop over the neighbors.
	
				double[] sentTable = new double[this.domainSize];
				sentTable = produceMessage(i, sentTable); // For each specific neighbor, sum all messages excluding the table of the receiving function node.
				MsgAlgorithmFactor newMsg; 		
				
				if (dampingOn) { // If damping is on will generate a damped message.
	
					sentTable = damping(i, sentTable); // Will produce a damped message.
					
				}
	
				newMsg = new MsgAlgorithmFactor(this.getNodeId(), i, sentTable, 0);
				mailer.sendMsg(newMsg);
					
				}
	
			}

	}
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Methods to initialize a new run  ******* ////

	//OmerP - Will initiate the list at the constructor for synchronous run. 
	public void initiatNeighborsMessageIteration() {
		
		this.neighborsMessageIteration = new HashMap<NodeId, Double>();
		
		for(NodeId i: functionMsgs.keySet()) {
			
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
