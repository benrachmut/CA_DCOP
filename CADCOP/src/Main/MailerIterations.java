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
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Problem.Dcop;

public class MailerIterations extends Mailer {

	public MailerIterations(ProtocolDelay delay, double terminationTime, Dcop dcop) {
		super(delay, terminationTime, dcop);
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
				agent.initialize(); // abstract method in agents
			} else {
				// compute (abstract method in agents) -->
				// varifyMsgSent-->
				// sendMsg(abstract method in agents)
				if (didAgentRecieveAlgorithmicMsgInThisIteration(agent)) {
					agent.reactionToAlgorithmicMsgs();
				}
				if (MainSimulator.anyTime) {
					if (didAgentRecieveAnytimeMsgInThisIteration(agent)) {
						agent.reactionToAnytimeMsgs();
					}
				}
			}
		}

	}

	private boolean didAgentRecieveAnytimeMsgInThisIteration(Agent agent) {
		if (this.recieversAnyTimeById.containsKey(agent.getId())) {
			return true;
		}
		return false;
	}

	private boolean didAgentRecieveAlgorithmicMsgInThisIteration(Agent agent) {
		if (agent instanceof AgentVariableSearch) {
			if (this.recieversAlgorithmById.containsKey(agent.getId())) {
				return true;
			}
		} else {
			NodeId nodeId = getNodeId(agent);
			if (this.recieversAlgortihmiByNodeId.containsKey(nodeId)) {
				return true;
			}
		}
		return false;
	}

	private NodeId getNodeId(Agent agent) {
		NodeId ans;
		if (agent instanceof AgentFunction) {
			ans = ((AgentFunction) agent).getNodeId();
		} else {
			ans = ((AgentVariableInference) agent).getNodeId();
		}
		return ans;
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
