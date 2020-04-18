import java.util.List;

public abstract class Dcop {
	protected int id;
	protected Agent[] agents;
	protected List<Neighbor> neighbors;

	protected int D;
	protected int costParameter;
	
	public Dcop(int A, int D, int costPrameter) {
		this.D = D;
		agents = new Agent[A];
		createAgents();
	}


	
	public abstract void createNeighbors();
	
	private void createAgents() {
		for (int agentId = 0; agentId < agents.length; agentId++) {
			agents[agentId] = createAgentInstance(agentId);
		}
		
	}


	private Agent createAgentInstance(int agentId) {
		Agent ans = null;
		int agentType = MainSimulator.agentType;
		
		if (agentType == 1) {
			ans = new AgentDSA_ASY(agentId, D,MainSimulator.dsaP);
		}
		if (agentType == 2) {
			ans = new AgentDSA_SY(agentId,D,MainSimulator.dsaP);
		}
		if (agentType == 3) {
			ans = new AgentMGM_ASY(agentId,D);
		}
		if (agentType == 4) {
			ans = new AgentMGM_SY(agentId,D);
		}
		if (agentType == 5) {
			ans = new AgentAMDLS(agentId,D);
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
