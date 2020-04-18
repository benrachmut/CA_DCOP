import java.util.Random;

public class DcopUniform extends Dcop {

	private Random randomP1, randomP2;
	private double p1;
	private double p2;


	public DcopUniform(int A, int D, int costPrameter, double p1, double p2) {
		super(A,D,costPrameter);
		this.randomP1 = new Random(this.id * 10);
		this.randomP2 = new Random(this.id * 20);
		this.p1 = p1;
		this.p2 = p2;	
		this.D = D;
		

	}
	
	public DcopUniform(int A, double p1, double p2) {
		this( A, 10, 100, p1,  p2);
	
	}
	

}
