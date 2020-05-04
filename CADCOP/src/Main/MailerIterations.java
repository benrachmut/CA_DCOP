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
	protected void execute() {
		for (int i = 0; i < this.agents.size(); i++) {
			
			
			if (i == 0) {
				agents.get(i).initialize();
			} else {
				agents.get(i).reactionToMsgs();
			}
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend); 
			
			
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