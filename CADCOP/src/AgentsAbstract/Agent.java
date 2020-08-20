package AgentsAbstract;

import java.util.ArrayList;
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

public abstract class Agent implements Runnable, Comparable<Agent> {

	protected Integer id;
	protected NodeId nodeId;

	protected int domainSize;
	protected int dcopId;
	protected int timeStampCounter;

	protected boolean isWithTimeStamp;
	protected Mailer mailer;
	private Double computationCounter;
	private boolean stopThreadCondition;

	public Agent(int dcopId, int D) {
		super();
		this.dcopId = dcopId;
		this.domainSize = D;
		this.timeStampCounter = 0;
		computationCounter = 0.0;
		stopThreadCondition = false;

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
		this.timeStampCounter = 0;
		computationCounter = 0.0;
		stopThreadCondition = false;
		resetAgentGivenParameters();
		changeRecieveFlagsToFalse();

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

	public void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		for (MsgAlgorithm msgAlgorithm : messages) {
			if (this.isWithTimeStamp) {
				int currentDateInContext;
				try {
					currentDateInContext = getSenderCurrentTimeStampFromContext(msgAlgorithm);

				} catch (NullPointerException e) {
					currentDateInContext = -1;
				}
				/*
				 * if (this.id==25 && msgAlgorithm.getSenderId().getId1()==12) {
				 * System.out.println(4); }
				 */

				if (msgAlgorithm.getTimeStamp() > currentDateInContext) {
					updateMessageInContextAndTreatFlag(msgAlgorithm);
				}
			} else {
				updateMessageInContextAndTreatFlag(msgAlgorithm);

			}
			
			
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
	 *        field
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
	public void reactionToAlgorithmicMsgs() {
		boolean isUpdate = compute();
		if (isMsgGoingToBeSent(isUpdate)) {
			if (getDidComputeInThisIteration()) {
				computationCounter = computationCounter + 1;
				this.timeStampCounter = this.timeStampCounter + 1;
				sendMsgs();
				changeRecieveFlagsToFalse();
			}
		}
	}

	protected abstract boolean getDidComputeInThisIteration();

	private boolean isMsgGoingToBeSent(boolean changeContext) {
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
	protected boolean terminationCondition() {
		// TO-DO
		return false;
	}

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
		resetAgent();
		initialize();
		while (stopThreadCondition == false) {
			waitUntilMsgsRecieved();
		}
	}

	private synchronized void waitUntilMsgsRecieved() {
		while (getDidComputeInThisIteration() == true) {
			try {
				this.wait();
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

	}

}
