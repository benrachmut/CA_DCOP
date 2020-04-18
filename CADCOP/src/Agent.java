
public abstract class Agent {
	protected int id;
	protected int [] D;
	public Agent(int id, int D) {
		super();
		this.id = id;
		this.D = new int[D];
		createDomainArray();
	}
	private void createDomainArray() {
		for (int domainValue = 0; domainValue < D.length; domainValue++) {
			D[domainValue] = domainValue;
		}
	}
	
	
}
