package edu.stthomas.seis610.tree;

import java.util.Vector;

import edu.stthomas.seis610.gp.FitnessDatum;
import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.tree.BinaryTreeNode.NodeType;
import edu.stthomas.seis610.util.GPException;

public class GeneticProgrammingTree extends BinaryTree {
	protected FitnessDatum xFitness = new FitnessDatum();
	protected Vector<TrainingData> xTrainingData;

	public GeneticProgrammingTree() {

	}

	public GeneticProgrammingTree(BinaryTreeNode aNode) {
		setRoot(aNode);
	}

	public GeneticProgrammingTree(Vector<TrainingData> aTrainingData) {
		setTrainingData(aTrainingData);
	}

	/**
	 * @return the fitness measurement datum associated with this binary tree
	 */
	public FitnessDatum getFitness() {
		return xFitness;
	}

	/**
	 * @param aValue the fitness measurement datum that represents the new fitness measurement
	 */
	public void setFitness(FitnessDatum aValue) {
		this.xFitness = aValue;
	}

	/**
	 * @return the list of training data values to use for this GP tree
	 */
	public Vector<TrainingData> getTrainingData() {
		return xTrainingData;
	}

	/**
	 * @param aTrainingData the training data values to assign for this GP tree
	 */
	public void setTrainingData(Vector<TrainingData> aTrainingData) {
		this.xTrainingData = aTrainingData;
	}

	/**
	 * Resets all of the cached instance variables.
	 */
	public void reset() {
		getRoot().resetTreeNodeInvalid();
		xFitness.reset();
	}

	/**
	 * A recursive way to evaluate the function represented by this tree.
	 * 
	 * @param aTrainingDatum the input training values (X and Y) of the target (perfect) function
	 * @return the fitness measurement result of the expression based upon the passed in input value
	 * @throws GPException
	 */
	public Double calculateFitness() {
		xFitness.clear();
		for (TrainingData trainingDatum : getTrainingData()) {
			try {
				xFitness.add(FitnessDatum.calculateStandardizedFitness(trainingDatum, evaluate(trainingDatum)));
			} catch (GPException e) {
				// When an error occurs during the evaluation, then simply add in the biggest standardized fitness value
				// into the datum.
				xFitness.add(new FitnessDatum(Double.MAX_VALUE));
				System.out.println("ERROR: <BinaryTree::evaluate>  x=" + trainingDatum.getInputData()
						+ "  BinaryTreeNode=" + this.toString());
				e.printStackTrace();
			}
		}
		return xFitness.getValue();
	}

	public void mutate() throws GPException {

		Integer aHeight = this.getHeight();
		BinaryTreeNode aNode = this.getRoot();
		Integer aRandomLevel = GPSettings.getRandomInt(aHeight + 1);

		for (int i = 0; i < aRandomLevel; i++) {
			Integer randomChild = GPSettings.getRandomInt(aNode.getNumberOfChildren());
			if ((randomChild == 0) && (aNode.hasLeftChild())) {
				aNode = aNode.getLeftChild();
			} else if ((randomChild == 1) && (aNode.hasRightChild())) {
				aNode = aNode.getRightChild();
			}
		}

		if (aNode instanceof OperandNode) {
			Integer randOperandPos = GPSettings.getRandomInt(GPSettings.getOperands().size());
			OperandNode.class.cast(aNode).setOperand(GPSettings.getOperands().elementAt(randOperandPos));
		} else {
			// TODO Make this work when mutating to/from unary operators
			Integer randOperatorPos = GPSettings.getRandomInt(GPSettings.getOperators().size());
			aNode.setData(GPSettings.getOperators().elementAt(randOperatorPos));
		}
		this.getRoot().resetTreeNodeInvalid();
	}

	/**
	 * Selects a random tree node from the expression tree and mutates that node into a newly generated subtree that is
	 * in turn spliced back into existing tree.
	 * 
	 * @throws GPException
	 */
	public void mutate_new() throws GPException {
		// Randomly select an individual node from anywhere within the expression tree as the target for mutation.
		BinaryTreeNode existingSubtreeNode = getRandomTreeNode();

		// Randomly determine the height of the new mutation subtree that will be spliced into the existing expression
		// node tree.
		Integer newSubtreeHeight = GPSettings.getRandomInt(GPSettings.getMaxHtOfMutationSubtree()) + 1;

		// Invoke the factory to generate a new subtree based upon our random height.
		BinaryTreeNode newSubtreeRoot = GPTreeFactory.generateGrowSubtree(newSubtreeHeight);

		// Determine if the randomly selected node to be mutated was the top or root node of the existing tree. If so,
		// then simply replace the entire expression node with the newly generated one and be done with it.
		if (existingSubtreeNode.getNodeType() == NodeType.ROOT || existingSubtreeNode.getParent() == null) {
			this.setRoot(newSubtreeRoot);
		} else {
			// Otherwise, splice the newly generated subtree into the existing expression node tree and fix up the node
			// types, parent and child references.
			BinaryTreeNode subtreeParent = existingSubtreeNode.getParent();
			newSubtreeRoot.setParent(subtreeParent);
			if (existingSubtreeNode.getNodeType() == NodeType.LEFT) {
				subtreeParent.setLeftChild(newSubtreeRoot);
				newSubtreeRoot.setNodeType(NodeType.LEFT);
			} else {
				subtreeParent.setRightChild(newSubtreeRoot);
				newSubtreeRoot.setNodeType(NodeType.RIGHT);
			}
		}

		// Reset the cached information stored about this expression node tree since we have modified it and it may no
		// longer be valid.
		this.getRoot().resetTreeNodeInvalid();
	}

