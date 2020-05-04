package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import Communication.ProtocolDelay;
import Messages.Msg;
import Problem.Dcop;

public abstract class Mailer {
	protected ProtocolDelay delay;
	protected List<Msg> messageBox;
	protected List<Agent>agents;
	protected int terminationTime;

	public Mailer(ProtocolDelay delay, int terminationTime) {
		super();
		this.delay = delay;
		
		this.messageBox = new ArrayList<Msg>();
		this.terminationTime = terminationTime;
	}
	
	public void resetMailer(int dcopId, List<Agent>agents) {
		this.messageBox = new ArrayList<Msg>();
		this.agents = agents;
		this.delay.setSeeds(dcopId);
	}
	
	@Override
	public String toString() {
		return delay.toString();
	}

	
	public void createMessage(Messageable sender, int decisionCounter, Messageable reciever, double context);

	
	
	
	

	
	
	public void printMailBox() {
		for (int i = 0; i < this.messageBox.size(); i++) {
			System.out.println("index: " + i + ", message: " + messageBox.get(i));
		}
	}
	
	public boolean isWithTimeStamp() {
		return this.delay.isWithTimeStamp();
	}

	// for debug
	public ProtocolDelay getDelay() {
		// TODO Auto-generated method stub
		return this.delay;
	}
	
	

	//public abstract void execute();

	protected abstract void execute();
	
	
	

}
