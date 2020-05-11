package Data;

import Main.Mailer;
import Problem.Dcop;

public class Data {
	private Double time;
	private DataGlobal global;
	private DataPerAgentVariable  perAgent;
	
	public Data(Double time, Dcop dcop, Mailer mailer) {
		this.time = time;
		this.global = new DataGlobal(dcop, mailer);
		this.perAgent = new DataPerAgentVariable(dcop, mailer);
	}
	public String globalDataString() {
		return this.global.toString();
	}
	
	public String perAgentDataString() {
		return this.perAgent.toString();
	}
	
	
}
