package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import Communication.ProtocolDelay;
import Comparators.CompMsgByDelay;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Problem.Dcop;

public class MailerIterations extends Mailer {

	public MailerIterations(ProtocolDelay delay, double terminationTime,Dcop dcop) {
		super(delay, terminationTime,dcop);
	}



	@Override
	public void execute() {
		createData(0);
		for (double iteration = 1; iteration < this.terminationTime; iteration++) {
			agentsReactToMsgs(iteration);
			createData((double)iteration);
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);
			
		}

	}
	
	




	private void agentsReactToMsgs(double iteration) {
		
		for (Agent agent : dcop.getAgents()) {
			if (iteration == 1) {
				agent.initialize(); // abstract method in agents
			} else {
				// compute (abstract method in agents) -->
				// varifyMsgSent-->
				// sendMsg(abstract method in agents) 
				agent.reactionToAlgorithmicMsgs(); 
			}
		}
		
		
	}



	public List<Msg> handleDelay() {
		Collections.sort(this.messageBox, new CompMsgByDelay());
		List<Msg> msgToSend = new ArrayList<Msg>();
		Iterator it = this.messageBox.iterator();

		while (it.hasNext()) {
			Msg msg = (Msg) it.next();
			if (msg.getDelay() == 0) {
				msgToSend.add(msg);
				it.remove();
			} else {
				msg.setDelay(msg.getDelay() - 1);
			}
		}
		return msgToSend;
	}

}
