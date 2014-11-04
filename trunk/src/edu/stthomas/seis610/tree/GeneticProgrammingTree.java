/**
 * 
 */
package edu.stthomas.seis610.tree;

import edu.stthomas.seis610.gp.GPSettings;



public class GeneticProgrammingTree extends BinaryTree {

	public GeneticProgrammingTree() {
		
	}
	
	public GeneticProgrammingTree(BinaryTreeNode aNode) {
		root=aNode;
	}
	
	public GeneticProgrammingTree(Integer hgtOfTheTree) {
		
		if (hgtOfTheTree==0) {
			Integer randomTerminalIndex=GPSettings.getRandomInt(GPSettings.getOperands().size());
			//System.out.println("A Terminal: "+GPSettings.getOperands().elementAt(randomTerminalIndex));
			this.root.setData(GPSettings.getOperands().elementAt(randomTerminalIndex));
			this.root.setLeftChild(null);
			this.root.setRightChild(null);
		} else {
			Integer randomFunctionIndex=GPSettings.getRandomInt(GPSettings.getOperators().size());
			//System.out.println("A Operator: "+GPSettings.getOperators().elementAt(randomFunctionIndex));
			this.root.setData(GPSettings.getOperators().elementAt(randomFunctionIndex));
			GeneticProgrammingTree leftSubTree=new GeneticProgrammingTree(hgtOfTheTree-1);
			GeneticProgrammingTree rightSubTree=new GeneticProgrammingTree(hgtOfTheTree-1);
			this.root.setLeftChild(leftSubTree.getRootNode());
			this.root.setRightChild(rightSubTree.getRootNode());
		}
		
		
	}

	public void mutate() {
		
		Integer aHeight = this.getHeight();
		BinaryTreeNode aNode=this.root;
		Integer aRandomLevel=GPSettings.getRandomInt(aHeight+1);
		
		for (int i=0; i<aRandomLevel; i++) {
			Integer randomChild=GPSettings.getRandomInt(2);
			if ((randomChild==0) && (aNode.hasLeftChild())) {
				aNode=aNode.getLeftChild();
			} else if ((randomChild==1) && (aNode.hasRightChild())) {
				aNode=aNode.getRightChild();
			}
		}

		if (aNode.isLeaf()) {
			Integer randOperandPos=GPSettings.getRandomInt(GPSettings.getOperands().size());
			aNode.setData(GPSettings.getOperands().elementAt(randOperandPos));
		} else {
			Integer randOperatorPos=GPSettings.getRandomInt(GPSettings.getOperators().size());
			aNode.setData(GPSettings.getOperators().elementAt(randOperatorPos));
		}
		
	}
	
	public void crossOver(GeneticProgrammingTree aGPT) {
		
		Integer aHeight = this.getHeight();
		//Integer bHeight = aGPT.getHeight();
		BinaryTreeNode aNode=this.root;
		BinaryTreeNode bNode=aGPT.root;
		
		Integer randomLevel=GPSettings.getRandomInt(aHeight);
		
		for (int i=0; i<randomLevel; i++) {
			Integer randomChild=GPSettings.getRandomInt(2);
			if ((randomChild==0) && (aNode.hasLeftChild())) {
				if (!aNode.getLeftChild().isLeaf()) {
					aNode=aNode.getLeftChild();
				}
			} else if ((randomChild==1) && (aNode.hasRightChild())) {
				if (!aNode.getRightChild().isLeaf()) {
					aNode=aNode.getRightChild();
				}
			}
		}
		
		for (int i=0; i<randomLevel; i++) {
			Integer randomChild=GPSettings.getRandomInt(2);
			if ((randomChild==0) && (bNode.hasLeftChild())) {
				if (!bNode.getLeftChild().isLeaf()) {
					bNode=bNode.getLeftChild();
				}
			} else if ((randomChild==1) && (bNode.hasRightChild())) {
				if (!bNode.getRightChild().isLeaf()) {
					bNode=bNode.getRightChild();
				}
			}
		}
		
		Integer aRandomChild=GPSettings.getRandomInt(2);
		Integer bRandomChild=GPSettings.getRandomInt(2);
		if ((aRandomChild==0) && (bRandomChild==0)) {
			BinaryTreeNode tempNode=aNode.getLeftChild();
			aNode.setLeftChild(bNode.getLeftChild());
			bNode.setLeftChild(tempNode);
		} else if ((aRandomChild==0) && (bRandomChild==1)) {
			BinaryTreeNode tempNode=aNode.getLeftChild();
			aNode.setLeftChild(bNode.getRightChild());
			bNode.setRightChild(tempNode);
		} else if ((aRandomChild==1) && (bRandomChild==0)) {
			BinaryTreeNode tempNode=aNode.getRightChild();
			aNode.setRightChild(bNode.getLeftChild());
			bNode.setLeftChild(tempNode);
		} else if ((aRandomChild==1) && (bRandomChild==1)) {
			BinaryTreeNode tempNode=aNode.getRightChild();
			aNode.setRightChild(bNode.getRightChild());
			bNode.setRightChild(tempNode);
		}
		
	}
	
}
