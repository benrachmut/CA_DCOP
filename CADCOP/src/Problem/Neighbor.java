package Problem;
import java.util.Random;

import Agents.AgentFunction;
import Agents.AgentVariable;

public class Neighbor {
	private AgentVariable a1, a2;
	private AgentFunction f;
	private Integer[][] constraints;
	private Integer[][] constraintsTranspose;

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
		neighborsMeetings();
	}



	public Neighbor(AgentVariable a1, AgentVariable a2, int D, int costParameter, int dcopId) {
		updateVariables(a1,a2,costParameter,D);
		createConstraintsForEquality();
		neighborsMeetings();
	}
	private void updateVariables(AgentVariable a1, AgentVariable a2, int costParameter, int D) {
		this.a1 = a1;
		this.a2 = a2;
		this.costParameter = costParameter;
		this.constraints = new Integer[D][D];
		this.constraintsTranspose= new Integer[D][D];
	}


	
	private void createConstraintsForEquality() {
		for (int i = 0; i < constraints.length; i++) {
			for (int j = 0; j < constraints[i].length; j++) {
				if (j==i) {
					constraints[i][j] = costParameter;
					constraintsTranspose[j][i] = costParameter;
				}
				else {
					constraints[i][j] = 0;
					constraintsTranspose[j][i] = 0;

				}
			}
		}

	}

	private void createConstraintsWithP2() {
		for (int i = 0; i < constraints.length; i++) {
			for (int j = 0; j < constraints[i].length; j++) {
				double rndProb = randomP2.nextDouble();
				if (rndProb < p2) {
					int rndCost = randomCost.nextInt(costParameter);
					constraints[i][j] = rndCost;
					constraintsTranspose[j][i] = rndCost;
				}
			}
		}
	}

	public int getCurrentCost() {
		int i = a1.getVariableX();
		int j = a2.getVariableX();
		return this.constraints[i][j];
	}

	public void neighborsMeetings() {
		a1.meetNeighbor(a2.getId(), this.constraints);
		a2.meetNeighbor(a1.getId(), this.constraintsTranspose);	
	}
	
	

}