	public void crossOver(GeneticProgrammingTree aGPT) {

		Integer aHeight = this.getHeight();
		// Integer bHeight = aGPT.getHeight();
		BinaryTreeNode aNode = this.getRoot();
		BinaryTreeNode bNode = aGPT.getRoot();

		// TODO: Until we get a more robust way to select candidates for crossOver, test to see if either of the two
		// trees has a height of zero which means they are a simple tree only comprised on a single operand. In these
		// cases a crossOver makes no sense so simply return. Plus the getRandomInt will choke and die with a bounded
		// height of zero passed in.
		if (0 == aHeight || 0 == aGPT.getHeight()) {
			return; // Do nothing...
		}
		Integer randomLevel = GPSettings.getRandomInt(aHeight);

		for (int i = 0; i < randomLevel; i++) {
			Integer randomChild = GPSettings.getRandomInt(aNode.getNumberOfChildren());
			if ((randomChild == 0) && (aNode.hasLeftChild())) {
				if (aNode.getLeftChild() instanceof OperatorNode) {
					aNode = aNode.getLeftChild();
				}
			} else if ((randomChild == 1) && (aNode.hasRightChild())) {
				if (aNode.getRightChild() instanceof OperatorNode) {
					aNode = aNode.getRightChild();
				}
			}
		}

		for (int i = 0; i < randomLevel; i++) {
			Integer randomChild = GPSettings.getRandomInt(bNode.getNumberOfChildren());
			if ((randomChild == 0) && (bNode.hasLeftChild())) {
				if (bNode.getLeftChild() instanceof OperatorNode) {
					bNode = bNode.getLeftChild();
				}
			} else if ((randomChild == 1) && (bNode.hasRightChild())) {
				if (bNode.getRightChild() instanceof OperatorNode) {
					bNode = bNode.getRightChild();
				}
			}
		}

		try {
			Integer aRandomChild = GPSettings.getRandomInt(aNode.getNumberOfChildren());
			Integer bRandomChild = GPSettings.getRandomInt(bNode.getNumberOfChildren());
			if ((aRandomChild == 0) && (bRandomChild == 0)) {
				BinaryTreeNode tempNode = aNode.getLeftChild();
				aNode.setLeftChild(bNode.getLeftChild());
				aNode.getLeftChild().setNodeType(NodeType.LEFT);
				aNode.getLeftChild().setParent(aNode);
				bNode.setLeftChild(tempNode);
				bNode.getLeftChild().setNodeType(NodeType.LEFT);
				bNode.getLeftChild().setParent(bNode);
			} else if ((aRandomChild == 0) && (bRandomChild == 1)) {
				BinaryTreeNode tempNode = aNode.getLeftChild();
				aNode.setLeftChild(bNode.getRightChild());
				aNode.getLeftChild().setNodeType(NodeType.LEFT);
				aNode.getLeftChild().setParent(aNode);
				bNode.setRightChild(tempNode);
				bNode.getRightChild().setNodeType(NodeType.RIGHT);
				bNode.getRightChild().setParent(bNode);
			} else if ((aRandomChild == 1) && (bRandomChild == 0)) {
				BinaryTreeNode tempNode = aNode.getRightChild();
				aNode.setRightChild(bNode.getLeftChild());
				aNode.getRightChild().setNodeType(NodeType.RIGHT);
				aNode.getRightChild().setParent(aNode);
				bNode.setLeftChild(tempNode);
				bNode.getLeftChild().setNodeType(NodeType.LEFT);
				bNode.getLeftChild().setParent(bNode);
			} else if ((aRandomChild == 1) && (bRandomChild == 1)) {
				BinaryTreeNode tempNode = aNode.getRightChild();
				aNode.setRightChild(bNode.getRightChild());
				aNode.getRightChild().setNodeType(NodeType.RIGHT);
				aNode.getRightChild().setParent(aNode);
				bNode.setRightChild(tempNode);
				bNode.getRightChild().setNodeType(NodeType.RIGHT);
				bNode.getRightChild().setParent(bNode);
			}
			this.getRoot().resetTreeNodeInvalid();
			aGPT.getRoot().resetTreeNodeInvalid();
		} catch (GPException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object clone() {
		GeneticProgrammingTree newClone = (GeneticProgrammingTree) super.clone();
		newClone.setFitness((FitnessDatum) getFitness().clone());

		return newClone;
	}
}
