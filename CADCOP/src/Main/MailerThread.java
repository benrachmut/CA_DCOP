package Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Delays.ProtocolDelayUniform;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgsAgentTimeComparator;
import Problem.Dcop;

public class MailerThread extends Mailer implements Runnable {
	private int time;
	private Collection<Thread> agentsThreads;
	//private boolean clockUpdatedFromMsgPlacedInBoxFlag;

	public MailerThread(Protocol protocol, int terminationTime, Dcop dcop, int dcopId) {
		super(protocol, terminationTime, dcop, dcopId);
		time = 0;
		this.agentsThreads = new HashSet<Thread>();

	}

	@Override
	public void execute() {
		System.out.println("wont be used");
	}



	@Override
	public void run() {
		
		createData(this.time);
		shouldUpdateClockBecuaseNoMsgsRecieved();
		List<Msg> msgToSend1 = this.handleDelay();
		agentsRecieveMsgs(msgToSend1);
		
		while (this.time < this.terminationTime) {
			createData(this.time);
			List<Msg> msgsFromInbox = inbox.extract();
			placeMsgsFromInboxInMessageBox(msgsFromInbox);
			if (!mailerHasMsgsToSend() && areAllIdle()) {
				shouldUpdateClockBecuaseNoMsgsRecieved();
			} 
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend1);
		}
		killAgents();
	}

	/**
	 * give msg delay and treat mailer clock 
	 * @param msgsFromInbox
	 */
	private void placeMsgsFromInboxInMessageBox(List<Msg> msgsFromInbox) {
		for (Msg m : msgsFromInbox) {
			changeMsgsCounter(m);
			int d = createDelay(m instanceof MsgAlgorithm);
			if (d != -1) {
				m.setDelay(d);
				this.messageBox.add(m);
			}
			
			updateMailerClockUponMsgRecieved(m);

			try {
				int t = m.getAgentTime();
				int d1 = m.getDelay();
				int timeToSendByMailer = t + d1;
				m.setAgentTime(timeToSendByMailer);
				m.setMailerTime(this.time);
			} catch (NullPointerException e) {
				m.setAgentTime(m.getAgentTime());
				m.setMailerTime(this.time);
			}

		}
		
	}

	private boolean mailerHasMsgsToSend() {
		Msg minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		int minTime = minTimeMsg.getAgentTime();

		if (minTime <= this.time) {
			return true;
		}
		return false;
	}

	private boolean areAllIdle() {
		for (Agent a : dcop.getAllAgents()) {
			if (!a.getIsIdle()) {
				return false;
			}
		}
		return true;
	}

	
	
	protected void updateMailerClockUponMsgRecieved(Msg m) {
		int timeMsg = m.getAgentTime();
		if (this.time <= timeMsg) {
			this.time = timeMsg;
		}
	}
	
	private synchronized void shouldUpdateClockBecuaseNoMsgsRecieved() {

		Msg<?> minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		int minTime = minTimeMsg.getAgentTime();
		// int oldTime = time;
		if (Main.MainSimulator.isThreadDebug) {
			System.out.println("min agent time check because no msgs to send");
		}
		if (minTime > this.time) {
			this.time = minTime;
		}

		/*
		 * if (clockUpdatedFromMsgPlacedInBoxFlag == false) { if (!messageBox.isEmpty())
		 * { Msg minTimeMsg = Collections.min(messageBox, new
		 * MsgsAgentTimeComparator()); int minTime = minTimeMsg.getAgentTime(); // int
		 * oldTime = time; if (Main.MainSimulator.isThreadDebug) {
		 * System.out.println("min agent time check because no msgs to send"); }
		 * this.time = minTime; } }
		 */
	}

	@Override
	public synchronized void sendMsg(Msg m) {
		super.sendMsg(m);
		updateMailerClockUponMsgRecieved(m);
		try {
			int t = m.getAgentTime();
			int d = m.getDelay();
			int timeToSendByMailer = t + d;
			m.setAgentTime(timeToSendByMailer);
			m.setMailerTime(this.time);
		} catch (NullPointerException e) {
			m.setAgentTime(m.getAgentTime());
			m.setMailerTime(this.time);
		}



	}

	private void killAgents() {
		for (UnboundedBuffer<Msg> ubb: outboxes.values()) {
			ubb.insert(null);
		}
		
		
	}

	

	@Override
	protected synchronized List<Msg> handleDelay() {
		List<Msg> toSend = new ArrayList<Msg>();
		for (Msg msg : messageBox) {
			if (msg.getAgentTime() <= this.time) {
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
