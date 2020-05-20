package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.NodeId;
import Data.Data;
import Delays.ProtocolDelay;
import Down.ProtocolDown;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgAnyTime;
import Problem.Dcop;

public abstract class Mailer {
	protected Protocol protocol;
	protected List<Msg> messageBox;
	protected Dcop dcop;
	protected double terminationTime;
	protected SortedMap<Double, Data> dataMap;
	private Integer algorithmMsgsCounter;
	private Integer anytimeMsgsCounter;
	protected Map<NodeId, List<MsgAlgorithm>> recieversAlgortihmicMsgs;
	protected Map<NodeId, List<MsgAnyTime>> recieversAnyTimeMsgs;

	

	
	public Mailer(Protocol protocol, double terminationTime, Dcop dcop) {
		super();
		this.dcop = dcop;
		this.protocol = protocol;
	
		this.protocol.setSeeds(dcop.getId());
		this.messageBox = new ArrayList<Msg>();
		this.terminationTime = terminationTime;
		this.dataMap = new TreeMap<Double, Data>();

	}

	/**
	 * used by agents when creating messages
	 * 
	 * @param m
	 */
	public void sendMsg(Msg m) {
		
		if (m instanceof MsgAlgorithm) {
			this.algorithmMsgsCounter++;
		}
		if (m instanceof MsgAnyTime) {
			this.anytimeMsgsCounter++;
		}
		
		double d = createDelay();
		if (d != -1) {
			m.setDelay(d);
			this.messageBox.add(m);
		}

	}

	private double createDelay() {
		Double d = this.protocol.getDelay().createDelay();
		if (d == null) {
			return -1;
		}
		double dd = d;
		if (this instanceof MailerIterations) {
			dd = (int) dd;
			System.out.println("mailer: this is mailer iteration");
		} else {
			System.out.println("mailer: this is mailer threads");
		}
		return dd;
	}

	public abstract void execute();

	protected abstract List<Msg> handleDelay();

	/**
	 * called from main each time a mailer and dcop meet prior to execution, thus
	 * the mailer updates its fields
	 * 
	 * @param dcopId
	 * @param agents
	 */
	public void mailerMeetsDcop(Dcop dcop, List<Agent> agents) {
		this.messageBox = new ArrayList<Msg>();
		this.dcop = dcop;
		boolean isWithTimeStamp = this.protocol.getDelay().isWithTimeStamp();
		for (Agent a : agents) {
			a.setIsWithTimeStamp(isWithTimeStamp);
		}
		this.protocol.getDelay().setSeeds(dcop.getId());
	}

	@Override
	public String toString() {
		return protocol.getDelay().toString()+","+protocol.getDown();
	}

	public boolean isWithTimeStamp() {
		return this.protocol.getDelay().isWithTimeStamp();
	}

	// public abstract void execute();

	// -------------** agentsRecieveMsgs methods**-------------
	/**
	 * @param msgToSend created by handle msgs
	 */
	protected void agentsRecieveMsgs(List<Msg> msgToSend) {
		List<MsgAnyTime> msgsAnyTime = new ArrayList<MsgAnyTime>();
		List<MsgAlgorithm> msgsAlgorithm = new ArrayList<MsgAlgorithm>();

		iterateOverMsgToSend(msgToSend, msgsAnyTime, msgsAlgorithm);
		handleMsgAlgorithm(msgsAlgorithm);
		handleMsgAnytime(msgsAnyTime);

	}

	/**
	 * sort messages by type, anytime and algorithm
	 * 
	 * @param msgToSend
	 * @param msgsAnyTime
	 * @param msgsAlgorithm
	 */
	private void iterateOverMsgToSend(List<Msg> msgToSend, List<MsgAnyTime> msgsAnyTime,
			List<MsgAlgorithm> msgsAlgorithm) {
		for (Msg msg : msgToSend) {
			if (msg instanceof MsgAnyTime) {
				msgsAnyTime.add((MsgAnyTime) msg);
			} else {
				msgsAlgorithm.add((MsgAlgorithm) msg);
			}
		}
	}

	// -------------** handleMsgAlgorithm methods**-------------

