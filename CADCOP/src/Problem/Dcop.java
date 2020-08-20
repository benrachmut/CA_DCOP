package Problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariable;

import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import AlgorithmSearch.DSA_ASY;
import AlgorithmSearch.DSA_SY;
import AlgorithmSearch.MGM_ASY;
import AlgorithmSearch.MGM_SY;
import AlgorithmsInference.MaxSumSplitConstraintFactorGraph;
import AlgorithmsInference.MaxSumStandardFunction;
import AlgorithmsInference.MaxSumStandardFunctionSync;
import AlgorithmsInference.MaxSumStandardVarible;
import AlgorithmsInference.MaxSumStandardVaribleSync;
import Comparators.CompAgentVariableByNeighborSize;
import Main.Mailer;
import Main.MainSimulator;
import Messages.Msg;
import Trees.BFS;
import Trees.DFS;
import Trees.Tree;

public abstract class Dcop {

	// ------- ** for graph use **------
	protected AgentVariable[] agentsVariables;
	protected List<Neighbor> neighbors;
	protected int D;

	// ------- ** for factor graph use **------
	protected List<AgentFunction> agentFunctions;
	protected List<Agent> agentsAll;

	protected int dcopId;
	
	public static String dcopName; 
	public static String dcopHeader;
	public static String dcopParameters;
//	protected List<AgentFunction> functionNodes;

	public Dcop(int dcopId, int A, int D) {
		this.D = D;
		this.agentFunctions = new ArrayList<AgentFunction>();
		this.dcopId = dcopId;
		agentsVariables = new AgentVariable[A];
		this.agentsAll = new ArrayList<Agent>();
		createVariableAgents();
		neighbors = new ArrayList<Neighbor>();
		
	}
	
	protected void updateNames() {
		setDcopName();
		setDcopHeader();
		setDcopParameters();
	}
	protected abstract void setDcopName();
	protected abstract void setDcopHeader();
	protected abstract void setDcopParameters();
	
	public void dcopMeetsMailer(Mailer mailer) {
		for (Agent a : agentsAll) {
			a.meetMailer(mailer);
		}
	}

	public int computeGlobalCost() {
		int ans = 0;
		for (Neighbor n : neighbors) {
			ans += n.getCurrentCost();
		}
		return ans;
	}

	public abstract void createNeighbors();

	private void createVariableAgents() {
		for (int agentId = 0; agentId < agentsVariables.length; agentId++) {
			agentsVariables[agentId] = createAgentInstance(agentId);
			this.agentsAll.add(agentsVariables[agentId]);
		}

	}

	private AgentVariable createAgentInstance(int agentId) {
		AgentVariable ans = null;
		int agentType = MainSimulator.agentType;

		if (agentType == 1) {
			ans = new DSA_ASY(dcopId, D, agentId);
		}
		if (agentType == 2) {
			ans = new DSA_SY(dcopId, D, agentId);
		}
		if (agentType == 3) {
			ans = new MGM_ASY(dcopId, D, agentId);
		}
		if (agentType == 4) {
			ans = new MGM_SY(dcopId, D, agentId);
		}
		/*
		if (agentType == 5) {
			ans = new AgentAMDLS(dcopId, D, agentId);
		}
		if (agentType == 6) {
			ans = new AgentDSASDP(dcopId, D, agentId);
		}
		*/
		if (agentType == 7){
			ans = new MaxSumStandardVarible(dcopId, D, agentId);
		}
		
		if (agentType == 8){
			ans = new MaxSumStandardVaribleSync(dcopId, D, agentId);
		}
		/*
		if (agentType == 9){
			ans = new MaxSumSplitConstraintFactorGraph(dcopId, D, agentId);
		}
		*/
		

		return ans;
	}

	public void createTrees() {
		Collection<Tree>trees = new HashSet<Tree>();
		trees.add(new DFS(this.agentsVariables, new CompAgentVariableByNeighborSize()));
		trees.add(new BFS(this.agentsVariables, new CompAgentVariableByNeighborSize()));

		for (Tree tree : trees) {
			tree.initiateTree();
		}
	}

	public Dcop initiate() {
		createNeighbors();
		//createTrees();

		if (isInferenceAgent()) {
			createFactorGraph();
		}
		return this;
	}

