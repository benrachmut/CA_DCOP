package AlgorithmSearch;

import java.util.SortedMap;
import java.util.TreeMap;

import AgentsAbstract.NodeId;
import Messages.MsgReceive;

public class KOptInfo {
	//---------------
		private Integer valueAssingment;
	//---------------
		private NodeId nodeId;
	//---------------
		private TreeMap<NodeId, Integer[][]> neighborsConstraint;
	//---------------
		private int[] domainArray;
	//---------------
		private SortedMap<NodeId, MsgReceive<Integer>> neighborsValueAssignmnet; // id, variable
		
	//------------ Constructor	------------
	public KOptInfo(Integer valueAssingment, NodeId nodeId, TreeMap<NodeId, Integer[][]> neighborsConstraint,
			int[] domainArray, SortedMap<NodeId, MsgReceive<Integer>> neighborsValueAssignmnet) {
		super();
		this.valueAssingment = valueAssingment;
		this.nodeId = nodeId;
		this.neighborsConstraint = neighborsConstraint;
		this.domainArray = domainArray;
		this.neighborsValueAssignmnet = neighborsValueAssignmnet;
	}

	public Integer getValueAssingment() {
		return valueAssingment;
	}

	public NodeId getNodeId() {
		return nodeId;
	}

	public TreeMap<NodeId, Integer[][]> getNeighborsConstraint() {
		return neighborsConstraint;
	}

	public int[] getDomainArray() {
		return domainArray;
	}

	public SortedMap<NodeId, MsgReceive<Integer>> getNeighborsValueAssignmnet() {
		return neighborsValueAssignmnet;
	}
	
	
		
		
		
		
}
