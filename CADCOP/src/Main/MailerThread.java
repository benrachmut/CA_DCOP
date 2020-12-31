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
	// private boolean clockUpdatedFromMsgPlacedInBoxFlag;

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
		// List<Msg> msgsFromInbox = new ArrayList<Msg>();
		// while (inbox.isEmpty() ==false) {
		// Msg m = inbox.extract();
		// msgsFromInbox.add(m);
		// }
		List<Msg> msgsFromInbox = inbox.extract();
		if (MainSimulator.isThreadDebug) {
			System.out.println("mailer msgs extract from inbox: " + msgsFromInbox);
		}
		placeMsgsFromInboxInMessageBox(msgsFromInbox);
		shouldUpdateClockBecuaseNoMsgsRecieved();
		//updateMailerClockUponMsgRecieved(msgToSend);

		List<Msg> msgToSend = this.handleDelay();
		agentsRecieveMsgs(msgToSend);

		msgsFromInbox = new ArrayList<Msg>();
		/*
		 * List<Msg> msgToSend1 = this.handleDelay(); agentsRecieveMsgs(msgToSend1);
		 */

		while (this.time < this.terminationTime) {

			createData(this.time);
			if (MainSimulator.isThreadDebug) {
				System.out.println("mailer goes to sleep");
			}
			//if (this.messageBox.isEmpty() || !inbox.isEmpty()) {
				// msgsFromInbox = new ArrayList<Msg>();
				msgsFromInbox = inbox.extract();
				// msgsFromInbox.add();
				placeMsgsFromInboxInMessageBox(msgsFromInbox);
				if (MainSimulator.isThreadDebug) {
					System.out.println("mailer wakes up");
			//	}
			//}
			// if (areAllIdle()) {
			if (!mailerHasMsgsToSend()) {
				// if (areAllIdle() && !mailerHasMsgsToSend()) {
				// if (areAllIdle() ) {
				shouldUpdateClockBecuaseNoMsgsRecieved();
				//System.out.println("aaaaaaaa");
				// }
				// }
			}
			msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);
			//updateMailerClockUponMsgRecieved(msgToSend);

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
			changeMsgsCounter(m);
			updateMailerClockUponMsgRecieved(m);
			if (m.isWithDelay()) {
				int d = createDelay(m instanceof MsgAlgorithm);
				m.setTimeOfMsg(d);
				/*
				 * if (d != -1) { m.setDelay(d); }
				 */
			}
			this.messageBox.add(m);

			/*
			 * try { int t = m.getTimeOfMsg(); int d1 = m.getDelay(); int timeToSendByMailer
			 * = t + d1; m.setTimeOfMsg(timeToSendByMailer); //m.setMailerTime(this.time); }
			 * catch (NullPointerException e) { m.setAgentTime(m.getAgentTime());
			 * //m.setMailerTime(this.time); }
			 */

		}

	}

	private boolean mailerHasMsgsToSend() {
		Msg minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		int minTime = minTimeMsg.getTimeOfMsg();

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
		//for (Msg msg : msgToSend) {
			int timeMsg = msg.getTimeOfMsg();
			if (this.time <= timeMsg) {
				this.time = timeMsg;
			}
		//}
		
	}

	private void shouldUpdateClockBecuaseNoMsgsRecieved() {

		Msg<?> minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
		int minTime = minTimeMsg.getTimeOfMsg();
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
