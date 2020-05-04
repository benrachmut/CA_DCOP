package AgentsAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

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

	
	
	
	
	//-----------------**methods of algorithms**---------------

	/**
	 * mailer activates prior to begin of algorithm
	 */
	public abstract void initialize();

	public boolean reactionToMsgs() {
		boolean isUpdate = compute();
		increaseTimeStampCounterAndSendContextMsg(isUpdate);
		return isUpdate;
	}

	@Override
	public int compareTo(Agent a) {
		return a.getId() - this.id;

	}
	
	
	/**
	 * will be used by mailer
	 * @param messages
	 */
	public abstract void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages);

	/**
	 * reaction to msgs include computation and send message to mailer
	 * 
	 * @return true if agents reaction caused change in statues
	 */
	
/**
 * used by public boolean reactionToMsgs()
 * @param changeContext
 */
	
	
	
	protected void increaseTimeStampCounterAndSendContextMsg(boolean changeContext) {
		this.timeStampCounter++;
		sendContextMsg(changeContext);
	}

	protected abstract boolean compute();

	protected abstract void handleAlgorithmicMsgs();

//-----------------**TO-DO**---------------
	protected boolean terminationCondition() {
		// TO-DO
		return false;
	}

}
