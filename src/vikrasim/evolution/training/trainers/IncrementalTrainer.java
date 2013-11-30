package vikrasim.evolution.training.trainers;

import java.io.IOException;
import java.text.DecimalFormat;

import jneat.Neat;
import jneat.evolution.Population;
import jneat.neuralNetwork.Genome;

public class IncrementalTrainer extends AverageTrainer {
	private void runExperiment(Genome starterGenome, int maxGenerations, String[][] trainingSets, double winnerPercentageThreshold) throws IOException{
		String mask6 = "000000";
		DecimalFormat fmt6 = new DecimalFormat(mask6);
		
		//Create population
		System.out.println("Spawning population from starter genome");
		Population pop = new Population(starterGenome, Neat.p_pop_size);
		
		//Verify population
		System.out.println("Verifying spawned population");
		pop.verify();
		
		//Run experiment
		System.out.println("Starting evolution");
		int totalNumberOfTrainedLevels = 1;
		int levelNumber = 0;
		int difficultyLevel = 0;
		int gen = 1;
		
		do {
			//Create folder to save winners and generation info
			winnerFolder = rootWinnerFolder + delimiter + "Levels " + totalNumberOfTrainedLevels + " Dif " + difficultyLevel;
			testAndCreate(winnerFolder);
			
			generationInfoFolder = rootGenerationInfoFolder + delimiter + "Levels " + totalNumberOfTrainedLevels + " Dif " + difficultyLevel;
			testAndCreate(generationInfoFolder);
			
			System.out.print("\n---------------- E P O C H  < " + gen +" >--------------");
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			String[] levelsToTrain = buildTrainingSet(trainingSets, totalNumberOfTrainedLevels, difficultyLevel);
			
			int numberOfWinners = goThroughEpoch(pop, gen, filenameEpochInfo, levelsToTrain);
			
			if (numberOfWinners > 0) {
				if (enoughWinnersInPopulation(pop, winnerPercentageThreshold, numberOfWinners)){
					totalNumberOfTrainedLevels++;
					levelNumber++;
					System.out.println();
					System.out.println("Adding another level to training set");
					System.out.println("Population is now trained on " + totalNumberOfTrainedLevels + " levels");
					if (levelNumber > trainingSets[difficultyLevel].length - 1){
						levelNumber = 0;
						difficultyLevel++;
						System.out.println("Changing difficulty level to " + difficultyLevel);
					}
					System.out.println();
				}
			}
			
			gen++;
		} while (gen <= maxGenerations && difficultyLevel < trainingSets.length);
		
		
		//Prints information about the last generation 
		System.out.print("\n  Population : innov num   = " + pop.getCur_innov_num()); //Prints the current number of innovations
		System.out.print("\n             : cur_node_id = " + pop.getCur_node_id());  //Current number of nodes (??)
		//Writes population info to file for the last population 
		pop.print_to_filename(lastPopulationInfoFileName);
	}
	
	private String[] buildTrainingSet(String[][] allSets, int numberOfLevels, int difficultyLevel){
		String[] result = new String[numberOfLevels];
		int addedLevels = 0;
		for (int i = 0; i < difficultyLevel + 1; i++){
			for (int j = 0; j < allSets[i].length ;j++){
				result[addedLevels] = allSets[i][j];
				addedLevels++;
				if (addedLevels == numberOfLevels){
					return result;
				}
			}
		}
		return null;
	}

}
