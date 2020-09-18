package Problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariable;

import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import AlgorithmSearch.AMDLS;
import AlgorithmSearch.DSA_B_ASY;
import AlgorithmSearch.DSA_B_SY;
import AlgorithmSearch.DSA_SDP_ASY;
import AlgorithmSearch.DSA_SDP_SY;
import AlgorithmSearch.MGM_ASY;
import AlgorithmSearch.MGM_SY;
import AlgorithmsInference.MaxSumSplitConstraintFactorGraph;
import AlgorithmsInference.MaxSumStandardFunction;
import AlgorithmsInference.MaxSumStandardFunctionSync;
import AlgorithmsInference.MaxSumStandardVarible;
import AlgorithmsInference.MaxSumStandardVaribleSync;
import Comparators.CompAgentVariableByNeighborSize;
import Formation.ColorFormation;
import Formation.DFS;
import Formation.Formation;
import Main.Mailer;
import Main.MainSimulator;
import Messages.Msg;

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
			ans = new DSA_B_ASY(dcopId, D, agentId);
		}
		if (agentType == 2) {
			ans = new DSA_B_SY(dcopId, D, agentId);
		}
		if (agentType == 3) {
			ans = new MGM_ASY(dcopId, D, agentId);
		}
		if (agentType == 4) {
			ans = new MGM_SY(dcopId, D, agentId);
		}

		if (agentType == 5) {
			ans = new AMDLS(dcopId, D, agentId);
		}

		if (agentType == 6) {
			ans = new DSA_SDP_ASY(dcopId, D, agentId);
		}
		if (agentType == 7) {
			ans = new DSA_SDP_SY(dcopId, D, agentId);
		}

		if (agentType == 100) {
			ans = new MaxSumStandardVarible(dcopId, D, agentId);
		}

		if (agentType == 101) {

			ans = new MaxSumStandardVaribleSync(dcopId, D, agentId);
		}

		if (agentType == 102) {

			agentId = agentId + 1;
			ans = new MaxSumStandardVaribleSync(dcopId, D, agentId);

		}

		return ans;
	}

	private void createFormations() {

		Formation[] formations = { new ColorFormation(agentsVariables), new DFS(agentsVariables) };
		for (Formation f : formations) {
			f.execute();
		}

		handleFormationForAMDLS(formations);
		handleFormationForSearchAnytime(formations);

		if (MainSimulator.isAnytime && MainSimulator.isAnytimeDebug) {
			formationPrinting();
			System.out.println();
		}
	}

	private void formationPrinting() {
		System.out.println("--------Agents neighbors-------");

		for (AgentVariable a : this.agentsVariables) {
			System.out.println("**" + a + "**");
			System.out.println(a.getNeigborSetId());

		}

		System.out.println("--------Agents Anytime Formation-------");
		for (AgentVariable a : this.agentsVariables) {
			AgentVariableSearch as = (AgentVariableSearch) a;
			System.out.println("**" + as + "**");
			if (as.getAnytimeFather() == null) {
				System.out.println("father: is TOP");
			} else {
				System.out.println("father: " + as.getAnytimeFather());
			}
			System.out.println("sons: " + as.getAnytimeSons());
		}

		System.out.println("--------Agents Anytime Formation Below-------");
		for (AgentVariable a : this.agentsVariables) {
			AgentVariableSearch as = (AgentVariableSearch) a;
			System.out.println("**" + as + "**");
			System.out.println("below agents:" + as.getBelowAnytime());
		}
	}

	private void handleFormationForSearchAnytime(Formation[] formations) {
		if (MainSimulator.isAnytime && this.isSearchAlgorithm()) {

			for (AgentVariable a : agentsVariables) {
				Set<NodeId> above = new HashSet<NodeId>();
				Set<NodeId> below = new HashSet<NodeId>();
				if (MainSimulator.anytimeFormation == 1) {
					formations[1].setAboveBelow(a, above, below);
					((AgentVariableSearch) a).turnDFStoAnytimeStructure(below);
				}
			}
		}

	}

	private void handleFormationForAMDLS(Formation[] formations) {

		if (MainSimulator.agentType == 5) {
			if (AMDLS.structureColor) {
				formations[0].setAboveBelow();
			} else {
				formations[1].setAboveBelow();
			}

		}

	}

	public Dcop initiate() {
		createNeighbors();
		createFormations();

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

			Integer[][] constraints = n.getConstraints();

			AgentFunction af = null;

			if (agentType == 100 || agentType == 101) {

				if (agentType == 101) {

					af = new MaxSumStandardFunction(dcopId, D, av1.getId(), av2.getId(), constraints);

				} else {

					af = new MaxSumStandardFunctionSync(dcopId, D, av1.getId(), av2.getId(), constraints);

				}

				this.agentFunctions.add(af);
				this.agentsAll.add(af);
				av1.meetFunction(af.getMyNodes());
				av2.meetFunction(af.getMyNodes());
				af.meetVariables(av1.getNodeId(), av2.getNodeId());

			}

			if (agentType == 102) {

				int avSplitOne = av1.getId() + 1;
				int avSplitTwo = av2.getId() + 1;

				af = new MaxSumSplitConstraintFactorGraphSync(dcopId, D, av1.getId(), av2.getId(), constraints); // Will
																													// create
																													// a
																													// new
																													// MaxSumSplitConstraintFactorGraphSync.
				MaxSumSplitConstraintFactorGraphSync splitConstraintAgent = (MaxSumSplitConstraintFactorGraphSync) af; // Casting
																														// af
																														// as
																														// MaxSumSplitConstraintFactorGraphSync.
				List<MaxSumStandardFunctionSync> splitList = splitConstraintAgent.getSplitFunctionNodes(); // Get the
																											// list of
																											// the
																											// function
																											// agents.

				for (int i = 0; i < splitConstraintAgent.getSplitFunctionNodes().size(); i++) { // Looping over the list
																								// and adds each
																								// function agent on the
																								// list to agentFunction
																								// and agentsAll.

					this.agentFunctions.add(splitList.get(i));
					this.agentsAll.add(splitList.get(i));

				}

				av1.meetFunction(af.getMyNodes());
				av2.meetFunction(af.getMyNodes());
				af.meetVariables(av1.getNodeId(), av2.getNodeId());

			}

		}

		// pringAgentAll();

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
		return agentsVariables;
	}

	public boolean isInferenceAgent() {
		return (this.agentsVariables[0] instanceof AgentVariableInference);
	}

	public boolean isSearchAlgorithm() {
		// TODO Auto-generated method stub
		return (this.agentsVariables[0] instanceof AgentVariableSearch);
	}

	///// ******* Debug methods ******* ////


}