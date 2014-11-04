/**
 * 
 */
package gp;


public class BinaryTree {

	protected BinaryTreeNode root=new BinaryTreeNode();
	
	
	
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
	
		return this.root.evaluateOutput(inputValue);
		
	}

	
}