	private void createFactorGraph() {
		
		int agentType = MainSimulator.agentType;

		for (Neighbor n : neighbors) {
			
			AgentVariableInference av1 = (AgentVariableInference) n.getA1();
			AgentVariableInference av2 = (AgentVariableInference) n.getA2();
			
			Integer[][]constraints = n.getConstraints();
			Integer[][]constraintsTranspose = n.getConstraintsTranspose();

			 
			AgentFunction af = null;
			
			if (agentType == 7) {
				
				af = new MaxSumStandardFunction(dcopId,D, av1.getId(), av2.getId(),constraints);
			
			}
			
			if(agentType == 8) {
				
				af = new MaxSumStandardFunctionSync(dcopId,D, av1.getId(), av2.getId(),constraints);

			}
			
			
			
			if (agentType == 9) {
				
				af = new MaxSumSplitConstraintFactorGraph(dcopId,D, av1.getId(), av2.getId(),constraints, constraintsTranspose); //Will create a new MaxSumSplitConstraintFactorGraph.
				
			}
					
			this.agentFunctions.add(af);
			this.agentsAll.add(af);
			av1.meetFunction(af.getMyNodes());
			av2.meetFunction(af.getMyNodes());
			af.meetVariables(av1.getNodeId(), av2.getNodeId());
			
		}
		
		printAllNeighbors();
		binaryDebug();
		
		
	}	

	public List<Agent> getAgents() {
		return agentsAll;
	}

	public int getId() {
		return this.dcopId;
	}

	public List<Neighbor> getNeighbors() {
		return this.neighbors;
	}

	public AgentVariable[] getVariableAgents() {
		return  agentsVariables;
	}

	public  boolean isInferenceAgent() {
		return (this.agentsVariables[0] instanceof AgentVariableInference) ;
	}

	public boolean isSearchAlgorithm() {
		// TODO Auto-generated method stub
		return (this.agentsVariables[0] instanceof AgentVariableSearch) ;
	}
	
	
	///// ******* Debug methods ******* ////

	//OmerP - To print all the neighbors list.
	public void printAllNeighbors() {
		
		for(int i = 0 ; i < neighbors.size() ; i++) {
			
			System.out.println("AgentVariable:(" + neighbors.get(i).getA1().getNodeId().getId1() + "," + neighbors.get(i).getA1().getNodeId().getId2() + 
					") is constraind with AgentVariable:(" + neighbors.get(i).getA2().getNodeId().getId1() + "," + neighbors.get(i).getA2().getNodeId().getId2() + ").\n");

		}
		
	}
	
	//OmerP - Print when variable and function were connected. 
	public void printVariablesandFunctionConstraints(AgentVariableInference av1, AgentVariableInference av2 , AgentFunction af) {
		
		
		System.out.println("Agent Variable Inference (" + av1.getNodeId().getId1() + "," + av1.getNodeId().getId2() 
				+ ") and Agent Variable Inference (" + av2.getNodeId().getId1() + "," + av2.getNodeId().getId2() + 
				") are connected to Agent Function (" + af.getNodeId().getId1() + "," + af.getNodeId().getId2() + ").\n");
		
		
		
		
	}
	
	//OmerP - This method aims to check if for all function node there are two variable nodes at variable message size.
	public void binaryDebug() {
		
		boolean okMessage = true;
		
		for(int i = 0 ; i < agentFunctions.size() ; i++) {
				
			if((MainSimulator.agentType == 7) || (MainSimulator.agentType == 8)) {
			
				if(agentFunctions.get(i).getVariableMsgsSize() != 2) {
				
					System.out.println("Severe error !!! Agent Function (" + agentFunctions.get(i).getNodeId().getId1() + "," + agentFunctions.get(i).getNodeId().getId2() + ") has " + agentFunctions.get(i).getVariableMsgsSize() + " neighbors.\n");
					okMessage = false; 
				
				}
			
			}
			
			if(MainSimulator.agentType == 9) {
				
				MaxSumSplitConstraintFactorGraph maxSplitFunctionNode = (MaxSumSplitConstraintFactorGraph) agentFunctions.get(i); //Casting the get access to getSplitFunctionNodes method. 

				for(int j = 0 ; j < maxSplitFunctionNode.getSplitFunctionNodes().size() ; j++) {
					
					if(maxSplitFunctionNode.getSplitFunctionNodes().get(j).getVariableMsgsSize() != 2) {
						
						System.out.println("Severe error !!! Agent Function (" + agentFunctions.get(i).getNodeId().getId1() + "," + agentFunctions.get(i).getNodeId().getId2() + ") has " + agentFunctions.get(i).getVariableMsgsSize() + " neighbors.\n");
						okMessage = false; 
						
					}
					
				}
				
			}
			
		}
		
		if(okMessage) {
			
			System.out.println("Factor Graph Check: All Constraints Are Binary.\n");
		
		}
		
	}
	
