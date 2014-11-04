/**
 * 
 */
package gp;

import java.util.Vector;



public class Settings {
	
	static private Double mutationProbability;
	static private Double fitnessProbability;
	static private Double fitnessMarginOfError;
	static private Vector <String> operators;
	static private Vector <String> operands;
	static private Integer maxHtOfInitTree;
	static private Integer populationSize;
	static private Integer numCrossOvers;
	static private Integer trainingDataSize;
	
	public Double getMutationProbability() {
	
		return mutationProbability;
		
	}
	
	public Double getFitnessMarginOfError() {
		
		return fitnessMarginOfError;
		
	}
	
	public Vector <String> getOperators() {
		
		return operators;
		
	}
	
	public Vector <String> getOperands() {
		
		return operands;
		
	}

	public void setOperators(Vector <String> inOperators) {
		
		operators=inOperators;
		
	}
	
	public void setOperands(Vector <String> inOperands) {
		
		operands=inOperands;
		
	}

	public Integer getMaxHtOfInitTree() {
		
		return maxHtOfInitTree;
		
	}
	
	public Integer getPopulationSize() {
		
		return populationSize;
		
	}
	
	public Integer getNumCrossOvers() {
		
		return numCrossOvers;
		
	}	
	
	public void setMutationProbability(Double aMutationProbability) {
		
		mutationProbability = aMutationProbability;
		
	}
	
	public void setFitnessMarginOfError(Double aFitnessMarginOfError) {
		
		fitnessMarginOfError = aFitnessMarginOfError;
		
	}
	
	public void setMaxHtOfInitTree(Integer aMaxHtOfInitTree) {
		
		maxHtOfInitTree = aMaxHtOfInitTree;
	}
	
	public void setPopulationSize(Integer aPopulationSize) {
		
		populationSize = aPopulationSize;
		
	}
	
	public void setNumCrossOvers(Integer aNumCrossOvers) {
		
		numCrossOvers = aNumCrossOvers;
		
	}

	public void setTrainingDataSize(Integer aTrainingDataSize) {
		trainingDataSize = aTrainingDataSize;
	}

	public Integer getTrainingDataSize() {
		return trainingDataSize;
	}

	/**
	 * @param fitnessProbability the fitnessProbability to set
	 */
	public void setFitnessProbability(Double fitnessProbability) {
		Settings.fitnessProbability = fitnessProbability;
	}

	/**
	 * @return the fitnessProbability
	 */
	public Double getFitnessProbability() {
		return fitnessProbability;
	}	
}
