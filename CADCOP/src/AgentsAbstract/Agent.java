package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import Main.Mailer;
import Main.MainSimulator;
import Main.UnboundedBuffer;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgReceive;
import Messages.MsgsMailerTimeComparator;

public abstract class Agent implements Runnable, Comparable<Agent> {

	protected Integer id;
	protected NodeId nodeId;

	protected int atomicActionCounter;
	protected int domainSize;
	protected int dcopId;
	protected int timeStampCounter;

	protected boolean isWithTimeStamp;
	//protected Mailer mailer;
	protected UnboundedBuffer<Msg> outbox;

	protected Double computationCounter;
	//protected boolean stopThreadCondition;
	protected int time;
	protected boolean isIdle;

	protected TimeObject timeObject;
	protected UnboundedBuffer<Msg> inbox;

	public Agent(int dcopId, int D) {
		super();
		this.dcopId = dcopId;
		this.domainSize = D;
		this.timeStampCounter = 0;
		computationCounter = 0.0;
		//stopThreadCondition = false;
		this.time = 0;
		isIdle = true;
		atomicActionCounter = 0;
		timeObject = new TimeObject(0);

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
		//stopThreadCondition = false;
		resetAgentGivenParameters();
		changeRecieveFlagsToFalse();
		isIdle = true;
		atomicActionCounter = 0;
		timeObject.setTimeOfObject(1);
	}

	public void setTimeObject(TimeObject input) {
		this.timeObject = input;
	}
	
	public TimeObject getTimeObject() {
		return this.timeObject;
	}

	// -----------------**methods of algorithms**---------------
	@Override
	public int compareTo(Agent a) {
		return this.nodeId.compareTo(a.getNodeId());

	}
	

	
	
	
	
	
	// ------------**Receive Algorithmic Msgs methods**------------

	public void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		if (MainSimulator.isMaxSumThreadDebug) {
			System.out.println(this+" recieve msgs");
		}
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

		if (!messages.isEmpty()) {
			if (MainSimulator.isThreadDebug) {
				System.out.println("mailer update " + this + " context, msg time_" + messages.get(0).getAgentTime());
				System.out.println(this + " is NOT idle");
			}
		}

		if (MainSimulator.isMaxSumThreadDebug) {
			System.out.println(this+" is about to notifyAll");
		}
		this.notifyAll();

	}

	
	protected void updateAgentTime(List<? extends Msg> messages) {
		Msg msgWithMaxTime = Collections.max(messages, new MsgsMailerTimeComparator());

		if (MainSimulator.isThreadDebug && messages.size() > 1) {
			System.out.println(this.toString() + " update time upon msg recieve");
		}

		int maxAgentTime = msgWithMaxTime.getMailerTime();

		if (this.time <= maxAgentTime) {
			int oldTime = this.time;
			this.time = maxAgentTime;
		}

	}


	/**
	 * 
	 * @param MsgAlgorithm, update message received in relevant context message
	 *                      field
	 * @return
	 */

	protected void updateMessageInContextAndTreatFlag(MsgAlgorithm msgAlgorithm) {
		boolean isUpdate = updateMessageInContext(msgAlgorithm);
		if (isUpdate) {
			changeRecieveFlagsToTrue(msgAlgorithm);

		}
	}

	
	// ------------**Reaction to algorithmic messages methods**------------
	/**
	 * used by mailer after, the mailer uses recieveAlgorithmicMsgs on the receiver
	 * 
	 * @param messages
	 * 
	 */
	public boolean reactionToAlgorithmicMsgs() {
		this.atomicActionCounter = 0;

		if (getDidComputeInThisIteration()) {
			boolean isUpdate = compute();
			if (isMsgGoingToBeSent(isUpdate)) {
				if (MainSimulator.isMaxSumThreadDebug) {
					System.out.println(this + "time is " + this.time + " BEFORE because computation");
				}
				computationCounter = computationCounter + 1;
				this.timeStampCounter = this.timeStampCounter + 1;
				if (MainSimulator.isAtomicTime) {
					this.time = this.time + this.atomicActionCounter;
					this.atomicActionCounter = 0;

				} else {
					this.time = this.time + 1;
				}
				this.sendMsgs();
				this.changeRecieveFlagsToFalse();

			}
			return isUpdate;

		}
		return false;
	}

	// protected abstract int numberOfAtomicActionsInComputation();


	protected boolean isMsgGoingToBeSent(boolean changeContext) {
		return (changeContext && (MainSimulator.sendOnlyIfChange == true)) || (MainSimulator.sendOnlyIfChange == false);
	}


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
	public void meetMailer(UnboundedBuffer<Msg> msgsFromMeToMailer, UnboundedBuffer<Msg> msgsFromMailerToMe) {
		this.outbox = msgsFromMeToMailer;
		this.inbox = msgsFromMailerToMe;

		this.resetAgent();

	}

	@Override
	public void run() {
		
		while (true) {
			
			isIdle = true;
			List<Msg> messages = this.inbox.extract();
			isIdle = false;

			if (messages == null) {
				break;
			}
			List<MsgAlgorithm> algorithmicMsgs = extractAlgorithmicMsgs(messages);
			checkingAllMsgsShouldBeAlgorithmicMsgs(messages, algorithmicMsgs);
			receiveAlgorithmicMsgs(algorithmicMsgs);
			reactionToAlgorithmicMsgs();

		}
	}

	protected void checkingAllMsgsShouldBeAlgorithmicMsgs(List<Msg> messages, List<MsgAlgorithm> algorithmicMsgs) {
		if (messages.size() != algorithmicMsgs.size()) {
			throw new RuntimeException("all messages should be algorithmic msgs");
		}
	}



	protected List<MsgAlgorithm> extractAlgorithmicMsgs(List<Msg> messages) {
		List<MsgAlgorithm> ans = new ArrayList<MsgAlgorithm>();
		for (Msg msg : messages) {
			if (msg instanceof MsgAlgorithm) {
				ans.add((MsgAlgorithm)msg);
			}
		}
		return ans;
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

	

	
	
	protected abstract void resetAgentGivenParameters();

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
	
	
	/**
	 * 
	 * @param MsgAlgorithm, uses it to get the sender's id
	 * @return last time stamp of message received by sender.
	 */
	protected abstract int getSenderCurrentTimeStampFromContext(MsgAlgorithm msgAlgorithm);
	
	
	protected abstract boolean updateMessageInContext(MsgAlgorithm msgAlgorithm);

	
	public abstract boolean getDidComputeInThisIteration();

	
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

	public abstract void sendMsgs();
	
	
	protected abstract void changeRecieveFlagsToTrue(MsgAlgorithm msgAlgorithm);

	public abstract void changeRecieveFlagsToFalse();
	
	

	public boolean getIsIdle() {
		return isIdle;
	}

}