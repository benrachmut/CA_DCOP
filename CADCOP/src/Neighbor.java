import java.util.Random;

public class Neighbor {
	private AgentVariable a1, a2;
	private AgentFunction f;
	private int[][] constraints;

	private int costParameter;
	private double p2;
	private Random randomP2, randomCost;

	public Neighbor(AgentVariable a1, AgentVariable a2, int D, int costParameter, int dcopId, double p2) {
		super();
		updateVariables(a1,a2,costParameter,D);
		this.p2 = p2;
		this.randomP2 = new Random(dcopId * 10 + a1.getId() * 100 + a2.getId() * 1000);
		this.randomCost = new Random(dcopId * 100 + a1.getId() * 300 + a2.getId() * 1200);
		createConstraintsWithP2();
	}

	public Neighbor(AgentVariable a1, AgentVariable a2, int D, int costParameter, int dcopId) {
		super();
		updateVariables(a1,a2,costParameter,D);
		createConstraintsForEquality();
	}

	private void updateVariables(AgentVariable a1, AgentVariable a2, int costParameter, int D) {
		this.a1 = a1;
		this.a2 = a2;
		this.costParameter = costParameter;
		this.constraints = new int[D][D];
		variableAgentsMeetEachOther();
	}

	private void variableAgentsMeetEachOther() {
		a1.meetNeighbor(a2.getId());
		a2.meetNeighbor(a1.getId());
		
	}

	private void createConstraintsForEquality() {
		for (int i = 0; i < constraints.length; i++) {
			for (int j = 0; j < constraints[i].length; j++) {
				if (j==i) {
					constraints[i][j] = costParameter;
				}
				else {
					constraints[i][j] = 0;
				}
			}
		}

	}

	private void createConstraintsWithP2() {
		for (int i = 0; i < constraints.length; i++) {
			for (int j = 0; j < constraints[i].length; j++) {
				double rndProb = randomP2.nextDouble();
				if (rndProb < p2) {
					constraints[i][j] = randomCost.nextInt(costParameter);
				}
			}
		}
	}

	public int getCurrentCost() {
		int i = a1.getVariableX();
		int j = a2.getVariableX();
		return this.constraints[i][j];
	}
	

}
