import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Agent implements Runnable{
	protected int id;
	protected int id2;
	protected int [] D;
	protected int defultMessageValue; 
	

	
	public Agent( int D, int dcopId,  int id) {
		super();
		this.id = id;
		defultMessageValue = -1;
		this.D = new int[D];
		createDomainArray();
		
	}
	private void createDomainArray() {
		for (int domainValue = 0; domainValue < D.length; domainValue++) {
			D[domainValue] = domainValue;
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	protected static SortedMap<Integer, Integer> turnAllValuesToDefult(SortedMap<Integer, Integer> input, int defult) {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer,Integer>();
		for (Integer k : input.keySet()) {
			ans.put(k, defult);
		}
		return ans;
	}
	
	
	public abstract void receiveMessage (List<? extends Message> messages);
	public void receiveAnytimeMessage (List<MessageAnytime> messages) {	}
	public abstract void resetAgent ();

	

	
}
