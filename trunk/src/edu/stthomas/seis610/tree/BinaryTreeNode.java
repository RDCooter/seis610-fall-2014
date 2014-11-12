package edu.stthomas.seis610.tree;

import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * The Binary Tree Node class defines the basic structure of a node within binary tree and the necessary methods to
 * operate upon it.
 * 
 * @author Pravesh Tamraker Oct 19, 2014 4:11:18 PM
 * @author Robert Driesch (cooter) Nov 7, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public abstract class BinaryTreeNode implements Cloneable {

	/**
	 * Define Enumeration to Describe the Different Types of Nodes in the Tree
	 */
	public enum NodeType {
		ROOT, LEFT, RIGHT
	};

	protected Object xData;
	protected BinaryTreeNode xLeftChild;
	protected BinaryTreeNode xRightChild;
	protected BinaryTreeNode xParent;
	protected boolean xValidTreeNode;
	protected NodeType xNodeType;

	/**
	 * Default constructor for this class.
	 */
	public BinaryTreeNode() {
		xData = null;
		xLeftChild = null;
		xRightChild = null;
		xParent = null;
		xValidTreeNode = true; // All nodes start out valid.
	}

	/**
	 * @returns the generic data object associated with this node
	 */
	public Object getData() {
		return xData;
	}

	/**
	 * @param aData the generic data object to assign to this node
	 */
	public void setData(Object aData) {
		this.xData = aData;
	}

	/**
	 * @returns the left child (or null) associated with this node
	 */
	public BinaryTreeNode getLeftChild() {
		return xLeftChild;
	}

	/**
	 * @param aChild the left child associated with this node
	 * @throws GPException
	 */
	public void setLeftChild(BinaryTreeNode aChild) throws GPException {
		this.xLeftChild = aChild;
	}

	/**
	 * @returns the right child (or null) associated with this node
	 */
	public BinaryTreeNode getRightChild() {
		return xRightChild;
	}

	/**
	 * @param aChild the right child associated with this node
	 * @throws GPException
	 */
	public void setRightChild(BinaryTreeNode aChild) throws GPException {
		this.xRightChild = aChild;
	}

	/**
	 * @returns the indicator if the expressions within the node tree are valid
	 */
	public boolean isTreeNodeValid() {
		return xValidTreeNode;
	}

	/**
	 * Recursive method to set the invalid tree indicator upstream through the tree via the parent nodes until you reach
	 * the root. All of these nodes are either themselves invalid or dependent upon an invalid result for their
	 * expression evaluations.
	 */
	protected void setTreeNodeInvalid() {
		this.xValidTreeNode = false;
		// Make sure that the invalid indicator gets "walked" up the tree through the parents until we get to the
		// root node so that it is quickly visible to the rest of the GP processing.
		if (hasParent()) {
			xParent.setTreeNodeInvalid();
		}
	}

	/**
	 * Recursive method to reset the cached TreeNodeInvalid indicator. This method should only be called when the tree
	 * has been modified because of mutation, cross-over, etc...
	 */
	public void resetTreeNodeInvalid() {
		this.xValidTreeNode = true;
		if (hasLeftChild()) {
			getLeftChild().resetTreeNodeInvalid();
		}
		if (hasRightChild()) {
			getRightChild().resetTreeNodeInvalid();
		}
	}

	/**
	 * @returns true if a left child exists for this node
	 */
	public boolean hasLeftChild() {
		return (xLeftChild != null) ? true : false;
	}

	/**
	 * @returns true if a right child exists for this node
	 */
	public boolean hasRightChild() {
		return (xRightChild != null) ? true : false;
	}

	/**
	 * @returns true if the parent of this node exists
	 */
	public boolean hasParent() {
		return (xParent != null) ? true : false;
	}

	/**
	 * @param aObject the other node object to compare against
	 * @returns true if this node object matches another node object
	 */
	@Override
	public boolean equals(Object aObject) {
		return equals((BinaryTreeNode) aObject);
	}

	/**
	 * @param aNode the other node to compare against
	 * @returns true if the contents of the data in this node matches another node
	 */
	public boolean equals(BinaryTreeNode aNode) {
		return this.getData().equals(aNode.getData());
	}

	/**
	 * @returns the parent of this node
	 */
	public BinaryTreeNode getParent() {
		return xParent;
	}

	/**
	 * @param aNode the node to assign as this nodes parent
	 */
	public void setParent(BinaryTreeNode aNode) {
		this.xParent = aNode;
	}

	/**
	 * @returns the node type within the tree that represent this node
	 */
	public NodeType getNodeType() {
		return xNodeType;
	}

	/**
	 * @param aNodeType the type this node represents within the tree
	 */
	public void setNodeType(NodeType aNodeType) {
		this.xNodeType = aNodeType;
	}

	@Override
	public Object clone() {
		BinaryTreeNode newNode = null;

		try {
			newNode = (BinaryTreeNode) super.clone();
			if (newNode instanceof OperatorNode) {
				newNode.setData(new String((String) newNode.getData()));				
			}
			else if (!((OperandNode)newNode).isVariable()) {
				newNode.setData(new Double((Double) newNode.getData()));
			}
			if (hasLeftChild()) {
				newNode.setLeftChild((BinaryTreeNode) getLeftChild().clone());
				newNode.getLeftChild().setParent(newNode);
			}
			if (hasRightChild()) {
				newNode.setRightChild((BinaryTreeNode) getRightChild().clone());
				newNode.getRightChild().setParent(newNode);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (GPException e) {
			e.printStackTrace();
		}
		return newNode;
	}

	/**
	 * Recursively evaluate the expression represented by this node and return the result of that expression back up the
	 * tree. Make sure to protect against an invalid division (divide by zero) and set the indicator that the tree will
	 * not produce a valid result.
	 * 
	 * @param aTrainingDatum the input training value (X) to use during the evaluation of the function
	 * @returns the evaluated result of the expression based upon the the type of operator in this node
	 * @throws GPException
	 */
	abstract public Double evaluateOutput(TrainingData aTrainingDatum) throws GPException;

	/**
	 * @returns an integer count of the height of the subtree
	 */
	abstract public Integer getHeight();

	/**
	 * @returns an integer count of the number of children this node contains
	 */
	abstract public Integer getNumberOfChildren();
}
