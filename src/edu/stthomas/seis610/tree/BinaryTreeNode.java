/**
 * 
 */
package edu.stthomas.seis610.tree;



public class BinaryTreeNode {
	private BinaryTreeNode leftChild;
	private BinaryTreeNode rightChild;
	private String data;
	private static boolean isvalidtreenode;

	public static boolean isIsvalidtreenode() {
		return isvalidtreenode;
	}

	public static void setIsvalidtreenode(boolean isvalidtreenode) {
		BinaryTreeNode.isvalidtreenode = isvalidtreenode;
	}

	public BinaryTreeNode() {

	}
	
	public BinaryTreeNode(BinaryTreeNode newNode) {
		this.leftChild = newNode.leftChild;
		this.rightChild = newNode.rightChild;
		this.data = newNode.data;
	}
	
	public String getData() {
		
		return data;
		
	}
	
	public void setData(String inData) {
		
		data = inData;
		
	}
	
	public BinaryTreeNode getLeftChild() {
		
		return leftChild;
		
	}
	
	public BinaryTreeNode getRightChild() {
		
		return rightChild;
		
	}
	
	public void setLeftChild(BinaryTreeNode l_node) {
		
		leftChild = l_node;
	}
	
	public void setRightChild(BinaryTreeNode r_node) {
		
		rightChild = r_node;
	}
	
	public boolean hasLeftChild() {
		if (leftChild != null)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean hasRightChild() {
		if (rightChild != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void printInOrder() {
	
		if (this == null) {
			System.out.println("null");
			return;
		}
		
		if (leftChild != null)
		{
			System.out.print("(");
			leftChild.printInOrder();
		}
		System.out.print(data);
		if (rightChild != null)
		{
			rightChild.printInOrder();
			System.out.print(")");
		}
	
	}
	
	public void printPostOrder() {
		
		if (leftChild != null)
		{
			leftChild.printInOrder();
		}
		if (rightChild != null)
		{
			rightChild.printInOrder();
		}
		System.out.println(data);
	
	}
	
	public boolean isLeaf() {
		
		return ((leftChild == null) && (rightChild == null));
		
	}
	
	public Integer getHeight() {
	
		if (this.isLeaf()) {
			return 0;
		}
	
		return (1 + Math.max(leftChild.getHeight(), rightChild.getHeight()));
	
	}
	
	public Integer getNumberOfNodes() {
		
		if (this.isLeaf()) {
			return 1;
		}
		
		return (1 + leftChild.getNumberOfNodes() + rightChild.getNumberOfNodes());
	}
	
	public BinaryTreeNode treeCopy() {
		BinaryTreeNode newNode = new BinaryTreeNode(this);
		
		if (newNode.hasLeftChild()) {
			newNode.setLeftChild(newNode.getLeftChild().treeCopy());
		}
		if (newNode.hasRightChild()) {
			newNode.setRightChild(newNode.getRightChild().treeCopy());
		}
		
		return newNode;
	
	}
	
	public Double evaluateOutput(Double inputValue) {
		
		String add = "+";
		String subtract = "-";
		String multiply = "*";
		String divide = "/";
		String vOperand = "x";
		Double leftTreeValue;
		Double rightTreeValue;
	
		
		if (this == null) {
			return 0.0;
		}
		
		if (this.hasLeftChild()) {
			leftTreeValue=this.getLeftChild().evaluateOutput(inputValue);
		} else {
			leftTreeValue=0.0;
		}
		if (this.hasRightChild()) {
			rightTreeValue=this.getRightChild().evaluateOutput(inputValue);
		} else {
			rightTreeValue=0.0;
		}
	
		if (this.isLeaf()) {
			
			if (this.getData().equals(vOperand)) {
				return inputValue;
			}
			else {
				return Double.valueOf(this.getData());
			}
		}
		
		if (this.getData().equals(add)) {
			return leftTreeValue+rightTreeValue;
		} 
		
		if (this.getData().equals(subtract)) {
			return leftTreeValue-rightTreeValue;
		} 
		if (this.getData().equals(multiply)) {
			return leftTreeValue*rightTreeValue;
		} 
		if (this.getData().equals(divide) && rightTreeValue!=0) {
			return leftTreeValue/rightTreeValue;
		} else {
			BinaryTreeNode.isvalidtreenode=false;
			return 0.0;
		}
	
		
	}

}