	//OmerP - This method aims to check if for all variable nodes that they have the number of function nodes as the number of neighbors. 
	public void functionDebug() {
		
		boolean okMessage = true;
		int[] checkArray = new int[neighbors.size()];
		
		for(int i = 0 ; i < neighbors.size() ; i++) { //OmerP - will  initialize the checkArray.

			checkArray[neighbors.get(i).getA1().getNodeId().getId1()]++; 
			checkArray[neighbors.get(i).getA2().getNodeId().getId1()]++; 

		}
		
		for(int i = 0 ; i < checkArray.length ; i++) {
			
			AgentVariableInference agentVariable = (AgentVariableInference) agentsVariables[i];
			int toCheck = agentVariable.getFunctionMsgsSize();
			
			if(checkArray[i] != toCheck) {
				
				System.out.println("Severe error !!! The number of neighbors of variable agent " + i + " that was initializaed is" + checkArray[i] + 
						"while the number of initializaed agents are " + toCheck + ".\n" );
				okMessage = false; 
				
			}
			
		
			
		}
		
		if(okMessage) {
			
			System.out.println("Factor Graph Check: All Variable Agent Initializaed Correctly.");
			
			}
		
	}
	
	//OmerP - Will check the each variable node has the number of function nodes as its neighbors. 
	public void variableNodeDebug() {
		
		for (Neighbor n : neighbors) {
			
			AgentVariableInference av1 = (AgentVariableInference) n.getA1();
			AgentVariableInference av2 = (AgentVariableInference) n.getA2();
			
			if(MainSimulator.agentType == 7) {
			
				NodeId nodeToCheck = new NodeId(av1.getNodeId().getId1(), av2.getNodeId().getId1());
				
				if(av1.checkIfNodeIsContained(nodeToCheck)) {
					
					printNodeConnection(nodeToCheck,av1, true);
					
				} else {
					
					printNodeConnection(nodeToCheck,av1, false);
						
				}
				
				if(av2.checkIfNodeIsContained(nodeToCheck)) {
					
					printNodeConnection(nodeToCheck,av2, true);
					
				} else {
					
					printNodeConnection(nodeToCheck,av2, false);
				}
				
			}
			
			if(MainSimulator.agentType == 8) {
				
				NodeId nodeToCheckOne = new NodeId(av1.getNodeId().getId1(), av2.getNodeId().getId1());
				NodeId nodeToCheckTwo = new NodeId(av2.getNodeId().getId1(), av1.getNodeId().getId1());

				if(av1.checkIfNodeIsContained(nodeToCheckOne)) {
					
					printNodeConnection(nodeToCheckOne,av1, true);
					
				} else {
					
					printNodeConnection(nodeToCheckOne,av1, false);
					
				}
				
				if(av1.checkIfNodeIsContained(nodeToCheckTwo)) {
					
					printNodeConnection(nodeToCheckTwo,av1, true);
					
				} else {
					
					printNodeConnection(nodeToCheckTwo,av1, false);
					
				}
				
				if(av2.checkIfNodeIsContained(nodeToCheckOne)) {
					
					printNodeConnection(nodeToCheckOne,av2, true);
					
				} else {
					
					printNodeConnection(nodeToCheckOne,av2, false);
					
				}
				
				if(av2.checkIfNodeIsContained(nodeToCheckTwo)) {
					
					printNodeConnection(nodeToCheckTwo,av2, true);
					
				} else {
					
					printNodeConnection(nodeToCheckTwo,av2, false);
					
				}
				
			}
		
		}
			
	}
	
