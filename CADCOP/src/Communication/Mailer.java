package Communication;

import java.util.ArrayList;
import java.util.List;
import Messages.Msg;
import Problem.Dcop;

public abstract class Mailer {
	private ProtocolDown down;
	private ProtocolDelay delay;
	private List<Msg> msgBox;
	
	
	
	
	public Mailer(ProtocolDown down, ProtocolDelay delay) {
		super();
		this.down = down;
		this.delay = delay;
		this.msgBox = new ArrayList<Msg>();
	}

	public void setSeed(int dcopId) {
		this.msgBox = new ArrayList<Msg>();
		this.delay.setSeeds(dcopId);
		this.down.setSeeds(dcopId);
	}
	
	public abstract void execute();
	
	
	

}
