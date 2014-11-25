package edu.stthomas.seis610.gp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stthomas.seis610.gp.GPGeneration.GenerationMethod;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;
import edu.stthomas.seis610.util.GPException;
import edu.stthomas.seis610.util.GPSimpleFormatter;

class GPMain extends java.lang.Thread {
	private static final Logger Log = Logger.getLogger("Global");
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
	private static final String COMMA = ",", NEWLINE = "\n";
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
	private static final int MAX_GP_TIME = 1000 * 60 * 15; // 15 minutes
	private static final int MAX_GP_SAMPLES_TIME = 1000 * 60 * 1200; // 180 minutes
	private static final int MAX_SAMPLE_SIZE = 100;
	private static final int RESTART_POPULATION_THRESHOLD = 10;

	private GPGeneration xGeneration;
	private static File xCsvFile;

	/**
	 * Displays a message preceded by the name of the current thread.
	 * 
	 * @param aMessage the string message to send associated with the thread
	 */
	private static void threadMessage(String aMessage) {
		String threadName = Thread.currentThread().getName();
		System.out.format("%s: %s%n", threadName, aMessage);
	}

	/**
	 * Calculates and formats the elapsed time into a readable string format.
	 * 
	 * @param aElapsedTime the elapsed time value to format
	 * @returns the formatted string representation of the elapsed time
	 */
	private static String calculateElapsedTime(long aElapsedTime) {
		long elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(aElapsedTime);
		long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(aElapsedTime)
				- TimeUnit.MINUTES.toSeconds(elapsedMinutes);
		long elapsedMillis = TimeUnit.MILLISECONDS.toMillis(aElapsedTime) - TimeUnit.MINUTES.toMillis(elapsedMinutes)
				- TimeUnit.SECONDS.toMillis(elapsedSeconds);

		return String.format("%02d:%02d.%d", elapsedMinutes, elapsedSeconds, elapsedMillis);
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) throws InterruptedException {

		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		Log.addHandler(myHandler);
		Log.setUseParentHandlers(false);

		xCsvFile = new File("GPResults." + SIMPLE_FORMAT.format(new Date()) + ".csv");

		/*
		 * Build and Start the Thread that will perform the GP.
		 */
		threadMessage("Starting GPMain thread");
		long mainStartTime = System.currentTimeMillis();
		GPMain gpThread = new GPMain();
		gpThread.setPriority(Thread.NORM_PRIORITY + 2);
		gpThread.start();

		/*
		 * Loop and wait while the thread is active and processing the GP.
		 */
		threadMessage("Waiting for the GPMain thread to finish");
		int loopCnt = 0;
		while (gpThread.isAlive()) {
			/*
			 * Wait a maximum of 60 seconds for the GP thread to finish.
			 */
			gpThread.join(1000 * 60);
			loopCnt++;

			if (((System.currentTimeMillis() - mainStartTime) > MAX_GP_SAMPLES_TIME) && gpThread.isAlive()) {
				/*
				 * Shouldn't be long now... wait indefinitely for the thread to be notified and end.
				 */
				threadMessage("Exceeded the max time for all GP processing!");
				gpThread.interrupt();
				gpThread.join();
			} else {
				threadMessage("[" + loopCnt + "]:  Still waiting for GPMain to finish... (elapsed time="
						+ calculateElapsedTime((System.currentTimeMillis() - mainStartTime)) + ")");
			}
		}
		threadMessage("GPMain thread has finally finished with " + loopCnt + " iterations in "
				+ calculateElapsedTime((System.currentTimeMillis() - mainStartTime)) + " (mm:ss.mili) !");
	}

