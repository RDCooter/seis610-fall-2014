package edu.stthomas.seis610.tree;

import java.util.LinkedList;
import java.util.List;

import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * The Binary Tree class defines the structure that contains the expression tree that represents the function and is
 * used by the GP.
 * 
 * @author Pravesh Tamraker Oct 19, 2014 4:11:18 PM
 * @author Robert Driesch (cooter) Nov 7, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public abstract class BinaryTree implements Cloneable {
	protected BinaryTreeNode xRoot;

	/**
	 * Default constructor for this class.
	 */
	public BinaryTree() {
	}

	/**
	 * @param aNode the node that represents the root of the expression tree
	 */
	public BinaryTree(BinaryTreeNode aNode) {
		setRoot(aNode);
	}

	/**
	 * @return the root node in the binary tree for the expression
	 */
	public BinaryTreeNode getRoot() {
		return xRoot;
	}

	/**
	 * @param aNode the node that represents the root of the expression tree
	 */
	public void setRoot(BinaryTreeNode aNode) {
		this.xRoot = aNode;
	}

	/**
	 * @return the indicator if the expressions within the node tree are valid
	 */
	public Boolean isTreeValid() {
		return xRoot.isTreeNodeValid();
	}

	/**
	 * @return an integer count of the height of the subtree
	 */
	public Integer getHeight() {
		return xRoot.getHeight();
	}

	/**
	 * A recursive way to evaluate the function represented by this tree.
	 * 
	 * @param aTrainingDatum the input training values (X and Y) of the target (perfect) function
	 * @return the fitness measurement result of the expression based upon the passed in input value
	 * @throws GPException
	 */
	public Double evaluate(TrainingData aTrainingDatum) throws GPException {
		return xRoot.evaluateOutput(aTrainingDatum);
	}

	/**
	 * Randomly select a single tree node from all of the nodes available within the expression tree.
	 * 
	 * @return a randomly selected node from the expression tree
	 */
	public BinaryTreeNode getRandomTreeNode() {
		List<BinaryTreeNode> treeNodes = getPostOrderList();
		Integer randomPosition = GPSettings.getRandomInt(treeNodes.size());
		return treeNodes.get(randomPosition);
	}

	/**
	 * Recursive method to format and output the expression tree in "in-order" notation with appropriate parenthesis
	 * added.
	 * 
	 * @param aNode the current node within the expression tree
	 * @return a string representation of the nodes in the expression tree
	 */
	private String toString(BinaryTreeNode aNode) {
		StringBuffer output;
		if (aNode instanceof OperandNode) {
			return aNode.toString();
		} else {
			output = new StringBuffer();
			if (aNode != getRoot())
				output.append("(");
			output.append(this.toString(aNode.getLeftChild()));
			output.append(aNode.toString());
			output.append(this.toString(aNode.getRightChild()));
			if (aNode != getRoot())
				output.append(")");
		}

		return output.toString();
	}

	/**
	 * @return a list of the nodes in the expression tree in "post-order"
	 */
	public List<BinaryTreeNode> getPostOrderList() {
		return getPostOrderList(getRoot());
	}

	/**
	 * Recursive method to extract all of the nodes within the expression tree and return them in a linked list in
	 * post-order notation.
	 * 
	 * @param aNode the current node within the expression tree
	 * @return a list of the nodes in the expression tree in "post-order"
	 */
	private LinkedList<BinaryTreeNode> getPostOrderList(BinaryTreeNode aNode) {
		if (aNode == null) {
			return null;
		} else {
			LinkedList<BinaryTreeNode> postOrderList = new LinkedList<BinaryTreeNode>();
			if (aNode.hasLeftChild()) {
				postOrderList.addAll(getPostOrderList(aNode.getLeftChild()));
			}
			if (aNode.hasRightChild()) {
				postOrderList.addAll(getPostOrderList(aNode.getRightChild()));
			}
			postOrderList.add(aNode);
			return postOrderList;
		}
	}

	/**
	 * @return a list of the nodes in the expression tree in "in-order"
	 */
	public List<BinaryTreeNode> getInOrderList() {
		return getInOrderList(getRoot());
	}

	/**
	 * Recursive method to extract all of the nodes within the expression tree and return them in a linked list in
	 * in-order notation.
	 * 
	 * @param aNode the current node within the expression tree
	 * @return a list of the nodes in the expression tree in "in-order"
	 */
	private LinkedList<BinaryTreeNode> getInOrderList(BinaryTreeNode aNode) {
		if (aNode == null) {
			return null;
		} else {
			LinkedList<BinaryTreeNode> inOrderList = new LinkedList<BinaryTreeNode>();
			if (aNode.hasLeftChild()) {
				inOrderList.addAll(getInOrderList(aNode.getLeftChild()));
			}
			inOrderList.add(aNode);
			if (aNode.hasRightChild()) {
				inOrderList.addAll(getInOrderList(aNode.getRightChild()));
			}
			return inOrderList;
		}
	}

	@Override
	public Object clone() {
		BinaryTree newTree = null;
		try {
			newTree = (BinaryTree) super.clone();
			newTree.setRoot((BinaryTreeNode) getRoot().clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return newTree;
	}

	@Override
	public String toString() {
		return toString(getRoot());
	}
}