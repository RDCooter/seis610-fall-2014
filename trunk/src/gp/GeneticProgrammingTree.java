/**
 * 
 */
package gp;

import java.util.Random;


public class GeneticProgrammingTree extends BinaryTree {

	private Settings gpSettings=new Settings();
	
	public GeneticProgrammingTree() {
		
	}
	
	public GeneticProgrammingTree(BinaryTreeNode aNode) {
		root=aNode;
	}
	
	public GeneticProgrammingTree(Integer hgtOfTheTree) {
		Random generator = new Random();
		
		if (hgtOfTheTree==0) {
			Integer randomTerminalIndex=generator.nextInt(gpSettings.getOperands().size());
			//System.out.println("A Terminal: "+gpSettings.getOperands().elementAt(randomTerminalIndex));
			this.root.setData(gpSettings.getOperands().elementAt(randomTerminalIndex));
			this.root.setLeftChild(null);
			this.root.setRightChild(null);
		} else {
			Integer randomFunctionIndex=generator.nextInt(gpSettings.getOperators().size());
			//System.out.println("A Operator: "+gpSettings.getOperators().elementAt(randomFunctionIndex));
			this.root.setData(gpSettings.getOperators().elementAt(randomFunctionIndex));
			GeneticProgrammingTree leftSubTree=new GeneticProgrammingTree(hgtOfTheTree-1);
			GeneticProgrammingTree rightSubTree=new GeneticProgrammingTree(hgtOfTheTree-1);
			this.root.setLeftChild(leftSubTree.getRootNode());
			this.root.setRightChild(rightSubTree.getRootNode());
		}
		
		
	}

	public void mutate() {
		
		Integer aHeight = this.getHeight();
		BinaryTreeNode aNode=this.root;
		Random generator = new Random();
		Integer aRandomLevel=generator.nextInt(aHeight+1);
		
		for (int i=0; i<aRandomLevel; i++) {
			Integer randomChild=generator.nextInt(2);
			if ((randomChild==0) && (aNode.hasLeftChild())) {
				aNode=aNode.getLeftChild();
			} else if ((randomChild==1) && (aNode.hasRightChild())) {
				aNode=aNode.getRightChild();
			}
		}

		if (aNode.isLeaf()) {
			Integer randOperandPos=generator.nextInt(gpSettings.getOperands().size());
			aNode.setData(gpSettings.getOperands().elementAt(randOperandPos));
		} else {
			Integer randOperatorPos=generator.nextInt(gpSettings.getOperators().size());
			aNode.setData(gpSettings.getOperators().elementAt(randOperatorPos));
		}
		
	}
	
	public void crossOver(GeneticProgrammingTree aGPT) {
		
		Integer aHeight = this.getHeight();
		//Integer bHeight = aGPT.getHeight();
		Random generator = new Random();
		BinaryTreeNode aNode=this.root;
		BinaryTreeNode bNode=aGPT.root;
		
		Integer randomLevel=generator.nextInt(aHeight);
		
		for (int i=0; i<randomLevel; i++) {
			Integer randomChild=generator.nextInt(2);
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
			Integer randomChild=generator.nextInt(2);
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
		
		Integer aRandomChild=generator.nextInt(2);
		Integer bRandomChild=generator.nextInt(2);
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
