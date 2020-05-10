package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import Communication.ProtocolDelay;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;

public class MailerIterations extends Mailer {

	@Override
	public void execute() {
		for (int iteration = 0; iteration < this.terminationTime; iteration++) {
			agentsReactToMsgs(iteration);
			addCostToMaps();
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);
			
		}

	}
	
	

	private void agentsReactToMsgs(int iteration) {
		
		for (Agent agent : dcop.getAgents()) {
			if (iteration == 0) {
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
		Collections.sort(this.messageBox);
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
