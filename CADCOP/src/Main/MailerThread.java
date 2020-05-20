package Main;

import java.util.List;

import Messages.Msg;
import Problem.Dcop;

public class MailerThread extends Mailer {

	public MailerThread(Protocol protocol, double terminationTime, Dcop dcop) {
		super(protocol, terminationTime, dcop);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<Msg> handleDelay() {
		// TODO Auto-generated method stub
		return null;
	}

}
