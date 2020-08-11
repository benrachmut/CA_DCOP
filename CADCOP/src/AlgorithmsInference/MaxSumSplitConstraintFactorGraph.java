package AlgorithmsInference;

import java.util.ArrayList;
import java.util.List;

import AgentsAbstract.AgentFunction;
import AgentsAbstract.NodeId;
import Problem.Neighbor;

public class MaxSumSplitConstraintFactorGraph extends MaxSumStandardFunction {

	///// ******* Variables ******* ////

	double SCFGRatio = 0.6;
	MaxSumStandardFunction firstSplit, secondSplit;
	protected List<MaxSumStandardFunction> splitFunctionNodes;
	
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor ******* ////

	// OmerP - Constructor for Split Constraint Factor Graph
	public MaxSumSplitConstraintFactorGraph(int dcopId, int D, int id1, int id2, Integer[][] constraints, Integer[][] constraintsTranspose) {

		super(dcopId, D, id1, id2, constraints, constraintsTranspose);
		this.constraints = AgentFunction.turnIntegerToDoubleMatrix(constraints);
		this.constraintsTranspose = AgentFunction.turnIntegerToDoubleMatrix(constraintsTranspose);
		this.splitFunctionNodes = new ArrayList<MaxSumStandardFunction>();
		this.firstSplit = new MaxSumStandardFunction(dcopId, D, id1, id2, this.splitConstraintFactorGraph(this.constraints),this.splitConstraintFactorGraph(this.constraintsTranspose));
		this.secondSplit = new MaxSumStandardFunction(dcopId, D, id2, id1, this.splitConstraintFactorGraph(this.constraints), this.splitConstraintFactorGraph(this.constraintsTranspose));
		splitFunctionNodes.add(firstSplit);
		splitFunctionNodes.add(secondSplit);
		updataNodes();
		
	}

	///// ******* Initialize Split Constraint Factor Graph Methods ******* //// - To add with Ben.

	//OmerP - Will add a new nodeId to the updated list. 
	public void updataNodes(){
		
		for(int i = 0 ; i < splitFunctionNodes.size() ; i++) {
			
			if(!nodes.contains(splitFunctionNodes.get(i).getNodeId()))
			
				updataNodes(splitFunctionNodes.get(i).getNodeId());
			
			}
		
	}
		
	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Split Constraint Factor Graph Methods ******* ////

	// OmerP - Will generate split constraint factor graph.
	protected Double[][] splitConstraintFactorGraph() {

		Double[][] splitConstraintTable = getConstraintMatrix();

		if (nodeId.getId1() > nodeId.getId2()) {

			tableMultiplication(splitConstraintTable, SCFGRatio);

		}

		else {

			tableMultiplication(splitConstraintTable, 1 - SCFGRatio);

		}

		return splitConstraintTable;

	}

	// OmerP - Will generate split constraint factor graph and will return the
	// matrix.
	protected Double[][] splitConstraintFactorGraph(Double[][] matrixToSplit) {

		Double[][] splitConstraintTable;

		if (nodeId.getId1() > nodeId.getId2()) {

			splitConstraintTable = tableMultiplication(matrixToSplit, SCFGRatio);

		}

		else {

			splitConstraintTable = tableMultiplication(matrixToSplit, 1 - SCFGRatio);

		}

		return splitConstraintTable;

	}

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Getters ******* ////

	// OmerP - Will return the firstSplit MaxSumStandardFunction.
	public MaxSumStandardFunction getFirstSplit() {

		return firstSplit;

	}

	// OmerP - Will return the secondSplit MaxSumStandardFunction.
	public MaxSumStandardFunction getSecondSplit() {

		return secondSplit;

	}

	// OmerP - Will return the list of Max Sum Standard Function. 
	public List<MaxSumStandardFunction> getSplitFunctionNodes(){
		
		return splitFunctionNodes; 
		
	}
	
	
	
}
