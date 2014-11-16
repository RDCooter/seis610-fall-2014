package edu.stthomas.seis610.test;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.stthomas.seis610.gp.FitnessDatum;
import edu.stthomas.seis610.gp.GPGeneration;
import edu.stthomas.seis610.gp.GPGeneration.GenerationMethod;
import edu.stthomas.seis610.gp.GPGeneration.ReproductionMethod;
import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.util.GPSimpleFormatter;

public class GenerationTest {
	private static final Logger toLog = Logger.getLogger("Global");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		toLog.addHandler(myHandler);
		toLog.setUseParentHandlers(false);

		/*
		 * Set the Global Settings so the results are Predictable
		 */
		GPSettings.setPopulationSize(3);
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setTrainingInputString("-5,-1,0,1,5");
	}

	@Before
	public void initialize() throws Exception {
		/*
		 * Resetting the Random Seed before each test allows us a way to predict the behavior of the random number
		 * generator and generate very predictable expression trees regardless of which order the tests are ran.
		 */
		GPSettings.setRandomSeed("12345");
		GPSettings.setPopulationSize(3);
	}

	@Test
	public void testGPGeneration() {
		Integer expectedValue = new Integer(0);
		GPGeneration newGeneration = new GPGeneration();

		toLog.info("newGeneration[size=" + newGeneration.getPopulation().size() + "]: \n" + newGeneration);
		toLog.info("GPGeneration Constructor: Size=" + newGeneration.getPopulation().size() + "  [Compare="
				+ expectedValue + "]");
		assertEquals("Constructor_Size=", expectedValue.intValue(), newGeneration.getPopulation().size());
	}

	@Test
	public void testInit_Full() {
		final Integer treeHeight = new Integer(3);
		GPSettings.setMaxHtOfInitTree(treeHeight);
		GPSettings.setGenerationMethod(GenerationMethod.FULL);
		String testLabel = GPSettings.getGenerationMethod().toLowerCase() + "Generation";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: \n" + testGeneration);

		/*
		 * Test for the First Tree in the Population:
		 */
		String treeDump = new String("((-1/6)*(6+-7))-((8+-7)+(-3-3))");
		toLog.info(testLabel + "[0]: Height=" + testGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: isValid=" + testGeneration.getPopulation().firstElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[0]: \"" + testGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, testGeneration.getPopulation().firstElement()
				.getHeight());
		assertEquals((testLabel + "[0]_isValid: "), true, testGeneration.getPopulation().firstElement().isTreeValid());
		assertEquals((testLabel + "[0]: "), treeDump, testGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("((3/6)+(2/3))+((-8/-4)*(2*x))");
		toLog.info(testLabel + "[1]: Height=" + testGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: isValid=" + testGeneration.getPopulation().elementAt(1).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[1]: \"" + testGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_isValid: "), true, testGeneration.getPopulation().elementAt(1).isTreeValid());
		assertEquals((testLabel + "[1]: "), treeDump, testGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("((-4/-6)*(3/2))*((7--7)*(9/9))");
		toLog.info(testLabel + "[2]: Height=" + testGeneration.getPopulation().lastElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[2]: isValid=" + testGeneration.getPopulation().lastElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[2]: \"" + testGeneration.getPopulation().lastElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, testGeneration.getPopulation().lastElement().getHeight());
		assertEquals((testLabel + "[2]_isValid: "), true, testGeneration.getPopulation().lastElement().isTreeValid());
		assertEquals((testLabel + "[2]: "), treeDump, testGeneration.getPopulation().lastElement().toString());

		/*
		 * Test for the Best Individual in this Population
		 */
		FitnessDatum bestFitness = new FitnessDatum(Double.parseDouble("29.666666666666668"));
		treeDump = new String("((-1/6)*(6+-7))-((8+-7)+(-3-3))");
		toLog.info(testLabel + ": BestIndividual=" + testGeneration.getBestIndividual().getFitness() + "  [Compare="
				+ bestFitness + "]");
		toLog.info(testLabel + ": BestIndividual=\"" + testGeneration.getBestIndividual() + "\"  [Compare=\""
				+ treeDump + "\"]\n");
		assertEquals((testLabel + "_Fitness: "), bestFitness, testGeneration.getBestIndividual().getFitness());
		assertEquals((testLabel + "_Fitness: "), treeDump, testGeneration.getBestIndividual().toString());
	}

	@Test
	public void testInit_Grow() {
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setGenerationMethod(GenerationMethod.GROW);
		String testLabel = GPSettings.getGenerationMethod().toLowerCase() + "Generation";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: \n" + testGeneration);

		/*
		 * Test for the First Tree in the Population:
		 */
		String treeDump = new String("(-7+0)/-7");
		Integer treeHeight = new Integer(2);
		toLog.info(testLabel + "[0]: Height=" + testGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: isValid=" + testGeneration.getPopulation().firstElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[0]: \"" + testGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, testGeneration.getPopulation().firstElement()
				.getHeight());
		assertEquals((testLabel + "[0]_isValid: "), true, testGeneration.getPopulation().firstElement().isTreeValid());
		assertEquals((testLabel + "[0]: "), treeDump, testGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("-6+((7+(-4--6))-x)");
		treeHeight = new Integer(4);
		toLog.info(testLabel + "[1]: Height=" + testGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: isValid=" + testGeneration.getPopulation().elementAt(1).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[1]: \"" + testGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_isValid: "), true, testGeneration.getPopulation().elementAt(1).isTreeValid());
		assertEquals((testLabel + "[1]: "), treeDump, testGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("(6-2)*(-7-(-9+2))");
		treeHeight = new Integer(3);
		toLog.info(testLabel + "[2]: Height=" + testGeneration.getPopulation().lastElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[2]: isValid=" + testGeneration.getPopulation().lastElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[2]: \"" + testGeneration.getPopulation().lastElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, testGeneration.getPopulation().lastElement().getHeight());
		assertEquals((testLabel + "[2]_isValid: "), true, testGeneration.getPopulation().lastElement().isTreeValid());
		assertEquals((testLabel + "[2]: "), treeDump, testGeneration.getPopulation().lastElement().toString());

		/*
		 * Test for the Best Individual in this Population
		 */
		FitnessDatum bestFitness = new FitnessDatum(Double.parseDouble("24.5"));
		treeDump = new String("(6-2)*(-7-(-9+2))");
		toLog.info(testLabel + ": BestIndividual=" + testGeneration.getBestIndividual().getFitness() + "  [Compare="
				+ bestFitness + "]");
		toLog.info(testLabel + ": BestIndividual=\"" + testGeneration.getBestIndividual() + "\"  [Compare=\""
				+ treeDump + "\"]\n");
		assertEquals((testLabel + "_Fitness: "), bestFitness, testGeneration.getBestIndividual().getFitness());
		assertEquals((testLabel + "_Fitness: "), treeDump, testGeneration.getBestIndividual().toString());
	}

	@Test
	public void testInit_RampedFull() {
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setGenerationMethod(GenerationMethod.RAMPED_FULL);
		String testLabel = GPSettings.getGenerationMethod().toLowerCase() + "Generation";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: RequestedSize="
				+ GPSettings.getPopulationSize() + "\n" + testGeneration);

		/*
		 * Test for the First Tree in the Population:
		 */
		String treeDump = new String("(-8*-1)-(-5/6)");
		Integer treeHeight = new Integer(2);
		toLog.info(testLabel + "[0]: Height=" + testGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: isValid=" + testGeneration.getPopulation().firstElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[0]: \"" + testGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, testGeneration.getPopulation().firstElement()
				.getHeight());
		assertEquals((testLabel + "[0]_isValid: "), true, testGeneration.getPopulation().firstElement().isTreeValid());
		assertEquals((testLabel + "[0]: "), treeDump, testGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("((8+-7)+(-3-3))+((-6+3)+(-8-2))");
		treeHeight = new Integer(3);
		toLog.info(testLabel + "[1]: Height=" + testGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: isValid=" + testGeneration.getPopulation().elementAt(1).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[1]: \"" + testGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_isValid: "), true, testGeneration.getPopulation().elementAt(1).isTreeValid());
		assertEquals((testLabel + "[1]: "), treeDump, testGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("(((-4--6)/(x-0))*((-4/-6)*(3/2)))+(((-7+x)-(9+-9))*((1/-1)-(-5+5)))");
		treeHeight = new Integer(4);
		toLog.info(testLabel + "[2]: Height=" + testGeneration.getPopulation().lastElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[2]: isValid=" + testGeneration.getPopulation().lastElement().isTreeValid()
				+ "  [Compare=false]");
		toLog.info(testLabel + "[2]: \"" + testGeneration.getPopulation().lastElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, testGeneration.getPopulation().lastElement().getHeight());
		assertEquals((testLabel + "[2]_isValid: "), false, testGeneration.getPopulation().lastElement().isTreeValid());
		assertEquals((testLabel + "[2]: "), treeDump, testGeneration.getPopulation().lastElement().toString());

		/*
		 * Test for the Best Individual in this Population
		 */
		FitnessDatum bestFitness = new FitnessDatum(Double.parseDouble("33.333333333333336"));
		treeDump = new String("(-8*-1)-(-5/6)");
		toLog.info(testLabel + ": BestIndividual=" + testGeneration.getBestIndividual().getFitness() + "  [Compare="
				+ bestFitness + "]");
		toLog.info(testLabel + ": BestIndividual=\"" + testGeneration.getBestIndividual() + "\"  [Compare=\""
				+ treeDump + "\"]\n");
		assertEquals((testLabel + "_Fitness: "), bestFitness, testGeneration.getBestIndividual().getFitness());
		assertEquals((testLabel + "_Fitness: "), treeDump, testGeneration.getBestIndividual().toString());
	}

	@Test
	public void testInit_RampedGrow() {
		GPSettings.setPopulationSize(3);
		GPSettings.setMaxHtOfInitTree(5);
		GPSettings.setGenerationMethod(GenerationMethod.RAMPED_GROW);
		String testLabel = GPSettings.getGenerationMethod().toLowerCase() + "Generation";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: RequestedSize="
				+ GPSettings.getPopulationSize() + "\n" + testGeneration);

		/*
		 * Test for the First Tree in the Population:
		 */
		String treeDump = new String("(6+-7)/0");
		Integer treeHeight = new Integer(2);
		toLog.info(testLabel + "[0]: Height=" + testGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: isValid=" + testGeneration.getPopulation().firstElement().isTreeValid()
				+ "  [Compare=false]");
		toLog.info(testLabel + "[0]: \"" + testGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, testGeneration.getPopulation().firstElement()
				.getHeight());
		assertEquals((testLabel + "[0]_isValid: "), false, testGeneration.getPopulation().firstElement().isTreeValid());
		assertEquals((testLabel + "[0]: "), treeDump, testGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("-6+((3+7)-(-4--6))");
		treeHeight = new Integer(3);
		toLog.info(testLabel + "[1]: Height=" + testGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: isValid=" + testGeneration.getPopulation().elementAt(1).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[1]: \"" + testGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_isValid: "), true, testGeneration.getPopulation().elementAt(1).isTreeValid());
		assertEquals((testLabel + "[1]: "), treeDump, testGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("(6-2)*(-7-(-9+2))");
		treeHeight = new Integer(3);
		toLog.info(testLabel + "[2]: Height=" + testGeneration.getPopulation().elementAt(2).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[2]: isValid=" + testGeneration.getPopulation().elementAt(2).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[2]: \"" + testGeneration.getPopulation().elementAt(2) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(2).getHeight());
		assertEquals((testLabel + "[2]_isValid: "), true, testGeneration.getPopulation().elementAt(2).isTreeValid());
		assertEquals((testLabel + "[2]: "), treeDump, testGeneration.getPopulation().elementAt(2).toString());

		/*
		 * Test for the Forth Tree in the Population:
		 */
		treeDump = new String("-5*-1");
		treeHeight = new Integer(1);
		toLog.info(testLabel + "[3]: Height=" + testGeneration.getPopulation().lastElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[3]: isValid=" + testGeneration.getPopulation().lastElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[3]: \"" + testGeneration.getPopulation().lastElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[3]_Height: "), treeHeight, testGeneration.getPopulation().lastElement().getHeight());
		assertEquals((testLabel + "[3]_isValid: "), true, testGeneration.getPopulation().lastElement().isTreeValid());
		assertEquals((testLabel + "[3]: "), treeDump, testGeneration.getPopulation().lastElement().toString());

		/*
		 * Test for the Best Individual in this Population
		 */
		FitnessDatum bestFitness = new FitnessDatum(Double.parseDouble("24.5"));
		treeDump = new String("(6-2)*(-7-(-9+2))");
		toLog.info(testLabel + ": BestIndividual=" + testGeneration.getBestIndividual().getFitness() + "  [Compare="
				+ bestFitness + "]");
		toLog.info(testLabel + ": BestIndividual=\"" + testGeneration.getBestIndividual() + "\"  [Compare=\""
				+ treeDump + "\"]\n");
		assertEquals((testLabel + "_Fitness: "), bestFitness, testGeneration.getBestIndividual().getFitness());
		assertEquals((testLabel + "_Fitness: "), treeDump, testGeneration.getBestIndividual().toString());
	}

	@Test
	public void testInit_HalfAndHalf() {
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setGenerationMethod(GenerationMethod.RAMPED_HALF_AND_HALF);
		String testLabel = GPSettings.getGenerationMethod().toLowerCase() + "Generation";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: RequestedSize="
				+ GPSettings.getPopulationSize() + "\n" + testGeneration);

		/*
		 * Test for the First Tree in the Population:
		 */
		String treeDump = new String("(-8*-1)-(-5/6)");
		Integer treeHeight = new Integer(2);
		toLog.info(testLabel + "[0]: Height=" + testGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: isValid=" + testGeneration.getPopulation().firstElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[0]: \"" + testGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, testGeneration.getPopulation().firstElement()
				.getHeight());
		assertEquals((testLabel + "[0]_isValid: "), true, testGeneration.getPopulation().firstElement().isTreeValid());
		assertEquals((testLabel + "[0]: "), treeDump, testGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("(-5*-2)-(6/-8)");
		treeHeight = new Integer(2);
		toLog.info(testLabel + "[1]: Height=" + testGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: isValid=" + testGeneration.getPopulation().elementAt(1).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[1]: \"" + testGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_isValid: "), true, testGeneration.getPopulation().elementAt(1).isTreeValid());
		assertEquals((testLabel + "[1]: "), treeDump, testGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("((-1*-8)+(-6-2))+((8*-8)-(-6-6))");
		treeHeight = new Integer(3);
		toLog.info(testLabel + "[2]: Height=" + testGeneration.getPopulation().elementAt(2).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[2]: isValid=" + testGeneration.getPopulation().elementAt(2).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[2]: \"" + testGeneration.getPopulation().elementAt(2) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(2).getHeight());
		assertEquals((testLabel + "[2]_isValid: "), true, testGeneration.getPopulation().elementAt(2).isTreeValid());
		assertEquals((testLabel + "[2]: "), treeDump, testGeneration.getPopulation().elementAt(2).toString());

		/*
		 * Test for the Forth Tree in the Population:
		 */
		treeDump = new String("-7-(-9+2)");
		treeHeight = new Integer(2);
		toLog.info(testLabel + "[3]: Height=" + testGeneration.getPopulation().elementAt(3).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[3]: isValid=" + testGeneration.getPopulation().elementAt(3).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[3]: \"" + testGeneration.getPopulation().elementAt(3) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[3]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(3).getHeight());
		assertEquals((testLabel + "[3]_isValid: "), true, testGeneration.getPopulation().elementAt(3).isTreeValid());
		assertEquals((testLabel + "[3]: "), treeDump, testGeneration.getPopulation().elementAt(3).toString());

		/*
		 * Test for the Fifth Tree in the Population:
		 */
		treeDump = new String("(((5+-1)+(x/9))*((8+5)+(4*9)))*(((4*1)+(7+-8))+((-1/-3)*(3+3)))");
		treeHeight = new Integer(4);
		toLog.info(testLabel + "[4]: Height=" + testGeneration.getPopulation().elementAt(4).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[4]: isValid=" + testGeneration.getPopulation().elementAt(4).isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[4]: \"" + testGeneration.getPopulation().elementAt(4) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[4]_Height: "), treeHeight, testGeneration.getPopulation().elementAt(4).getHeight());
		assertEquals((testLabel + "[4]_isValid: "), true, testGeneration.getPopulation().elementAt(4).isTreeValid());
		assertEquals((testLabel + "[4]: "), treeDump, testGeneration.getPopulation().elementAt(4).toString());

		/*
		 * Test for the Last Tree in the Population:
		 */
		treeDump = new String("3*((9*(x-4))-((-3*5)*1))");
		treeHeight = new Integer(4);
		toLog.info(testLabel + "[5]: Height=" + testGeneration.getPopulation().lastElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[5]: isValid=" + testGeneration.getPopulation().lastElement().isTreeValid()
				+ "  [Compare=true]");
		toLog.info(testLabel + "[5]: \"" + testGeneration.getPopulation().lastElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[5]_Height: "), treeHeight, testGeneration.getPopulation().lastElement().getHeight());
		assertEquals((testLabel + "[5]_isValid: "), true, testGeneration.getPopulation().lastElement().isTreeValid());
		assertEquals((testLabel + "[5]: "), treeDump, testGeneration.getPopulation().lastElement().toString());

		/*
		 * Test for the Best Individual in this Population
		 */
		FitnessDatum bestFitness = new FitnessDatum(Double.parseDouble("24.5"));
		treeDump = new String("-7-(-9+2)");
		toLog.info(testLabel + ": BestIndividual=" + testGeneration.getBestIndividual().getFitness() + "  [Compare="
				+ bestFitness + "]");
		toLog.info(testLabel + ": BestIndividual=\"" + testGeneration.getBestIndividual() + "\"  [Compare=\""
				+ treeDump + "\"]\n");
		assertEquals((testLabel + "_Fitness: "), bestFitness, testGeneration.getBestIndividual().getFitness());
		assertEquals((testLabel + "_Fitness: "), treeDump, testGeneration.getBestIndividual().toString());
	}

	@Test
	public void testReproduction_NaturalSelection() {
		GPSettings.setPopulationSize(12);
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setCrossoverProbability(0.70);
		GPSettings.setGenerationMethod(GenerationMethod.FULL);
		GPSettings.setReproductionMethod(ReproductionMethod.NATURAL_SELECTION);

		String testLabel = GPSettings.getReproductionMethod().toLowerCase() + "Reproduction";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: \n" + testGeneration);

		GPGeneration newGeneration = new GPGeneration();
		Vector<GeneticProgrammingTree> newRepro = new Vector<GeneticProgrammingTree>();
		newRepro.addAll(testGeneration.reproduction());
		newGeneration.setPopulation(newRepro);
		toLog.info(testLabel + "_newGeneration[size=" + newGeneration.getPopulation().size() + "]: \n" + newGeneration);

		/*
		 * Test for the First Tree in the Selection:
		 */
		String treeDump = new String("(((5--4)/(-3*-3))-((-5*x)-(2/9)))*(((-2/-8)-(-9+5))+((-6+-2)+(0+1)))");
		Integer treeHeight = new Integer(4);
		FitnessDatum fitnessScore = new FitnessDatum(Double.parseDouble("167.86111111111111"));
		toLog.info(testLabel + "[0]: Height=" + newGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: Fitness=" + newGeneration.getPopulation().firstElement().getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[0]: \"" + newGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, newGeneration.getPopulation().firstElement().getHeight());
		assertEquals((testLabel + "[0]_Fitness: "), fitnessScore, newGeneration.getPopulation().firstElement()
				.getFitness());
		assertEquals((testLabel + "[0]: "), treeDump, newGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("(((1+4)+(x-7))*((7*-1)+(0-3)))+(((-9*3)/(6*-5))+((9+7)*(x-4)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("238.99999999999997"));
		toLog.info(testLabel + "[1]: Height=" + newGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: Fitness=" + newGeneration.getPopulation().elementAt(1).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[1]: \"" + newGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(1)
				.getFitness());
		assertEquals((testLabel + "[1]: "), treeDump, newGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("(((6*8)+(5*x))-((2*-3)+(x-x)))-(((-9*-7)/(-2+-1))/((7*-1)*(8*9)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("246.29166666666669"));
		toLog.info(testLabel + "[2]: Height=" + newGeneration.getPopulation().elementAt(2).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[2]: Fitness=" + newGeneration.getPopulation().elementAt(2).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[2]: \"" + newGeneration.getPopulation().elementAt(2) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(2).getHeight());
		assertEquals((testLabel + "[2]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(2)
				.getFitness());
		assertEquals((testLabel + "[2]: "), treeDump, newGeneration.getPopulation().elementAt(2).toString());

		/*
		 * Test for the Forth Tree in the Population:
		 */
		treeDump = new String("(((3+-8)-(x-1))-((2--1)-(x--8)))+(((-4*9)/(-4/-6))+((-6*3)*(x/3)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("288.5"));
		toLog.info(testLabel + "[3]: Height=" + newGeneration.getPopulation().elementAt(3).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[3]: Fitness=" + newGeneration.getPopulation().elementAt(3).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[3]: \"" + newGeneration.getPopulation().elementAt(3) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[3]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(3).getHeight());
		assertEquals((testLabel + "[3]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(3)
				.getFitness());
		assertEquals((testLabel + "[3]: "), treeDump, newGeneration.getPopulation().elementAt(3).toString());
	}

	@Test
	public void testReproduction_TournamentSelection() {
		GPSettings.setPopulationSize(50);
		GPSettings.setMaxHtOfInitTree(4);
		GPSettings.setTournamentSize(6);
		GPSettings.setCrossoverProbability(0.90);
		GPSettings.setGenerationMethod(GenerationMethod.FULL);
		GPSettings.setReproductionMethod(ReproductionMethod.TOURNAMENT_SELECTION);

		String testLabel = GPSettings.getReproductionMethod().toLowerCase() + "Reproduction";

		GPGeneration testGeneration = new GPGeneration();
		testGeneration.init();
		// toLog.info(testLabel + "[size=" + testGeneration.getPopulation().size() + "]: \n" + testGeneration);

		GPGeneration newGeneration = new GPGeneration();
		Vector<GeneticProgrammingTree> newRepro = new Vector<GeneticProgrammingTree>();
		newRepro.addAll(testGeneration.reproduction());
		Collections.sort(newRepro);
		newGeneration.setPopulation(newRepro);
		toLog.info(testLabel + "_newGeneration[size=" + newGeneration.getPopulation().size() + "]: \n" + newGeneration);

		/*
		 * Test for the First Tree in the Selection:
		 */
		String treeDump = new String("(((-4*-5)/(8+9))/((-8-5)*(1/-5)))/(((-8-9)-(-8*-7))-((-1-8)+(4+x)))");
		Integer treeHeight = new Integer(4);
		FitnessDatum fitnessScore = new FitnessDatum(Double.parseDouble("24.520037959463608"));
		toLog.info(testLabel + "[0]: Height=" + newGeneration.getPopulation().firstElement().getHeight()
				+ "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: Fitness=" + newGeneration.getPopulation().firstElement().getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[0]: \"" + newGeneration.getPopulation().firstElement() + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, newGeneration.getPopulation().firstElement().getHeight());
		assertEquals((testLabel + "[0]_Fitness: "), fitnessScore, newGeneration.getPopulation().firstElement()
				.getFitness());
		assertEquals((testLabel + "[0]: "), treeDump, newGeneration.getPopulation().firstElement().toString());

		/*
		 * Test for the Second Tree in the Population:
		 */
		treeDump = new String("(((6--4)+(-8+-8))+((1*-1)/(-8*-9)))/(((4*-5)*(3--5))+((-8/-1)-(6*x)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("24.536481149407052"));
		toLog.info(testLabel + "[1]: Height=" + newGeneration.getPopulation().elementAt(1).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[1]: Fitness=" + newGeneration.getPopulation().elementAt(1).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[1]: \"" + newGeneration.getPopulation().elementAt(1) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[1]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(1).getHeight());
		assertEquals((testLabel + "[1]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(1)
				.getFitness());
		assertEquals((testLabel + "[1]: "), treeDump, newGeneration.getPopulation().elementAt(1).toString());

		/*
		 * Test for the Third Tree in the Population:
		 */
		treeDump = new String("(((8/1)+(0-0))-((4*-7)-(7*1)))/(((-4--3)/(2+9))+((8--4)*(-4*9)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("24.7985482852935"));
		toLog.info(testLabel + "[2]: Height=" + newGeneration.getPopulation().elementAt(2).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[2]: Fitness=" + newGeneration.getPopulation().elementAt(2).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[2]: \"" + newGeneration.getPopulation().elementAt(2) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(2).getHeight());
		assertEquals((testLabel + "[2]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(2)
				.getFitness());
		assertEquals((testLabel + "[2]: "), treeDump, newGeneration.getPopulation().elementAt(2).toString());

		/*
		 * Test for the Forth Tree in the Population:
		 */
		treeDump = new String("(((-4*-1)+(3--3))+((8*2)-(3--5)))/(((5/-5)-(-2--7))*((9+x)-(2*-7)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("24.904738377564463"));
		toLog.info(testLabel + "[3]: Height=" + newGeneration.getPopulation().elementAt(3).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[3]: Fitness=" + newGeneration.getPopulation().elementAt(3).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[3]: \"" + newGeneration.getPopulation().elementAt(3) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[3]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(3).getHeight());
		assertEquals((testLabel + "[3]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(3)
				.getFitness());
		assertEquals((testLabel + "[3]: "), treeDump, newGeneration.getPopulation().elementAt(3).toString());

		/*
		 * Test for the Fifth Tree in the Population:
		 */
		treeDump = new String("(((2/5)-(-1/-1))+((6/9)-(5/2)))+(((-9-4)/(7*7))-((7*-1)-(-6/1)))");
		treeHeight = new Integer(4);
		fitnessScore = new FitnessDatum(Double.parseDouble("31.993197278911566"));
		toLog.info(testLabel + "[4]: Height=" + newGeneration.getPopulation().elementAt(4).getHeight() + "  [Compare="
				+ treeHeight + "]");
		toLog.info(testLabel + "[4]: Fitness=" + newGeneration.getPopulation().elementAt(4).getFitness()
				+ "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[4]: \"" + newGeneration.getPopulation().elementAt(4) + "\"  [Compare=\"" + treeDump
				+ "\"]\n");
		assertEquals((testLabel + "[4]_Height: "), treeHeight, newGeneration.getPopulation().elementAt(4).getHeight());
		assertEquals((testLabel + "[4]_Fitness: "), fitnessScore, newGeneration.getPopulation().elementAt(4)
				.getFitness());
		assertEquals((testLabel + "[4]: "), treeDump, newGeneration.getPopulation().elementAt(4).toString());
	}

	@Test
	public void testMutate() {
		GPSettings.setPopulationSize(10);
		GPSettings.setMaxHtOfInitTree(3);
		GPSettings.setGenerationMethod(GenerationMethod.FULL);
		GPSettings.setMutationProbability(0.50);
		GPSettings.setMaxHtOfMutationSubtree(2);

		String testLabel = "Mutation";

		GPGeneration mutateGen = new GPGeneration();
		mutateGen.init();
		Vector<GeneticProgrammingTree> mutatePop = mutateGen.getPopulation();

		toLog.info(testLabel + "_BEFORE" + "[size=" + mutatePop.size() + "]: \n" +
				mutateGen);

		mutateGen.mutate();
		toLog.info(testLabel + "_AFTER" + "[size=" + mutatePop.size() + "]: \n" +
				 mutateGen);

		/*
		 * Test for the First Tree in the Mutated Selection:
		 */
		String treeDump = new String("((-1/6)*(6+-6))-((8+-7)+(-3-3))");
		Integer treeHeight = new Integer(3);
		FitnessDatum fitnessScore = new FitnessDatum(Double.parseDouble("29.5"));
		toLog.info(testLabel + "[0]: Height=" + mutatePop.firstElement().getHeight() + "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[0]: Fitness_Mutated=" + mutatePop.firstElement().getFitness() + "  [Compare=*Invalid]");
		assertEquals((testLabel + "[0]_Fitness_Mutated: "), "*Invalid", mutatePop.firstElement().getFitness().toString());
		mutatePop.firstElement().calculateFitness();
		toLog.info(testLabel + "[0]: Fitness_Recalculated=" + mutatePop.firstElement().getFitness() + "  [Compare="	+ fitnessScore + "]");
		toLog.info(testLabel + "[0]: \"" + mutatePop.firstElement() + "\"  [Compare=\"" + treeDump + "\"]\n");
		assertEquals((testLabel + "[0]_Height: "), treeHeight, mutatePop.firstElement().getHeight());
		assertEquals((testLabel + "[0]_Fitness_Recalculated: "), fitnessScore, mutatePop.firstElement().getFitness());
		assertEquals((testLabel + "[0]: "), treeDump, mutatePop.firstElement().toString());

		/*
		 * Test for the Third Tree in the Mutated Selection:
		 */
		treeDump = new String("((-4/-3)*(3/2))*((7--7)*(9/9))");
		treeHeight = new Integer(3);
		fitnessScore = new FitnessDatum(Double.parseDouble("116.5"));
		toLog.info(testLabel + "[2]: Height=" + mutatePop.elementAt(2).getHeight() + "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[2]: Fitness_Mutated=" + mutatePop.elementAt(2).getFitness() + "  [Compare=*Invalid]");
		assertEquals((testLabel + "[2]_Fitness_Mutated: "), "*Invalid", mutatePop.elementAt(2).getFitness().toString());
		mutatePop.elementAt(2).calculateFitness();
		toLog.info(testLabel + "[2]: Fitness_Recalculated=" + mutatePop.elementAt(2).getFitness() + "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[2]: \"" + mutatePop.elementAt(2) + "\"  [Compare=\"" + treeDump + "\"]\n");
		assertEquals((testLabel + "[2]_Height: "), treeHeight, mutatePop.elementAt(2).getHeight());
		assertEquals((testLabel + "[2]_Fitness_Recalculated: "), fitnessScore, mutatePop.elementAt(2).getFitness());
		assertEquals((testLabel + "[2]: "), treeDump, mutatePop.elementAt(2).toString());

		/*
		 * Test for the Fifth Tree in the Mutated Selection:
		 */
		treeDump = new String("-1*(-8*8)");
		treeHeight = new Integer(2);
		fitnessScore = new FitnessDatum(Double.parseDouble("296.5"));
		toLog.info(testLabel + "[4]: Height=" + mutatePop.elementAt(4).getHeight() + "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[4]: Fitness_Mutated=" + mutatePop.elementAt(4).getFitness() + "  [Compare=*Invalid]");
		assertEquals((testLabel + "[4]_Fitness_Mutated: "), "*Invalid", mutatePop.elementAt(4).getFitness().toString());
		mutatePop.elementAt(4).calculateFitness();
		toLog.info(testLabel + "[4]: Fitness_Recalculated=" + mutatePop.elementAt(4).getFitness() + "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[4]: \"" + mutatePop.elementAt(4) + "\"  [Compare=\"" + treeDump + "\"]\n");
		assertEquals((testLabel + "[4]_Height: "), treeHeight, mutatePop.elementAt(4).getHeight());
		assertEquals((testLabel + "[4]_Fitness_Recalculated: "), fitnessScore, mutatePop.elementAt(4).getFitness());
		assertEquals((testLabel + "[4]: "), treeDump, mutatePop.elementAt(4).toString());

		/*
		 * Test for the Seventh Tree in the Mutated Selection:
		 */
		treeDump = new String("((-2+2)/(((-4-5)*-4)*-3))-((1+-5)*(-8+7))");
		treeHeight = new Integer(5);
		fitnessScore = new FitnessDatum(Double.parseDouble("43.5"));
		toLog.info(testLabel + "[6]: Height=" + mutatePop.elementAt(6).getHeight() + "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[6]: Fitness_Mutated=" + mutatePop.elementAt(6).getFitness() + "  [Compare=*Invalid]");
		assertEquals((testLabel + "[6]_Fitness_Mutated: "), "*Invalid", mutatePop.elementAt(6).getFitness().toString());
		mutatePop.elementAt(6).calculateFitness();
		toLog.info(testLabel + "[6]: Fitness_Recalculated=" + mutatePop.elementAt(6).getFitness() + "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[6]: \"" + mutatePop.elementAt(6) + "\"  [Compare=\"" + treeDump + "\"]\n");
		assertEquals((testLabel + "[6]_Height: "), treeHeight, mutatePop.elementAt(6).getHeight());
		assertEquals((testLabel + "[6]_Fitness_Recalculated: "), fitnessScore, mutatePop.elementAt(6).getFitness());
		assertEquals((testLabel + "[6]: "), treeDump, mutatePop.elementAt(6).toString());

		/*
		 * Test for the Eighth Tree in the Mutated Selection:
		 */
		treeDump = new String("((1/5)/(-6+3))/((-9--3)+(-5*-6))");
		treeHeight = new Integer(3);
		fitnessScore = new FitnessDatum(Double.parseDouble("24.508333333333333"));
		toLog.info(testLabel + "[7]: Height=" + mutatePop.elementAt(7).getHeight() + "  [Compare=" + treeHeight + "]");
		toLog.info(testLabel + "[7]: Fitness_Mutated=" + mutatePop.elementAt(7).getFitness() + "  [Compare=*Invalid]");
		assertEquals((testLabel + "[7]_Fitness_Mutated: "), "*Invalid", mutatePop.elementAt(7).getFitness().toString());
		mutatePop.elementAt(7).calculateFitness();
		toLog.info(testLabel + "[7]: Fitness_Recalculated=" + mutatePop.elementAt(7).getFitness() + "  [Compare=" + fitnessScore + "]");
		toLog.info(testLabel + "[7]: \"" + mutatePop.elementAt(7) + "\"  [Compare=\"" + treeDump + "\"]\n");
		assertEquals((testLabel + "[7]_Height: "), treeHeight, mutatePop.elementAt(7).getHeight());
		assertEquals((testLabel + "[7]_Fitness_Recalculated: "), fitnessScore, mutatePop.elementAt(7).getFitness());
		assertEquals((testLabel + "[7]: "), treeDump, mutatePop.elementAt(7).toString());
	}
}
