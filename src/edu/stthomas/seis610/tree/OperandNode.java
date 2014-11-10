package edu.stthomas.seis610.tree;

import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * A tree node that represents the terminal operands within the tree. This operand will either be a variable represented
 * in the function or a constant value.
 * 
 * @author Robert Driesch (cooter) Nov 7, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class OperandNode extends BinaryTreeNode {
	private final static String _DEFAULT_VARIABLE = new String("x");
	private boolean xVariable;

	/**
	 * Constructor to create a new operand node with either a variable or constant value.
	 * 
	 * @param aOperand the operand (variable or constant value) to assign to this node
	 */
	public OperandNode(String aOperand) {
		super();
		setOperand(aOperand);
	}

	/**
	 * Constructor to create a new constant operand node.
	 * 
	 * @param aOperand the constant value to assign to this node
	 */
	public OperandNode(Double aOperand) {
		super();
		setData(aOperand);
	}

	/**
	 * @param aOperand the operand (variable or constant value) to assign to this node
	 */
	public void setOperand(String aOperand) {
		// TODO throw exception if invalid operand is passed in
		if (aOperand.equalsIgnoreCase(_DEFAULT_VARIABLE)) {
			setVariable(true);
		} else {
			setData(new Double(Double.parseDouble(aOperand)));
		}
	}

	/**
	 * @param aChild the left child associated with this node
	 * @throws GPException
	 */
	public void setLeftChild(BinaryTreeNode aChild) throws GPException {
		throw new GPException("Operands cannot have (left) subnodes.");
	}

	/**
	 * @param aChild the right child associated with this node
	 * @throws GPException
	 */
	public void setRightChild(BinaryTreeNode aChild) throws GPException {
		throw new GPException("Operands cannot have (right) subnodes.");
	}

	/**
	 * @returns an integer count of the height of the subtree
	 */
	public Integer getHeight() {
		return 0;
	}

	/**
	 * @returns an integer count of the number of children this node contains
	 */
	public Integer getNumberOfChildren() {
		return 0;
	}

	/**
	 * @returns a boolean indicator if this is a variable for the expression
	 */
	public boolean isVariable() {
		return xVariable;
	}

	/**
	 * @param aVariable indicator if this is a variable for the expression
	 */
	public void setVariable(boolean aVariable) {
		this.xVariable = aVariable;
	}

	/**
	 * @param aData the generic data object to assign to this node
	 */
	@Override
	public void setData(Object data) {
		super.setData(data);
		setVariable(false);
	}

	/**
	 * Recursively evaluate the expression represented by this node and return the result of that expression back up the
	 * tree. Make sure to protect against an invalid division (divide by zero) and set the indicator that the tree will
	 * not produce a valid result.
	 * 
	 * @returns the terminal value (variable or constant) associated with this operand node
	 * @throws GPException
	 */
	public Double evaluateOutput(Double inputValue) throws GPException {
		if (isVariable()) {
			return inputValue;
		} else {
			return (Double) getData();
		}
	}

	@Override
	public String toString() {
		if (isVariable()) {
			return "x";
		} else {
			Double value = (Double) getData();
			int operandInt = value.intValue();
			return String.valueOf(operandInt);
		}
	}
}