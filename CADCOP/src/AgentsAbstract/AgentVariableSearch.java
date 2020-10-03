package AgentsAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import Comparators.ContextSimilarityComparator;

import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import Main.MailerIterations;
import Main.MainSimulator;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Messages.MsgAnyTimeDown;
import Messages.MsgAnyTimeDownTopFound;
import Messages.MsgAnyTimeUp;
import Messages.MsgReceive;
import Messages.MsgValueAssignmnet;

public abstract class AgentVariableSearch extends AgentVariable {

	protected SortedMap<NodeId, MsgReceive<Integer>> neighborsValueAssignmnet; // id, variable

	// ------Anytime----
	protected NodeId anytimeFather;
	protected Set<NodeId> anytimeSons;
	private Set<NodeId> belowAnytime;
	protected List<Context> anytimeUpToSend;
	// protected List<Context> anytimeDownToSend;
	private Integer anytimeValueAssignmnet;
	private Context anytimeBestContext;
	private List<Context> contextInMemory;
	private Random randContext;
	private Context bestContexFound;
	private boolean hasAnytimeNews;

	private int topAnytimeCounter;

	private Set<Context> fullContextFound;
	private Set<Context> newFullContextFoundToSend;

	public AgentVariableSearch(int dcopId, int D, int id1) {
		super(dcopId, D, id1);
		this.neighborsValueAssignmnet = new TreeMap<NodeId, MsgReceive<Integer>>();
		anytimeUpToSend = new ArrayList<Context>();
		fullContextFound = new HashSet<Context>();
		newFullContextFoundToSend = new HashSet<Context>();
		bestContexFound = null;
		anytimeValueAssignmnet = null;
		anytimeBestContext = null;
		hasAnytimeNews = false;
		contextInMemory = new ArrayList<Context>();
		randContext = new Random(this.id * 10 + dcopId * 153);
		topAnytimeCounter = 0;
		if (MainSimulator.isAnytime) {
			//this.isWithTimeStamp = true;
		}
	}

