package Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import Messages.Msg;
import Messages.MsgAlgorithm;

public abstract class Agent<Identity, Context> implements Runnable {
	protected Integer id;
	protected int dcopId;
	
	protected  List<MsgAlgorithm<Identity, Context>> msgBoxAlgorithmic;

	public Agent(int dcopId, int id) {
		super();
		this.dcopId = dcopId;
		this.id = id;

		msgBoxAlgorithmic = new ArrayList<MsgAlgorithm<Identity, Context>>();

	}

	public int getId() {
		return this.id;
	}

	protected static<Identity, Context> SortedMap<Identity, Context> resetMapToValueNull(SortedMap<Identity, Context> input) {
		SortedMap<Identity, Context> ans = new TreeMap<Identity, Context>();
		for (Identity k : input.keySet()) {
			ans.put(k, null);
		}
		return ans;
	}

	
	
	public void resetAgent() {
		this.msgBoxAlgorithmic = new ArrayList<MsgAlgorithm<Identity, Context>>();

	}

	public abstract void initialize();



	
	public abstract void recieveAlgorithmicMsgs(List<? extends MsgAlgorithm<Identity, Context>> messages);	
	/**
	 * reaction to msgs include computation and send message to mailer
	 * @return true if agents reaction caused change in statues 
	 */
	public  boolean reactionToMsgs() {
		boolean isUpdate = compute();
		sendContextMsgs(isUpdate);
		return isUpdate;
	}
	

	/**
	 * 
	 * @return
	 */
	protected abstract boolean compute();

	protected abstract void sendContextMsgs(boolean changeContext);

	protected abstract void handleAlgorithmicMsgs();

//-----------------**TO-DO**---------------
	protected boolean terminationCondition() {
		// TO-DO
		return false;
	}
}
