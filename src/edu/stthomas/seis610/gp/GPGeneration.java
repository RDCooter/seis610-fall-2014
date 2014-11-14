package edu.stthomas.seis610.gp;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import edu.stthomas.seis610.tree.BinaryTreeNode;
import edu.stthomas.seis610.tree.BinaryTreeNode.NodeType;
import edu.stthomas.seis610.tree.GPTreeFactory;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.util.GPException;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
 * Creates and own a specific generation of GP trees and their entire population of GP trees. Will control aspects such
 * as initial generation, reproduction and mutation between generations.
 * 
 * @author Robert Driesch (cooter) Nov 12, 2014 2:25:18 PM
 * @version 1.2
 */
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class GPGeneration {
	/**
	 * Define Enumerations to Describe the types of Generation Methods
	 */
	public enum GenerationMethod {
		FULL, GROW, RAMPED_FULL, RAMPED_GROW, RAMPED_HALF_AND_HALF;
	};

	public enum ReproductionMethod {
		NATURAL_SELECTION, TOURNAMENT_SELECTION;
	};

	private static final Logger Log = Logger.getLogger("Global");
	private Vector<GeneticProgrammingTree> xPopulation;

	private Vector<GeneticProgrammingTree> xCrossoverIndividuals;
	private Vector<GeneticProgrammingTree> xReproducedIndividuals;
	private Vector<GeneticProgrammingTree> xTournamentIndividuals;

	/**
	 * Default constructor for this class
	 */
	public GPGeneration() {
		Integer populationSize = GPSettings.getPopulationSize();

		xPopulation = new Vector<GeneticProgrammingTree>(populationSize);

		xCrossoverIndividuals = new Vector<GeneticProgrammingTree>(getCrossoverCount());
		xReproducedIndividuals = new Vector<GeneticProgrammingTree>(getReproductionCount());
		xTournamentIndividuals = new Vector<GeneticProgrammingTree>(GPSettings.getTournamentSize());
	}

	/**
	 * Populates the initial generation with the technique specified within the Settings. Can be either a Full or Grow
	 * technique or a ramped version of either of these two where the initial height of the tree is adjusted creating a
	 * variety of different types of trees for the GP algorithms to work against.
	 * <p>
	 * Lastly, we have the ramped half and half technique. This samples from both a full/bushy formatted tree along with
	 * a grown tree at a varying series of allowed heights. This way we are giving the GP algorithms the broadest sample
	 * of available types of trees to operate upon.
	 * <p>
	 * Note: While we break apart the initial population size to account for the half and half portion of this
	 * technique, no attempt is made to make sure that any remaining slots in the initial population are filled in at
	 * the end. This means that we might end up with fewer individuals in the initial population then the settings
	 * suggest. This should get rectified after we spawn the next generation.
	 * 
	 * @throws GPException
	 */
	public void init() {
		int maxPopulationSize = GPSettings.getPopulationSize();
		int maxInitialHeight = GPSettings.getMaxHtOfInitTree();
		GenerationMethod genMethod = GenerationMethod.valueOf(GPSettings.getGenerationMethod());
		int batchCount;

		// Get the type of subtree generation that we are supposed to perform from the Settings and invoke the correct
		// generation method(s) based upon the selection.
		switch (genMethod) {
		case RAMPED_HALF_AND_HALF:
			/*
			 * For the Ramped-Half-and-Half, we need to calculate the batchCount to make sure it takes into account that
			 * we will be processing both the Full and Grow methods for each pass through the loop (hence the * 2).
			 */
			batchCount = Math.max((maxPopulationSize / ((maxInitialHeight - 1) * 2)), 1);
			for (int rampedHeight = 2; rampedHeight <= maxInitialHeight; rampedHeight++) {
				for (int i = 0; i < batchCount; i++) {
					xPopulation.add(GPTreeFactory.generateFullTree(rampedHeight));
					xPopulation.add(GPTreeFactory.generateGrowTree(rampedHeight));
				}
			}
			break;
		case RAMPED_FULL:
		case RAMPED_GROW:
			batchCount = Math.max((maxPopulationSize / (maxInitialHeight - 1)), 1);
			for (int rampedHeight = 2; rampedHeight <= maxInitialHeight; rampedHeight++) {
				for (int i = 0; i < batchCount; i++) {
					if (genMethod == GenerationMethod.RAMPED_FULL) {
						xPopulation.add(GPTreeFactory.generateFullTree(rampedHeight));
					} else if (genMethod == GenerationMethod.RAMPED_GROW) {
						xPopulation.add(GPTreeFactory.generateGrowTree(rampedHeight));
					}
				}
			}
			break;
		case FULL:
			for (int cnt = 0; cnt < maxPopulationSize; cnt++) {
				xPopulation.add(GPTreeFactory.generateFullTree(maxInitialHeight));
			}
			break;
		case GROW:
			for (int cnt = 0; cnt < maxPopulationSize; cnt++) {
				xPopulation.add(GPTreeFactory.generateGrowTree(maxInitialHeight));
			}
		}

		/*
		 * Force all of the fitness scores to be calculated now that the population has been initialized.
		 */
		scoreFitness();

		Log.info("Population initialized [method=" + genMethod + "]:  ActualSize=" + xPopulation.size()
				+ "  TargetSize=" + maxPopulationSize);
		Log.config("Initial Population:  \n" + this);
	}

	/**
	 * @return the current population for this generation
	 */
	public Vector<GeneticProgrammingTree> getPopulation() {
		return xPopulation;
	}

	/**
	 * @param aPopulation represents the new population to assign to this generation
	 */
	public void setPopulation(Vector<GeneticProgrammingTree> aPopulation) {
		this.xPopulation = aPopulation;
	}

	/**
	 * Force all of the individuals within this population to have their trees scored for their fitness measurements.
	 */
	private void scoreFitness() {
		for (GeneticProgrammingTree currentIndividual : xPopulation) {
			currentIndividual.calculateFitness();
		}
	}

	/**
	 * Note: Between the reproduction and the crossover, we need to fully re-populate the next generation. So using the
	 * same setting for both makes the most sense since whatever probability is used for one of them, then the inverse
	 * can be used for the other and still reach 100%.
	 * 
	 * @return the number of expected reproduced individuals in the population for each generation
	 */
	private int getReproductionCount() {
		return Math.max(
				(int) Math.round((1.0 - GPSettings.getCrossoverProbability()) * GPSettings.getPopulationSize()), 1);
	}

	/**
	 * Note: Between the reproduction and the crossover, we need to fully re-populate the next generation. So using the
	 * same setting for both makes the most sense since whatever probability is used for one of them, then the inverse
	 * can be used for the other and still reach 100%.
	 * 
	 * @return the number of expected crossover individuals in the population for each generation
	 */
	private int getCrossoverCount() {
		return Math.max((int) Math.round(GPSettings.getCrossoverProbability() * GPSettings.getPopulationSize()), 2);
	}

	public GPGeneration nextGeneration() throws GPException {
		GPGeneration nextGeneration = new GPGeneration();
		nextGeneration.getPopulation().addAll(reproduction());
		nextGeneration.getPopulation().addAll(crossover());
		nextGeneration.mutate();
		nextGeneration.scoreFitness();
		return nextGeneration;
	}

	/**
	 * @return the current best individual within the population for this generation
	 * @throws GPException
	 */
	public GeneticProgrammingTree getBestIndividual() {
		GeneticProgrammingTree bestIndividual = xPopulation.firstElement();
		for (GeneticProgrammingTree currentIndividual : xPopulation) {
			if (currentIndividual.getFitness().compareTo(bestIndividual.getFitness()) < 0) {
				bestIndividual = currentIndividual;
			}
		}

		return bestIndividual;
	}

	/**
	 * Starts to populate the next generation of individuals using the technique specified within the Settings. It will
	 * either choose to perform a version of natural selection where top-n individuals are selected or a form of a
	 * tournament selection where groups of random individuals are chosen to compete and the best from each tournament
	 * are selected to be a part of the next generation.
	 * <p>
	 * Note: No attempt is made to clone or make a copy of the individuals selected from either technique since we are
	 * not modifying the structure of the individuals at all. We are safe allowing a reference to existing individual to
	 * be used and passed along to the next generation instead.
	 * 
	 * @return a list of individuals for the next generation of the population chosen based upon the reproduction
	 *         technique
	 */
	public Vector<GeneticProgrammingTree> reproduction() {
		ReproductionMethod reproductionMethod = ReproductionMethod.valueOf(GPSettings.getReproductionMethod());
		int reproductionCount = getReproductionCount();
		xReproducedIndividuals.clear(); // Clear the cached variable
		if (reproductionMethod == ReproductionMethod.NATURAL_SELECTION) {
			// Use natural selection to extract the Top-N individuals from the population.

			List<GeneticProgrammingTree> lst = naturalSelection(reproductionCount);
			Log.finer("Natural Selection [size=" + lst.size() + "]:  Tree=" + lst);

			xReproducedIndividuals.addAll(lst);
		} else if (reproductionMethod == ReproductionMethod.TOURNAMENT_SELECTION) {
			// When dealing with a tournament selection technique, we should still bring along the current best
			// individual to the next generation.
//			xReproducedIndividuals.add(getBestIndividual());
//			reproductionCount = reproductionCount - 1;
			
			// Use a tournament selection technique to find the fittest individuals from a number of different random
			// selections (tournaments) of different individuals.
			for (int i = 0; i < reproductionCount; i++) {
				xReproducedIndividuals.add(tournamentSelection());
			}
		}

		return xReproducedIndividuals;
	}

	/**
	 * Choose the top-n individuals from the population based upon their fitness and return back a list of those
	 * individuals back to the invoker.
	 * 
	 * @param aTopIndividuals the count of the number of individuals to return
	 * @return a list of the top-n individuals for the next generation of the population
	 */
	private List<GeneticProgrammingTree> naturalSelection(int aTopIndividuals) {
		// Sort the entire population based upon the fitness measurement and simply return the top-n individuals back in
		// a new sublist.
		Collections.sort(xPopulation);
		Log.finer("Natural Selection [aTopIndividuals=" + aTopIndividuals + "]:  Tree=" + xPopulation);
		return xPopulation.subList(0, aTopIndividuals);
	}

	/**
	 * Choose the best (fittest) individual from a randomly selected list of individuals to compete in a tournament to
	 * find and return the best individual.
	 * 
	 * @return the best individual from a randomly selected list of individuals within the population
	 */
	private GeneticProgrammingTree tournamentSelection() {
		return tournamentSelection(0);
	}

	/**
	 * Recursive method to find the best (fittest) individual from a randomly selected list of individuals to compete in
	 * a tournament to return the best valid individual. If the fittest individual selected is not valid, then it will
	 * recursively request another tournament be performed until a valid winner is determined.
	 * 
	 * @param aRecursionLevel the current level of recursion to prevent excessive (runaway) processing
	 * @return the best individual from a randomly selected list of individuals within the population
	 */
	private GeneticProgrammingTree tournamentSelection(int aRecursionLevel) {
		int tournamentSize = GPSettings.getTournamentSize();
		xTournamentIndividuals.clear(); // Clear the cached variable
		for (int i = 0; i < tournamentSize; i++) {
			// Copy a reference to the individual over to the tournament list so we can easily determine the winner
			// without making any changes to the overall population.
			xTournamentIndividuals.add(xPopulation.elementAt(GPSettings.getRandomInt(xPopulation.size())));
		}
		// Sort the entire population of tournament individuals based upon the fitness measurement and simply return the
		// top (first) entry back.
		Collections.sort(xTournamentIndividuals);
		GeneticProgrammingTree bestIndividual = xTournamentIndividuals.firstElement();
		// If the winner of the tournament does not contain a valid tree or if the fitness measurement is not valid for
		// some reason, then recursively request a new tournament take place to determine the best individual.
		if (!bestIndividual.getFitness().isValid() || !bestIndividual.isTreeValid()) {
			// Log.warning("Tournament Selection found an invalid tree [recursion=" + aRecursionLevel + "]:  Tree="
			// + bestIndividual);
			if (aRecursionLevel >= 5) { // Enough already, just generate a new individual
				Log.warning("Recursion level too deep for tournament selection. Generating new tree from factory.");
				bestIndividual = GPTreeFactory.generateGrowTree(GPSettings.getMaxHtOfInitTree());
			} else {
				bestIndividual = tournamentSelection(++aRecursionLevel);
			}
		}

		return bestIndividual;
	}

	/**
	 * Choose a random sampling of individuals and perform crossover operations against those individuals and add the
	 * results back for the next generation.
	 * 
	 * @return a list of the individuals involved in the crossover operation for the next generation of the population
	 */
	private Vector<GeneticProgrammingTree> crossover() throws GPException {
		int crossoverCount = getCrossoverCount();
		xCrossoverIndividuals.clear(); // Clear the cached variable
		while (xCrossoverIndividuals.size() < crossoverCount) {
			GeneticProgrammingTree parent1 = tournamentSelection();
			GeneticProgrammingTree parent2 = tournamentSelection();
			crossoverOperation(parent1, parent2, xCrossoverIndividuals);
		}
		return xCrossoverIndividuals;
	}

	/**
	 * @param aParentX the source of the X chromosome in the crossover operation
	 * @param aParentY the source of the X chromosome in the crossover operation
	 * @param aCrosssoverList the list that will contain the results of the crossover operation
	 */
	private void crossoverOperation(GeneticProgrammingTree aParentX, GeneticProgrammingTree aParentY,
			Vector<GeneticProgrammingTree> aCrosssoverList) throws GPException {
		// Generate deep copies of each of the parent individuals before we start making any changes.
		GeneticProgrammingTree offspring1 = (GeneticProgrammingTree) aParentX.clone();
		GeneticProgrammingTree offspring2 = (GeneticProgrammingTree) aParentY.clone();

		// Reset the cached information within each individual so the fitness and the invalid indicators get reset
		// before the changes start happening.
		offspring1.reset();
		offspring2.reset();

		// Select a random node from each individual as the crossover operation point.
		BinaryTreeNode crossoverPoint1 = offspring1.getRandomTreeNode();
		BinaryTreeNode crossoverPoint2 = offspring2.getRandomTreeNode();

		// Now swap the selected nodes by updating the appropriate references
		BinaryTreeNode coPoint1Parent = crossoverPoint1.getParent();
		BinaryTreeNode coPoint2Parent = crossoverPoint2.getParent();

		NodeType coPoint1Type = crossoverPoint1.getNodeType();
		NodeType coPoint2Type = crossoverPoint2.getNodeType();

		if (coPoint1Type == BinaryTreeNode.NodeType.LEFT) {
			coPoint1Parent.setLeftChild(crossoverPoint2);
			crossoverPoint2.setNodeType(BinaryTreeNode.NodeType.LEFT);
			crossoverPoint2.setParent(coPoint1Parent);
		} else if (coPoint1Type == BinaryTreeNode.NodeType.RIGHT) {
			coPoint1Parent.setRightChild(crossoverPoint2);
			crossoverPoint2.setNodeType(BinaryTreeNode.NodeType.RIGHT);
			crossoverPoint2.setParent(coPoint1Parent);
		} else {
			offspring1.setRoot(crossoverPoint2);
			crossoverPoint2.setNodeType(BinaryTreeNode.NodeType.ROOT);
			crossoverPoint2.setParent(null);
		}

		if (coPoint2Type == BinaryTreeNode.NodeType.LEFT) {
			coPoint2Parent.setLeftChild(crossoverPoint1);
			crossoverPoint1.setNodeType(BinaryTreeNode.NodeType.LEFT);
			crossoverPoint1.setParent(coPoint2Parent);
		} else if (coPoint2Type == BinaryTreeNode.NodeType.RIGHT) {
			coPoint2Parent.setRightChild(crossoverPoint1);
			crossoverPoint1.setNodeType(BinaryTreeNode.NodeType.RIGHT);
			crossoverPoint1.setParent(coPoint2Parent);
		} else {
			offspring2.setRoot(crossoverPoint1);
			crossoverPoint1.setNodeType(BinaryTreeNode.NodeType.ROOT);
			crossoverPoint1.setParent(null);
		}

		// Check the resulting trees to determine if they are too large and they exceed the height limit from the
		// settings. If so then simply use the original parent instead.
		// aCrosssoverList.add((offspring1.getHeight() <= GPSettings.getMaxHtOfCrossoverTree())? offspring1 : aParentX);
		// aCrosssoverList.add((offspring2.getHeight() <= GPSettings.getMaxHtOfCrossoverTree())? offspring2 : aParentY);
		if (offspring1.getHeight() <= GPSettings.getMaxHtOfCrossoverTree()) {
			aCrosssoverList.add(offspring1);
		} else {
			// Log.warning("Height of crossover operation is too large, generating a new tree from the factory instead.");
			aCrosssoverList.add(GPTreeFactory.generateGrowTree(GPSettings.getMaxHtOfInitTree()));
		}
		if (offspring2.getHeight() <= GPSettings.getMaxHtOfCrossoverTree()) {
			aCrosssoverList.add(offspring2);
		} else {
			// Log.warning("Height of crossover operation is too large, generating a new tree from the factory instead.");
			aCrosssoverList.add(GPTreeFactory.generateGrowTree(GPSettings.getMaxHtOfInitTree()));
		}
	}

	/**
	 * Selects a random individual from the population and mutates the expression node tree contained within that
	 * individual.
	 * <p>
	 * Note: No attempt is made to make sure that the individual selected from the population is valid or not. For all
	 * that we know the mutation may make the currently invalid node tree valid and this could end up being the best
	 * individual of the whole series. It is safer to simply allow all individuals to participate in the mutation
	 * lottery.
	 */
	public void mutate() {
		StringBuffer outputBuf = new StringBuffer();
		outputBuf.append("\n");
		int mutateCount = (int) (getPopulation().size() * GPSettings.getMutationProbability());
		for (int i = 0; i < mutateCount; i++) {
			Integer mutateIndex = GPSettings.getRandomInt(getPopulation().size());
			try {
				outputBuf.append("Mutate BEFORE=" + fornmatIndividual(mutateIndex));
				// getPopulation().elementAt(mutateIndex).mutate();
				getPopulation().elementAt(mutateIndex).mutate_new();
				outputBuf.append("  AFTER=" + fornmatIndividual(mutateIndex) + "\n");
			} catch (GPException e) {
				// When an error occurs during the evaluation, then simply add in the biggest standardized fitness value
				// into the datum.
				Log.severe("Attempting to mutate index=" + mutateIndex + " tree=\""
						+ getPopulation().elementAt(mutateIndex) + "\"");
				e.printStackTrace();
			}
		}
		Log.fine(outputBuf.toString());
	}

	/**
	 * Convenience method to help format the individual population entries for output.
	 * 
	 * @param aIndividualIndex the index into the list of individuals to use as the source for the output
	 * @return the formatted string of the individual
	 */
	private String fornmatIndividual(int aIndividualIndex) {
		StringBuffer outputBuf = new StringBuffer();
		outputBuf.append("[" + aIndividualIndex + ":");
		outputBuf.append(" height=" + xPopulation.elementAt(aIndividualIndex).getHeight());
		outputBuf.append(" fitness=" + xPopulation.elementAt(aIndividualIndex).getFitness());
		outputBuf.append("]:  " + xPopulation.elementAt(aIndividualIndex).toString());
		return outputBuf.toString();
	}

	@Override
	public String toString() {
		StringBuffer outputBuf = new StringBuffer();
		for (int i = 0; i < xPopulation.size(); i++) {
			outputBuf.append(fornmatIndividual(i));
			outputBuf.append("\n");
		}
		return outputBuf.toString();
	}
}
