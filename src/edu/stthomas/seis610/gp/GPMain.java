package edu.stthomas.seis610.gp;

import java.util.Collections;
import java.util.Vector;

import edu.stthomas.seis610.tree.GPTreeFactory;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.util.GPException;

class GPMain extends java.lang.Thread {

	/**
	 * @param args
	 */
	static volatile boolean Stop = false;

	private static Integer minimumValIdx(Vector<Double> inputVector) {

		Integer minValIdx = 0;
		if (inputVector.size() != 0) {
			minValIdx = 0;
		}
		for (int i = 0; i < inputVector.size(); i++) {
			if (inputVector.elementAt(minValIdx) > inputVector.elementAt(i)) {
				minValIdx = i;
			}
		}
		return minValIdx;
	}

	public static void main(String args[]) throws InterruptedException {

		GPMain thread = new GPMain();
		thread.setPriority(7); // 1st thread at 4th non-RT priority
		thread.start(); // start 1st thread to execute run()
		Thread.sleep(900 * 1000);

		Stop = true;

	}

	public void run() {

		Vector<TrainingData> TrainingDataSet = GPSettings.getTrainingData();
		Vector<GeneticProgrammingTree> InitPopulation = new Vector<GeneticProgrammingTree>();
		Vector<GeneticProgrammingTree> NewPopulation = new Vector<GeneticProgrammingTree>();
		Vector<Double> Fitness = new Vector<Double>();
		Integer generationCount = 0;
		Vector<Boolean> valid = new Vector<Boolean>();

		// Print out the Training Data Set
		System.out.println("Training Data Set [size=" + TrainingDataSet.size() + "]:");
		for (int i = 0; i < TrainingDataSet.size(); i++) {
			System.out.println("[" + i + "]  " + TrainingDataSet.elementAt(i));
		}

		// Prepare the Initial Population
		for (int i = 0; i < GPSettings.getPopulationSize(); i++) {
			InitPopulation.add(i, GPTreeFactory.generateFullTree(GPSettings.getMaxHtOfInitTree()));
		}

		Double minFitness = GPSettings.getFitnessMarginOfError() + 1;

		// while (!Stop) { // continue until asked to stop

		for (int i = 0; i < GPSettings.getPopulationSize(); i++) {
			valid.add(i, true);
		}
		// System.out.println("Value of Stop: "+Stop);
		while (!Stop) {

			generationCount++;
			// Compute the fitness of the Initial Population
			for (int i = 0; i < GPSettings.getPopulationSize(); i++) {
				valid.set(i, true);
				InitPopulation.elementAt(i).reset();

//				Double iFitness = 0.0;
				Double iFitness = InitPopulation.elementAt(i).calculateFitness();
//				for (int j = 0; j < TrainingDataSet.size(); j++) {
//					try {
//						Double iOutput = InitPopulation.elementAt(i).evaluate(TrainingDataSet.elementAt(j));
//						Double jDelta = Math.abs(iOutput - TrainingDataSet.elementAt(j).getOutputData());
//						iFitness += jDelta;
//					} catch (GPException e) {
//						// When an error occurs during the evaluation, then simply add in the biggest standardized fitness value
//						// into the datum.
//						iFitness += Double.MAX_VALUE;
//						e.printStackTrace();
//					}
//				}
				if (InitPopulation.elementAt(i).isTreeValid() == false) {
					valid.set(i, false);
				}

				Fitness.add(i, iFitness);
			}
			minFitness = Collections.min(Fitness);
			System.out.println();
			System.out.println("\nFitness: " + minFitness);
			Integer minIdxx = minimumValIdx(Fitness);
			System.out.println("Generation: " + generationCount);
			System.out.println("Ht: " + InitPopulation.elementAt(minIdxx).getHeight());
			System.out.println(InitPopulation.elementAt(minIdxx));
			System.out.println("\nbinarytree valid or not: " + valid.elementAt(minIdxx));

			if (minFitness < GPSettings.getFitnessMarginOfError()) {
				if (valid.elementAt(minIdxx) != false) {
					Stop = true;
					break;
				}

			}

			Double numFitMembers = GPSettings.getPopulationSize() * GPSettings.getFitnessProbability();
			for (int i = 0; i < numFitMembers; i++) {
				Integer minIdx = minimumValIdx(Fitness);
				NewPopulation.add(InitPopulation.elementAt(minIdx));
				InitPopulation.removeElementAt(minIdx);
				Fitness.removeElementAt(minIdx);
			}
			Fitness.clear();
			InitPopulation.clear();

			// Prepare Crossover gpTrees
			for (int i = 0; i < GPSettings.getNumCrossOvers(); i++) {
				Integer populationIdx = GPSettings.getRandomInt(NewPopulation.size());
				GeneticProgrammingTree aGPT = (GeneticProgrammingTree) NewPopulation.elementAt(populationIdx).clone();
//				BinaryTreeNode aNode = (BinaryTreeNode) NewPopulation.elementAt(populationIdx).getRoot().clone();
				populationIdx = GPSettings.getRandomInt(NewPopulation.size());
				GeneticProgrammingTree bGPT = (GeneticProgrammingTree) NewPopulation.elementAt(populationIdx).clone();
//				BinaryTreeNode bNode = (BinaryTreeNode) NewPopulation.elementAt(populationIdx).getRoot().clone();
//				GeneticProgrammingTree aGPT = new GeneticProgrammingTree(aNode);
//				GeneticProgrammingTree bGPT = new GeneticProgrammingTree(bNode);

				aGPT.crossOver(bGPT);

				NewPopulation.add(aGPT);
				NewPopulation.add(bGPT);

			}

			// Do some mutation
			for (int i = 0; i < GPSettings.getMutationProbability() * NewPopulation.size(); i++) {
				Integer populationIdx = GPSettings.getRandomInt(NewPopulation.size());

				try {
//					NewPopulation.elementAt(populationIdx).mutate();
					NewPopulation.elementAt(populationIdx).mutate_new();
				} catch (GPException e) {
					System.out.println("ERROR: <GeneticProgrammingTree.mutate()>: " + e.getMessage());
				}

			}

			for (int i = 0; i < NewPopulation.size(); i++) {
				InitPopulation.add(NewPopulation.elementAt(i));
			}
			NewPopulation.clear();

		}
		Thread.yield();
	}
	// }

}
