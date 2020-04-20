package Problem;
import java.util.ArrayList;
import java.util.List;

import Agents.AgentFunction;
import Agents.AgentVariable;

public abstract class Dcop {
	protected int id;
	protected AgentVariable[] agentsVariables;
	protected List<Neighbor> neighbors;
	protected List<AgentFunction> functionNodes;

	protected int D;
	protected int costParameter;
	protected int defultMessageValue; 

	public Dcop(int A, int D, int costPrameter) {
		this.D = D;
		agentsVariables = new AgentVariable[A];
		createVariableAgents();
		neighbors = new ArrayList<Neighbor>();
	}


	public int computeGlobalCost() {
		int ans = 0;
		for (Neighbor n : neighbors) {
			ans+=n.getCurrentCost();
		}
		return ans;
	}
	
	public abstract void createNeighbors();
	
	private void createVariableAgents() {
		for (int agentId = 0; agentId < agentsVariables.length; agentId++) {
			agentsVariables[agentId] = createAgentInstance(agentId);
		}
		
	}


	private AgentVariable createAgentInstance(int agentId) {
		AgentVariable ans = null;
		int agentType = MainSimulator.agentType;
		
		if (agentType == 1) {
			ans = new AgentDSA_ASY<Integer, Integer>(dcopId,D, agentId ,MainSimulator.dsaP);
		}
		if (agentType == 2) {
			ans = new AgentDSA_SY<Integer, Integer>(dcopId,D, agentId,MainSimulator.dsaP);
		}
		if (agentType == 3) {
			ans = new AgentMGM_ASY<Integer, Integer>(dcopId,D, agentId);
		}
		if (agentType == 4) {
			ans = new AgentMGM_SY<Integer, Integer>(dcopId,D, agentId);
		}
		if (agentType == 5) {
			ans = new AgentAMDLS<Integer, Integer>(dcopId,D, agentId);
		}
		if (agentType == 6) {
			double pA = MainSimulator.dsaSdpPA;
			double pB = MainSimulator.dsaSdpPB;
			double pC = MainSimulator.dsaSdpPC;
			double pD = MainSimulator.dsaSdpPD;
			int k = MainSimulator.dsaSdpK;
			ans = new AgentDSASDP<Integer, Integer>(dcopId,D,agentId, pA,pB,pC,pD,K);
		}
		
		
		
		return ans;
	}



	public void createTrees() {
		dfs();
		bfs();
		
	}


	public Dcop initiate() {
		createNeighbors();
		createFactorGraph();
		createTrees();
		return this;
	}


	


	

}