	@Override
	public void run() {
		ArrayList<GenerationMethod> generationMethods = new ArrayList<>(Arrays.asList(GenerationMethod.FULL,
				GenerationMethod.GROW, GenerationMethod.RAMPED_FULL, GenerationMethod.RAMPED_GROW,
				GenerationMethod.RAMPED_HALF_AND_HALF));
		ArrayList<Integer> populationSizes = new ArrayList<>(Arrays.asList(100, 200, 300, 400, 500, 600, 700, 800, 900,
				1000));
		ArrayList<Integer> tournamentSizes = new ArrayList<>(Arrays.asList(4, 6, 8, 10));

		/*
		 * Write the Header for the CSV File only once.
		 */
		writeCsv(true);

		for (GenerationMethod genMethod : generationMethods) {
			// Update the Generation Method as we process the different permutations.
			GPSettings.setGenerationMethod(genMethod);

			for (Integer populationSize : populationSizes) {
				// Update the Population Size as we process the different permutations.
				GPSettings.setPopulationSize(populationSize);

				for (Integer tournamentSize : tournamentSizes) {
					// Update the Tournament Size as we process the different permutations.
					GPSettings.setTournamentSize(tournamentSize);

					/*
					 * Create a number of permutations (samples) for each combination of settings so we can get a good
					 * average.
					 */
					for (int i = 0; i < MAX_SAMPLE_SIZE; i++) {
						/*
						 * Perform the GP processing with a new Random Generator (each time).
						 */
						GPSettings.recycleRandomGenerator();
						processGP();

						/*
						 * Test for the thread to be finished.
						 */
						if (isInterrupted()) {
							threadMessage("Thread has been notified that it is interrupted and should prepare to finish.");
							return;
						}
					}
					writeCsv(false); // Simply add a blank line to the CSV file between samples.
				}
			}
		}

	}

