package AgentsAbstract;
import java.util.Collections;
/**
 * 
 * @author Omer Perry
 *
 */
public class NodeId implements Comparable<NodeId> {

    ///// ******* Variables ******* ////

	public enum NodeType {Variable, Function}; 
	private NodeType type;
	private int id1;
	private int id2;
	
	//-----------------------------------------------------------------------------------------------------------//

	//OmerP - Constructor for Variable Node.
	public NodeId(int id1) {

		this.id1 = id1; 
		this.id2 = 0;
		setType(NodeType.Variable);
		
	}
	
	//OmerP - Constructor for Function Node.
	public NodeId(int id1, int id2) {
		
		this.id1 = id1; 
		this.id2 = id2;
		setType(NodeType.Function);

	}
	
    ///// ******* Compare and Equals ******* ////
	
	@Override
	public int compareTo(NodeId toCheck) {
		// TODO Auto-generated method stub
		
		if(this.id1 < toCheck.id1) { //Variable node this is in before variable node toCheck
			
			return 1; 
			
		}
		
		if(this.id1 > toCheck.id1) { //Variable node this is in after variable node toCheck

			return -1; 
			
		}
		
		else { //For the case that are both variable nodes have the same id1, than its a function node comparison. 
			
			 if(this.id2 < toCheck.id2) { //Function node this is before function node toCheck. 
				 
				 return 1; 
				 
			 }
			 
			 if(this.id2 < toCheck.id2) { //Function node this is after function node toCheck.
				 
				 return -1;
				 
			 }
			 
			 else {
				 
				 return 0; //The same function node. 
				 
			 }
			
		}
			
	}
		
    ///// ******* Getters ******* ////

	public int getId1() {
		
		return id1; 
		
	}
	
	public int getId2() {
		
		return id2; 
		
	}
	
    ///// ******* Setter ******* ////

	public void setId1(int id1) {
		
		this.id1 = id1;
		
	}
	
	public void setId2(int id2) {
		
		this.id2 = id2;
		
	}
	
	public void setType(NodeType type) {
		
		this.type = type;
		
	}




	
	
	
	
}