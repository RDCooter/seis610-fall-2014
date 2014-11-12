package edu.stthomas.seis610.tree;

import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * A tree node that represents the operator within the tree. This operator can be as the name implies a binary operator
 * with both a left and right child or a unary operator with only a left child defined and set.
 * 
 * @author Robert Driesch (cooter) Nov 7, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class OperatorNode extends BinaryTreeNode {

	/**
	 * Define Enumeration to Describe the Different Operators of the Tree
	 */
	public enum OperatorType {
		ADD, SUB, MUL, DIV, SIN, COS, POW;

		@Override
		public String toString() {
			switch (this) {
			case ADD:
				return "+";
			case SUB:
				return "-";
			case MUL:
				return "*";
			case DIV:
				return "/";
			case SIN:
				return "Sine";
			case COS:
				return "Cosine";
			case POW:
				return "Exponent";
			}
			return "<InvalidOperatorType>";
		};
	};

	/**
	 * Constructor to create a new operator node with the specified operator.
	 * 
	 * @param aOperand the operand (variable or constant value) to assign to this node
	 */
	public OperatorNode(String aOperator) {
		super();
		setOperator(OperatorType.valueOf(aOperator));
	}

	/**
	 * Constructor to create a new operator node with the specified operator.
	 * 
	 * @param aOperator the operator type to assign to this node
	 */
	public OperatorNode(OperatorType aOperator) {
		super();
		setOperator(aOperator);
	}

	/**
	 * @returns an integer count of the height of the subtree
	 */
	public Integer getHeight() {
		// Should always have a left child no matter what type of operator, but a right child may not always exist.
		return (1 + Math.max(getLeftChild().getHeight().intValue(), (hasRightChild() ? getRightChild().getHeight().intValue() : 0)));
	}

	/**
	 * @returns an integer count of the number of children this node contains
	 */
	public Integer getNumberOfChildren() {
		// Should always have a left child no matter what type of operator, but a right child may not always exist.
		switch (getOperator()) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case POW:
			return 2;
		case SIN:
		case COS:
		default:
			return 1;
		}
	}

	/**
	 * @returns the operator type (enum) associated with this node
	 */
	public OperatorType getOperator() {
		return OperatorType.valueOf((String) getData());
	}

	/**
	 * @param aOperator the operator type to assign to this node
	 */
	public void setOperator(OperatorType aOperator) {
		// TODO throw exception if an invalid operator is passed in
		setData(aOperator.name());
	}

	/**
	 * Recursively evaluate the expression represented by this node and return the result of that expression back up the
	 * tree. Make sure to protect against an invalid division (divide by zero) and set the indicator that the tree will
	 * not produce a valid result.
	 * 
	 * In the case of an invalid tree, in addition to setting the invalid indicator simply return a large (max) value
	 * back to the caller.
	 * 
	 * @param aTrainingDatum the input training value (X) to use during the evaluation of the function
	 * @returns the evaluated result of the expression based upon the the type of operator in this node
	 * @throws GPException
	 */
	public Double evaluateOutput(TrainingData aTrainingDatum) throws GPException {
		Double output = Double.MAX_VALUE;
		if (isTreeNodeValid()) {
			switch (getOperator()) {
			case ADD:
				output = getLeftChild().evaluateOutput(aTrainingDatum) + getRightChild().evaluateOutput(aTrainingDatum);
				break;
			case SUB:
				output = getLeftChild().evaluateOutput(aTrainingDatum) - getRightChild().evaluateOutput(aTrainingDatum);
				break;
			case MUL:
				output = getLeftChild().evaluateOutput(aTrainingDatum) * getRightChild().evaluateOutput(aTrainingDatum);
				break;
			case DIV:
				Double rightTreeValue = getRightChild().evaluateOutput(aTrainingDatum);
				if (rightTreeValue != 0) {
					output = getLeftChild().evaluateOutput(aTrainingDatum) / rightTreeValue;
				} else {
					setTreeNodeInvalid();
				}
				break;
			case SIN:
				output = Math.sin(getLeftChild().evaluateOutput(aTrainingDatum));
				setTreeNodeInvalid();
				break;
			case COS:
				output = Math.cos(getLeftChild().evaluateOutput(aTrainingDatum));
				setTreeNodeInvalid();
				break;
			case POW:
				output = Math.pow(getLeftChild().evaluateOutput(aTrainingDatum),
						Math.min(getRightChild().evaluateOutput(aTrainingDatum), 20));
				setTreeNodeInvalid();
				break;
			default:
				throw new GPException("Invalid operator encountered during evaluation: " + getOperator());
			}
		} else {
			// Make sure that the invalid indicator gets "walked" up the tree through the parents until we get to the
			// root node so that it is quickly visible to the rest of the GP processing.
			setTreeNodeInvalid();
		}
		return output;
	}

	@Override
	public String toString() {
		return getOperator().toString();
	}
}
