package AlgorithmSearch;

import java.util.Random;

import AgentsAbstract.AgentVariable;
import Main.MailerIterations;
import Main.MainSimulator;

public class AMDLS_V3 extends AMDLS_V2 {

	private double rndStochastic;
	private static double stochastic = 1;
	private boolean firstFlag;

	public AMDLS_V3(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		Random r = new Random(this.dcopId * 10 + this.id * 100);
		this.rndStochastic = r.nextDouble();
		firstFlag = false;
	}

	@Override
	protected void resetAgentGivenParametersV3() {
		// TODO Auto-generated method stub
		super.resetAgentGivenParametersV3();
		Random r = new Random(this.dcopId * 10 + this.id * 100);
		this.rndStochastic = r.nextDouble();
		firstFlag = false;
	}

	@Override
	public void updateAlgorithmName() {
		String a = "AMDLS";
		String b = "V3";
		String c = "";
		if (AMDLS_V1.typeDecision == 'A' || AMDLS_V1.typeDecision == 'a') {
			c = "a";
		}

		if (AMDLS_V1.typeDecision == 'B' || AMDLS_V1.typeDecision == 'b') {
			c = "b";
		}

		if (AMDLS_V1.typeDecision == 'C' || AMDLS_V1.typeDecision == 'c') {
			c = "c";
		}
		AgentVariable.AlgorithmName = a + "_" + b + "_" + c;
	}

	@Override
	public void initialize() {
		if (canSetColorInitilize()) {
			chooseColor();
			sendAMDLSColorMsgs();
			this.myCounter = 1;
			firstFlag = true;

			isWaitingToSetColor = false;
		} else {
			this.valueAssignment = Integer.MIN_VALUE;
			this.myCounter = 0;
		}
	}

	protected boolean compute() {

		if (this.rndStochastic < stochastic) {
			if (firstFlag == false) {
				this.myCounter = myCounter + 1;
				this.valueAssignment = this.firstRandomVariable;
				releaseFutureMsgs_distributed();
			}
			firstFlag = true;
			super.compute();

		} else {

			boolean flag = false;
			if (canSetColorFlag) {
				chooseColor();
				setAboveAndBelow();
				flag = true;
			}

			if (flag || (consistentFlag && !canSetColorFlag)) {
				releaseFutureMsgs_distributed();
				decideAndChange();
			}
		}
		/*
		 * if (flag) { double rnd = rndStochastic.nextDouble(); if ( rnd < stochastic )
		 * { releaseFutureMsgs_distributed(); this.valueAssignment =
		 * this.firstRandomVariable; } }
		 */

		return true;
	}

	// done
	@Override
	public void updateAlgorithmHeader() {
		AgentVariable.algorithmHeader = "Structure Heuristic" + "," + "Message Frequency" + ',' + "Decision" + ","
				+ "Stochastic";
	}

	// done
	@Override
	public void updateAlgorithmData() {
		String heuristic = "";
		if (structureHeuristic == 1) {
			heuristic = "Index";
		}
		// -------------------------
		String freq = "";
		if (AMDLS_V1.sendWhenMsgReceive) {
			freq = "high";
		} else {
			freq = "low";
		}
		// -------------------------
		String t = "";
		if (AMDLS_V1.typeDecision == 'A' || AMDLS_V1.typeDecision == 'a') {
			t = "a";
		}

		if (AMDLS_V1.typeDecision == 'B' || AMDLS_V1.typeDecision == 'b') {
			t = "b";
		}

		if (AMDLS_V1.typeDecision == 'C' || AMDLS_V1.typeDecision == 'c') {
			t = "c";
		}

		// -------------------------

		AgentVariable.algorithmData = heuristic + "," + freq + "," + t + "," + stochastic;
	}

}
