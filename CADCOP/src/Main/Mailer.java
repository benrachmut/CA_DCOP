package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Receiver;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Communication.ProtocolDelay;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAlgorithmFactor;
import Messages.MsgAnyTime;
import Problem.Dcop;

public abstract class Mailer {
	protected ProtocolDelay delay;
	protected List<Msg> messageBox;
	protected Dcop dcop;
	protected double terminationTime;

	public Mailer(ProtocolDelay delay, double terminationTime) {
		super();
		this.delay = delay;
		
		this.messageBox = new ArrayList<Msg>();
		this.terminationTime = terminationTime;
		this.dcop = null;
	}
	
	
	public abstract void execute();

	protected abstract List<Msg> handleDelay();
	
	
	/**
	 * called from main each time a mailer and dcop meet prior to execution, 
	 * thus the mailer updates its fields 
	 * @param dcopId
	 * @param agents
	 */
	public void mailerMeetsDcop(Dcop dcop, List<Agent>agents) {
		this.messageBox = new ArrayList<Msg>();
		this.dcop = dcop;
		boolean isWithTimeStamp = this.delay.isWithTimeStamp();
		for (Agent a : agents) {
			a.setIsWithTimeStamp(isWithTimeStamp);
		}
		this.delay.setSeeds(dcop.getId());
	}
	
	@Override
	public String toString() {
		return delay.toString();
	}


	
	public void createMessage(Messageable sender, int decisionCounter, Messageable reciever, double context);

	
	
	
	

	
	/*
	public void printMailBox() {
		for (int i = 0; i < this.messageBox.size(); i++) {
			System.out.println("index: " + i + ", message: " + messageBox.get(i));
		}
	}
	*/
	
	public boolean isWithTimeStamp() {
		return this.delay.isWithTimeStamp();
	}

	
	
	

	//public abstract void execute();


	//-------------** agentsRecieveMsgs methods**-------------
	/**
	 * 
	 * @param msgToSend created by handle msgs
	 */
	protected void agentsRecieveMsgs(List<Msg> msgToSend) {
		List<MsgAnyTime>msgsAnyTime = new ArrayList<MsgAnyTime>();
		List<MsgAlgorithm>msgsAlgorithm = new ArrayList<MsgAlgorithm>();
		
		iterateOverMsgToSend(msgToSend,msgsAnyTime,msgsAlgorithm);
		handleMsgAlgorithm(msgsAlgorithm);
		
		
		
		
	}
// receiveAlgorithmicMsgs
	private void handleMsgAlgorithm(List<MsgAlgorithm> msgsAlgorithm) {
		if (MainSimulator.isFactorAgent(this.agents.get(0))) {
			Map<NodeId,List<MsgAlgorithm>> recieversByNodeId = new HashMap<NodeId,List<MsgAlgorithm>>();
			for (MsgAlgorithm msg : msgsAlgorithm) {
				NodeId reciever = ((MsgAlgorithmFactor)msg).getReciver();
				if (!recieversByNodeId.containsKey(reciever)) {
					recieversByNodeId
				}
				
			}
		}
		
		for (MsgAlgorithm msgAlgorithm : msgsAlgorithm) {
			msgAlgorithm
		}
		//	public abstract void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages);
	}

	private void iterateOverMsgToSend(List<Msg> msgToSend, List<MsgAnyTime> msgsAnyTime, List<MsgAlgorithm> msgsAlgorithm) {
		for (Msg msg : msgToSend) {
			if (msg instanceof MsgAnyTime) {
				msgsAnyTime.add((MsgAnyTime)msg);
			}else {
				msgsAlgorithm.add((MsgAlgorithm)msg);
			}
		}
		
	}

}
