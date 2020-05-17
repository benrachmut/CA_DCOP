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
import Algorithms.MaxSumSplitConstraintFactorGraph;
import Algorithms.MaxSumStandardFunction;
import Algorithms.MaxSumStandardVarible;
import Comparators.CompAgentVariableByNeighborSize;
import Main.Mailer;
import Main.MainSimulator;
import Messages.Msg;
import Trees.BFS;
import Trees.DFS;
import Trees.Tree;

public abstract class Dcop {
	protected int id;

	// ------- ** for graph use **------
	protected AgentVariable[] agentsVariables;
	protected List<Neighbor> neighbors;
	protected int D;
	protected int costParameter;

	// ------- ** for factor graph use **------
	protected List<AgentFunction> agentFunctions;
	protected SortedSet<Agent> agentsAll;

	protected int dcopId;
//	protected List<AgentFunction> functionNodes;

	public Dcop(int dcopId, int A, int D, int costPrameter) {
		this.D = D;
		this.costParameter = costPrameter;
		this.agentFunctions = new ArrayList<AgentFunction>();
		this.dcopId = dcopId;
		agentsVariables = new AgentVariable[A];
		this.agentsAll = new TreeSet<Agent>();
		createVariableAgents();
		neighbors = new ArrayList<Neighbor>();
	}
	
	public void DcopMeetsMailer(Mailer mailer) {
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
/*
		if (agentType == 1) {
			ans = new AgentDSA_ASY(dcopId, D, agentId);
		}
		if (agentType == 2) {
			ans = new AgentDSA_SY(dcopId, D, agentId);
		}
		if (agentType == 3) {
			ans = new AgentMGM_ASY(dcopId, D, agentId);
		}
		if (agentType == 4) {
			ans = new AgentMGM_SY(dcopId, D, agentId);
		}
		if (agentType == 5) {
			ans = new AgentAMDLS(dcopId, D, agentId);
		}
		if (agentType == 6) {
			ans = new AgentDSASDP(dcopId, D, agentId);
		}
		*/
		if (agentType == 7) {
		
			ans = new MaxSumStandardVarible(dcopId, D, agentId);
		}

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
		createTrees();

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
				af = new MaxSumStandardFunction(id,D, av1.getId(), av2.getId(),constraints, constraintsTranspose);
			}
			if (agentType == 8) {
				af = new MaxSumSplitConstraintFactorGraph(id,D, av1.getId(), av2.getId(),constraints, constraintsTranspose);

			}
			
			this.agentFunctions.add(af);
			agentsAll.add(af);
			//av1.meetFunction(af);
			//av2.meetFunction(af);
			
			
			
			
			if (agentType == 7) {
				
				af = new MaxSumStandardFunction(id,D, av1.getId(), av2.getId(),constraints, constraintsTranspose);
				this.agentFunctions.add(af);
				agentsAll.add(af);
				av1.meetFunction(af.getNodeId());
				av2.meetFunction(af.getNodeId());
				
			}
			
			 /*
			if(agentType == 8) {
			
				MaxSumSplitConstraintFactorGraph splitFunctionAgent = new MaxSumSplitConstraintFactorGraph(id,D, av1.getId(), av2.getId(), constraints, constraintsTranspose);
				this.agentFunctions.add(splitFunctionAgent);
				agentsAll.add(splitFunctionAgent);
				av1.meetFunction(splitFunctionAgent.getFirstSplit().getNodeId());
				av1.meetFunction(splitFunctionAgent.getSecondSplit().getNodeId());
				av2.meetFunction(splitFunctionAgent.getFirstSplit().getNodeId());
				av2.meetFunction(splitFunctionAgent.getSecondSplit().getNodeId());
			
			}*/
			
			
		}
		
	}	

	public SortedSet<Agent> getAgents() {
		return agentsAll;
	}

	public int getId() {
		return this.id;
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
}
