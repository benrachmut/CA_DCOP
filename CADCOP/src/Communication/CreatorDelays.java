package Communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Communication.ProtocolDelay;


/**
 * class is used for creating different combinations of delay protocols
 * needs to correspond with Protocol delay class
 * @return
 */
public abstract class CreatorDelays  {

	protected boolean[] perfectCommunications={true,false};
	protected boolean[] isTimeStamps = { true, false };
	protected double[] gammas = {0};

	
	/**
	 * creates combinations if assuming imperfect communications, each class with its 
	 * Appropriate createCombinationsDelay method.
	 * 
	 * if perfectCommunications = false each extends class needs to return its default constructor
	 * @return
	 */
	public  List<ProtocolDelay> createProtocolDelays() {
		List<ProtocolDelay> ans = new ArrayList<ProtocolDelay>();
		for (boolean perfectP : perfectCommunications) {
			if (perfectP == true) {
				ans.add(createDefultProtocol());
			} else {
				for (boolean isTimeStamp : isTimeStamps) {
					for (double gamma : gammas) {
						ans.addAll(createCombinationsDelay(isTimeStamp,gamma));
					}
					
				}
			}
		}
		return ans;
	}
	

	protected abstract  ProtocolDelay createDefultProtocol();

	protected abstract Collection<? extends ProtocolDelay> createCombinationsDelay(boolean isTimeStamp, double gamma);
	
	public String header() {
		return "Perfect Communication,Time Stamp Use, Message Lost Prob";
	}
		
	
	
}
