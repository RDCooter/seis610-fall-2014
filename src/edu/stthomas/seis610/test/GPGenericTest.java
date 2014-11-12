package edu.stthomas.seis610.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stthomas.seis610.gp.FitnessDatum;
import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.tree.BinaryTreeNode;
import edu.stthomas.seis610.tree.BinaryTreeNode.NodeType;
import edu.stthomas.seis610.tree.GPTreeFactory;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.tree.OperandNode;
import edu.stthomas.seis610.tree.OperatorNode;
import edu.stthomas.seis610.util.GPException;
import edu.stthomas.seis610.util.GPSimpleFormatter;

public class GPGenericTest {
	private static final Logger toLog = Logger.getLogger(GPGenericTest.class.getName());
	
	public static OperatorNode divide;
	public static OperatorNode minus;
	public static OperandNode six;
	public static OperandNode two;
	public static OperandNode four;
	public static GeneticProgrammingTree anyIndividual;
	public static GeneticProgrammingTree perfectIndividual;
	public static Vector<TrainingData> dftTrainingDataSet;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
 		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		toLog.addHandler(myHandler);
		toLog.setUseParentHandlers(false);

		dftTrainingDataSet = GPSettings.getTrainingData();
		
		divide = new OperatorNode("DIV");
		minus = new OperatorNode("SUB");
		six = new OperandNode("6");
		two = new OperandNode("2");
		four = new OperandNode("4");
		
		/*
		 * Expected Function: (6-2)/4
		 */
		divide.setNodeType(NodeType.ROOT);
		divide.setLeftChild(minus);
		minus.setNodeType(NodeType.LEFT);
		minus.setParent(divide);
		divide.setRightChild(four);
		four.setNodeType(NodeType.RIGHT);
		four.setParent(divide);
		minus.setLeftChild(six);
		six.setNodeType(NodeType.LEFT);
		six.setParent(minus);
		minus.setRightChild(two);
		two.setNodeType(NodeType.RIGHT);
		two.setParent(minus);
		anyIndividual = new GeneticProgrammingTree(divide);
		anyIndividual.setTrainingData(GPSettings.getTrainingData());
		
		/*
		 * Expected Function: ((x*x)-1)/2
		 */
		OperatorNode root = new OperatorNode("DIV");
		root.setNodeType(NodeType.ROOT);

		BinaryTreeNode secondLevel =  new OperatorNode("SUB");
		root.setLeftChild(secondLevel);
		root.getLeftChild().setNodeType(NodeType.LEFT);
		root.getLeftChild().setParent(root);
		root.setRightChild(new OperandNode("2"));
		root.getRightChild().setNodeType(NodeType.RIGHT);
		root.getRightChild().setParent(root);

		BinaryTreeNode thirdLevel =  new OperatorNode("MUL");
		secondLevel.setLeftChild(thirdLevel);
		secondLevel.getLeftChild().setNodeType(NodeType.LEFT);
		secondLevel.getLeftChild().setParent(secondLevel);
		secondLevel.setRightChild(new OperandNode("1"));
		secondLevel.getRightChild().setNodeType(NodeType.RIGHT);
		secondLevel.getRightChild().setParent(secondLevel);
		
		thirdLevel.setLeftChild(new OperandNode("x"));
		thirdLevel.getLeftChild().setNodeType(NodeType.LEFT);
		thirdLevel.getLeftChild().setParent(thirdLevel);
		thirdLevel.setRightChild(new OperandNode("X"));
		thirdLevel.getRightChild().setNodeType(NodeType.RIGHT);
		thirdLevel.getRightChild().setParent(thirdLevel);

		perfectIndividual = new GeneticProgrammingTree(root);
		perfectIndividual.setTrainingData(GPSettings.getTrainingData());
	}
	
	@Test
	public void testGetHeight() throws GPException {
		toLog.info("perfectIndividual[height=" + perfectIndividual.getHeight() + "]: " + perfectIndividual);
		assertEquals(perfectIndividual.getHeight(), new Integer(3));
	}
	
	@Test
	public void testHeight() {
		toLog.info("minus[height=" + minus.getHeight() + "]: " + new GeneticProgrammingTree(minus));
		assertEquals(minus.getHeight(), new Integer(1));
	}
	
	@Test
	public void testPostOrderList() throws GPException {
		List<BinaryTreeNode> postOrderList = anyIndividual.getPostOrderList();
		List<BinaryTreeNode> referenceList = new ArrayList<BinaryTreeNode>(5);
		referenceList.add(six);
		referenceList.add(two);
		referenceList.add(minus);
		referenceList.add(four);
		referenceList.add(divide);
		 
		toLog.info("anyIndividual[size=" + postOrderList.size() + "]: " + postOrderList.toString());
	    assertEquals(postOrderList, referenceList);
	}
	
