package edu.stthomas.seis610.gp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * The Settings class defines the interface to the user specified settings that will be used to control the operations
 * within the Genetic Programming application.
 * 
 * @author Pravesh Tamraker Oct 19, 2014 4:11:18 PM
 * @author Robert Driesch Nov 4, 2014 2:25:18 PM
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class GPSettings extends Properties {

	/**
	 * Define a private variable for the Singleton Instance of this class.
	 */
	private static GPSettings xSingletonInstance = null;
	private static final long serialVersionUID = 1L;
	private static final String xInitialPropertiesFile = "init.gp.properties";
	private static final String xOutputPropertiesFile = "consumed.gp.properties";
	private static OutputStream xOutputStream;

	private Random xRandomGenerator;
	private Vector<String> xOperators;
	private Vector<String> xOperands;

	/**
	 * Define Constants for the Property Names for the different Settings
	 */
	private final static String _RANDOM_SEED = new String("randomSeed");
	private final static String _MUTATION_PROBABILITY = new String("mutationProbability");
	private final static String _FITNESS_PROBABILITY = new String("fitnessProbability");
	private final static String _FITNESS_MARGIN_ERROR = new String("fitnessMarginOfError");
	private final static String _OPERATORS = new String("operators");
	private final static String _OPERANDS = new String("operands");
	private final static String _CROSSOVER_SIZE = new String("numberOfCrossOvers");
	private final static String _POPULATION_SIZE = new String("populationSize");
	private final static String _TRAINING_DATA_SIZE = new String("trainingDataSize");
	private final static String _SUBTREE_HEIGHT = new String("maxSubtreeHeight");
	private final static String _CROSSOVER_SUBTREE_HEIGHT = new String("maxCrossoverSubtreeHeight");

	/**
	 * Define Default (Constant) Values for the Settings
	 */
	private final static String _DEFAULT_RANDOM_SEED = new String("12345");
	private final static String _DEFAULT_MUTATION_PROBABILITY = new String("0.70");
	private final static String _DEFAULT_FITNESS_PROBABILITY = new String("0.50");
	private final static String _DEFAULT_FITNESS_MARGIN_ERROR = new String("0.01");
	private final static String _DEFAULT_OPERATORS = new String("+,-,*,/");
	private final static String _DEFAULT_OPERANDS = new String("0,1,2,3,4,5,6,7,8,9,x");
	private final static Integer _DEFAULT_CROSSOVER_RATIO = new Integer(4);
	private final static String _DEFAULT_MAX_POPULATION_SIZE = new String("100");
	private final static String _DEFAULT_MAX_TRAINING_DATA_SIZE = new String("10");
	private final static String _DEFAULT_MAX_SUBTREE_HEIGHT = new String("4");
	private final static String _DEFAULT_MAX_CROSSOVER_HEIGHT = new String("20");

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
		// extracted from the properties file.
		xRandomGenerator = new Random(getRandomSeed());

		// Construct the vector stubs for the operators and the operands that will be populated later.
		xOperators = new Vector<String>();
		xOperands = new Vector<String>();
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

	// TODO: Finish this by adding a setOperatorProperty() that will perform the add/contains on the vector and also
	// still update the properties file.
//	public void setOperators(Vector<String> inOperators) {
//		operators = inOperators;
//	}

	// TODO: Finish this by adding a setOperandProperty() that will perform the add/contains on the vector and also
	// still update the properties file.
//	public void setOperands(Vector<String> inOperands) {
//		operands = inOperands;
//	}

	/**
	 * @return the initial random seed value for GP algorithms
	 */
	private Integer getRandomSeed() {
		return getIntProperty(_RANDOM_SEED, _DEFAULT_RANDOM_SEED);
	}

	/**
	 * @param aRandomSeedValue the new random seed value for this property
	 */
	public static void setRandomSeed(Integer aRandomSeedValue) {
		setIntProperty(_RANDOM_SEED, aRandomSeedValue);
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
	 * @return the initial training data size for the GP algorithms
	 */
	public static Integer getTrainingDataSize() {
		return getInstance().getIntProperty(_TRAINING_DATA_SIZE, _DEFAULT_MAX_TRAINING_DATA_SIZE);
	}

	/**
	 * @param aTrainingDataSize the new training data size value for this property
	 */
	public static void setTrainingDataSize(Integer aTrainingDataSize) {
		setIntProperty(_TRAINING_DATA_SIZE, aTrainingDataSize);
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
	 * @param aKey the property name (key) to lookup for the property
	 * @return the logical (boolean) value of the property
	 */
	private boolean getBooleanProperty(String aKey) {
		return getBooleanProperty(aKey, null);
	}

	/**
	 * @param aKey the property name (key) to lookup for the property
	 * @param aDefaultValue the default value to use if no property exists
	 * @return the logical (boolean) value of the property
	 */
	private boolean getBooleanProperty(String aKey, String aDefaultValue) {
		return Boolean.parseBoolean(getProperty(aKey, aDefaultValue));
	}

	/**
	 * @param aKey the property name (key) to lookup for the property
	 * @return the numeric (integer) value of the property
	 */
	private int getIntProperty(String aKey) {
		return getIntProperty(aKey, null);
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
	 * @return the big numeric (long integer) value of the property
	 */
	private long getLongProperty(String aKey) {
		return getLongProperty(aKey, null);
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
	 * @return the floating point (double) value of the property
	 */
	private double getDoubleProperty(String aKey) {
		return getDoubleProperty(aKey, null);
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
				xOutputStream = new FileOutputStream(new File(xOutputPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xOutputPropertiesFile
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
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue
					+ " into the output properties file [" + xOutputPropertiesFile + "].");
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
				xOutputStream = new FileOutputStream(new File(xOutputPropertiesFile));
			} catch (IOException e) {
				System.err.println("IOException: Unable to open output properties file [" + xOutputPropertiesFile
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
			System.err.println("IOException: Unable to store " + aKey + "=" + aValue
					+ " into the output properties file [" + xOutputPropertiesFile + "].");
		}
	}
}
