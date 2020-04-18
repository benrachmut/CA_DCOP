import java.util.Random;

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

}
