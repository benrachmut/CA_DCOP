package Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import AgentsAbstract.NodeId;
import Delays.ProtocolDelayUniform;
import Messages.Msg;
import Messages.MsgsAgentTimeComparator;
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
			if (MainSimulator.isThreadDebug)  {
				System.out.println(a+" initialize");
			}
		}
		createAndStartAgentThreads();

		createData(this.time);
		shouldUpdateClockBecuaseNoMsgsRecieved();
		List<Msg> msgToSend1 = this.handleDelay();
		agentsRecieveMsgs(msgToSend1);
		clockUpdatedFromMsgPlacedInBoxFlag = false;

		while (this.time < this.terminationTime) {
			synchronized (this) {
				while (this.messageBox.isEmpty() || !areAllIdle()) {
					try {
						if (MainSimulator.isThreadDebug) {
							System.out.println("mailer went to sleep");
						}
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

	private boolean areAllIdle() {
		for (Agent a : dcop.getAgents()) {
			if (!a.getIsIdle()) {
				return false;
			}
		}
		return true;
	}

	private  void shouldUpdateClockBecuaseNoMsgsRecieved() {
		if (clockUpdatedFromMsgPlacedInBoxFlag == false) {
			if (!messageBox.isEmpty()) {
				Msg minTimeMsg = Collections.min(messageBox, new MsgsAgentTimeComparator());
				int minTime = minTimeMsg.getAgentTime();
				//int oldTime = time;
				if (Main.MainSimulator.isThreadDebug) {
					System.out.println("min agent time check because no msgs to send");
				}
				
				this.time = minTime;
			}
			
		}
	}

	@Override
	public synchronized void sendMsg(Msg m) {
		super.sendMsg(m);
		updateMailerClockUponMsgRecieved(m);
		int timeToSendByMailer = m.getAgentTime() + m.getDelay();

		m.setAgentTime(timeToSendByMailer);
		m.setMailerTime(this.time);
		if (MainSimulator.isThreadDebug) {
			NodeId sender = m.getSenderId();
			NodeId reciever = m.getRecieverId();
			int t = m.getAgentTime();
			
			System.out.println("Msg placed in box: s_"+sender+" r_"+reciever+" mailer time_"+this.time+" will be sent_"+t);
			System.out.println("A_"+sender+"wakes mailer up");
		}
		
		clockUpdatedFromMsgPlacedInBoxFlag = true;
		
		this.wakeUp();

	}

	private void killAgents() {
		for (Agent a : this.dcop.getAgents()) {
			a.setStopThreadCondition();
		}
	}

	protected void updateMailerClockUponMsgRecieved(Msg m) {

		int timeMsg = m.getAgentTime();
		if (MainSimulator.isThreadDebug && timeMsg>1) {
			System.out.println();
		}
		if (this.time <= timeMsg) {
			int oldTime = this.time;
			this.time = timeMsg;
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
			if (msg.getAgentTime()<= this.time) {
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
