package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.Mailer;
import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;

public abstract class Agent implements Runnable, Comparable<Agent> {
	protected Integer id;
	protected int domainSize;
	protected int dcopId;
	protected int timeStampCounter;

	private boolean isWithTimeStamp;
	protected Mailer mailer;

	public Agent(int dcopId, int D) {
		super();
		this.dcopId = dcopId;
		this.domainSize = D;
		this.timeStampCounter = 0;
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
		resetAgentGivenParameters();
	}

	protected abstract void  resetAgentGivenParameters();

	// -----------------**methods of algorithms**---------------
	@Override
	public int compareTo(Agent a) {
		return a.getId() - this.id;

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
				double currentDateInContext = getSenderCurrentTimeStampFromContext(msgAlgorithm);
				if (msgAlgorithm.getTimeStamp() > currentDateInContext) {
					updateMessageInContext(msgAlgorithm);
				}
			} else {
				updateMessageInContext(msgAlgorithm);
			}
		}
	}

	/**
	 * 
	 * @param MsgAlgorithm, uses it to get the sender's id
	 * @return last time stamp of message received by sender.
	 */
	protected abstract double getSenderCurrentTimeStampFromContext(MsgAlgorithm MsgAlgorithm);

	/**
	 * 
	 * @param MsgAlgorithm, update message received in relevant context message
	 *        field
	 * @return
	 */
	protected abstract double updateMessageInContext(MsgAlgorithm MsgAlgorithm);

	// ------------**Reaction to algorithmic messages methods**------------
	/**
	 * used by mailer after, the mailer uses recieveAlgorithmicMsgs on the receiver
	 * 
	 * @param messages
	 * 
	 */
	public boolean reactionToAlgorithmicMsgs() {
		boolean isUpdate = compute();
		varifyIfMsgsWillBeSent(isUpdate);
		return isUpdate;
	}

	/**
	 * After the context was updated by messages received, computation takes place
	 * using the new information and preparation on context to be sent takes place
	 * 
	 * @return if statues changed after context was updated
	 */
	protected abstract boolean compute();

	/**
	 * used by reactionToAlgorithmicMsgs and sent under condition of context sent of
	 * input boolean MainSimulator.sendOnlyIfChange == false
	 * 
	 * @param changeContext
	 */
	protected void varifyIfMsgsWillBeSent(boolean changeContext) {
		if (isMsgGoingToBeSent(changeContext)) {
			this.timeStampCounter++;
			sendMsg();
		}
	}

	private boolean isMsgGoingToBeSent(boolean changeContext) {
		return (changeContext && MainSimulator.sendOnlyIfChange == true) || (MainSimulator.sendOnlyIfChange == false);
	}

	/**
	 * after varification, loop over neighbors and send them the message using the
	 * mailer
	 */
	
	
	protected abstract void sendMsg();

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
	 * @return
	 */
	public NodeId getNodeId() {
		return null;
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
	 * @param mailer
	 */
	public void meetMailer(Mailer mailer) {
		this.mailer = mailer;
		this.resetAgent();
		
	}

}
