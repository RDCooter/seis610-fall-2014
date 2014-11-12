package edu.stthomas.seis610.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stthomas.seis610.gp.GPSettings;
import edu.stthomas.seis610.gp.TrainingData;
import edu.stthomas.seis610.tree.GPTreeFactory.GenerationMethod;
import edu.stthomas.seis610.util.GPSimpleFormatter;

public class SettingsTest {
	private static final Logger toLog = Logger.getLogger(GPGenericTest.class.getName());
	private static final String dftString = new String("DEFAULT.");
	private static final String updString = new String("UPDATED.");
	private static final double dftDelta = 0.0;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		toLog.addHandler(myHandler);
		toLog.setUseParentHandlers(false);
	}

	@Test
	public void testGetMutationProbability() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._MUTATION_PROBABILITY + ": " + GPSettings.getMutationProbability() + "  [Compare=" + GPSettings._DEFAULT_MUTATION_PROBABILITY + "]");
		assertEquals((dftString + GPSettings._MUTATION_PROBABILITY), Double.parseDouble(GPSettings._DEFAULT_MUTATION_PROBABILITY), GPSettings.getMutationProbability().doubleValue(), dftDelta);

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Double newSettingValue = new Double(0.07);
		GPSettings.setMutationProbability(newSettingValue);
		toLog.info(updString + GPSettings._MUTATION_PROBABILITY + ": " + GPSettings.getMutationProbability() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._MUTATION_PROBABILITY), newSettingValue, GPSettings.getMutationProbability(), dftDelta);
	}

	@Test
	public void testGetFitnessProbability() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._FITNESS_PROBABILITY + ": " + GPSettings.getFitnessProbability() + "  [Compare=" + GPSettings._DEFAULT_FITNESS_PROBABILITY + "]");
		assertEquals((dftString + GPSettings._FITNESS_PROBABILITY), Double.parseDouble(GPSettings._DEFAULT_FITNESS_PROBABILITY), GPSettings.getFitnessProbability().doubleValue(), dftDelta);

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Double newSettingValue = new Double(0.05);
		GPSettings.setFitnessProbability(newSettingValue);
		toLog.info(updString + GPSettings._FITNESS_PROBABILITY + ": " + GPSettings.getFitnessProbability() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._FITNESS_PROBABILITY), newSettingValue, GPSettings.getFitnessProbability(), dftDelta);
	}

	@Test
	public void testGetFitnessMarginOfError() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._FITNESS_MARGIN_ERROR + ": " + GPSettings.getFitnessMarginOfError() + "  [Compare=" + GPSettings._DEFAULT_FITNESS_MARGIN_ERROR + "]");
		assertEquals((dftString + GPSettings._FITNESS_MARGIN_ERROR), Double.parseDouble(GPSettings._DEFAULT_FITNESS_MARGIN_ERROR), GPSettings.getFitnessMarginOfError().doubleValue(), dftDelta);

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Double newSettingValue = new Double(0.10);
		GPSettings.setFitnessMarginOfError(newSettingValue);
		toLog.info(updString + GPSettings._FITNESS_MARGIN_ERROR + ": " + GPSettings.getFitnessMarginOfError() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._FITNESS_MARGIN_ERROR), newSettingValue, GPSettings.getFitnessMarginOfError(), dftDelta);
	}

	@Test
	public void testGetOperatorString() {
		// Test the Default Settings (first retrieval should use default)
		Vector<String> vectorValues = new Vector<String>();
		vectorValues.add("ADD");
		vectorValues.add("SUB");
		vectorValues.add("MUL");
		vectorValues.add("DIV");
		toLog.info(dftString + GPSettings._OPERATORS  + "String" + ": \"" + GPSettings.getOperatorString() + "\"  [Compare=\"" + GPSettings._DEFAULT_OPERATORS + "\"]");
		assertEquals((dftString + GPSettings._OPERATORS + "String"), GPSettings._DEFAULT_OPERATORS, GPSettings.getOperatorString());
		toLog.info(dftString + GPSettings._OPERATORS + ": " + GPSettings.getOperators() + "  [Compare=" + vectorValues + "]");
		assertEquals((dftString + GPSettings._OPERATORS), vectorValues.toString(), GPSettings.getOperators().toString());

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		String newSettingValue = new String("SUB,MUL,POW,DIV,SIN,COS,ADD");
		GPSettings.addOperators("POW");
		GPSettings.addOperators("DIV");
		GPSettings.addOperators("SIN");
		GPSettings.addOperators("COS");
		GPSettings.addOperators("ADD");
		vectorValues.clear();
		vectorValues.add("SUB");
		vectorValues.add("MUL");
		vectorValues.add("POW");
		vectorValues.add("DIV");
		vectorValues.add("SIN");
		vectorValues.add("COS");
		vectorValues.add("ADD");
		toLog.info(updString + GPSettings._OPERATORS + "String" + ": \"" + GPSettings.getOperatorString() + "\"  [Compare=\"" + newSettingValue + "\"]");
		assertEquals((updString + GPSettings._OPERATORS + "String"), newSettingValue, GPSettings.getOperatorString());
		toLog.info(updString + GPSettings._OPERATORS + ": " + GPSettings.getOperators() + "  [Compare=" + vectorValues + "]");
		assertEquals((updString + GPSettings._OPERATORS), vectorValues.toString(), GPSettings.getOperators().toString());
	}

	@Test
	public void testGetOperandsString() {
		// Test the Default Settings (first retrieval should use default)
		String[] dftOperands = {"-9","-8","-7","-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6","7","8","9","x"};
		Vector<String> vectorValues = new Vector<String>();
		vectorValues.addAll(Arrays.asList(dftOperands));
		toLog.info(dftString + GPSettings._OPERANDS  + "String" + ": \"" + GPSettings.getOperandsString() + "\"  [Compare=\"" + GPSettings._DEFAULT_OPERANDS + "\"]");
		assertEquals((dftString + GPSettings._OPERANDS + "String"), GPSettings._DEFAULT_OPERANDS, GPSettings.getOperandsString());
		toLog.info(dftString + GPSettings._OPERANDS + ": " + GPSettings.getOperands() + "  [Compare=" + vectorValues + "]");
		assertEquals((dftString + GPSettings._OPERANDS), vectorValues.toString(), GPSettings.getOperands().toString());

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		String newSettingValue = new String("-9,-8,-7,-6,-4,-3,-2,-1,1,2,3,5,6,7,8,9,x,-200,4,-5,0,169");
		String[] updOperands = {"-9","-8","-7","-6","-4","-3","-2","-1","1","2","3","5","6","7","8","9","x","-200","4","-5","0","169"};
		GPSettings.addOperands("-200");
		GPSettings.addOperands("4");
		GPSettings.addOperands("-5");
		GPSettings.addOperands("0");
		GPSettings.addOperands("169");
		vectorValues.clear();
		vectorValues.addAll(Arrays.asList(updOperands));
		toLog.info(updString + GPSettings._OPERANDS + "String" + ": \"" + GPSettings.getOperandsString() + "\"  [Compare=\"" + newSettingValue + "\"]");
		assertEquals((updString + GPSettings._OPERANDS + "String"), newSettingValue, GPSettings.getOperandsString());
		toLog.info(updString + GPSettings._OPERANDS + ": " + GPSettings.getOperands() + "  [Compare=" + vectorValues + "]");
		assertEquals((updString + GPSettings._OPERANDS), vectorValues.toString(), GPSettings.getOperands().toString());
	}

	@Test
	public void testGetNumCrossOvers() {
		// Test the Default Settings (first retrieval should use default)
		GPSettings.setPopulationSize(Integer.parseInt(GPSettings._DEFAULT_MAX_POPULATION_SIZE));//Restore just in case it has already been updated
		Integer defaultNumberOfCrossOvers = GPSettings.getPopulationSize() / GPSettings._DEFAULT_CROSSOVER_RATIO;
		toLog.info(dftString + GPSettings._CROSSOVER_SIZE + ": " + GPSettings.getNumCrossOvers() + "  [Compare=" + defaultNumberOfCrossOvers + "]");
		assertEquals((dftString + GPSettings._CROSSOVER_SIZE), defaultNumberOfCrossOvers.longValue(), GPSettings.getNumCrossOvers().longValue());

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Integer newSettingValue = new Integer(44);
		GPSettings.setNumCrossOvers(newSettingValue);
		toLog.info(updString + GPSettings._CROSSOVER_SIZE + ": " + GPSettings.getNumCrossOvers() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._CROSSOVER_SIZE), newSettingValue.longValue(), GPSettings.getNumCrossOvers().longValue());
	}

	@Test
	public void testGetPopulationSize() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._POPULATION_SIZE + ": " + GPSettings.getPopulationSize() + "  [Compare=" + GPSettings._DEFAULT_MAX_POPULATION_SIZE + "]");
		assertEquals((dftString + GPSettings._POPULATION_SIZE), Long.parseLong(GPSettings._DEFAULT_MAX_POPULATION_SIZE), GPSettings.getPopulationSize().longValue());
		
		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Integer newSettingValue = new Integer(169);
		GPSettings.setPopulationSize(newSettingValue);
		toLog.info(updString + GPSettings._POPULATION_SIZE + ": " + GPSettings.getPopulationSize() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._POPULATION_SIZE), newSettingValue.longValue(), GPSettings.getPopulationSize().longValue());
	}

	@Test
	public void testGetMaxHtOfInitTree() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfInitTree() + "  [Compare=" + GPSettings._DEFAULT_MAX_SUBTREE_HEIGHT + "]");
		assertEquals((dftString + GPSettings._SUBTREE_HEIGHT), Long.parseLong(GPSettings._DEFAULT_MAX_SUBTREE_HEIGHT), GPSettings.getMaxHtOfInitTree().longValue());
		
		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Integer newSettingValue = new Integer(40);
		GPSettings.setMaxHtOfInitTree(newSettingValue);
		toLog.info(updString + GPSettings._SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfInitTree() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._SUBTREE_HEIGHT), newSettingValue.longValue(), GPSettings.getMaxHtOfInitTree().longValue());
	}

	@Test
	public void testGetMaxHtOfCrossoverTree() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._CROSSOVER_SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfCrossoverTree() + "  [Compare=" + GPSettings._DEFAULT_MAX_CROSSOVER_HEIGHT + "]");
		assertEquals((dftString + GPSettings._CROSSOVER_SUBTREE_HEIGHT), Long.parseLong(GPSettings._DEFAULT_MAX_CROSSOVER_HEIGHT), GPSettings.getMaxHtOfCrossoverTree().longValue());
		
		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Integer newSettingValue = new Integer(100);
		GPSettings.setMaxHtOfCrossoverTree(newSettingValue);
		toLog.info(updString + GPSettings._CROSSOVER_SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfCrossoverTree() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._CROSSOVER_SUBTREE_HEIGHT), newSettingValue.longValue(), GPSettings.getMaxHtOfCrossoverTree().longValue());
	}

	@Test
	public void testGetMaxHtOfMutationSubtree() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._MUTATION_SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfMutationSubtree() + "  [Compare=" + GPSettings._DEFAULT_MAX_MUTATION_HEIGHT + "]");
		assertEquals((dftString + GPSettings._MUTATION_SUBTREE_HEIGHT), Long.parseLong(GPSettings._DEFAULT_MAX_MUTATION_HEIGHT), GPSettings.getMaxHtOfMutationSubtree().longValue());
		
		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		Integer newSettingValue = new Integer(30);
		GPSettings.setMaxHtOfMutationSubtree(newSettingValue);
		toLog.info(updString + GPSettings._MUTATION_SUBTREE_HEIGHT + ": " + GPSettings.getMaxHtOfMutationSubtree() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._MUTATION_SUBTREE_HEIGHT), newSettingValue.longValue(), GPSettings.getMaxHtOfMutationSubtree().longValue());
	}

	@Test
	public void testGetGenerationMethod() {
		// Test the Default Settings (first retrieval should use default)
		toLog.info(dftString + GPSettings._GENERATION_METHOD + ": " + GPSettings.getGenerationMethod() + "  [Compare=" + GPSettings._DEFAULT_GENERATION_METHOD + "]");
		assertEquals((dftString + GPSettings._GENERATION_METHOD), GPSettings._DEFAULT_GENERATION_METHOD, GPSettings.getGenerationMethod());
		
		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		String newSettingValue = new String("RANDOM");
		GPSettings.setGenerationMethod(GenerationMethod.RANDOM);
		toLog.info(updString + GPSettings._GENERATION_METHOD + ": " + GPSettings.getGenerationMethod() + "  [Compare=" + newSettingValue + "]");
		assertEquals((updString + GPSettings._GENERATION_METHOD), newSettingValue, GPSettings.getGenerationMethod());
	}

	@Test
	public void testGetTrainingInputString() {
		// Test the Default Settings (first retrieval should use default)
		GPSettings.setTrainingInputString(GPSettings._DEFAULT_INPUT_TRAINING_DATA);//Restore just in case it has already been updated
		toLog.info(dftString + GPSettings._INPUT_TRAINING_DATA  + "String" + ": \"" + GPSettings.getTrainingInputString() + "\"  [Compare=\"" + GPSettings._DEFAULT_INPUT_TRAINING_DATA + "\"]");
		assertEquals((dftString + GPSettings._INPUT_TRAINING_DATA + "String"), GPSettings._DEFAULT_INPUT_TRAINING_DATA, GPSettings.getTrainingInputString());

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		String newSettingValue = new String("20001,15313.5,3961.5,1105.5,3,1.5,365.5,4803,7939,147425.5");
		GPSettings.setTrainingInputString(newSettingValue);
		toLog.info(updString + GPSettings._INPUT_TRAINING_DATA + "String" + ": \"" + GPSettings.getTrainingInputString() + "\"  [Compare=\"" + newSettingValue + "\"]");
		assertEquals((updString + GPSettings._INPUT_TRAINING_DATA + "String"), newSettingValue, GPSettings.getTrainingInputString());
	}

	@Test
	public void testGetTrainingData() {
		Vector<String> vectorValues = new Vector<String>();

		// Test the Default Settings (first retrieval should use default)
		String[] dftXOperands = {"-5","-4","-3","-2","-1","0","1","2","3","4","5"};
		String[] dftYOperands = {"12.0","7.5","4.0","1.5","0.0","-0.5","0.0","1.5","4.0","7.5","12.0"};
		Vector<TrainingData> dftTrainingDataSet = GPSettings.getTrainingData();

		// Test the X-Values for Default Settings
		for (int i = 0; i < dftTrainingDataSet.size(); i++) {
			vectorValues.add(String.valueOf(dftTrainingDataSet.elementAt(i).getInputData().intValue()));
		}
		toLog.info(dftString + GPSettings._INPUT_TRAINING_DATA + ".X: x=" + vectorValues + "  [Compare=" + Arrays.asList(dftXOperands).toString() + "]");
		assertEquals((dftString + GPSettings._INPUT_TRAINING_DATA + ".X"), Arrays.asList(dftXOperands).toString(), vectorValues.toString());

		// Test the Y-Values for Default Settings
		vectorValues.clear();
		for (int i = 0; i < dftTrainingDataSet.size(); i++) {
			vectorValues.add(String.valueOf(dftTrainingDataSet.elementAt(i).getOutputData().doubleValue()));
		}
		toLog.info(dftString + GPSettings._INPUT_TRAINING_DATA + ".Y: y=" + vectorValues + "  [Compare=" + Arrays.asList(dftYOperands).toString() + "]");
		assertEquals((dftString + GPSettings._INPUT_TRAINING_DATA + ".Y"), Arrays.asList(dftYOperands).toString(), vectorValues.toString());

		// Test for Updated Settings (any retrieval after a setter has been invoked should retrieve the new value)
		String newSettingValue = new String("20001,15313.5,3961.5,1105.5,3,1.5,365.5,4803,7939,147425.5");
		String[] updXOperands = {"20001.0","15313.5","3961.5","1105.5","3.0","1.5","365.5","4803.0","7939.0","147425.5"};
		String[] updYOperands = {"2.0002E8","1.17251640625E8","7846740.625","611064.625","4.0","0.625","66794.625","1.1534404E7","3.151386E7","1.0867139024625E10"};
		GPSettings.setTrainingInputString(newSettingValue);
		Vector<TrainingData> updTrainingDataSet = GPSettings.getTrainingData();

		// Test the X-Values for Updated Settings
		vectorValues.clear();
		for (int i = 0; i < updTrainingDataSet.size(); i++) {
			vectorValues.add(String.valueOf(updTrainingDataSet.elementAt(i).getInputData().doubleValue()));
		}
		toLog.info(updString + GPSettings._INPUT_TRAINING_DATA + ".X: x=" + vectorValues + "  [Compare=" + Arrays.asList(updXOperands).toString() + "]");
		assertEquals((updString + GPSettings._INPUT_TRAINING_DATA + ".X"), Arrays.asList(updXOperands).toString(), vectorValues.toString());

		// Test the Y-Values for Updated Settings
		vectorValues.clear();
		for (int i = 0; i < updTrainingDataSet.size(); i++) {
			vectorValues.add(String.valueOf(updTrainingDataSet.elementAt(i).getOutputData().doubleValue()));
		}
		toLog.info(updString + GPSettings._INPUT_TRAINING_DATA + ".Y: y=" + vectorValues + "  [Compare=" + Arrays.asList(updYOperands).toString() + "]");
		assertEquals((updString + GPSettings._INPUT_TRAINING_DATA + ".Y"), Arrays.asList(updYOperands).toString(), vectorValues.toString());
	}

}
