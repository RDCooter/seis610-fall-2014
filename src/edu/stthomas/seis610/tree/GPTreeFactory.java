package edu.stthomas.seis610.tree;

import java.util.Vector;

import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.tree.BinaryTreeNode.NodeType;
import edu.stthomas.seis610.tree.OperatorNode.OperatorType;
import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * The GPTreeFactory class defines the interface to a create new functions and their associated expression subtrees that
 * can be evaluated as a part of the Genetic Programming Project to find a function to match a set of known inputs and
 * their computed outputs.
 * 
 * @author Robert Driesch (cooter) Nov 8, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class GPTreeFactory {
	/**
	 * Define Enumeration to Describe the Generation Methods of Trees in the Factory
	 */
	public enum GenerationMethod {
		FULL, GROW, RANDOM;
	};

	private static Vector<String> operatorList = GPSettings.getOperators();
	private static Vector<String> operandList = GPSettings.getOperands();
	private static Vector<TrainingData> trainingData = GPSettings.getTrainingData();

	/**
	 * Generate a new expression node tree using the value extracted from the user settings as to which generation
	 * method to employ to generate the actual tree.
	 * 
	 * @return the newly constructed expression tree
	 * @throws GPException
	 */
	public static GeneticProgrammingTree generateTree() throws GPException {
		GeneticProgrammingTree newGPFunction = null;

		// Get the type of subtree generation that we are supposed to perform from the Settings and invoke the correct
		// generation method based upon the selection.
		switch (GenerationMethod.valueOf(GPSettings.getGenerationMethod())) {
		case FULL:
			newGPFunction = generateFullTree();
			break;
		case GROW:
			newGPFunction = generateGrowTree();
			break;
		case RANDOM:
			// In this case randomly select one of the previous generation methods and allow the tree to be
			// generated. This should result in a more diverse population of functions and trees.
			if (GPSettings.getRandomInt(2) == 0) {
				newGPFunction = generateFullTree();
			} else {
				newGPFunction = generateGrowTree();
			}
			break;
		default:
			throw new GPException("Invalid Generation Method detected in generateTree.");
		}
		return newGPFunction;
	}

	/**
	 * Generate a new expression node subtree using the value extracted from the user settings as to which generation
	 * method to employ to generate the actual subtree.
	 * 
	 * @return the newly constructed expression subtree
	 * @throws GPException
	 */
	public static GeneticProgrammingTree generateSubtree() throws GPException {
		GeneticProgrammingTree newGPFunction = null;

		// Get the type of subtree generation that we are supposed to perform from the Settings and invoke the correct
		// generation method based upon the selection.
		switch (GenerationMethod.valueOf(GPSettings.getGenerationMethod())) {
		case FULL:
			newGPFunction = generateFullSubtree();
			break;
		case GROW:
			newGPFunction = generateGrowSubtree();
			break;
		case RANDOM:
			// In this case randomly select one of the previous generation methods and allow the tree to be
			// generated. This should result in a more diverse population of functions and trees.
			if (GPSettings.getRandomInt(2) == 0) {
				newGPFunction = generateFullSubtree();
			} else {
				newGPFunction = generateGrowSubtree();
			}
			break;
		default:
			throw new GPException("Invalid Generation Method detected in generateSubtree.");
		}
		return newGPFunction;
	}

	/**
	 * Generate a new "Full" expression node tree using the value extracted from the user settings as to the max height
	 * allowed for this generated tree.
	 * 
	 * @return the newly constructed expression tree
	 */
	public static GeneticProgrammingTree generateFullTree() {
		return generateFullTree(GPSettings.getMaxHtOfInitTree());
	}

	/**
	 * Generate a new "Full" expression node subtree using the value extracted from the user settings as to the max
	 * height allowed for this generated subtree.
	 * 
	 * @return the newly constructed expression subtree
	 */
	public static GeneticProgrammingTree generateFullSubtree() {
		return generateFullTree(GPSettings.getMaxHtOfCrossoverTree());
	}

	/**
	 * Generate a new "Grow" expression node tree using the value extracted from the user settings as to the max height
	 * allowed for this generated tree.
	 * 
	 * @return the newly constructed expression tree
	 */
	public static GeneticProgrammingTree generateGrowTree() {
		return generateGrowTree(GPSettings.getMaxHtOfInitTree(), false);
	}

	/**
	 * Generate a new "Grow" expression node subtree using the value extracted from the user settings as to the max
	 * height allowed for this generated subtree.
	 * 
	 * @return the newly constructed expression subtree
	 */
	public static GeneticProgrammingTree generateGrowSubtree() {
		return generateGrowTree(GPSettings.getMaxHtOfCrossoverTree(), true);
	}

	/**
	 * Generate a new Genetic Programming Tree to represent this function along with its expression node tree for the GP
	 * processing that will take place.
	 * 
	 * The constructed expression node tree will be a balanced "Full" or "Bushy" expression tree only allowing terminal
	 * operand nodes at the bottom (leaf) of the tree.
	 * 
	 * @param aMaxHeight the max generated height allowed for this node tree
	 * @return the newly constructed GP tree representing this function and expression node tree
	 */
	private static GeneticProgrammingTree generateFullTree(Integer aMaxHeight) {
		/*
		 * Generate the complete Full or Bushy node tree with all of its children so that it can be assigned as the root
		 * of the GeneticProgrammingTree that we are going to construct.
		 */
		BinaryTreeNode nodeTreeRoot = generateFullSubtree(0, aMaxHeight);
		nodeTreeRoot.setNodeType(NodeType.ROOT);

		/*
		 * Generate the GeneticProgrammingTree to represent this expression node tree for the GP processing.
		 */
		GeneticProgrammingTree gpFunction = new GeneticProgrammingTree(nodeTreeRoot);
		// gpFunction.setTrainingData(trainingData);

		return gpFunction;
	}

	/**
	 * Generate a new Genetic Programming Tree to represent this function along with its expression node tree for the GP
	 * processing that will take place.
	 * 
	 * The constructed expression node tree will be a deep or grown expression tree allowing any node at any level of
	 * the tree.
	 * 
	 * @param aMaxHeight the max generated height allowed for this node tree
	 * @param aSubtree indicates if this is being built for a subtree or not
	 * @return the newly constructed GP tree representing this function and expression node tree
	 */
	private static GeneticProgrammingTree generateGrowTree(Integer aMaxHeight, boolean aSubtree) {
		/*
		 * Generate the complete grown node tree with all of its children so that it can be assigned as the root of the
		 * GeneticProgrammingTree that we are going to construct. If this is not being built for a subtree and the
		 * height of the tree is zero, then it makes no sense to leave this full node tree with just a simple single
		 * operand. In that case keep attempting to rebuild this node tree until something more interesting comes up.
		 */
		BinaryTreeNode nodeTreeRoot;
		do {
			nodeTreeRoot = generateGrowSubtree(0, aMaxHeight);
			nodeTreeRoot.setNodeType(NodeType.ROOT);
		} while (!aSubtree && nodeTreeRoot.getHeight().intValue() == 0);

		/*
		 * Generate the GeneticProgrammingTree to represent this expression node tree for the GP processing.
		 */
		GeneticProgrammingTree gpFunction = new GeneticProgrammingTree(nodeTreeRoot);
		// gpFunction.setTrainingData(trainingData);

		return gpFunction;
	}

	/**
	 * Generate a new "Full" or "Bushy" expression node tree by recursively building the tree from the top down only
	 * allowing terminal operand nodes at the bottom (leaf) of the tree. This will generate a full balanced (or bushy)
	 * tree.
	 * 
	 * @param aCurrentHeight the current height or level of the subtree
	 * @param aMaxHeight the max generated height allowed for this subtree
	 * @return the newly constructed expression node subtree
	 */
	private static BinaryTreeNode generateFullSubtree(Integer aCurrentHeight, Integer aMaxHeight) {
		BinaryTreeNode subTreeRoot;

		// For a bushy tree, if we are not at the leaves of the tree then we need to add an operator.
		if (aCurrentHeight < aMaxHeight) {
			subTreeRoot = getRandomOperator();

			try {
				// An operator always has at least one (left) node. The type of the random operator will determine if a
				// second (right) node is required.
				subTreeRoot.setLeftChild(generateFullSubtree((1 + aCurrentHeight), aMaxHeight));
				subTreeRoot.getLeftChild().setNodeType(NodeType.LEFT);
				subTreeRoot.getLeftChild().setParent(subTreeRoot);
				if (subTreeRoot.getNumberOfChildren() > 1) {
					subTreeRoot.setRightChild(generateFullSubtree((1 + aCurrentHeight), aMaxHeight));
					subTreeRoot.getRightChild().setNodeType(NodeType.RIGHT);
					subTreeRoot.getRightChild().setParent(subTreeRoot);
				}
			} catch (GPException e) {
				e.printStackTrace();
			}
		} else {
			// If we are already at the max height, then simply choose a terminal operand node.
			subTreeRoot = getRandomOperand();
		}

		return subTreeRoot;
	}

	/**
	 * Generate a new "Grow" expression node tree by recursively building the tree from the top down allowing any node
	 * (operators or terminal operands) to be added to the tree at any level of the tree. This will generate a tree
	 * where some branches are longer than others.
	 * 
	 * @param aCurrentHeight the current height or level of the subtree
	 * @param aMaxHeight the max generated height allowed for this subtree
	 * @return the newly constructed expression node subtree
	 */
	private static BinaryTreeNode generateGrowSubtree(Integer aCurrentHeight, Integer aMaxHeight) {
		BinaryTreeNode subTreeRoot;

		// For a bushy tree, if we are not at the leaves of the tree then we need to add an operator.
		if (aCurrentHeight < aMaxHeight) {
			subTreeRoot = getRandomNode();

			try {
				if (subTreeRoot instanceof OperatorNode) {
					// An operator always has at least one (left) node. The type of the random operator will determine
					// if a second (right) node is required.
					subTreeRoot.setLeftChild(generateGrowSubtree((1 + aCurrentHeight), aMaxHeight));
					subTreeRoot.getLeftChild().setNodeType(NodeType.LEFT);
					subTreeRoot.getLeftChild().setParent(subTreeRoot);
					if (subTreeRoot.getNumberOfChildren() > 1) {
						subTreeRoot.setRightChild(generateGrowSubtree((1 + aCurrentHeight), aMaxHeight));
						subTreeRoot.getRightChild().setNodeType(NodeType.RIGHT);
						subTreeRoot.getRightChild().setParent(subTreeRoot);
					}
				}
			} catch (GPException e) {
				e.printStackTrace();
			}
		} else {
			// If we are already at the max height, then simply choose a terminal operand node.
			subTreeRoot = getRandomOperand();
		}

		return subTreeRoot;
	}

	/**
	 * Generate a new operator node randomly chosen to be a part of any expression tree that gets constructed.
	 * 
	 * @return the newly constructed operator node
	 */
	public static OperatorNode getRandomOperator() {
		int index = GPSettings.getRandomInt(operatorList.size());
		String operator = operatorList.get(index);

		return new OperatorNode(OperatorType.valueOf(operator));
	}

	/**
	 * Generate a new operand terminal node (constant or variable) randomly chosen to be a part of any expression tree
	 * that gets constructed.
	 * 
	 * @return the newly constructed operand node
	 */
	public static OperandNode getRandomOperand() {
		int index = GPSettings.getRandomInt(operandList.size());
		String operand = operandList.get(index);

		return new OperandNode(operand);
	}

	/**
	 * Generate a new node (constant, variable or operator) randomly chosen to be a part of any expression tree that
	 * gets constructed.
	 * 
	 * @return the newly constructed node
	 */
	public static BinaryTreeNode getRandomNode() {
		if (GPSettings.getRandomInt(2) == 1)
			return getRandomOperator();
		else {
			return getRandomOperand();
		}
	}

}
