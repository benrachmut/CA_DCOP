package Messages;

import java.util.Comparator;

public class MsgsTimestampComparator implements Comparator<Msg> {

	@Override
	public int compare(Msg o1, Msg o2) {
		// TODO Auto-generated method stub
		return o1.getTimeStamp()-o2.getTimeStamp();
	}

}