	public void processGP() {
		int duplicateIndividualCnt = 0, injectNewDNACount = 0;
		int injectNewDNAThreshold = (int) Math.round(GPSettings.getMaxGenerations() * 0.001);

		xGeneration = new GPGeneration();
		long gpStartTime = System.currentTimeMillis();
		try {
			Log.info("Starting the GP Program...");

			Integer generationCount = 1;
			Integer maxGenerations = GPSettings.getMaxGenerations();
			FitnessDatum fitnessGoal = new FitnessDatum(GPSettings.getFitnessMarginOfError());

			// Print out the Training Data Set
			Vector<TrainingData> trainingDataSet = GPSettings.getTrainingData();
			Log.fine("Training Data Set [size=" + trainingDataSet.size() + "]:");
			for (int i = 0; i < trainingDataSet.size(); i++) {
				Log.fine("[" + i + "]  " + trainingDataSet.elementAt(i));
			}

			// Process the first generation...
			xGeneration.init();
			Log.fine("Generation " + generationCount + ": [size=" + xGeneration.getPopulation().size() + "]: \n"
					+ xGeneration);

			GeneticProgrammingTree currentBestIndividual = xGeneration.getBestIndividual();
			GeneticProgrammingTree firstBestIndividual = currentBestIndividual;
			GeneticProgrammingTree previousBestIndividual = currentBestIndividual;

			FitnessDatum minFitness = currentBestIndividual.getFitness();
			Log.info("First Generation " + generationCount + ": [height=" + currentBestIndividual.getHeight()
					+ " fitness=" + currentBestIndividual.getFitness() + "]  " + currentBestIndividual.toString());

			// Loop until we find the best individual
			while (minFitness.compareTo(fitnessGoal) >= 0 && generationCount < maxGenerations
					&& ((System.currentTimeMillis() - gpStartTime) <= MAX_GP_TIME)) {

				/*
				 * Determine how to generate the next population. If we have reached an interval of close to 1% of total
				 * attempts without any progress, then completely start over. Otherwise determine if we should inject
				 * some new DNA (every 0.1%) interval or simply use normal evolution to generate the next generation.
				 */
				if (injectNewDNACount > RESTART_POPULATION_THRESHOLD) {
					/*
					 * Completely throw away the current population and start over from scratch with a new random
					 * initial population in the hopes that we don't end up back into a corner with an individual with
					 * same fitness score for such a large percentage of the total attempts.
					 */
					Log.warning("Population was been abandoned and recreated from scratch because "
							+ duplicateIndividualCnt + " generations have made no progress!");
					Log.warning("Abandoned Generation " + generationCount + ": [height="
							+ currentBestIndividual.getHeight() + " fitness=" + currentBestIndividual.getFitness()
							+ " isValid=" + currentBestIndividual.isTreeValid() + "]  "
							+ currentBestIndividual.toString());
					xGeneration = new GPGeneration();
					xGeneration.init();
					injectNewDNACount = 0;
				} else if ((duplicateIndividualCnt / injectNewDNAThreshold) > injectNewDNACount) { // Integer division
					/*
					 * The current generation appears to have stalled and we have not make any progress toward our goal
					 * for a number of generations, inject some new DNA into the next generation but still allow the
					 * existing generation to bring along its best individuals. Hopefully this will kick-start the
					 * process and we start making progress again.
					 */
					Log.warning("New DNA has been added to the population because " + duplicateIndividualCnt
							+ " generations have made no progress!");
					xGeneration = xGeneration.nextGeneration(true);
					injectNewDNACount++;
				} else {
					/*
					 * Should continue to perform normal evolution of the generations and only use the current
					 * generation as a source of DNA for the next generation as we keep making progress towards our
					 * goal.
					 */
					xGeneration = xGeneration.nextGeneration(false);
					Log.fine("Generation " + generationCount + ": [size=" + xGeneration.getPopulation().size()
							+ "]: \n" + xGeneration);
				}

				/*
				 * Determine if the best individual for this generation is a duplicate of the previous generation and
				 * keep track of how often this occurs so we don't end up with a generation that cannot evolve towards
				 * our goal with injecting some new DNA or starting over.
				 */
				if (currentBestIndividual.compareTo(previousBestIndividual) == 0)
					duplicateIndividualCnt++;
				else {
					// restart the counters since we had made some progress in evolution!
					duplicateIndividualCnt = 0;
					injectNewDNACount = 0;
				}
				generationCount++;
				previousBestIndividual = currentBestIndividual;
				currentBestIndividual = xGeneration.getBestIndividual();
				minFitness = currentBestIndividual.getFitness();

				Log.fine("Generation " + generationCount + ": [height=" + currentBestIndividual.getHeight()
						+ " fitness=" + currentBestIndividual.getFitness() + "]  " + currentBestIndividual.toString());

			}
			long timeElapsed = System.currentTimeMillis() - gpStartTime;

			// Log.info("\nBEST INDIVIDUAL FOUND!!!\nGeneration " + generationCount + ": [height="
			// + currentBestIndividual.getHeight() + " fitness=" + currentBestIndividual.getFitness()
			// + " isValid=" + currentBestIndividual.isTreeValid() + "]  \n" + currentBestIndividual.toString());
			Log.info("Final Generation " + generationCount + ": [height=" + currentBestIndividual.getHeight()
					+ " fitness=" + currentBestIndividual.getFitness() + " isValid="
					+ currentBestIndividual.isTreeValid() + "]  " + currentBestIndividual.toString());
			Log.info("It took " + calculateElapsedTime(timeElapsed) + " (mm:ss.mili) to find the best individual!");
			Log.info("End of GP Program...\n\n");

			writeCsvResults(generationCount, currentBestIndividual, firstBestIndividual, timeElapsed);
		} catch (GPException e) {
			Log.severe("Problem encountered during main line processing: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void writeCsvResults(Integer aGenerationCnt, GeneticProgrammingTree aFinalIndividual,
			GeneticProgrammingTree aFirstBestIndividual, long aElapsedTime) {
		try {
			FileWriter csvWriter = new FileWriter(xCsvFile, true);

			// Write the number of generations it took to find the best plan
			csvWriter.append(aGenerationCnt.toString());

			// Write the elapsed time it took to determine the best plan for the final generation.
			csvWriter.append(COMMA);
			csvWriter.append(Long.toString(aElapsedTime));
			csvWriter.append(COMMA);
			csvWriter.append("\" " + calculateElapsedTime(aElapsedTime) + " \"");

			// Write Size Settings
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getPopulationSize().toString());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getTournamentSize().toString());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getMaxGenerations().toString());

			// Write Height Settings
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getMaxHtOfInitTree().toString());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getMaxHtOfCrossoverTree().toString());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getMaxHtOfMutationSubtree().toString());

			// Write String Value Settings
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getGenerationMethod());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getReproductionMethod());
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getTrainingInputString().replaceAll(",", " "));

			// Write Probability Value Settings
			csvWriter.append(COMMA);
			csvWriter.append(GPSettings.getFitnessMarginOfError().toString());
			csvWriter.append(COMMA);
			csvWriter.append(PERCENT_FORMAT.format(GPSettings.getCrossoverProbability()));
			csvWriter.append(COMMA);
			csvWriter.append(PERCENT_FORMAT.format((1.0 - GPSettings.getCrossoverProbability())));
			csvWriter.append(COMMA);
			csvWriter.append(PERCENT_FORMAT.format(GPSettings.getMutationProbability()));

			// Write the details for the best plan from the final generation.
			csvWriter.append(COMMA);
			csvWriter.append(aFinalIndividual.getFitness().toString());
			csvWriter.append(COMMA);
			csvWriter.append(aFinalIndividual.getHeight().toString());
			csvWriter.append(COMMA);
			csvWriter.append(aFinalIndividual.isTreeValid().toString());

			// Write the details for the best plan from the first generation
			csvWriter.append(COMMA);
			csvWriter.append(aFirstBestIndividual.getFitness().toString());
			csvWriter.append(COMMA);
			csvWriter.append(aFirstBestIndividual.getHeight().toString());
			csvWriter.append(COMMA);
			csvWriter.append(aFirstBestIndividual.isTreeValid().toString());

			// Write the tree for the best (final generation) plan and the best plan for the first generations.
			csvWriter.append(COMMA);
			csvWriter.append(aFinalIndividual.toString());
			csvWriter.append(COMMA);
			csvWriter.append(aFirstBestIndividual.toString());

			// Write a new line character at the end of this record.
			csvWriter.append(NEWLINE);

			// Make sure the results are written and flushed out to the file.
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException ex) {
			Log.log(Level.SEVERE, "Error attempting to write the CSV ", ex);
			ex.printStackTrace();
		}
	}

	public void writeCsv(boolean aHeader) {
		try {
			FileWriter csvHeader = new FileWriter(xCsvFile, true);

			if (aHeader) {
				// Write the number of generations it took to find the best plan
				csvHeader.append("Generation");

				// Write the elapsed time it took to determine the best plan for the final generation.
				csvHeader.append(COMMA);
				csvHeader.append("Elapsed Time (milliseconds)");
				csvHeader.append(COMMA);
				csvHeader.append("Elapsed Time (mm:ss.mili)");

				// Write the details of the Settings used to find this GP.
				csvHeader.append(COMMA);
				csvHeader.append("Population Size,Tournament Size,Max Generations");
				csvHeader.append(COMMA);
				csvHeader.append("Initial Tree Height,Max Tree Height,Mutation Tree Height");
				csvHeader.append(COMMA);
				csvHeader.append("Initial Generation Method,Reproduction Method,Input Training Data");
				csvHeader.append(COMMA);
				csvHeader
						.append("Final Fitness Goal,Crossover Probability,Reproduction Probability,Mutation Probability");

				// Write the details for the best plan from the final generation.
				csvHeader.append(COMMA);
				csvHeader.append("Final Fitness,Final Height,Final isValid");

				// Write the details for the best plan from the first generation
				csvHeader.append(COMMA);
				csvHeader.append("First Fitness,First Height,First isValid");

				// Write the tree for the best (final generation) plan and the best plan for the first generations.
				csvHeader.append(COMMA);
				csvHeader.append("Final Tree,First Tree");
			}

			// Write a new line character at the end of this record.
			csvHeader.append(NEWLINE);

			// Make sure the results are written and flushed out to the file.
			csvHeader.flush();
			csvHeader.close();
		} catch (IOException ex) {
			Log.log(Level.SEVERE, "Error attempting to write the CSV ", ex);
			ex.printStackTrace();
		}
	}
}
