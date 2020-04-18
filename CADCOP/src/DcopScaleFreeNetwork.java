import java.util.Random;

public class DcopScaleFreeNetwork extends Dcop {

	private int hubs;
	private int neighborsPerAgent;
	private double p2;
	
	private Random randomHub , randomNotHub, randomP2; 

	public DcopScaleFreeNetwork(int A, int D, int costPrameter, int hubs, int neighborsPerAgent, double p2) {
		super(A, D, costPrameter);
		this.hubs = hubs;
		this.neighborsPerAgent =neighborsPerAgent;
		this.p2 = p2;
		randomHub = new Random(this.id*10);
		randomNotHub = new Random(this.id*20);
		randomP2 = new Random(this.id*30);

		
	}

	public DcopScaleFreeNetwork(int A,  int hubs, int neighborsPerAgent, double p2) {
		this(A, 10, 100, hubs,neighborsPerAgent,p2 );
	}

	

}