//	@Test
//	public void testEvaluate() throws GPException {
//		double result = anyIndividual.evaluate(4.0, anyIndividual.getPostOrderList());
//		assertEquals(result, 2.0, 0);
//	}
	
	@Test
	public void testAnyIndividualFitness() throws GPException {
//		Vector<TrainingData> simpleTrainingDataSet = new Vector<TrainingData>();
//		simpleTrainingDataSet.add(new TrainingData(-5.0, TrainingData.calculatePerfectOutput(-5.0)));
		FitnessDatum expectedFitness = new FitnessDatum(45.5); 
		anyIndividual.calculateFitness();
		toLog.info("anyIndividual.getFitness: " + anyIndividual.getFitness() + "  [Compare: " + expectedFitness + "]");
		assertEquals("anyIndividual", expectedFitness, anyIndividual.getFitness());
	}
	
	@Test
	public void testPerfectFitness() throws GPException {
		FitnessDatum expectedFitness = new FitnessDatum(0.0); 
		perfectIndividual.getFitness().clear();
		for (TrainingData trainingDatum : dftTrainingDataSet) {
			perfectIndividual.evaluate(trainingDatum);
		}
		toLog.info("perfectIndividual.getFitness: " + perfectIndividual.getFitness() + "  [Compare: " + expectedFitness + "]");
		assertEquals("perfectIndividual", expectedFitness, perfectIndividual.getFitness());
	}
	
	@Test
	public void testReadProperty() {
		GPSettings.setPopulationSize(100);
		int populationSize = GPSettings.getPopulationSize();
		assertEquals(populationSize, 100);
	}
	
	@Test
	public void testPrintTree() {
		String perfectTree = new String("((x*x)-1)/2");
		toLog.info("perfectIndividual.toString[height=" + perfectIndividual.getHeight() + "]: " + perfectIndividual + "  [Compare: " + perfectTree + "]");
		assertEquals("perfectIndividual", perfectTree, perfectIndividual.toString());

		String anyIndividualTree = new String("(6-2)/4");
		toLog.info("anyIndividual.toString[height=" + anyIndividual.getHeight() + "]: " + anyIndividual + "  [Compare: " + anyIndividualTree + "]");
		assertEquals("anyIndividual", anyIndividualTree, anyIndividual.toString());
	}
	
	@Test
	public void testDeepCopy() {
		GeneticProgrammingTree copy = (GeneticProgrammingTree) anyIndividual.clone();
		toLog.info("anyIndividual[height=" + anyIndividual.getHeight() + "]: " + anyIndividual);
		toLog.info("copyAnyIndividual[height=" + copy.getHeight() + "]: " + copy);
		assertNotSame(anyIndividual, copy);
		assertNotSame(anyIndividual.getRoot(), copy.getRoot());
		assertNotSame(anyIndividual.getRoot().getLeftChild(), copy.getRoot().getLeftChild());
		assertNotSame(anyIndividual.getRoot().getRightChild(), copy.getRoot().getRightChild());
		
		// Test an OperatorNode
		assertEquals(anyIndividual.getRoot().getData(), copy.getRoot().getData());
		assertNotSame(anyIndividual.getRoot().getData(), copy.getRoot().getData());
	
		// Test an OperandNode
		assertEquals(anyIndividual.getRoot().getRightChild().getData(), copy.getRoot().getRightChild().getData());
		assertNotSame(anyIndividual.getRoot().getRightChild().getData(), copy.getRoot().getRightChild().getData());
	}
	
	@Test
	public void testFullTree() throws GPException {
		Integer maxHeight = GPSettings.getMaxHtOfInitTree();
		GeneticProgrammingTree fullIndividual = GPTreeFactory.generateFullTree();
		toLog.info("fullIndividual[height=" + fullIndividual.getHeight() + "]: " + fullIndividual);
		assertEquals(fullIndividual.getHeight(), maxHeight);
	}
	
	@Test
	public void testFullSubtree() throws GPException {
		Integer maxHeight = GPSettings.getMaxHtOfCrossoverTree();
		GeneticProgrammingTree fullIndividual = new GeneticProgrammingTree(GPTreeFactory.generateFullSubtree(maxHeight));
		toLog.info("fullIndividual[height=" + fullIndividual.getHeight() + "]: " + fullIndividual);
		assertEquals(fullIndividual.getHeight(), maxHeight);
	}
	
	@Test
	public void testGrowTree() throws GPException {
		Integer maxHeight = GPSettings.getMaxHtOfInitTree();
		GeneticProgrammingTree growIndividual = GPTreeFactory.generateGrowTree();
		toLog.info("growIndividual[height=" + growIndividual.getHeight() + "]: " + growIndividual);
		assertTrue(growIndividual.getHeight() <= maxHeight);
	}
	
	@Test
	public void testGrowSubtree() throws GPException {
		Integer maxHeight = GPSettings.getMaxHtOfCrossoverTree();
		GeneticProgrammingTree growIndividual = new GeneticProgrammingTree(GPTreeFactory.generateGrowSubtree(maxHeight));
		toLog.info("growIndividual[height=" + growIndividual.getHeight() + "]: " + growIndividual);
		assertTrue(growIndividual.getHeight() <= maxHeight);
	}
	
}
