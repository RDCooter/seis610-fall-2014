/**
 * 
 */
package edu.stthomas.seis610.tree;


public class BinaryTree {

	protected BinaryTreeNode root=new BinaryTreeNode();
	private static Boolean isvalidbtree;
	
	
	public static Boolean getIsvalidbtree() {
		return isvalidbtree;
	}

	public static void setIsvalidbtree(Boolean isvalidbtree1) {
		isvalidbtree = isvalidbtree1;
	}

	// ESCA-JAVA0117:
	public void setTree(BinaryTree aTree) {
		
		this.root = aTree.root;
		
	}

	public void buildTree(BinaryTreeNode aNode) {
		
		if (aNode.hasLeftChild()) {
			BinaryTreeNode leftNode=new BinaryTreeNode(aNode.getLeftChild());
			this.buildTree(leftNode);
		}
		
		
	}
	
	public boolean isEmpty() {
		
		return (root == null);
		
	}
	
	public void setRootData(String aData) {
		
		this.root.setData(aData);
		
	}
	
	public void setRootNode(BinaryTreeNode rootNode) {
		
		this.root = rootNode;
		
	}
	
	public BinaryTreeNode getRootNode() {
		
		return this.root;
		
	}
	
	public Integer getNumberOfNodes() {
		
		return root.getNumberOfNodes();
		
	}
	
	public Integer getHeight() {
		
		return root.getHeight();
		
	}
	
	public Double evaluate(Double inputValue) {
		double i=this.root.evaluateOutput(inputValue);
		isvalidbtree=BinaryTreeNode.isIsvalidtreenode();
		return i;
		
	}

	
}

