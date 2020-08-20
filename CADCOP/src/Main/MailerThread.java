package Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import AgentsAbstract.Agent;
import Messages.Msg;
import Messages.MsgsTimeComparator;
import Problem.Dcop;

public class MailerThread extends Mailer implements Runnable {
	private int time;
	private Collection<Thread> agentsThreads;
	private boolean clockUpdatedFromMsgPlacedInBoxFlag;

	public MailerThread(Protocol protocol, int terminationTime, Dcop dcop) {
		super(protocol, terminationTime, dcop);
		time = 0;
		this.agentsThreads = new HashSet<Thread>();
		clockUpdatedFromMsgPlacedInBoxFlag = false;
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
		while (this.time < this.terminationTime) {
			synchronized (this) {
				while (this.messageBox.isEmpty()) {
					if (MainSimulator.isThreadDebug) {
						System.out.println("mailer msg box is empty at time"+this.time);
					}	
					try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				createData(this.time);
				shouldUpdateClockBecuaseNoMsgsRecieved();
				List<Msg> msgToSend = this.handleDelay();
				agentsRecieveMsgs(msgToSend);
				clockUpdatedFromMsgPlacedInBoxFlag = false;
			}
		}
		killAgents();
	}

	private void shouldUpdateClockBecuaseNoMsgsRecieved() {
		if (clockUpdatedFromMsgPlacedInBoxFlag == false) {
			Msg minTimeMsg = Collections.min(messageBox, new MsgsTimeComparator());
			int minTime = minTimeMsg.getTime();
			int oldTime = time;
			this.time = minTime;
			if (MainSimulator.isThreadDebug) {
				System.out.println("mailer did not recieve new messages. mailer time moved from "+oldTime+" to "+this.time);
			}
		}
	}

	@Override
	public synchronized void sendMsg(Msg m) {
		super.sendMsg(m);
		updateMailerClockUponMsgRecieved(m);
		clockUpdatedFromMsgPlacedInBoxFlag = true;
		notifyAll();
	}

	private void killAgents() {
		for (Agent a : this.dcop.getAgents()) {
			a.setStopThreadCondition();
			a.notifyAll();
		}
	}

	protected void updateMailerClockUponMsgRecieved(Msg m) {
		int timeMsg = m.getTimeStamp();
		if (this.time <= timeMsg) {
			int oldTime =  this.time;
			this.time = timeMsg;
			
			if (MainSimulator.isThreadDebug) {
				System.out.println("mailer time is updated from "+ oldTime + "to "+this.time+" because time of sender a"+m.getSenderId().getId1()+" is "+m.getTime());
			}
		}else {
			System.err.println("something went wrong with mailer's time with threads");
			throw new RuntimeException();
		}
		
		
	}

	@Override
	protected synchronized List<Msg> handleDelay() {
		List<Msg> toSend = new ArrayList<Msg>();
		for (Msg msg : messageBox) {
			if (msg.getTime() <= this.time) {
				toSend.add(msg);
			}
		}
		this.messageBox.removeAll(toSend);
		
		if (MainSimulator.isThreadDebug) {
			String toprint = "";
			for (Msg msg : toSend) {
				toprint = toprint+ msg.getTime()+", ";
			}
			
			System.out.println("mailer time is "+ this.time +" so msgs with times: "+ toprint+" were sent");
		}
		return toSend;
	}

	@Override
	public void setMailerName() {
		Mailer.mailerName = "Thread";
	}

}