	/**
	 * handle messages differently if factor graph or not because of node id
	 * 
	 * @param msgsAlgorithm
	 */
	private void handleMsgAlgorithm(List<MsgAlgorithm> msgsAlgorithm) {
		this.recieversAlgortihmicMsgs = getRecieversByNodeIdAlgorithmic(msgsAlgorithm);
		for (Entry<NodeId, List<MsgAlgorithm>> e : recieversAlgortihmicMsgs.entrySet()) {
			NodeId recieverId = e.getKey();
			List<MsgAlgorithm> msgsForAnAgnet = e.getValue();
			Agent recieverAgent = getAgentByNodeId(recieverId);
			
			if (recieverAgent == null) {
				System.err.println("from mailer: something is wrong with finding the recieverAgent");
			}
			
			recieverAgent.receiveAlgorithmicMsgs(msgsForAnAgnet);
		}
		//if (this.dcop.isInferenceAgent()) {
			//handleMsgAlgorithmIfFactor(msgsAlgorithm);
		//} else {
		//	handleMsgAlgorithmIfNotFactor(msgsAlgorithm);
		//}

		// public abstract void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm>
		// messages);
	}

	/**
	 * handle messages if not factor graph: create map of messages were the key is
	 * receiver id and key is list of messages destine to it use
	 * receiveAlgorithmicMsgs on each receiver
	 * 
	 * @param msgsAlgorithm
	 */
/*
	private void handleMsgAlgorithmIfNotFactor(List<MsgAlgorithm> msgsAlgorithm) {
		this.recieversAlgorithmById = getRecieversByIntegerId(msgsAlgorithm);
		for (Entry<Integer, List<MsgAlgorithm>> e : recieversAlgorithmById.entrySet()) {
			Integer recieverId = e.getKey();
			List<MsgAlgorithm> msgsForAnAgnet = e.getValue();
			Agent recieverAgent = getAgentByIntegerId(recieverId);
			if (recieverAgent == null) {
				System.err.println("from mailer: something is wrong with finding the recieverAgent");
			}
			recieverAgent.receiveAlgorithmicMsgs(msgsForAnAgnet);
		}

	}
*/
	/**
	 * create map of messages were the key is receiver id and key is list of
	 * messages destine to it
	 * 
	 * @param msgsAlgorithm
	 * @return
	 */
	/*
	private Map<Integer, List<MsgAlgorithm>> getRecieversByIntegerId(List<MsgAlgorithm> msgsAlgorithm) {
		Map<Integer, List<MsgAlgorithm>> ans = new HashMap<Integer, List<MsgAlgorithm>>();
		for (MsgAlgorithm msg : msgsAlgorithm) {
			Integer reciever = msg.getRecieverId();

			if (reciever == -1) {
				System.err.println("from mailer: bug because message was suppose to be msgFactor but its not");
			}
			if (!ans.containsKey(reciever)) {
				ans.put(reciever, new ArrayList<MsgAlgorithm>());
			}
			ans.get(reciever).add(msg);
		}

		return ans;
	}
	*/
	/*
	private Map<Integer, List<MsgAnyTime>> getRecieversByIntegerIdForAnyTime(List<MsgAnyTime> msgsAlgorithm) {
		Map<Integer, List<MsgAnyTime>> ans = new HashMap<Integer, List<MsgAnyTime>>();
		for (MsgAnyTime msg : msgsAlgorithm) {
			Integer reciever = msg.getRecieverId();

			if (reciever == -1) {
				System.err.println("from mailer: bug because message was suppose to be msgFactor but its not");
			}
			if (!ans.containsKey(reciever)) {
				ans.put(reciever, new ArrayList<MsgAnyTime>());
			}
			ans.get(reciever).add(msg);
		}

		return ans;
	}
*/
	/**
	 * get agent from dcop given its id
	 * 
	 * @param recieverId
	 * @return
	 */
	/*
	private Agent getAgentByIntegerId(Integer recieverId) {
		for (Agent a : dcop.getAgents()) {
			Integer aId = a.getId();

			if (aId == recieverId) {
				return a;
			}
		}
		return null;
	}
*/
	/**
	 * handle messages if factor graph: create map of messages were the key is
	 * receiver id and key is list of messages destine to it use
	 * receiveAlgorithmicMsgs on each receiver
	 * 
	 * @param msgsAlgorithm
	 */
	/*
	private void handleMsgAlgorithmIfFactor(List<MsgAlgorithm> msgsAlgorithm) {
		this.recieversAlgortihmiByNodeId = getRecieversByNodeId(msgsAlgorithm);
		for (Entry<NodeId, List<MsgAlgorithm>> e : recieversAlgortihmiByNodeId.entrySet()) {
			NodeId recieverId = e.getKey();
			List<MsgAlgorithm> msgsForAnAgnet = e.getValue();

			Agent recieverAgent = getAgentByNodeId(recieverId);
			if (recieverAgent == null) {
				System.err.println("from mailer: something is wrong with finding the recieverAgent");
			}
			recieverAgent.receiveAlgorithmicMsgs(msgsForAnAgnet);
		}

	}
*/
	/**
	 * get agent from dcop given its NodeId
	 * 
	 * @param recieverId
	 * @return
	 */
	private Agent getAgentByNodeId(NodeId recieverId) {
		for (Agent a : dcop.getAgents()) {
			NodeId aId = a.getNodeId();
			if (aId == null) {
				System.err.println("from mailer: you didnt make sure that the agents are in factor graph");
			}
			if (aId.equals(recieverId)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * create map of messages were the key is receiver NodeId and key is list of
	 * messages destine to it
	 * 
	 * @param msgsAlgorithm
	 * @return
	 */
	
	
	private Map<NodeId, List<MsgAlgorithm>> getRecieversByNodeIdAlgorithmic(List<MsgAlgorithm> msgsAlgorithm) {
		Map<NodeId, List<MsgAlgorithm>> ans = new HashMap<NodeId, List<MsgAlgorithm>>();
		for (MsgAlgorithm msg : msgsAlgorithm) {
			NodeId reciever = msg.getRecieverId();
			if (!ans.containsKey(reciever)) {
				ans.put(reciever, new ArrayList<MsgAlgorithm>());
			}
			ans.get(reciever).add(msg);
		}

		return ans;
	}
	
	
	private Map<NodeId, List<MsgAnyTime>> getRecieversByNodeIdAnyTime(List<MsgAnyTime> msgsAnyTime) {
		Map<NodeId, List<MsgAnyTime>> ans = new HashMap<NodeId, List<MsgAnyTime>>();
		for (MsgAnyTime msg : msgsAnyTime) {
			NodeId reciever = msg.getRecieverId();
			if (!ans.containsKey(reciever)) {
				ans.put(reciever, new ArrayList<MsgAnyTime>());
			}
			ans.get(reciever).add(msg);
		}

		return ans;
	}

	// -----** TODO **--------
	private void handleMsgAnytime(List<MsgAnyTime> msgsAnyTime) {
		this.recieversAnyTimeMsgs = getRecieversByNodeIdAnyTime(msgsAnyTime);
		for (Entry<NodeId, List<MsgAnyTime>> e : recieversAnyTimeMsgs.entrySet()) {
			NodeId recieverId = e.getKey();
			List<MsgAnyTime> msgsForAnAgnet = e.getValue();
			Agent recieverAgent = getAgentByNodeId(recieverId);
			if (recieverAgent == null) {
				System.err.println("from mailer: something is wrong with finding the recieverAgent");
			}
			if (recieverAgent instanceof AgentVariable) {
				((AgentVariable)recieverAgent).recieveAnyTimeMsgs(msgsForAnAgnet);
			}
			
		}
	}


/*
	private void handleMsgAnytimeIfNotFactor(List<MsgAnyTime> msgsAnyTime) {
		this.recieversAnyTimeById = getRecieversByIntegerIdForAnyTime(msgsAnyTime);
		for (Entry<Integer, List<MsgAnyTime>> e : recieversAnyTimeById.entrySet()) {
			Integer recieverId = e.getKey();
			List<MsgAnyTime> msgsForAnAgnet = e.getValue();
			Agent recieverAgent = getAgentByIntegerId(recieverId);
			if (recieverAgent == null) {
				System.err.println("from mailer: something is wrong with finding the recieverAgent");
			}
			if (recieverAgent instanceof AgentVariable) {
				recieverAgent = (AgentVariable)recieverAgent;
				((AgentVariable)recieverAgent).recieveAnyTimeMsgs(msgsForAnAgnet);

			}
			else {
				System.err.println("from mailer: something is wrong with anytime msg sent to a non variable agent");
			}
			
			
			

		}
		
	}
*/
	
	public Integer getAlgorithmMsgsCounter() {
		return this.algorithmMsgsCounter;
	}
	public Integer getAnytimeMsgsCounter() {
		return this.anytimeMsgsCounter;
	}
	
	protected void createData(double i) {
		dataMap.put(i, new Data(i, this.dcop, this));
		
	}
	
	
}
