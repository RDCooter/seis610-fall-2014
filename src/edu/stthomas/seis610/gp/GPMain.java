package edu.stthomas.seis610.gp;

import java.util.Collections;
import java.util.Vector;

import edu.stthomas.seis610.tree.BinaryTree;
import edu.stthomas.seis610.tree.BinaryTreeNode;
import edu.stthomas.seis610.tree.GeneticProgrammingTree;

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
	
	@SuppressWarnings({ "static-access" })
	public void run() { 
		
		Vector<TrainingData> TrainingDataSet=new Vector<TrainingData>();
		Vector <GeneticProgrammingTree> InitPopulation=
			new Vector<GeneticProgrammingTree>();
		Vector <GeneticProgrammingTree> NewPopulation=
			new Vector<GeneticProgrammingTree>();
		Vector<Double> Fitness=new Vector<Double>();
		Integer generationCount=0;
		Vector <Boolean> valid= new Vector<Boolean>();
		

		//Create the Training Data Set
		for (int i=0;i<GPSettings.getTrainingDataSize();i++) {
			int h=GPSettings.getTrainingDataSize()/2;
			TrainingDataSet.add(i, new TrainingData((double)(i-h), ((i-h)*(i-h)+1.0)/2.0));
			System.out.println((double)(i-h)+", "+((i-h)*(i-h)+1.0)/2.0);

		}

		//Prepare the Initial Population
		for (int i=0;i<GPSettings.getPopulationSize();i++) {
			InitPopulation.add(i, new GeneticProgrammingTree(
					GPSettings.getMaxHtOfInitTree()));
		}
		
		Double minFitness=GPSettings.getFitnessMarginOfError()+1;
		
		//while (!Stop) {    // continue until asked to stop

		for (int i=0;i<GPSettings.getPopulationSize();i++) {
			valid.add(i, true);
		}
			//System.out.println("Value of Stop: "+Stop);
			while (!Stop) {
				
				generationCount++;
				//Compute the fitness of the Initial Population
				for (int i=0;i<GPSettings.getPopulationSize();i++) {
					valid.set(i, true);
					BinaryTree.setIsvalidbtree(true);
					BinaryTreeNode.setIsvalidtreenode(true);
					
					Double iFitness=0.0;
					for (int j=0;j<GPSettings.getTrainingDataSize();j++) {
						Double iOutput=InitPopulation.elementAt(i).evaluate(
								TrainingDataSet.elementAt(j).getInputData());
						Double jDelta=Math.abs(iOutput-
								TrainingDataSet.elementAt(j).getOutputData());
						iFitness+=jDelta;
					}
					if(InitPopulation.elementAt(i).getIsvalidbtree()==false)
					{
						valid.set(i, false);
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
				System.out.println("\nbinarytree valid or not: "+valid.elementAt(minIdxx));

					
				if (minFitness<GPSettings.getFitnessMarginOfError())
				{
					if(valid.elementAt(minIdxx)!=false)
					{
						Stop =true;
						break;		
					}
						
				}
				
				Double numFitMembers=
						GPSettings.getPopulationSize()*GPSettings.getFitnessProbability();
				for (int i=0;i<numFitMembers;i++) {
					Integer minIdx=minimumValIdx(Fitness);
					NewPopulation.add(InitPopulation.elementAt(minIdx));
					InitPopulation.removeElementAt(minIdx);
					Fitness.removeElementAt(minIdx);
				}
				Fitness.clear();
				InitPopulation.clear();
				
				//Prepare Crossover gpTrees
				for (int i=0;i<GPSettings.getNumCrossOvers();i++) {
					Integer populationIdx=GPSettings.getRandomInt(NewPopulation.size());
					BinaryTreeNode aNode=NewPopulation.elementAt(populationIdx).getRootNode().treeCopy();
					populationIdx=GPSettings.getRandomInt(NewPopulation.size());
					BinaryTreeNode bNode=NewPopulation.elementAt(populationIdx).getRootNode().treeCopy();
					GeneticProgrammingTree aGPT=new GeneticProgrammingTree(aNode);
					GeneticProgrammingTree bGPT=new GeneticProgrammingTree(bNode);
			
					aGPT.crossOver(bGPT);
					
					NewPopulation.add(aGPT);
					NewPopulation.add(bGPT);
					
				}
				
				//Do some mutation
				for (int i=0;i<GPSettings.getMutationProbability()*NewPopulation.size();i++) {
					Integer populationIdx=GPSettings.getRandomInt(NewPopulation.size());
			
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

}