	@Override
	public void initialize() {
		this.sendValueAssignmnetMsgs();

		if (MainSimulator.isAnytime) {
			if (this.neighborsValueAssignmnet.isEmpty()) {
				this.bestContexFound = createMyContext();
				this.anytimeValueAssignmnet = this.bestContexFound.getValueAssignmentPerAgent(this.id);
			}
		}
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
		topAnytimeCounter = 0;
		fullContextFound = new HashSet<Context>();
		newFullContextFoundToSend = new HashSet<Context>();
		anytimeUpToSend = new ArrayList<Context>();
		bestContexFound = null;
		anytimeBestContext = null;
		randContext = new Random(this.id * 10 + dcopId * 153);
		hasAnytimeNews = false;

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
				try {
					ans += nConst[input][nValueAssignmnet];
				} catch (ArrayIndexOutOfBoundsException g) {
					return 0;
				}
			}
		}
		return ans;
	}

	public Integer getValueAssignmentOfAnytime() {
		return this.anytimeValueAssignmnet;
	}

	protected SortedMap<Integer, Integer> getCostPerDomain() {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer, Integer>();
		for (int domainCandidate : domainArray) {

			int sumCostPerAgent = this.getCostPerInput(domainCandidate);
			ans.put(domainCandidate, sumCostPerAgent);

		}
		return ans;
	}

	protected int getCandidateToChange_B() {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int minCost = Collections.min(costPerDomain.values());
		try {
			Integer costOfCurrentValue = costPerDomain.get(this.valueAssignment);
			if (minCost <= costOfCurrentValue && costOfCurrentValue != 0) {
				SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
				if (alternatives.isEmpty()) {
					return this.valueAssignment;
				}
				return alternatives.first();
			}
		} catch (NullPointerException e) {
			SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
			if (alternatives.isEmpty()) {
				return this.valueAssignment;
			}
			return alternatives.first();
		}
		/*
		 * Integer costOfCurrentValue = costPerDomain.get(this.valueAssignment); if
		 * (costOfCurrentValue == null || minCost <= costOfCurrentValue &&
		 * costOfCurrentValue != 0) { SortedSet<Integer> alternatives =
		 * getAlternativeCandidate(minCost, costPerDomain); if (alternatives.isEmpty())
		 * { return this.valueAssignment; } return alternatives.first(); }
		 */
		return this.valueAssignment;

	}

	protected int getCandidateToChange_A() {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int minCost = Collections.min(costPerDomain.values());
		try {
			Integer costOfCurrentValue = costPerDomain.get(this.valueAssignment);
			if (minCost < costOfCurrentValue) {
				SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
				if (alternatives.isEmpty()) {
					return this.valueAssignment;
				}
				return alternatives.first();
			}
		} catch (NullPointerException e) {
			SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
			if (alternatives.isEmpty()) {
				return this.valueAssignment;
			}
			return alternatives.first();
		}

		return this.valueAssignment;

	}

	protected int getCandidateToChange_C() {
		SortedMap<Integer, Integer> costPerDomain = this.getCostPerDomain();
		int minCost = Collections.min(costPerDomain.values());
		try {
			Integer costOfCurrentValue = costPerDomain.get(this.valueAssignment);
			if (minCost <= costOfCurrentValue) {
				SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
				if (alternatives.isEmpty()) {
					return this.valueAssignment;
				}
				return alternatives.first();
			}
		} catch (NullPointerException e) {
			SortedSet<Integer> alternatives = getAlternativeCandidate(minCost, costPerDomain);
			if (alternatives.isEmpty()) {
				return this.valueAssignment;
			}
			return alternatives.first();
		}
		/*
		 * Integer costOfCurrentValue = costPerDomain.get(this.valueAssignment); if
		 * (costOfCurrentValue == null || minCost <= costOfCurrentValue) {
		 * SortedSet<Integer> alternatives = getAlternativeCandidate(minCost,
		 * costPerDomain); if (alternatives.isEmpty()) { return this.valueAssignment; }
		 * return alternatives.first(); }
		 */
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
		/*
		 * if (MainSimulator.isSDPdebug && this.id==4 && msgReceive.getContext()==1 ) {
		 * System.out.println("from search"); }
		 */

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
				if (context_i != null) {
					placeContextInMemory(context_i);
				}
			}
		}
		return isValueAssignmnetChange;

	}

	public synchronized void receiveAlgorithmicMsgs(List<? extends MsgAlgorithm> messages) {
		Context context_i_beforeMsgUpdate = null;

		if (MainSimulator.isAnytime) {
			if (!messages.isEmpty()) {
				try {
					context_i_beforeMsgUpdate = createMyContext();
				} catch (NullPointerException e) {

				}
			}
		}

		super.receiveAlgorithmicMsgs(messages);

		if (MainSimulator.isAnytime) {
			//if (isWithTimeStamp) {
				if (!messages.isEmpty()) {
					Context context_i_AfterMsgUpdate = null;

					context_i_AfterMsgUpdate = createMyContext();

					if (context_i_AfterMsgUpdate != null) {
						if ((context_i_AfterMsgUpdate != null && context_i_beforeMsgUpdate == null)
								|| !context_i_beforeMsgUpdate.isSameValueAssignmnets(context_i_AfterMsgUpdate)) {
							placeContextInMemory(context_i_AfterMsgUpdate);
						}
					}

				}
			//}
		}

	}

	public Context createMyContext() {

		TreeMap<Integer, Integer> m = new TreeMap<Integer, Integer>();
		try {
			for (Entry<NodeId, MsgReceive<Integer>> e : this.neighborsValueAssignmnet.entrySet()) {
				m.put(e.getKey().getId1(), e.getValue().getContext());
			}
		} catch (NullPointerException e) {
			return null;
		}

		int myCost = this.getCostPerDomain().get(this.valueAssignment);

		return new Context(m, this.id, this.valueAssignment, myCost);
	}

	public synchronized void recieveAnyTimeMsgs(List<? extends MsgAnyTime> messages) {

		for (MsgAnyTime msgAnyTime : messages) {
			if (msgAnyTime instanceof MsgAnyTimeUp) {
				if (MainSimulator.isAnytimeDebug && this.id==22	) { //&& lessNullsInContext(context,12)) {	
				//	System.out.println();
				}
				placeContextInMemory((Context) msgAnyTime.getContext());
			}
			if (msgAnyTime instanceof MsgAnyTimeDown) {
				MsgAnyTimeDown down = (MsgAnyTimeDown) msgAnyTime;
				Context c = (Context) down.getContext();
				if (this.bestContexFound == null) {
					this.bestContexFound = c;
					this.anytimeValueAssignmnet = c.getValueAssignmentPerAgent(this.id);
					this.hasAnytimeNews = true;
				} else {
					int costOfContextRecived = c.getTotalCost();
					int costOfBestContextFound = bestContexFound.getTotalCost();
					if (costOfContextRecived < costOfBestContextFound) {
						bestContexFound = c;
						this.anytimeValueAssignmnet = c.getValueAssignmentPerAgent(this.id);
						this.hasAnytimeNews = true;
					}
				}
			}
			if (msgAnyTime instanceof MsgAnyTimeDownTopFound) {
				this.fullContextFound.add((Context) msgAnyTime.getContext());
				Collection<Context> toRemove = new HashSet<Context>();
				for (Context c : this.contextInMemory) {
					if (c.isConsistentWith((Context) msgAnyTime.getContext())) {
						toRemove.add(c);
					}
				}
				this.contextInMemory.removeAll(toRemove);
			}

		}

		isIdle = false;
		updateAgentTime(messages);
		this.notifyAll();

	}

	private void placeContextInMemory(Context input) {

		whichContextsShouldBeSent(this.contextInMemory);
		if (contextHasNulls()) {
			return;
		}
		Set<Context> toAdd = new HashSet<Context>();
		getConextToAdd(input, toAdd);
		whichContextsShouldBeSent(toAdd);
		addGivenHeuristic(toAdd, this.contextInMemory, MainSimulator.anytimeMemoryLimitedSize);
	}

	private void whichContextsShouldBeSent(Collection<Context> toAdd) {
		Set<Context> toSendUp = new HashSet<Context>();
		Set<Context> reachTop = new HashSet<Context>();
		for (Context context : toAdd) {
			
			if (this.isAnytimeTop()) {
				handleContextAdditionForTreeTop(context, reachTop);
			} // if top
			else {
				if (contextIncludesCostOfAllBelowAndMe(context)) {
				
					if (!isContextInCollection(context, toSendUp)) {
						if (bestContexFound == null || (bestContexFound != null
								&& bestContexFound.getTotalCost() > context.getTotalCost())) {
							toSendUp.add(context);
							
						
						}
					}
				}
			}
		} // for toAdd

		Set<Context> temp = new HashSet<Context>();
		temp.addAll(this.anytimeUpToSend);
		temp.addAll(toSendUp);
		anytimeUpToSend.clear();
		Set<Context> willNotBeSent = addGivenHeuristic(temp, this.anytimeUpToSend,
				MainSimulator.anytimeMemoryLimitedSize);
		toAdd.addAll(willNotBeSent);
		toAdd.removeAll(anytimeUpToSend);
		toAdd.removeAll(reachTop);

		for (Context cont : reachTop) {
			if (!isContextInCollection(cont, fullContextFound)) {
				fullContextFound.add(cont);
				newFullContextFoundToSend.add(cont);
			}
		}

	}

	private boolean lessNullsInContext(Context context, int i) {
		int numOfNulls = context.numberOfNulls();
		
		return numOfNulls<i;
	}

	private void handleContextAdditionForTreeTop(Context context, Set<Context> reachTop) {
		if (contextIncludesCostOfAllBelowAndMe(context)) {
			int costOfCandidateContext = context.getTotalCost();
			if (bestContexFound == null) {
				System.out.println(MailerIterations.m_iteration + ": bestContexFound: " + costOfCandidateContext);
				bestContexFound = context;
				this.topAnytimeCounter = this.topAnytimeCounter + 1;
				this.anytimeValueAssignmnet = context.getValueAssignmentPerAgent(this.id);
				this.hasAnytimeNews = true;
				reachTop.add(context);
			} else {
				int costOfBestContext = this.bestContexFound.getTotalCost();
				this.topAnytimeCounter = this.topAnytimeCounter + 1;
				if (MainSimulator.isAnytimeDebug) {
					System.out.println(MailerIterations.m_iteration + ": costOfBestContext: " + costOfBestContext
							+ ", costOfCandidateContext: " + costOfCandidateContext);
				}
				reachTop.add(context);

				if (costOfBestContext > costOfCandidateContext) {
					bestContexFound = context;
					this.hasAnytimeNews = true;
					this.anytimeValueAssignmnet = context.getValueAssignmentPerAgent(this.id);
					// reachTop.add(context);

				}
			}
		}
	}

	public boolean isAnytimeTop() {
		return this.anytimeFather == null;
	}

	private boolean contextIncludesCostOfAllBelowAndMe(Context context) {
		boolean isIIncluded = context.getCost(this.id) != null;
		if (!isIIncluded) {
			return false;
		}
		return isAllBelowMeIncluded(context);
	}

	private boolean isAllBelowMeIncluded(Context context) {
		for (NodeId nodeId : this.belowAnytime) {
			if (context.getCost(nodeId.getId1()) == null) {
				return false;
			}
		}
		return true;
	}

	private Set<Context> addGivenHeuristic(Set<Context> toAddFrom, List<Context> cToAddTo, int limitedMemorySize) {
		Set<Context> ans = new HashSet<Context>();
		int memoryHeurstic = MainSimulator.anytimeMemoryHuerstic;
		if (memoryHeurstic == 1) {
			for (Context c : toAddFrom) {
				cToAddTo.add(c);
			}
		} else {
			for (Context c : toAddFrom) {
				cToAddTo.add(c);
			}
			int amountToRemove = cToAddTo.size() - limitedMemorySize;
			if (amountToRemove > 0) {
				for (int i = 0; i < amountToRemove; i++) {
					ans.add(selectContextAndToRemove(limitedMemorySize, cToAddTo));
				}
			}
		}
		return ans;
	}

	private void getConextToAdd(Context input, Set<Context> toAdd) {
		Set<Context> toDelete = new HashSet<Context>();

		for (Context context : this.contextInMemory) {

			if (MainSimulator.isAtomicTime) {
				this.time = this.time + (context.getContextSize() * input.getContextSize())/MainSimulator.dividAtomicTime+1;;
			} else {
				this.time = this.time + 1;
			}
			Context combined = context.combineWith(input);
			if (combined != null) {
				if (!isConsistentWithTop(context)) {
					if (!isContextInCollection(combined, contextInMemory)) {
						toAdd.add(combined);

					}
				}
			}
		}

		Set<Context> setAll = new HashSet<Context>(this.contextInMemory);
		boolean isAdded = setAll.add(input);
		if (isAdded) {
			toAdd.add(input);
		}
		/*
		 * if (MainSimulator.deleteAfterCombine) {
		 * this.contextInMemory.removeAll(toDelete); }
		 */
	}

	private int anytimeComputationTime(Context c1, Context c2) {

		return c1.getContextSize() * c2.getContextSize();
	}

	private boolean isConsistentWithTop(Context context) {
		if (bestContexFound == null) {
			return false;
		} else {

			for (Context c : fullContextFound) {
				if (c.isConsistentWith(context)) {
					return true;
				}
			}
			if (context.isConsistentWith(bestContexFound)) {
				return true;
			}
			return false;
		}
	}

	private static boolean isContextInCollection(Context combined, Collection<Context> c) {
		for (Context inMemory : c) {
			if (inMemory.equals(combined)) {
				return true;
			}
		}
		return false;
	}

	private boolean contextHasNulls() {
		for (MsgReceive<Integer> m : neighborsValueAssignmnet.values()) {
			if (m == null) {
				return true;
			}
		}
		return false;
	}

	private Context selectContextAndToRemove(int limitedMemorySize, List<Context> cToAddTo) {

		int anytimeMemoryHuerstic = MainSimulator.anytimeMemoryHuerstic;
		// 1 = no memoryLimit, 2=MSC, 3=Fifo, 4=Random
		Context c = null;
		if (anytimeMemoryHuerstic == 2) {
			c = Collections.min(cToAddTo, new ContextSimilarityComparator(this.createMyContext()));
		}
		if (anytimeMemoryHuerstic == 3) {
			c = cToAddTo.get(0);
		}
		if (anytimeMemoryHuerstic == 4) {
			int rndIndex = randContext.nextInt(cToAddTo.size());
			c = cToAddTo.get(rndIndex);
		}
		cToAddTo.remove(c);
		return c;

	}

	public void sendAnytimeMsgs() {
		for (Context c : anytimeUpToSend) {
			Msg m = new MsgAnyTimeUp(this.nodeId, this.anytimeFather, c, this.timeStampCounter, this.time);
			mailer.sendMsg(m);
		}

		this.anytimeUpToSend = new ArrayList<Context>();

		if (hasAnytimeNews) {
			for (NodeId son : this.anytimeSons) {
				Msg m = new MsgAnyTimeDown(this.nodeId, son, this.bestContexFound, this.timeStampCounter, this.time);
				mailer.sendMsg(m);
			}
		}
		hasAnytimeNews = false;

		/*
		 * fullContextFound.add(cont); newFullContextFoundToSend.add(cont);
		 */
	
			for (NodeId son : this.anytimeSons) {

				for (Context c : newFullContextFoundToSend) {
					Msg m = new MsgAnyTimeDownTopFound(this.nodeId, son, c, this.timeStampCounter, this.time);
					mailer.sendMsg(m);
				}
			}
		
		newFullContextFoundToSend = new HashSet<Context>();
	}
	/*
	 * private void createVariableAssignmentMsg() { for (NodeId reciever :
	 * this.getNeigborSetId()) { Msg m = new MsgValueAssignmnet(this.nodeId,
	 * reciever, this.getValueAssignment(), this.timeStampCounter, this.time);
	 * this.mailer.sendMsg(m); }
	 * 
	 * }
	 */

	public void turnDFStoAnytimeStructure(Set<NodeId> below) {
		this.belowAnytime = below;
		this.anytimeSons = dfsSons;
		this.anytimeFather = dfsFather;
	}

	public Double getCostOfBestContext() {
		if (bestContexFound == null) {
			return null;
		} else {
			double ans = this.bestContexFound.getTotalCost();
			return ans;
		}
	}

	public int getCounterOfContext() {
		return this.topAnytimeCounter;
	}

	public NodeId getAnytimeFather() {
		// TODO Auto-generated method stub
		return this.anytimeFather;
	}

	public Set<NodeId> getAnytimeSons() {
		return this.anytimeSons;
	}

	public Set<NodeId> getBelowAnytime() {
		// TODO Auto-generated method stub
		return this.belowAnytime;
	}

	public int getAnytimeUpToSendSize() {
		// TODO Auto-generated method stub
		return this.anytimeUpToSend.size();
	}

	@Override
	protected synchronized void waitUntilMsgsRecieved() {
		if (getDidComputeInThisIteration() == false) {
			waitingMethodology();
			if (stopThreadCondition == true) {
				return;
			}
		}

		if (this.getDidComputeInThisIteration()) {
			this.reactionToAlgorithmicMsgs();
			this.sendMsgs();
			this.changeRecieveFlagsToFalse();
		}
		if (MainSimulator.isAnytime) {
			this.sendAnytimeMsgs();
		}
	}

	public void setAnytimeFather(NodeId father) {
		this.anytimeFather = father;
	}

	public void setAnytimeSons(Set<NodeId> sons) {
		this.anytimeSons = sons;
	}

	public void setAnytimeBelow(Set<NodeId> below) {
		this.belowAnytime = below;
	}

}
