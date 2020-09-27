package AlgorithmSearch;

import AgentsAbstract.AgentVariable;

public class AMDLS_V3 extends AMDLS_V2{

	public AMDLS_V3(int dcopId, int D, int agentId) {
		super(dcopId, D, agentId);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void updateAlgorithmName() {
		String a = "AMDLS";
		String b = "V3";
		String c = "";
		if (AMDLS_V1.typeDecision=='A' || AMDLS_V1.typeDecision=='a') {
			c = "a";
		}
		
		if (AMDLS_V1.typeDecision=='B' || AMDLS_V1.typeDecision=='b') {
			c = "b";
		}
		
		if (AMDLS_V1.typeDecision=='C' || AMDLS_V1.typeDecision=='c') {
			c = "c";
		}
		AgentVariable.AlgorithmName = a+"_"+b+"_"+c;
	}
	
	@Override
	public void initialize() {
		if (canSetColorInitilize()) {
			chooseColor();
			sendAMDLSColorMsgs();
			this.myCounter = 1;
			isWaitingToSetColor = false;
		}else {
			this.valueAssignment = Integer.MIN_VALUE;
			this.myCounter = 0;
		}
	}
	
	protected boolean compute() {
		boolean flag = false;
		if (canSetColorFlag) {
			chooseColor();
			setAboveAndBelow();
			flag = true;
		}
		if (flag || (consistentFlag && !canSetColorFlag)) {
			decideAndChange();
		}

		return true;
	}


}
