import java.util.ArrayList;
import java.util.List;

public abstract class Dcop {
	protected int id;
	protected AgentVariable[] agentsVariables;
	protected List<Neighbor> neighbors;
	protected List<AgentFunction> functionNodes;

	protected int D;
	protected int costParameter;
	
	public Dcop(int A, int D, int costPrameter) {
		this.D = D;
		agentsVariables = new AgentVariable[A];
		neighbors = new ArrayList<Neighbor>();
		createVariableAgents();
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
			ans = new AgentDSA_ASY(D, dcopId, agentId ,MainSimulator.dsaP);
		}
		if (agentType == 2) {
			ans = new AgentDSA_SY(D, dcopId, agentId,MainSimulator.dsaP);
		}
		if (agentType == 3) {
			ans = new AgentMGM_ASY(D, dcopId, agentId);
		}
		if (agentType == 4) {
			ans = new AgentMGM_SY(D, dcopId, agentId);
		}
		if (agentType == 5) {
			ans = new AgentAMDLS(D, dcopId, agentId);
		}
		if (agentType == 6) {
			double pA = MainSimulator.dsaSdpPA;
			double pB = MainSimulator.dsaSdpPB;
			double pC = MainSimulator.dsaSdpPC;
			double pD = MainSimulator.dsaSdpPD;
			int k = MainSimulator.dsaSdpK;
			ans = new AgentDSASDP(agentId,D, pA,pB,pC,pD,K);
		}
		
		
		
		return ans;
	}



	public void createTrees() {
		dfs();
		bfs();
		
	}


	

}
