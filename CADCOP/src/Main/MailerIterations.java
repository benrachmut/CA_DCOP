package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import AgentsAbstract.Agent;
import AgentsAbstract.AgentFunction;
import AgentsAbstract.AgentVariable;
import AgentsAbstract.AgentVariableInference;
import AgentsAbstract.AgentVariableSearch;
import AgentsAbstract.NodeId;
import AlgorithmSearch.DSA_SY;
import AlgorithmSearch.MGM;
import Comparators.CompMsgByDelay;
import Data.Data;
import Delays.ProtocolDelay;
import Down.ProtocolDown;
import Messages.Msg;
import Messages.MsgAlgorithm;
import Messages.MsgAnyTime;
import Problem.Dcop;

public class MailerIterations extends Mailer {

	public MailerIterations(Protocol protocol, int terminationTime, Dcop dcop) {
		super(protocol, terminationTime, dcop);
	}

	@Override
	public void execute() {
		//printHeaderForDebugDSA_SY();
		//printHeaderForDegbugMgm();
		for (int iteration = 0; iteration < this.terminationTime; iteration++) {
			agentsReactToMsgs(iteration);
			//printForDegbugMgm(iteration);
			//printForDebugDSA_SY(iteration);
			createData(iteration);
			List<Msg> msgToSend = this.handleDelay();
			agentsRecieveMsgs(msgToSend);
		}
	}

	private void printHeaderForDegbugMgm() {
		String ans ="iteration"+",";
		for (AgentVariable a : dcop.getVariableAgents()) {
			ans =ans+ "a"+a.getId()+" VA"+",";
		}
		for (AgentVariable a : dcop.getVariableAgents()) {
			ans =ans+"a"+a.getId()+" LR"+",";
		}
		
		System.out.println(ans);
		
	}

	private void printForDegbugMgm(int iteration) {
		String ans =iteration+",";
		for (AgentVariable a : dcop.getVariableAgents()) {
			ans = ans+a.getValueAssignment()+",";
		}
		for (AgentVariable a : dcop.getVariableAgents()) {
			ans = ans+((MGM)a).getLR()+",";
		}
		System.out.println(ans);
	}

	private void printHeaderForDebugDSA_SY() {
		String ans = "Iteration,Global_Cost,";
		for (int i = 0; i < dcop.getVariableAgents().length; i++) {
			AgentVariable av = dcop.getVariableAgents()[i];
			if (av instanceof DSA_SY) {
				int currentId = av.getId();
				ans = ans+currentId+"_rnd"+","+currentId+"_value,";
				for (NodeId nNodeId : av.getNeigborSetId()) {
					int n = nNodeId.getId1();
					ans = ans + currentId + "_view_on_"+n+","+currentId + "_timestamp_on_"+n+",";
				}
			}
		}
		System.out.println(ans);

	}

	private void printForDebugDSA_SY(int iteration) {
		String ans = iteration + "," + this.dataMap.get(iteration).getGlobalCost() + ",";

		for (int i = 0; i < dcop.getVariableAgents().length; i++) {
			AgentVariable av = dcop.getVariableAgents()[i];
			if (av instanceof DSA_SY) {
				DSA_SY a = (DSA_SY) av;
				ans = ans+a.getStringForDebug();
			} else {
				System.out.println("should not use printForDebugDSA_SY");
				throw new RuntimeException();
			}
		}
		System.out.println(ans);
	}

	private void agentsReactToMsgs(int iteration) {

		for (Agent agent : dcop.getAgents()) {
			if (iteration == 0) {
				agent.resetAgent();
				agent.initialize(); // abstract method in agents
			} else {
				agent.reactionToAlgorithmicMsgs();
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

	public synchronized List<Msg> handleDelay() {
		Collections.sort(this.messageBox, new CompMsgByDelay());
		List<Msg> msgToSend = new ArrayList<Msg>();
		Iterator it = this.messageBox.iterator();

		while (it.hasNext()) {
			Msg msg = (Msg) it.next();
			if (msg.getDelay() <= 0) {
				msgToSend.add(msg);
				it.remove();
			} else {
				msg.setDelay(msg.getDelay() - 1);
			}
		}
		return msgToSend;
	}

	@Override
	public void setMailerName() {
		Mailer.mailerName = "Iteration";

	}



}
