package edu.stthomas.seis610.gp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import edu.stthomas.seis610.tree.GPTreeFactory.GenerationMethod;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * The Settings class defines the interface to the user specified settings that will be used to control the operations
 * within the Genetic Programming application.
 * 
 * @author Pravesh Tamraker Oct 19, 2014 4:11:18 PM
 * @author Robert Driesch	cooter		Nov 4, 2014 2:25:18 PM
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class GPSettings extends Properties {

	/**
	 * Define private variables for the Singleton Instance of this class.
	 */
	private static GPSettings xSingletonInstance = null;
	private static final long serialVersionUID = 1L;
	private static final String xInitialPropertiesFile = "init.gp.properties";
	private static OutputStream xOutputStream;

	private Random xRandomGenerator;
	private Vector<String> xOperators;
	private Vector<String> xOperands;
	private Vector<TrainingData> xTrainingData;

	/**
	 * Define Constants for the Property Names for the different Settings
	 */
	public final static String _RANDOM_SEED = new String("randomSeed");
	public final static String _MUTATION_PROBABILITY = new String("mutationProbability");
	public final static String _FITNESS_PROBABILITY = new String("fitnessProbability");
	public final static String _FITNESS_MARGIN_ERROR = new String("fitnessMarginOfError");
	public final static String _OPERATORS = new String("operators");
	public final static String _OPERANDS = new String("operands");
	public final static String _CROSSOVER_SIZE = new String("numberOfCrossOvers");
	public final static String _POPULATION_SIZE = new String("populationSize");
	public final static String _SUBTREE_HEIGHT = new String("maxSubtreeHeight");
	public final static String _CROSSOVER_SUBTREE_HEIGHT = new String("maxCrossoverSubtreeHeight");
	public final static String _GENERATION_METHOD = new String("treeGenerationMethod");
	public final static String _INPUT_TRAINING_DATA = new String("trainingDataInput");

	/**
	 * Define Default (Constant) Values for the Settings
	 */
	public final static String _DEFAULT_RANDOM_SEED = new String("12345");
	public final static String _DEFAULT_MUTATION_PROBABILITY = new String("0.70");
	public final static String _DEFAULT_FITNESS_PROBABILITY = new String("0.50");
	public final static String _DEFAULT_FITNESS_MARGIN_ERROR = new String("0.01");
	public final static String _DEFAULT_OPERATORS = new String("ADD,SUB,MUL,DIV");
	public final static String _DEFAULT_OPERANDS = new String("-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9,x");
	public final static Integer _DEFAULT_CROSSOVER_RATIO = new Integer(4);
	public final static String _DEFAULT_MAX_POPULATION_SIZE = new String("100");
	public final static String _DEFAULT_MAX_SUBTREE_HEIGHT = new String("4");
	public final static String _DEFAULT_MAX_CROSSOVER_HEIGHT = new String("10");
	public final static String _DEFAULT_GENERATION_METHOD = new String("FULL");
	public final static String _DEFAULT_INPUT_TRAINING_DATA = new String("-5,-4,-3,-2,-1,0,1,2,3,4,5");

	/**
	 * Private default constructor for singleton instance of this class.
	 */
	private GPSettings() {
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(xInitialPropertiesFile);
			load(inputStream);
			inputStream.close();
		} catch (IOException | NullPointerException e) {
			System.err.println("IOException: Unable to load initial properties file [" + xInitialPropertiesFile
					+ "] for GPSettings.");
		}

		// Construct the Random Number Generator to use for the GP algorithm. Seed the random generator with value
		// extracted from the properties file as long as the seed value exists within the properties.
		xRandomGenerator = new Random();
		Long randomSeed = getRandomSeed();
		if (randomSeed != null) {
			xRandomGenerator.setSeed(randomSeed);
		}

		// Construct the vector stubs for the operators and the operands that will be populated later.
		xOperators = new Vector<String>();
		xOperands = new Vector<String>();

		// Construct the vector stub for the training data that will be populated later.
		xTrainingData = new Vector<TrainingData>();
	}

	/**
	 * Public static method to get the singleton instance of this class and instantiate it (lazy instantiation) if it
	 * does not already exist.
	 * 
	 * @return the singleton instance of the Settings object
	 */
	public static GPSettings getInstance() {
		if (xSingletonInstance == null) {
			xSingletonInstance = new GPSettings();
		}
		return xSingletonInstance;
	}

	/**
	 * @param aBoundedValue high end bounded value to use for generation of the random integer
	 * @return a random integer value bounded by the parameter
	 */
	public static Integer getRandomInt(int aBoundedValue) {
		return getInstance().xRandomGenerator.nextInt(aBoundedValue);
	}

	/**
	 * @return the mutation probability ratio value for the GP algorithm
	 */
	public static Double getMutationProbability() {
		return getInstance().getDoubleProperty(_MUTATION_PROBABILITY, _DEFAULT_MUTATION_PROBABILITY);
	}

	/**
	 * @param aMutationProbability the new mutation probability ratio value for this property
	 */
	public static void setMutationProbability(Double aMutationProbability) {
		setDoubleProperty(_MUTATION_PROBABILITY, aMutationProbability);
	}

	/**
	 * @return the fitness probability ratio value for the GP algorithm
	 */
	public static Double getFitnessProbability() {
		return getInstance().getDoubleProperty(_FITNESS_PROBABILITY, _DEFAULT_FITNESS_PROBABILITY);
	}

	/**
	 * @param aFitnessProbability the new fitness probability ratio value for this property
	 */
	public static void setFitnessProbability(Double aFitnessProbability) {
		setDoubleProperty(_FITNESS_PROBABILITY, aFitnessProbability);
	}

	/**
	 * @return the fitness margin of error ratio value for the GP algorithm
	 */
	public static Double getFitnessMarginOfError() {
		return getInstance().getDoubleProperty(_FITNESS_MARGIN_ERROR, _DEFAULT_FITNESS_MARGIN_ERROR);
	}

	/**
	 * @param aFitnessMarginOfError the new fitness margin of error ratio value for this property
	 */
	public static void setFitnessMarginOfError(Double aFitnessMarginOfError) {
		setDoubleProperty(_FITNESS_MARGIN_ERROR, aFitnessMarginOfError);
	}

	/**
	 * @return the comma separated string of valid operators for the GP trees
	 */
	public static String getOperatorString() {
		return getInstance().getProperty(_OPERATORS, _DEFAULT_OPERATORS);
	}

	/**
	 * @return the list (vector) of valid operators for the GP trees
	 */
	public static Vector<String> getOperators() {
		return getInstance().getOperatorsProperty();
	}

	/**
	 * @param aOperatorValue the operator value to add to the operator property
	 */
	public static void addOperators(String aOperatorValue) {
		getInstance().setOperatorProperty(aOperatorValue);
	}

	/**
	 * @return the comma separated string of valid operators for the GP trees
	 */
	public static String getOperandsString() {
		return getInstance().getProperty(_OPERANDS, _DEFAULT_OPERANDS);
	}

	/**
	 * @return the list (vector) of valid operands for the GP trees
	 */
	public static Vector<String> getOperands() {
		return getInstance().getOperandsProperty();
	}

	/**
	 * @param aOperandValue the operand value to add to the operand property
	 */
	public static void addOperands(String aOperandValue) {
		getInstance().setOperandProperty(aOperandValue);
	}

	/**
	 * @return the initial random seed value for GP algorithms
	 */
	private Long getRandomSeed() {
		return getLongProperty(_RANDOM_SEED, _DEFAULT_RANDOM_SEED);
	}

	/**
	 * @param aRandomSeedValue the new random seed value for this property
	 */
	public static void setRandomSeed(Long aRandomSeedValue) {
		// Make sure that we update the already instantiated random number generator with the new seed value along with
		// updating the property settings.
		getInstance().xRandomGenerator.setSeed(aRandomSeedValue);
		setLongProperty(_RANDOM_SEED, aRandomSeedValue);
	}

	/**
	 * @return the initial number of cross over trees for the GP algorithms
	 */
	public static Integer getNumCrossOvers() {
		Integer defaultNumberOfCrossOvers = GPSettings.getPopulationSize() / _DEFAULT_CROSSOVER_RATIO;
		return getInstance().getIntProperty(_CROSSOVER_SIZE, defaultNumberOfCrossOvers.toString());
	}

	/**
	 * @param aNumCrossOvers the new number of cross overs value for this property
	 */
	public static void setNumCrossOvers(Integer aNumCrossOvers) {
		setIntProperty(_CROSSOVER_SIZE, aNumCrossOvers);
	}

	/**
	 * @return the initial population size for the GP algorithms
	 */
	public static Integer getPopulationSize() {
		return getInstance().getIntProperty(_POPULATION_SIZE, _DEFAULT_MAX_POPULATION_SIZE);
	}

	/**
	 * @param aPopulationSize the new population size value for this property
	 */
	public static void setPopulationSize(Integer aPopulationSize) {
		setIntProperty(_POPULATION_SIZE, aPopulationSize);
	}

	/**
	 * @return the initial max height of a subtree for new GP trees
	 */
	public static Integer getMaxHtOfInitTree() {
		return getInstance().getIntProperty(_SUBTREE_HEIGHT, _DEFAULT_MAX_SUBTREE_HEIGHT);
	}

	/**
	 * @param aMaxHtOfInitTree the new max height of a subtree value for this property
	 */
	public static void setMaxHtOfInitTree(Integer aMaxHtOfInitTree) {
		setIntProperty(_SUBTREE_HEIGHT, aMaxHtOfInitTree);
	}

	/**
	 * @return the initial max height of a crossover subtree for new GP trees
	 */
	public static Integer getMaxHtOfCrossoverTree() {
		return getInstance().getIntProperty(_CROSSOVER_SUBTREE_HEIGHT, _DEFAULT_MAX_CROSSOVER_HEIGHT);
	}

	/**
	 * @param aMaxHtOfCrossoverTree the new max height of a subtree value for this property
	 */
	public static void setMaxHtOfCrossoverTree(Integer aMaxHtOfCrossoverTree) {
		setIntProperty(_CROSSOVER_SUBTREE_HEIGHT, aMaxHtOfCrossoverTree);
	}

	/**
	 * @return the method to use for tree generation
	 */
	public static String getGenerationMethod() {
		return getInstance().getProperty(_GENERATION_METHOD, _DEFAULT_GENERATION_METHOD);
	}

	/**
	 * @param aMethod the method to use for tree generation
	 */
	public static void setGenerationMethod(GenerationMethod aMethod) {
		setStringProperty(_GENERATION_METHOD, aMethod.name());
	}

	/**
	 * @return the comma separated string of input training data (doubles)
	 */
	public static String getTrainingInputString() {
		return getInstance().getProperty(_INPUT_TRAINING_DATA, _DEFAULT_INPUT_TRAINING_DATA);
	}

	/**
	 * @param aTrainingVals a comma separated string of the new input training values
	 */
	public static void setTrainingInputString(String aTrainingVals) {
		setStringProperty(_INPUT_TRAINING_DATA, aTrainingVals);
		getInstance().xTrainingData.clear();
	}

	/**
	 * @return the list (vector) of training data for the GP trees
	 */
	public static Vector<TrainingData> getTrainingData() {
		return getInstance().getTrainingDataProperty();
	}

	/**
	 * @param aKey the property name (key) to lookup for the property
	 * @param aDefaultValue the default value to use if no property exists
	 * @return the numeric (integer) value of the property
	 */
	private int getIntProperty(String aKey, String aDefaultValue) {
		return Integer.parseInt(getProperty(aKey, aDefaultValue));
	}

	/**
	 * @param aKey the property name (key) to lookup for the property
	 * @param aDefaultValue the default value to use if no property exists
	 * @return the big numeric (long integer) value of the property
	 */
	private long getLongProperty(String aKey, String aDefaultValue) {
		return Long.parseLong(getProperty(aKey, aDefaultValue));
	}

	/**
	 * @param aKey the property name (key) to lookup for the property
	 * @param aDefaultValue the default value to use if no property exists
	 * @return the floating point (double) value of the property
	 */
	private double getDoubleProperty(String aKey, String aDefaultValue) {
		return Double.parseDouble(getProperty(aKey, aDefaultValue));
	}

	/**
	 * @return the list of valid operators for the property
	 */
	private Vector<String> getOperatorsProperty() {

		if (xOperators.isEmpty()) {
			for (String operatorToken : GPSettings.getOperatorString().split(",")) {
				xOperators.add(operatorToken.trim());
			}
		}
		return xOperators;
	}

	/**
	 * @return the list of valid operators for the property
	 */
	private Vector<String> getOperandsProperty() {
		if (xOperands.isEmpty()) {
			for (String operandToken : GPSettings.getOperandsString().split(",")) {
				xOperands.add(operandToken.trim());
			}
		}
		return xOperands;
	}

	/**
	 * @return the list of training data for the property
	 */
	private Vector<TrainingData> getTrainingDataProperty() {
		if (xTrainingData.isEmpty()) {
			for (String inputVal : GPSettings.getTrainingInputString().split(",")) {
				double input = Double.parseDouble(inputVal);
				TrainingData datum = new TrainingData(input, TrainingData.calculatePerfectOutput(input));
				xTrainingData.add(datum);
			}
		}
		return xTrainingData;
	}

	/**
	 * Update the properties object with the new value for the passed in property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aKey the property name (key) to update in the properties object/file
	 * @param aValue the numeric (integer) value for this property
	 */
	private static void setIntProperty(String aKey, Integer aValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(aKey, "" + aValue);
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue + " into the properties file ["
					+ xInitialPropertiesFile + "].");
		}
	}

	/**
	 * Update the properties object with the new value for the passed in property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aKey the property name (key) to update in the properties object/file
	 * @param aValue the numeric (long integer) value for this property
	 */
	private static void setLongProperty(String aKey, Long aValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(aKey, "" + aValue);
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue + " into the properties file ["
					+ xInitialPropertiesFile + "].");
		}
	}

	/**
	 * Update the properties object with the new value for the passed in property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aKey the property name (key) to update in the properties object/file
	 * @param aValue the floating point (double) value for this property
	 */
	private static void setDoubleProperty(String aKey, Double aValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(aKey, "" + aValue);
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue + " into the properties file ["
					+ xInitialPropertiesFile + "].");
		}
	}

	/**
	 * Update the properties object with the new value for the passed in property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aKey the property name (key) to update in the properties object/file
	 * @param aValue the string value for this property
	 */
	private static void setStringProperty(String aKey, String aValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(aKey, aValue);
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue + " into the properties file ["
					+ xInitialPropertiesFile + "].");
		}
	}

	/**
	 * Update the properties object with the new value for the operator property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aOperatorValue the operator value to add to the operator property
	 */
	private void setOperatorProperty(String aOperatorValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Since we are dealing with a vector, make sure that we don't simply append on a new (duplicate) value onto
			 * the end of the array. First remove the operator if it exists before adding the operator to the end of the
			 * existing array.
			 */
			if (xOperators.contains(aOperatorValue)) {
				xOperators.remove(aOperatorValue);
			}
			xOperators.add(aOperatorValue);

			/*
			 * Generate a new comma separated string of all of the allowed operators stored in the vector so that we can
			 * set it back into the properties file.
			 */
			StringBuffer compositeOperators = new StringBuffer();
			compositeOperators.append(xOperators.firstElement());
			for (int i = 1; i < xOperators.size(); i++) {
				compositeOperators.append("," + xOperators.elementAt(i));
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(_OPERATORS, compositeOperators.toString());
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aOperatorValue + " into the " + _OPERATORS
					+ " in the properties file [" + xInitialPropertiesFile + "].");
		}
	}

	/**
	 * Update the properties object with the new value for the operand property. Additionally continuously update the
	 * output properties file with the current settings for ALL of the current properties that are being used within
	 * this properties object.
	 * 
	 * @param aOperandValue the operand value to add to the operand property
	 */
	private void setOperandProperty(String aOperandValue) {
		try {
			try {
				xOutputStream = new FileOutputStream(new File(xInitialPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xInitialPropertiesFile
						+ "] for GPSettings.");
			}

			/*
			 * Since we are dealing with a vector, make sure that we don't simply append on a new (duplicate) value onto
			 * the end of the array. First remove the operand if it exists before adding the operand to the end of the
			 * existing array.
			 */
			if (xOperands.contains(aOperandValue)) {
				xOperands.remove(aOperandValue);
			}
			xOperands.add(aOperandValue);

			/*
			 * Generate a new comma separated string of all of the allowed operands stored in the vector so that we can
			 * set it back into the properties file.
			 */
			StringBuffer compositeOperands = new StringBuffer();
			compositeOperands.append(xOperands.firstElement());
			for (int i = 1; i < xOperands.size(); i++) {
				compositeOperands.append("," + xOperands.elementAt(i));
			}

			/*
			 * Update the singleton properties object with the new value for the key and re-write all of the current
			 * properties into the output properties file so that it will always show the current state of the
			 * properties (if not simply the defaults).
			 */
			getInstance().setProperty(_OPERANDS, compositeOperands.toString());
			getInstance().store(xOutputStream, null);

			xOutputStream.close();
		} catch (IOException e) {
			System.err.println("IOException: Unable to store " + aOperandValue + " into the " + _OPERANDS
					+ " in the properties file [" + xInitialPropertiesFile + "].");
		}
	}

	@Override
	public String toString() {
		// Display all of the properties within the settings in a Key=Value format.
		System.out.println("GPSettings [");
		System.out.println("   Instance Variables:");
		System.out.println("  =====================");
		System.out.println("     xOperators=" + xOperators);
		System.out.println("     xOperands=" + xOperands);
		System.out.println("     xTrainingData=" + xTrainingData);
		System.out.println("   Properties:");
		System.out.println("  =====================");
		for (String propertyKey : stringPropertyNames()) {
			String propertyValue = getProperty(propertyKey);
			System.out.println("     " + propertyKey + "=" + propertyValue);
		}
		System.out.println("]");
		return "";
	}
}
