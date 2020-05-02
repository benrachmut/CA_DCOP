package Problem;
import java.util.Random;

import AgentsAbstract.AgentVariable;

public class DcopGraphColoring extends Dcop {

	
	private double p1;
	Random randomP1 = new Random(this.id * 100);
	

	
	public DcopGraphColoring(int A, int D, int costPrameter, double p1) {
		super(A, D, costPrameter);
		this.p1 = p1;
		randomP1 = new Random(this.id*10);
		
	}
	
	public DcopGraphColoring(int A, double p1) {
		this(A, 3, 10, p1);
	}

	@Override
	public void createNeighbors() {
		for (int i = 0; i < agentsVariables.length; i++) {
			for (int j = i + 1; j < agentsVariables.length; j++) {
				double rnd = randomP1.nextDouble();
				if (rnd < p1) {
					AgentVariable a1 = agentsVariables[i];
					AgentVariable a2 = agentsVariables[j];
					
					this.neighbors.add(new Neighbor(a1, a2, D, costParameter, id));
			
				} // if neighbors
			} // for j
		} // for i
		
	}

	
	/*
	 * for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = rP1.nextDouble();
				if (p1Max < currentP1) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];
					this.neighbors.add(new Neighbors(af1, af2));
				} // if neighbors
			} // for j
		} // for i
	 */
}