	//OmerP - Will check the each function node has the number of variable nodes as its neighbors. 
	public void functionNodeDebug() {
		
		for(int i = 0 ; i < agentFunctions.size() ; i++) {
			
			NodeId nodeToCheckOne = new NodeId(agentFunctions.get(i).getNodeId().getId1(), 0);
			NodeId nodeToCheckTwo = new NodeId(agentFunctions.get(i).getNodeId().getId2(), 0);
			
			if(MainSimulator.agentType == 7) {
			
				if(agentFunctions.get(i).checkIfNodeIsContained(nodeToCheckOne)) {
					
					printNodeConnection(nodeToCheckOne, agentFunctions.get(i), true);
							
				} else {
					
					printNodeConnection(nodeToCheckOne, agentFunctions.get(i), false);
					
				}
	
				if(agentFunctions.get(i).checkIfNodeIsContained(nodeToCheckTwo)) {
					
					printNodeConnection(nodeToCheckTwo, agentFunctions.get(i), true);
					
				} else {
					
					printNodeConnection(nodeToCheckTwo, agentFunctions.get(i), false);
					
				}
			
			}
			
			if(MainSimulator.agentType == 8) {
				
				MaxSumSplitConstraintFactorGraph maxSplitFunctionNode = (MaxSumSplitConstraintFactorGraph) agentFunctions.get(i); //Casting the get access to getSplitFunctionNodes method. 

				for(int j = 0 ; j < maxSplitFunctionNode.getSplitFunctionNodes().size() ; j++) {
					
					if(maxSplitFunctionNode.getSplitFunctionNodes().get(j).checkIfNodeIsContained(nodeToCheckOne)) {
						
						System.out.println("NodeId:(" + nodeToCheckOne.getId1() + "," + nodeToCheckOne.getId2() + ") is in Agent Function Inference (" +
								maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId1()
								+ "," + maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId2() + ") list.\n");
						
					} else {
						
						System.out.println("NodeId:(" + nodeToCheckOne.getId1() + "," + nodeToCheckOne.getId2() + ") is NOT Agent Function Inference (" +
								maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId1() + "," + maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId2() + ") list.\n");
						
					}
					
					if(maxSplitFunctionNode.getSplitFunctionNodes().get(j).checkIfNodeIsContained(nodeToCheckTwo)) {
						
						System.out.println("NodeId:(" + nodeToCheckTwo.getId1() + "," + nodeToCheckTwo.getId2() + ") is in Agent Function Inference (" +
								maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId1() + "," + maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId2() + ") list.\n");
						
					} else {
						
						System.out.println("NodeId:(" + nodeToCheckTwo.getId1() + "," + nodeToCheckTwo.getId2() + ") is NOT Agent Function Inference (" +
								maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId1() + "," + maxSplitFunctionNode.getSplitFunctionNodes().get(j).getNodeId().getId2() + ") list.\n");
						
					}
									
			}
		}
		

		
	}
	
	//-----------------------------------------------------------------------------------------------------------//
	}
	
	
	///// ******* Print methods ******* ////

	public void printNodeConnection(NodeId nodeId, AgentFunction agentFunction, boolean check) {
		
		if(check) {
		
		System.out.println("NodeId:(" + nodeId.getId1() + "," + nodeId.getId2() + ") is in Agent Function Inference (" +
				agentFunction.getNodeId().getId1() + "," + agentFunction.getNodeId().getId2() + ") list.\n");
		
		}
		
		if(!check) {
			
			System.out.println("NodeId:(" + nodeId.getId1() + "," + nodeId.getId2() + ") is NOT in Agent Function Inference (" +
					agentFunction.getNodeId().getId1() + "," + agentFunction.getNodeId().getId2() + ") list.\n");
			
		}
		
	}
	
	public void printNodeConnection(NodeId nodeId, AgentVariableInference agentVariable, boolean check) {
		
		if(check) {
		
		System.out.println("NodeId:(" + nodeId.getId1() + "," + nodeId.getId2() + ") is in Agent Function Inference (" +
				agentVariable.getNodeId().getId1() + "," + agentVariable.getNodeId().getId2() + ") list.\n");
		
		}
		
		if(!check) {
			
			System.out.println("NodeId:(" + nodeId.getId1() + "," + nodeId.getId2() + ") is NOT in Agent Function Inference (" +
					agentVariable.getNodeId().getId1() + "," + agentVariable.getNodeId().getId2() + ") list.\n");
			
		}
		
	}

	public AgentVariable getVariableAgents(int i) {
		for (AgentVariable av : agentsVariables) {
			if (av.getId() == i) {
				return av;
			}
		}
		throw new RuntimeException();
	}
	
}