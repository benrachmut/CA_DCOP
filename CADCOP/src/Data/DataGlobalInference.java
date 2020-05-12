package Data;

import Main.Mailer;
import Problem.Dcop;

public class DataGlobalInference extends DataGlobal {

	public DataGlobalInference(Dcop dcop, Mailer mailer) {
		super(dcop, mailer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String header() {
		return "";
	}

	@Override
	protected String getToStringGivenParameters() {
		return "";
	}

}
