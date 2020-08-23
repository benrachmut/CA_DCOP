package Delays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Delays.ProtocolDelay;


/**
 * class is used for creating different combinations of delay protocols
 * needs to correspond with Protocol delay class
 * @return
 */
public abstract class CreatorDelays  {

	protected boolean[] imperfectCommunicationScenario= {false,true};
	protected boolean[] isTimeStamps = {true};
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
		for (boolean perfectP : imperfectCommunicationScenario) {
			if (perfectP == false) {
				ans.add(createDefultProtocol());
			} else {
				for (boolean isTimeStamp : isTimeStamps) {
					for (double gamma : gammas) {	
						Collection<? extends ProtocolDelay> toAdd = createCombinationsDelay(isTimeStamp,gamma);
						if (toAdd!=null) {
							ans.addAll(toAdd);
						}	
					}	
				}
			}
		}
		return ans;
	}
	

	protected abstract  ProtocolDelay createDefultProtocol();

	protected abstract Collection<? extends ProtocolDelay> createCombinationsDelay(boolean isTimeStamp, double gamma);
	
	public String getHeader() {
		return "Perfect Communication,Timestamp Use,Message Lost Prob,"+ header();
	}


	protected abstract String header();
		
	
	
}
