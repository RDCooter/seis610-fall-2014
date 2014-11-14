package edu.stthomas.seis610.gp;

import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.util.GPException;
import edu.stthomas.seis610.util.GPSimpleFormatter;

class GPMain extends java.lang.Thread {
	private static final Logger Log = Logger.getLogger("Global");
	private GPGeneration xGeneration;

	/**
	 * @param args
	 */
	static volatile boolean Stop = false;

	public static void main(String args[]) throws InterruptedException {
		
		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		Log.addHandler(myHandler);
		Log.setUseParentHandlers(false);

		GPMain thread = new GPMain();
		thread.setPriority(7); // 1st thread at 4th non-RT priority
		thread.start(); // start 1st thread to execute run()
		Thread.sleep(900 * 1000);

		Stop = true;

	}

	public void run() {
		xGeneration = new GPGeneration();
		try {
			Log.info("Starting the GP Program...");

			Integer generationCount = 1;
			Integer maxGenerations = GPSettings.getMaxGenerations();
			FitnessDatum fitnessGoal = new FitnessDatum(GPSettings.getFitnessMarginOfError());

			// Print out the Training Data Set
			Vector<TrainingData> trainingDataSet = GPSettings.getTrainingData();
			Log.info("Training Data Set [size=" + trainingDataSet.size() + "]:");
			for (int i = 0; i < trainingDataSet.size(); i++) {
				Log.info("[" + i + "]  " + trainingDataSet.elementAt(i));
			}

			// Process the first generation...
			xGeneration.init();
			Log.fine("Generation " + generationCount + ": [size=" + xGeneration.getPopulation().size() + "]: \n"
					+ xGeneration);

			GeneticProgrammingTree currentBestIndividual = xGeneration.getBestIndividual();
			FitnessDatum minFitness = currentBestIndividual.getFitness();
			Log.info("Generation " + generationCount + ": [height=" + currentBestIndividual.getHeight() + " fitness="
					+ currentBestIndividual.getFitness() + "]  " + currentBestIndividual.toString());

			// Loop until we find the best individual
			while (!Stop && minFitness.compareTo(fitnessGoal) >= 0 && generationCount < maxGenerations) {

				xGeneration = xGeneration.nextGeneration();
				Log.fine("Generation " + generationCount + ": [size=" + xGeneration.getPopulation().size() + "]: \n"
						+ xGeneration);

				generationCount++;
				currentBestIndividual = xGeneration.getBestIndividual();
				minFitness = currentBestIndividual.getFitness();

				Log.info("Generation " + generationCount + ": [height=" + currentBestIndividual.getHeight()
						+ " fitness=" + currentBestIndividual.getFitness() + "]  " + currentBestIndividual.toString());

			}
			Log.info("\nBEST INDIVIDUAL FOUND!!!\nGeneration " + generationCount + ": [height=" + currentBestIndividual.getHeight()
					+ " fitness=" + currentBestIndividual.getFitness() + " isValid=" + currentBestIndividual.isTreeValid() + "]  \n" + currentBestIndividual.toString());
			Log.info("End of GP Program...");
		} catch (GPException e) {
			Log.severe("Problem encountered during main line processing: " + e.getMessage());
			e.printStackTrace();
		}
		Thread.yield();
	}

}
