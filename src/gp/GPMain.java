package gp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

class GPMain extends java.lang.Thread {

	/**
	 * @param args
	 */
	static volatile boolean Stop = false;
	
	private static Integer minimumValIdx(Vector<Double> inputVector) {
		
		Integer minValIdx=0;
		if (inputVector.size()!=0) {
			minValIdx=0;
		}
		for (int i=0;i<inputVector.size();i++) {
			if (inputVector.elementAt(minValIdx) > inputVector.elementAt(i)) {
				minValIdx=i;
			}
		}
		return minValIdx;
	}
	
	public static void main(String args[]) throws InterruptedException {
			
			GPMain thread = new GPMain();
		    thread.setPriority(7);    // 1st thread at 4th non-RT priority
		    thread.start();           // start 1st thread to execute run()
		    Thread.sleep(900*1000);
		    
		    Stop = true;
		    
	}
	
	public void run() { 
		
		Settings InitSettings=new Settings();
		Vector<TrainingData> TrainingDataSet=new Vector<TrainingData>();
		Vector <GeneticProgrammingTree> InitPopulation=
			new Vector<GeneticProgrammingTree>();
		Vector <GeneticProgrammingTree> NewPopulation=
			new Vector<GeneticProgrammingTree>();
		Vector<Double> Fitness=new Vector<Double>();
		Integer generationCount=0;
		

		// Prepare Initial Settings
		
		PrepareInitialSettings(InitSettings);
		
		//Create the Training Data Set
		for (int i=0;i<InitSettings.getTrainingDataSize();i++) {
			int h=InitSettings.getTrainingDataSize()/2;
			TrainingDataSet.add(i, new TrainingData((double)(i-h), ((i-h)*(i-h)+1.0)/2.0));
			System.out.println((double)(i-h)+", "+((i-h)*(i-h)+1.0)/2.0);

		}

		//Prepare the Initial Population
		for (int i=0;i<InitSettings.getPopulationSize();i++) {
			InitPopulation.add(i, new GeneticProgrammingTree(
					InitSettings.getMaxHtOfInitTree()));
		}
		
		Double minFitness=InitSettings.getFitnessMarginOfError()+1;
		
		//while (!Stop) {    // continue until asked to stop
			
			//System.out.println("Value of Stop: "+Stop);
			while ((minFitness>InitSettings.getFitnessMarginOfError()) && !Stop) {
				
				generationCount++;
				//Compute the fitness of the Initial Population
				for (int i=0;i<InitSettings.getPopulationSize();i++) {
					Double iFitness=0.0;
					for (int j=0;j<InitSettings.getTrainingDataSize();j++) {
						Double iOutput=InitPopulation.elementAt(i).evaluate(
								TrainingDataSet.elementAt(j).getInputData());
						Double jDelta=Math.abs(iOutput-
								TrainingDataSet.elementAt(j).getOutputData());
						iFitness+=jDelta;
					}
					Fitness.add(i, iFitness);
				}
				minFitness=Collections.min(Fitness);
				System.out.println();
				System.out.println("\nFitness: "+minFitness);
				Integer minIdxx=minimumValIdx(Fitness);
				System.out.println("Generation: "+generationCount);
				System.out.println("Ht: "+InitPopulation.elementAt(minIdxx).getHeight());
				InitPopulation.elementAt(minIdxx).getRootNode().printInOrder();
				
				Double numFitMembers=
					InitSettings.getPopulationSize()*InitSettings.getFitnessProbability();
				for (int i=0;i<numFitMembers;i++) {
					Integer minIdx=minimumValIdx(Fitness);
					NewPopulation.add(InitPopulation.elementAt(minIdx));
					InitPopulation.removeElementAt(minIdx);
					Fitness.removeElementAt(minIdx);
				}
				Fitness.clear();
				InitPopulation.clear();
				
				//Prepare Crossover gpTrees
				Random generator = new Random();
				for (int i=0;i<InitSettings.getNumCrossOvers();i++) {
					Integer populationIdx=generator.nextInt(NewPopulation.size());
					BinaryTreeNode aNode=NewPopulation.elementAt(populationIdx).getRootNode().treeCopy();
					populationIdx=generator.nextInt(NewPopulation.size());
					BinaryTreeNode bNode=NewPopulation.elementAt(populationIdx).getRootNode().treeCopy();
					GeneticProgrammingTree aGPT=new GeneticProgrammingTree(aNode);
					GeneticProgrammingTree bGPT=new GeneticProgrammingTree(bNode);
			
					aGPT.crossOver(bGPT);
					
					NewPopulation.add(aGPT);
					NewPopulation.add(bGPT);
					
				}
				
				//Do some mutation
				for (int i=0;i<InitSettings.getMutationProbability()*NewPopulation.size();i++) {
					Integer populationIdx=generator.nextInt(NewPopulation.size());
			
					NewPopulation.elementAt(populationIdx).mutate();
				
				}
				
				for (int i=0;i<NewPopulation.size();i++) {
					InitPopulation.add(NewPopulation.elementAt(i));
				}
				NewPopulation.clear();
				
			}
			Thread.yield();
		}
	//}

	private static void PrepareInitialSettings(Settings initSettings) {
		// TODO Auto-generated method stub
		initSettings.setPopulationSize(100);
		initSettings.setMaxHtOfInitTree(4);
		initSettings.setTrainingDataSize(10);
		initSettings.setMutationProbability(0.7);
		initSettings.setFitnessProbability(0.5);
		initSettings.setFitnessMarginOfError(.01);
		initSettings.setNumCrossOvers(initSettings.getPopulationSize()/4);
		initSettings.setOperands(new 
				Vector<String>(
						Arrays.asList("0","1","2","3","4","5","6","7","8","9","x")
						)
						);
		initSettings.setOperators(new
				Vector<String>(
						Arrays.asList("+","-","*","/")
						)
						);
		
	}

}
