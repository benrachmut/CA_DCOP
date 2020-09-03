package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Messages.MsgAnyTimeDown;
import Messages.MsgAnyTimeUp;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public abstract class AgentVariableSearch extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<Integer>> neighborsValueAssignmnet; // id, variable

	// ------Anytime
	
	protected NodeId anytimeFather;
	protected Set<NodeId> anytimeSons;
	private Set<NodeId> belowAnytime;

	
	
	protected List<Context> anytimeUpToSend;
	protected List<Context> anytimeDownToSend;
	

	private Integer anytimeValueAssignmnet;
	private MsgReceive<Context> anytimeBestContext;
	private List<Context> contextInMemory;
	private Random randContext;


	public AgentVariableSearch(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.neighborsValueAssignmnet = new TreeMap<NodeId, MsgReceive<Integer>>();
		anytimeUpToSend = new ArrayList<Context>();
		anytimeDownToSend = new ArrayList<Context>();
		anytimeValueAssignmnet = null;
		anytimeBestContext = null;
		contextInMemory = new ArrayList<Context>();
		randContext = new Random(this.id * 10 + dcopId * 153);
		if (MainSimulator.isAnytime) {
			this.isWithTimeStamp = true;
		}
	}

	@Override
	public void initialize() {
		this.sendValueAssignmnetMsgs();
	}

	@Override
	public void meetNeighbor(int neighborId, Integer[][] constraint) {
		super.meetNeighbor(neighborId, constraint);
		this.neighborsValueAssignmnet.put(new NodeId(neighborId), null);
	}

	@Override
	public void resetAgentGivenParametersV2() {
		this.neighborsValueAssignmnet = Agent
				.<NodeId, MsgReceive<Integer>>resetMapToValueNull(this.neighborsValueAssignmnet);
		anytimeValueAssignmnet = null;
		contextInMemory = new ArrayList<Context>();

		anytimeUpToSend = new ArrayList<Context>();
		anytimeDownToSend = new ArrayList<Context>();
		anytimeBestContext = null;
		randContext = new Random(this.id * 10 + dcopId * 153);

		resetAgentGivenParametersV3();
	}

	protected abstract void resetAgentGivenParametersV3();

	public double getCostPov() {
		return getCostPerInput(this.valueAssignment);
	}

	public int getCostPerInput(int input) {
		int ans = 0;
		for (Entry<NodeId, MsgReceive<Integer>> e : this.neighborsValueAssignmnet.entrySet()) {

			if (e.getValue() != null) {
				Object context = e.getValue().getContext();
				int nValueAssignmnet = e.getValue().getContext();

				Integer[][] nConst = this.neighborsConstraint.get(e.getKey());
				ans += nConst[input][nValueAssignmnet];
			}
		}
		return ans;
	}

	protected SortedMap<Integer, Integer> getCostPerDomain() {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();
		for (int domainCandidate : domainArray) {

			int sumCostPerAgent = this.getCostPerInput(domainCandidate);
			ans.put(domainCandidate, sumCostPerAgent);

		}
		return ans;
	}

	protected int getCandidateToChange() {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int minCost = Collections.min(costPerDomain.values());
		int costOfCurrentValue = costPerDomain.get(this.valueAssignment);
		if (minCost <= costOfCurrentValue) {
			SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
			if (alternatives.isEmpty()) {
				return this.valueAssignment;
			}
			return alternatives.first();
		}
		return this.valueAssignment;

	}

	private SortedSet<Integer> getAlternativeCandidate(int minCost, SortedMap<Integer, Integer> costPerDomain) {
		SortedSet<Integer> ans = new TreeSet<Integer>();
		for (Entry<Integer, Integer> e : costPerDomain.entrySet()) {
			int cost = e.getValue();
			int valueAssignmnet = e.getKey();
			if (cost == minCost) {
				ans.add(valueAssignmnet);
			}
		}
		if (ans.contains(this.valueAssignment)) {
			ans.remove(this.valueAssignment);
		}

		return ans;
	}

	protected void updateMsgInContextValueAssignmnet(MsgAlgorithm msgAlgorithm) {
		Integer context = (Integer) msgAlgorithm.getContext();
		int timestamp = msgAlgorithm.getTimeStamp();
		MsgReceive<Integer> msgReceive = new MsgReceive<Integer>(context, timestamp);
		this.neighborsValueAssignmnet.put(msgAlgorithm.getSenderId(), msgReceive);
	}

	protected int getTimestampOfValueAssignmnets(MsgAlgorithm msgAlgorithm) {
		NodeId senderNodeId = msgAlgorithm.getSenderId();
		MsgReceive<Integer> msgReceive = this.neighborsValueAssignmnet.get(senderNodeId);
		if (msgReceive == null) {
			return -1;
		}
		return msgReceive.getTimestamp();
	}

	protected void sendValueAssignmnetMsgs() {

		if (MainSimulator.isThreadDebug) {
			System.out.println("Agent " + this.id + " is sending msgs at time " + this.time);
		}

		if (MainSimulator.isWhatAgentDebug && this.id == 1) {
			System.out.println("sending msgs");
		}

		for (NodeId recieverNodeId : neighborsConstraint.keySet()) {
			MsgValueAssignmnet mva = new MsgValueAssignmnet(this.nodeId, recieverNodeId, this.valueAssignment,
					this.timeStampCounter, this.time);
			this.mailer.sendMsg(mva);
		}

	}

	@Override
	public synchronized boolean reactionToAlgorithmicMsgs() {
		boolean isValueAssignmnetChange = super.reactionToAlgorithmicMsgs();
		if (MainSimulator.isAnytime) {
			if (isValueAssignmnetChange) {
				Context context_i = createMyContext();
				placeContextInMemory(context_i);
			}
		}
		return isValueAssignmnetChange;

	}

	public synchronized void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		Context context_i_beforeMsgUpdate = null;

		if (MainSimulator.isAnytime) {
			if (!messages.isEmpty()) {
				context_i_beforeMsgUpdate = createMyContext();
			}
		}

		super.receiveAlgorithmicMsgs(messages);

		if (MainSimulator.isAnytime) {
			if (isWithTimeStamp) {
				if (!messages.isEmpty()) {
					Context context_i_AfterMsgUpdate = createMyContext();
					if (!context_i_beforeMsgUpdate.isSameValueAssignmnets(context_i_AfterMsgUpdate)) {
						placeContextInMemory(context_i_AfterMsgUpdate);
					}
				}
			}
		}

	}

	private Context createMyContext() {

		TreeMap<Integer, Integer> m = new TreeMap<Integer, Integer>();
		for (Entry<NodeId, MsgReceive<Integer>> e : this.neighborsValueAssignmnet.entrySet()) {
			m.put(e.getKey().getId1(), e.getValue().getContext());
		}

		int myCost = this.getCostPerDomain().get(this.valueAssignment);

		return new Context(m, this.id, myCost);
	}

	// --------------**TO-DO**--------------------
	public synchronized void recieveAnyTimeMsgs(List<? extends MsgAnyTime> messages) {

		for (MsgAnyTime msgAnyTime : messages) {
			if (msgAnyTime instanceof MsgAnyTimeUp) {
				placeContextInMemory((Context) msgAnyTime.getContext());
			}
			if (msgAnyTime instanceof MsgAnyTimeDown) {
				MsgAnyTimeDown down = (MsgAnyTimeDown) msgAnyTime;
				int ts = down.getTimeStamp();
				Context c = (Context) down.getContext();
				MsgReceive<Context> mr = new MsgReceive<Context>(c, ts);
				if (this.anytimeBestContext.getTimestamp() < ts) {
					anytimeBestContext = mr;
					this.anytimeValueAssignmnet = c.getValueAssignmentPerAgent(this.id);
					this.anytimeDownToSend.add(c);
				}

			}
		}

		updateAgentTime(messages);

	}

	private void placeContextInMemory(Context input) {
		if (contextHasNulls()) {
			return;
		}
		Collection<Context> toAdd = new ArrayList<Context>();
		Collection<Context> toDelete = new ArrayList<Context>();
		getConextToAdd(input, toAdd, toDelete);

		Collection<Context> toSend = new ArrayList<Context>();
		whichContextsShouldBeSent(toAdd, toSend);

		addGivenHeuristic(toAdd);

	}

	private void whichContextsShouldBeSent(Collection<Context> toAdd, Collection<Context> toSend) {
		for (Context context : toAdd) {
			if (contextIncludesCostOfAllBelowAndMe(context)) {
				toSend.add(context);
			}
		}

	}

	private boolean contextIncludesCostOfAllBelowAndMe(Context context) {
		boolean isIIncluded = context.getCost(this.id)!=null;
		if (!isIIncluded) {
			return false;
		}
		boolean allBelowMeIncluded = isAllBelowMeIncluded(context);
		return r;
	}

	private void addGivenHeuristic(Collection<Context> toAdd) {
		int memoryHeurstic = MainSimulator.anytimeMemoryHuerstic;
		if (memoryHeurstic == 1) {
			for (Context c : toAdd) {
				this.contextInMemory.add(c);
			}
		} else {
			int limitedMemorySize = MainSimulator.anytimeMemoryLimitedSize;
			for (int i = 0; i < toAdd.size(); i++) {
				selectContextAndToRemove();
			}
			for (Context c : toAdd) {
				contextInMemory.add(c);
			}
		}

	}

	private void getConextToAdd(Context input, Collection<Context> toAdd, Collection<Context> toDelete) {
		for (Context context : this.contextInMemory) {
			Context combined = context.combineWith(input);
			if (combined != null) {
				toAdd.add(combined);
				if (MainSimulator.deleteAfterCombine) {
					toDelete.add(context);
				}
			}
		}
		toAdd.add(input);
		if (MainSimulator.deleteAfterCombine) {
			this.contextInMemory.removeAll(toDelete);
		}
	}

	private boolean contextHasNulls() {
		for (MsgReceive<Integer> m : neighborsValueAssignmnet.values()) {
			if (m == null) {
				return true;
			}
		}
		return false;
	}

	private void selectContextAndToRemove() {
		int anytimeMemoryHuerstic = MainSimulator.anytimeMemoryHuerstic;
		// 1 = no memoryLimit, 2=MSC, 3=Fifo, 4=Random
		Context c = null;
		if (anytimeMemoryHuerstic == 2) {
			c = Collections.min(this.contextInMemory, new contextSimilarityComparator(this.createMyContext()));
		}
		if (anytimeMemoryHuerstic == 3) {
			c = this.contextInMemory.get(0);
		}
		if (anytimeMemoryHuerstic == 4) {
			int rndIndex = randContext.nextInt(this.contextInMemory.size());
			c = this.contextInMemory.get(rndIndex);
		}
		this.contextInMemory.remove(c);
	}

	public void sendAnytimeMsgs() {
		for (Context c : anytimeUpToSend) {
			Msg m = new MsgAnyTimeUp(this.nodeId, this.anytimeFather, c, this.timeStampCounter, this.time);
			mailer.sendMsg(m);
		}

		this.anytimeUpToSend.clear();

		for (Context c : anytimeDownToSend) {
			for (NodeId son : this.anytimeSons) {
				Msg m = new MsgAnyTimeUp(this.nodeId, son, c, this.timeStampCounter, this.time);
				mailer.sendMsg(m);
			}
		}

		this.anytimeDownToSend.clear();
	}
	/*
	 * private void createVariableAssignmentMsg() { for (NodeId reciever :
	 * this.getNeigborSetId()) { Msg m = new MsgValueAssignmnet(this.nodeId,
	 * reciever, this.getValueAssignment(), this.timeStampCounter, this.time);
	 * this.mailer.sendMsg(m); }
	 * 
	 * }
	 */

	public void setBelowAnytime(Set<NodeId> below) {
		this.belowAnytime = below;

	}

}
