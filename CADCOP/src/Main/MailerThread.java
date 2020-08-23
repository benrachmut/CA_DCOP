package Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.sun.swing.internal.plaf.synth.resources.synth;

import AgentsAbstract.Agent;
import Messages.Msg;
import Messages.MsgAlgorithm;
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
		System.out.println("wont be used");
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
		for (Agent a : this.dcop.getAgents()) {
			a.resetAgent();
			a.initialize();
		}
		createAndStartAgentThreads();
		
		createData(this.time);
		shouldUpdateClockBecuaseNoMsgsRecieved();
		List<Msg> msgToSend1 = this.handleDelay();
		agentsRecieveMsgs(msgToSend1);
		clockUpdatedFromMsgPlacedInBoxFlag = false;
		

		while (this.time < this.terminationTime) {
			synchronized (this) {
				while (this.messageBox.isEmpty()  ) {

					if (MainSimulator.isThreadDebug) {
						System.out.println("mailer went to sleep");
					}
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			createData(this.time);
			shouldUpdateClockBecuaseNoMsgsRecieved();
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);
			clockUpdatedFromMsgPlacedInBoxFlag = false;

		}
		killAgents();
	}



	private synchronized void shouldUpdateClockBecuaseNoMsgsRecieved() {
		if (clockUpdatedFromMsgPlacedInBoxFlag == false) {
			
			Msg minTimeMsg = Collections.min(messageBox, new MsgsTimeComparator());
			int minTime = minTimeMsg.getTime();
			int oldTime = time;
			this.time = minTime;
		}
	}

	@Override
	public synchronized void sendMsg(Msg m) {
		super.sendMsg(m);
		updateMailerClockUponMsgRecieved(m);
		//int timeToSendByMailer = this.time + m.getDelay();
		if (m instanceof MsgAlgorithm) {
			((MsgAlgorithm)m).setArtificialMsg(true);
		}
		//m.setTime(timeToSendByMailer);
		

		this.notifyAll();
		
	}
	
	
	public synchronized void sendMsgWitoutDelay(MsgAlgorithm m) {
		super.sendMsgWitoutDelay(m);
		
		updateMailerClockUponMsgRecieved(m);
		int timeToSendByMailer = this.time + m.getDelay();

		m.setTime(timeToSendByMailer);
	
		clockUpdatedFromMsgPlacedInBoxFlag = true;

		this.notifyAll();
		if (MainSimulator.isThreadDebug) {
			System.out.println("mailer woke up");
		}
		
		
	}

	private  void killAgents() {
		for (Agent a : this.dcop.getAgents()) {
			a.setStopThreadCondition();
		}
	}

	protected void updateMailerClockUponMsgRecieved(Msg m) {
		int timeMsg = m.getTime();
		if (this.time <= timeMsg) {
			int oldTime = this.time;
			this.time = timeMsg;
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
	
		return toSend;
	}

	@Override
	public void setMailerName() {
		Mailer.mailerName = "Thread";
	}

}
