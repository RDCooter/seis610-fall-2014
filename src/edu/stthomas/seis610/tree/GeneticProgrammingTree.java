package edu.stthomas.seis610.tree;

import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.tree.BinaryTreeNode.NodeType;
import edu.stthomas.seis610.util.GPException;

public class GeneticProgrammingTree extends BinaryTree {

	public GeneticProgrammingTree() {

	}

	public GeneticProgrammingTree(BinaryTreeNode aNode) {
		setRoot(aNode);
	}

	public void mutate() {

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

	public void crossOver(GeneticProgrammingTree aGPT) {

		Integer aHeight = this.getHeight();
		// Integer bHeight = aGPT.getHeight();
		BinaryTreeNode aNode = this.getRoot();
		BinaryTreeNode bNode = aGPT.getRoot();

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

}
