package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.Mailer;
import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgsTimeComparator;

public abstract class Agent implements Runnable, Comparable<Agent> {

	protected Integer id;
	protected NodeId nodeId;

	protected int domainSize;
	protected int dcopId;
	protected int timeStampCounter;

	protected boolean isWithTimeStamp;
	protected Mailer mailer;
	protected Double computationCounter;
	private boolean stopThreadCondition;
	protected int time;
	private boolean isIdle;

	public Agent(int dcopId, int D) {
		super();
		this.dcopId = dcopId;
		this.domainSize = D;
		this.timeStampCounter = 0;
		computationCounter = 0.0;
		stopThreadCondition = false;
		this.time = 0;
		isIdle = true;

	}

	public int getId() {
		return this.id;
	}

	protected static <Identity, Context> SortedMap<Identity, Context> resetMapToValueNull(
			SortedMap<Identity, Context> input) {
		SortedMap<Identity, Context> ans = new TreeMap<Identity, Context>();
		for (Identity k : input.keySet()) {
			ans.put(k, null);
		}
		return ans;
	}

	public void resetAgent() {
		this.time = 1;
		this.timeStampCounter = 0;
		computationCounter = 0.0;
		stopThreadCondition = false;
		resetAgentGivenParameters();
		changeRecieveFlagsToFalse();
		isIdle = true;

	}

	protected abstract void resetAgentGivenParameters();

	// -----------------**methods of algorithms**---------------
	@Override
	public int compareTo(Agent a) {
		return this.nodeId.compareTo(a.getNodeId());

	}

	/**
	 * mailer activates prior to algorithm's launch at time 0
	 */
	public abstract void initialize();

	/**
	 * used by mailer, when it has msgs with the receivers address, each agent
	 * updates the relevant field according to the context recieved in the msg
	 * 
	 * @param messages
	 */

	// ------------**Receive Algorithmic Msgs methods**------------

	public synchronized void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {

		for (MsgAlgorithm msgAlgorithm : messages) {
			if (this.isWithTimeStamp) {
				int currentDateInContext;
				try {
					currentDateInContext = getSenderCurrentTimeStampFromContext(msgAlgorithm);
				} catch (NullPointerException e) {
					currentDateInContext = -1;
				}
				if (msgAlgorithm.getTimeStamp() > currentDateInContext) {
					updateMessageInContextAndTreatFlag(msgAlgorithm);
				}
			} else {
				updateMessageInContextAndTreatFlag(msgAlgorithm);
			}
		}
		updateAgentTime(messages);

	
		isIdle = false;
		if (!messages.isEmpty()) {
			if (MainSimulator.isThreadDebug) {
				System.out.println("mailer update "+this+" context, msg time_"+messages.get(0).getTime());
				System.out.println(this+" is NOT idle");
			}	
		}
		this.notifyAll();

	}

	protected void updateAgentTime(List<? extends Msg> messages) {
		Msg msgWithMaxTime = Collections.max(messages, new MsgsTimeComparator());
		int maxTime = msgWithMaxTime.getTime();
		if (this.time <= maxTime) {
			int oldTime = this.time;
			this.time = maxTime;
		}
	}

	/**
	 * 
	 * @param MsgAlgorithm, uses it to get the sender's id
	 * @return last time stamp of message received by sender.
	 */
	protected abstract int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm);

	/**
	 * 
	 * @param MsgAlgorithm, update message received in relevant context message
	 *                      field
	 * @return
	 */

	protected void updateMessageInContextAndTreatFlag(MsgAlgorithm msgAlgorithm) {

		updateMessageInContext(msgAlgorithm);

		changeRecieveFlagsToTrue(msgAlgorithm);
	}

	protected abstract void updateMessageInContext(MsgAlgorithm msgAlgorithm);

	// ------------**Reaction to algorithmic messages methods**------------
	/**
	 * used by mailer after, the mailer uses recieveAlgorithmicMsgs on the receiver
	 * 
	 * @param messages
	 * 
	 */
	public synchronized boolean reactionToAlgorithmicMsgs() {

		if (getDidComputeInThisIteration()) {

			boolean isUpdate = compute();
			if (isMsgGoingToBeSent(isUpdate)) {
				computationCounter = computationCounter + 1;
				this.timeStampCounter = this.timeStampCounter + 1;
				this.time = this.time + 1;
				sendMsgs();
				changeRecieveFlagsToFalse();
			}
			return isUpdate;
		}
		return false;
	}

	public abstract boolean getDidComputeInThisIteration();

	protected boolean isMsgGoingToBeSent(boolean changeContext) {
		return (changeContext && (MainSimulator.sendOnlyIfChange == true)) || (MainSimulator.sendOnlyIfChange == false);
	}

	/**
	 * used by reactionToAlgorithmicMsgs and sent under condition of context sent of
	 * input boolean MainSimulator.sendOnlyIfChange == false
	 * 
	 * @param changeContext
	 */

	public void reactionToAnytimeMsgs() {
	}

	/**
	 * After the context was updated by messages received, computation takes place
	 * using the new information and preparation on context to be sent takes place
	 * 
	 * @return if statues changed after context was updated
	 */
	protected abstract boolean compute();

	/**
	 * after verification, loop over neighbors and send them the message using the
	 * mailer
	 */

	protected abstract void sendMsgs();

	/**
	 * reaction to msgs include computation and send message to mailer
	 * 
	 * @return true if agents reaction caused change in statues
	 */

	/**
	 * used by public boolean reactionToMsgs()
	 * 
	 * @param changeContext
	 */

	/**
	 * in inference needs to return nodeId, else return null
	 * 
	 * @return
	 */
	public NodeId getNodeId() {
		return this.nodeId;
	}

//-----------------**TO-DO**---------------

	public void setIsWithTimeStamp(boolean input) {
		this.isWithTimeStamp = input;

	}

	/**
	 * used by dcop before starting solving dcop
	 * 
	 * @param mailer
	 */
	public void meetMailer(Mailer mailer) {
		this.mailer = mailer;

		this.resetAgent();

	}

	@Override
	public void run() {
		// resetAgent();
		// initialize();
		while (stopThreadCondition == false) {
			waitUntilMsgsRecieved();
		}
	}

	private synchronized void waitUntilMsgsRecieved() {
		if (getDidComputeInThisIteration() == false) {
			try {
				
				isIdle = true;
				if (MainSimulator.isThreadDebug) {
					System.out.println(this+" is idle");
				}
				this.wait();
				mailer.wakeUp();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (stopThreadCondition == true) {
				return;
			}
		}
		this.reactionToAlgorithmicMsgs();
	}

	public static SortedMap<NodeId, Integer> turnMapWithMsgRecieveToContextValues(
			SortedMap<NodeId, MsgReceive<Integer>> input) {
		SortedMap<NodeId, Integer> ans = new TreeMap<NodeId, Integer>();
		for (Entry<NodeId, MsgReceive<Integer>> e : input.entrySet()) {
			if (e.getValue() == null) {
				ans.put(e.getKey(), -1);
			} else {
				ans.put(e.getKey(), e.getValue().getContext());
			}
		}
		return ans;
	}

	protected abstract void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm);

	protected abstract void changeRecieveFlagsToFalse();

	public synchronized void setStopThreadCondition() {
		this.stopThreadCondition = true;
		this.notifyAll();

	}

	public synchronized boolean getIsIdle() {
		return isIdle;
	}

}
