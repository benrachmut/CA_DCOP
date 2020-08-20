package Main;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import AgentsAbstract.Agent;
import Messages.Msg;
import Problem.Dcop;

public class MailerThread extends Mailer implements Runnable {
	public int time;
	Collection<Thread> agentsThreads;
	
	public MailerThread(Protocol protocol, int terminationTime, Dcop dcop) {
		super(protocol, terminationTime, dcop);
		time = 0;
		this.agentsThreads =  new HashSet<Thread>();
	}

	@Override
	public void execute() {
		createAndStartAgentThreads();
		createAndStartMailerThreads();	
	}

	private void createAndStartMailerThreads() {
		Thread selfThread = new Thread(this);
		selfThread.start();
	}

	private void createAndStartAgentThreads() {
		for (Agent a : this.dcop.getAgents()) {
			agentsThreads.add(new Thread(a));
		}
		for (Thread thread : agentsThreads) {
			thread.start();
		}
	}
	
	@Override
	public void run() {
		while (this.time< this.terminationTime) {
				wait();
		}
		killAgents();
	}
	

	private void killAgents() {
		for (Agent a : this.dcop.getAgents()) {
			a.setStopThreadCondition();
			a.notifyAll();
		}
		
	}

	@Override
	protected List<Msg> handleDelay() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setMailerName() {
		Mailer.mailerName = "Thread";
	}

	@Override
	protected void updateMailerClockUponMsgRecieved(Msg m) {
		int timeMsg = m.getTimeStamp();
		if (this.time<timeMsg) {
			this.time = timeMsg;
		}	
	}

	

}
