package Messages;

import java.util.Comparator;

public class MsgsMailerTimeComparator implements Comparator<Msg> {

	@Override
	public int compare(Msg o1, Msg o2) {
		// TODO Auto-generated method stub
		return o1.getMailerTime()-o2.getMailerTime();
	}


}
