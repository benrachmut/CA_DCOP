package Algorithms;

import AgentsAbstract.AgentFunction;

public class MaxSumSplitConstraintFactorGraph extends MaxSumStandardFunction {

	///// ******* Variables ******* ////

	double SCFGRatio = 0.6;
	MaxSumStandardFunction firstSplit, secondSplit;

	// -----------------------------------------------------------------------------------------------------------//

	///// ******* Constructor ******* ////

	// OmerP - Constructor for Split Constraint Factor Graph
	public MaxSumSplitConstraintFactorGraph(int dcopId, int D, int id1, int id2, Integer[][] constraints,
			Integer[][] constraintsTranspose) {

		super(dcopId, D, id1, id2, constraints, constraintsTranspose);

		this.constraints = AgentFunction.turnIntegerToDoubleMatrix(constraints);
		this.constraintsTranspose = AgentFunction.turnIntegerToDoubleMatrix(constraintsTranspose);

		this.firstSplit = new MaxSumStandardFunction(dcopId, D, id1, id2,
				this.splitConstraintFactorGraph(this.constraints),
				this.splitConstraintFactorGraph(this.constraintsTranspose));
		this.secondSplit = new MaxSumStandardFunction(dcopId, D, id2, id1,
				this.splitConstraintFactorGraph(this.constraints),
				this.splitConstraintFactorGraph(this.constraintsTranspose));

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

	// OmerP - will return the firstSplit MaxSumStandardFunction.
	public MaxSumStandardFunction getFirstSplit() {

		return firstSplit;

	}

	// OmerP - will return the secondSplit MaxSumStandardFunction.
	public MaxSumStandardFunction getSecondSplit() {

		return secondSplit;

	}

}
