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
import AlgorithmSearch.AMDLS_V1;
import Delays.ProtocolDelayUniform;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgsAgentTimeComparator;
import Problem.Dcop;

public class MailerThread extends Mailer implements Runnable {
	private long time;
	private Collection<Thread> agentsThreads;
	// private boolean clockUpdatedFromMsgPlacedInBoxFlag;

	public MailerThread(Protocol protocol, long terminationTime, Dcop dcop, int dcopId) {
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
		if (MainSimulator.isAMDLSDistributedDebug) {
			for (Agent a : dcop.getAllAgents()) {
				System.out.println((AMDLS_V1)a+" neighbors: "+((AMDLS_V1)a).getNeigborSetId());
			}
		}
		
		
		createData(this.time);
	
		List<Msg> msgsFromInbox = inbox.extract();
		if (MainSimulator.isThreadDebug) {
			System.out.println("mailer msgs extract from inbox: " + msgsFromInbox);
		}
		placeMsgsFromInboxInMessageBox(msgsFromInbox);
		shouldUpdateClockBecuaseNoMsgsRecieved();

		List<Msg> msgToSend = this.handleDelay();
		agentsRecieveMsgs(msgToSend);

		msgsFromInbox = new ArrayList<Msg>();
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		boolean flag5 = false;
		boolean flag6 = false;
		boolean flag7 = false;
		boolean flag8 = false;
		boolean flag9 = false;

		while (this.time < this.terminationTime) {
			
			if (MainSimulator.isAnytimeDebug && this.time>500000000 && !flag1) {
				System.out.println(this.time);
				flag1 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>1000000000 && !flag2) {
				System.out.println(this.time);
				flag2 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>1500000000L && !flag3) {
				System.out.println(this.time);
				flag3 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>2000000000L && !flag4) {
				System.out.println(this.time);
				flag4 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>2500000000L && !flag5) {
				System.out.println(this.time);
				flag5 = true;
			}
			
			if (MainSimulator.isAnytimeDebug && this.time>3000000000L && !flag6) {
				System.out.println(this.time);
				flag6 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>3500000000L && !flag7) {
				System.out.println(this.time);
				flag7 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>4000000000L && !flag8) {
				System.out.println(this.time);
				flag8 = true;
			}
			if (MainSimulator.isAnytimeDebug && this.time>4500000000L && !flag9) {
				System.out.println(this.time);
				flag9 = true;
			}
			createData(this.time);

			while (inbox.isEmpty()) {
				if (areAllIdle() && inbox.isEmpty() && !this.messageBox.isEmpty()) {
					shouldUpdateClockBecuaseNoMsgsRecieved();
					msgToSend = this.handleDelay();
					agentsRecieveMsgs(msgToSend);
					//System.out.println("aaaaaa");
				}
			}
		

		if (MainSimulator.isThreadDebug) {
			System.out.println("mailer goes to sleep");
		}

		msgsFromInbox = inbox.extract();
		placeMsgsFromInboxInMessageBox(msgsFromInbox);
		if (MainSimulator.isThreadDebug) {
			System.out.println("mailer wakes up");

		}

		msgToSend = this.handleDelay();
		agentsRecieveMsgs(msgToSend);

	}

	killAgents();

	}

	/**
	 * give msg delay and treat mailer clock
	 * 
	 * @param msgsFromInbox
	 */
	private void placeMsgsFromInboxInMessageBox(List<Msg> msgsFromInbox) {
		for (Msg m : msgsFromInbox) {
			boolean flag = false;
			
			updateMailerClockUponMsgRecieved(m);
			if (m.isWithDelay()) {
				int d = createDelay(m instanceof MsgAlgorithm);
				if (d == -1) {
					flag = true;
				}
				m.setTimeOfMsg(d);
			}
			if (!flag) {
				changeMsgsCounter(m);
				this.messageBox.add(m);
			}
		}

	}

	
	private boolean mailerHasMsgsToSend() {
		Msg minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		long minTime = minTimeMsg.getTimeOfMsg();

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

	protected void updateMailerClockUponMsgRecieved(Msg msg) {
		// for (Msg msg : msgToSend) {
		long timeMsg = msg.getTimeOfMsg();
		if (this.time <= timeMsg) {
			this.time = timeMsg;
		}
		// }

	}

	private void shouldUpdateClockBecuaseNoMsgsRecieved() {

		Msg<?> minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		long minTime = minTimeMsg.getTimeOfMsg();
		// int oldTime = time;

		if (minTime > this.time) {
			this.time = minTime;
		}

	}
	/*
	 * @Override public void sendMsg(Msg m) { super.sendMsg(m);
	 * updateMailerClockUponMsgRecieved(m); try { int t = m.getAgentTime(); int d =
	 * m.getDelay(); int timeToSendByMailer = t + d;
	 * m.setAgentTime(timeToSendByMailer); m.setMailerTime(this.time); } catch
	 * (NullPointerException e) { m.setAgentTime(m.getAgentTime());
	 * m.setMailerTime(this.time); }
	 * 
	 * }
	 */

	private void killAgents() {
		for (UnboundedBuffer<Msg> ubb : outboxes.values()) {
			ubb.removeAllMsgs();
			ubb.insert(null);
		}

	}

	@Override
	protected List<Msg> handleDelay() {
		List<Msg> toSend = new ArrayList<Msg>();
		for (Msg msg : messageBox) {
			if (msg.getTimeOfMsg() <= this.time) {
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
