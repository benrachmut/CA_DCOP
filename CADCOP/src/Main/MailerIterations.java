package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import Comparators.CompMsgByDelay;
import Delays.ProtocolDelay;
import Down.ProtocolDown;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Problem.Dcop;

public class MailerIterations extends Mailer {

	public MailerIterations(Protocol protocol, double terminationTime, Dcop dcop) {
		super(protocol, terminationTime, dcop);
	}

	@Override
	public void execute() {

		for (double iteration = 0; iteration < this.terminationTime; iteration++) {
			agentsReactToMsgs(iteration);
			createData((double) iteration);
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);

		}

	}

	private void agentsReactToMsgs(double iteration) {

		for (Agent agent : dcop.getAgents()) {
			if (iteration == 0) {
				agent.resetAgent();
				agent.initialize(); // abstract method in agents
			} else {
				// compute (abstract method in agents) -->
				// varifyMsgSent-->
				// sendMsg(abstract method in agents)
				// if (didAgentRecieveAlgorithmicMsgInThisIteration(agent)) { // check if needs
				// to add this
				agent.reactionToAlgorithmicMsgs();
			}
			if (MainSimulator.anyTime) {
				// if (didAgentRecieveAnytimeMsgInThisIteration(agent)) {
				agent.reactionToAnytimeMsgs();
				// }
			}
		}
	}

	private boolean didAgentRecieveAnytimeMsgInThisIteration(Agent agent) {
		if (this.recieversAnyTimeMsgs.containsKey(agent.getNodeId())) {
			return true;
		}
		return false;
	}

	private boolean didAgentRecieveAlgorithmicMsgInThisIteration(Agent agent) {
		if (this.recieversAlgortihmicMsgs.containsKey(agent.getNodeId())) {
			return true;
		}

		return false;
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
