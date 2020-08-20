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
		
		createAndStartAgentThreads();

		while (this.time < this.terminationTime) {
			synchronized (this) {
				while (this.messageBox.isEmpty()) {

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
		int timeToSendByMailer = this.time + m.getDelay();

		m.setTime(timeToSendByMailer);
		if (MainSimulator.isThreadDebug) {
			System.out.println("the time msg will be sent is " + timeToSendByMailer + " from "
					+ m.getSenderId().getId1() + " to " + m.getRecieverId().getId1());
		}
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
		if (MainSimulator.isThreadDebug) {
			System.out.println("the time of mailer is updated to " + this.time);
		}

		// else {
		// System.err.println("time Msg is "+timeMsg+" and mailer time is "+this.time+
		// ". something went wrong with mailer's time with threads");
		// throw new RuntimeException();
		// }

	}

	@Override
	protected synchronized List<Msg> handleDelay() {

		List<Msg> toSend = new ArrayList<Msg>();
		for (Msg msg : messageBox) {
			if (msg.getTime() <= this.time) {
				if (MainSimulator.isThreadDebug) {
					System.out.println("msg is sent at time " + this.time + " from " + msg.getSenderId().getId1()
							+ " to " + msg.getRecieverId().getId1());
				}

				toSend.add(msg);
			}
		}
		this.messageBox.removeAll(toSend);
		if (MainSimulator.isThreadDebug) {
			for (Msg msg : toSend) {
				System.out.println("delivered " + msg);

			}
		}
		return toSend;
	}

	@Override
	public void setMailerName() {
		Mailer.mailerName = "Thread";
	}

}
