package AlgorithmSearch;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.NodeId;
import Messages.MsgReceive;

public class Find2Opt {
//---------------
	private Integer currentLocalCost; // id, variable
	private Integer currentValueAssingment1;
	private Integer currentValueAssingment2;
//---------------
	private NodeId nodeId1;
	private NodeId nodeId2;
//---------------
	private TreeMap<NodeId, Integer[][]> neighborsConstraint1;
	private TreeMap<NodeId, Integer[][]> neighborsConstraint2;
//---------------
	private int[] domainArray1;
	private int[] domainArray2;
//---------------
	private SortedMap<NodeId, Integer> neighborsValueAssignmnet1; // id, variable
	private SortedMap<NodeId, Integer> neighborsValueAssignmnet2;

	private Integer foundValueAssignmnet1;
	private Integer foundValueAssignmnet2;
	private Integer bestCostFound;
	
	private int atomicActionCounter;

	public Find2Opt(KOptInfo kOptInfo1, KOptInfo kOptInfo2) {
		super();
		this.currentValueAssingment1 = kOptInfo1.getValueAssingment();
		this.currentValueAssingment2 = kOptInfo2.getValueAssingment();
		this.nodeId1 = kOptInfo1.getNodeId();
		this.nodeId2 = kOptInfo2.getNodeId();
		this.neighborsConstraint1 = kOptInfo1.getNeighborsConstraint();
		this.neighborsConstraint2 = kOptInfo2.getNeighborsConstraint();
		this.domainArray1 = kOptInfo1.getDomainArray();;
		this.domainArray2 = kOptInfo2.getDomainArray();
		this.neighborsValueAssignmnet1 = copyNeighborValAss(kOptInfo1.getNeighborsValueAssignmnet());
		this.neighborsValueAssignmnet2 = copyNeighborValAss(kOptInfo2.getNeighborsValueAssignmnet());
		this.atomicActionCounter = 0;
		
		this.currentLocalCost = this.findCurrentLocalReduction();

		this.foundValueAssignmnet1 = -1;
		this.foundValueAssignmnet2 = -1;
		this.bestCostFound = Integer.MAX_VALUE;
		
		this.findOpt2ValueAssignments();
	}

	private void findOpt2ValueAssignments() {
		TreeMap<Integer,Integer> costPerDomainNoLinkOne = getCostPerDomainNoLinkOne(); // domain, cost without link
		TreeMap<Integer,Integer> costPerDomainNoLinkTwo = getCostPerDomainNoLinkTwo(); // domain, cost without link

		
		
		for (int i = 0; i < domainArray1.length; i++) {
			int d1 = domainArray1[i];
			int costD1 = costPerDomainNoLinkOne.get(d1);
			for (int j = 0; j < domainArray2.length; j++) {
				int d2 = domainArray2[j];
				int costD2 = costPerDomainNoLinkOne.get(d2);
				Integer costOfLink = findCostOfLink(costD1, costD2,true);
				Integer cost = costD1+ costD2+costOfLink;
				if (cost<bestCostFound) {
					
				}
			}	
		}
	} 
	

	private TreeMap<Integer, Integer> getCostPerDomainNoLinkTwo() {
		TreeMap<Integer,Integer> costPerDomainNoLinkTwo = new TreeMap<Integer,Integer>(); // domain, cost

		for (int j = 0; j < domainArray2.length; j++) {
			int d2 = domainArray2[j];
			Integer currentCostTwo = findCostWithoutLink(d2, neighborsValueAssignmnet2, neighborsConstraint2, nodeId1,
					true);
			costPerDomainNoLinkTwo.put(d2, currentCostTwo);

		}
		return costPerDomainNoLinkTwo;
	}

	private TreeMap<Integer, Integer> getCostPerDomainNoLinkOne() {
		TreeMap<Integer,Integer> costPerDomainNoLinkOne = new TreeMap<Integer,Integer>(); // domain, cost
		for (int i = 0; i < domainArray1.length; i++) {
			int d1 = domainArray1[i];
			Integer currentCostOne = findCostWithoutLink(d1, neighborsValueAssignmnet1, neighborsConstraint1, nodeId2,
					true);
			costPerDomainNoLinkOne.put(d1, currentCostOne);
			
		}
		return costPerDomainNoLinkOne;
	}

	public int findCostWithoutLink(int domain, SortedMap<NodeId, Integer> neighborsValueAssignmnet,
			TreeMap<NodeId, Integer[][]> neighborsConstraint, NodeId nodeIdLink, boolean includeCounter) {
		
		int ans = 0;
		
		for (Entry<NodeId, Integer> e : neighborsValueAssignmnet.entrySet()) {
			if (e.getValue() != null ) {
				if (!e.getKey().equals(nodeIdLink)) {
					int nValueAssignmnet = e.getValue();
					Integer[][] nConst = neighborsConstraint.get(e.getKey());
					ans += nConst[domain][nValueAssignmnet];
					if (includeCounter) {
						atomicActionCounter = atomicActionCounter + 1;
					}
				}
			}
		}
		return ans;
	}


	


	private Integer findCurrentLocalReduction() {
		Integer currentCostOne = findCost(currentValueAssingment1, neighborsValueAssignmnet1, neighborsConstraint1,
				false);
		Integer currentCostTwo = findCost(currentValueAssingment2, neighborsValueAssignmnet2, neighborsConstraint2,
				false);
		Integer costOfLink = findCostOfLink(currentValueAssingment1, currentValueAssingment2,false);

		return currentCostOne + currentCostTwo - costOfLink;
	}

	private Integer findCostOfLink(Integer valueAssingment1, Integer valueAssingment2,boolean includeCount) {

		Integer[][] const1And2 = this.neighborsConstraint1.get(nodeId2);
		int ans12 = const1And2[valueAssingment1][valueAssingment2];

		Integer[][] const2And1 = this.neighborsConstraint2.get(nodeId1);
		int ans21 = const2And1[valueAssingment2][valueAssingment1];

		if (ans12 != ans21) {
			throw new RuntimeException("it should be equals");
		} else {
			if (includeCount) {
				this.atomicActionCounter = atomicActionCounter+1;
			}
			return ans12;
		}

	}

	public int findCost(int domain, SortedMap<NodeId, Integer> neighborsValueAssignmnet,
			TreeMap<NodeId, Integer[][]> neighborsConstraint, boolean includeCounter) {
		int ans = 0;
		for (Entry<NodeId, Integer> e : neighborsValueAssignmnet.entrySet()) {
			if (e.getValue() != null) {
				int nValueAssignmnet = e.getValue();
				Integer[][] nConst = neighborsConstraint.get(e.getKey());
				ans += nConst[domain][nValueAssignmnet];
				if (includeCounter) {
					atomicActionCounter = atomicActionCounter + 1;
				}
			}
		}
		return ans;
	}

	private SortedMap<NodeId, Integer> copyNeighborValAss(SortedMap<NodeId, MsgReceive<Integer>> input) {

		SortedMap<NodeId, Integer> ans = new TreeMap<NodeId, Integer>();
		for (Entry<NodeId, MsgReceive<Integer>> e : input.entrySet()) {
			ans.put(e.getKey(), e.getValue().getContext());

		}
		return ans;
	}

	

}
