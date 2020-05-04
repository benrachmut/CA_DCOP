package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;

public abstract class Agent implements Runnable, Comparable<Agent> {
	protected Integer id;
	protected int domainSize;
	protected int dcopId;
	protected int timeStampCounter;

	protected List<MsgAlgorithm> msgBoxAlgorithmic;

	public Agent(int dcopId, int D) {
		super();
		this.dcopId = dcopId;
		this.domainSize = D;
		this.timeStampCounter = 0;
		msgBoxAlgorithmic = new ArrayList<MsgAlgorithm>();

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
		this.msgBoxAlgorithmic = new ArrayList<MsgAlgorithm>();
		this.timeStampCounter = 0;

	}

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
	 * @param messages
	 */
	
	public abstract void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages);

	
	/**
	 * used by mailer after, the mailer uses recieveAlgorithmicMsgs on the receiver
	 * @param messages
	 */ 
	public boolean reactionToAlgorithmicMsgs() {
		boolean isUpdate = compute();
		varifyIfMsgsWillBeSent(isUpdate);
		return isUpdate;
	}

	/**
	 * After the context was updated by messages received, 
	 * computation takes place using the new information
	 * and preparation on context to be sent takes place
	 * @return if statues changed after context was updated
	 */
	protected abstract boolean compute();

	/**
	 * used by reactionToAlgorithmicMsgs and sent under condition of context sent of 
	 * input boolean MainSimulator.sendOnlyIfChange == false
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
	 * after varification, loop over neighbors and send them the message using the mailer
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

//-----------------**TO-DO**---------------
	protected boolean terminationCondition() {
		// TO-DO
		return false;
	}

}
