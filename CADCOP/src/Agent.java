import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Agent implements Runnable{
	protected int id;
	protected int id2;
	protected int dcopId;
	protected int defultMessageValue; 
	
	

	
	public Agent( int dcopId,  int agentId) {
		super();
		this.dcopId=dcopId;
		this.id = agentId;
		defultMessageValue = -1;
		
		
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
	
	
	public abstract void receiveMessage (List<? extends MsgAlgorithm> messages);
	public void receiveAnytimeMessage (List<? extends MsgAnytime> messages) {	}
	public abstract void resetAgent ();

	

	
}
