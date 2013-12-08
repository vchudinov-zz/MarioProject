package vikrasim.evolution.training.trainers;

import java.io.IOException;
import java.text.DecimalFormat;

import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import jneat.Neat;
import jneat.evolution.Population;
import jneat.neuralNetwork.Genome;

public class IncrementalTrainerMaxLevels extends AverageTrainer {

	public IncrementalTrainerMaxLevels(String parameterFileName,
			String debugParameterFileName, String genomeFileName,
			String genomeBackupFileName, String lastPopulationInfoFileName,
			String generationInfoFolder, String winnerFolder,
			String nameOfExperiment, int numberOfGenerations,
			MasterEvaluator evaluator, String delimiter) {
		super(parameterFileName, debugParameterFileName, genomeFileName,
				genomeBackupFileName, lastPopulationInfoFileName,
				generationInfoFolder, winnerFolder, nameOfExperiment,
				numberOfGenerations, evaluator, delimiter);
	}

	public boolean trainNetwork(String[] trainingSets,
			double winnerPercentageThreshold, MasterAgent agent,
			int maxDifficulty) throws IOException {
		boolean status;

		// Test if all variables have been set
		if (!testVariables()) {
			System.out
					.println("Not all string variables set. Training will not commence");
			return false;
		}

		// Initialise the neat class
		Neat.initbase();

		// Import the parameters to be used by NEAT
		status = importParameters(parameterFileName);
		if (!status) {
			return false;
		}

		// Save imported parameters to new file
		// Can be used when debugging
		writeParametersToFile(debugParameterFileName);

		// Run experiments
		System.out.println("Start experiment " + nameOfExperiment);
		experimentSession(agent, numberOfGenerations, trainingSets,
				winnerPercentageThreshold, maxDifficulty);

		return status;
	}

	public boolean trainNetwork(String[] trainingSets,
			double winnerPercentageThreshold, MasterAgent agent,
			int maxDifficulty, String populationFile, int minDifficulty, int startingNumberOfLevels)
			throws IOException {
		boolean status;

		// Test if all variables have been set
		if (!testVariables()) {
			System.out
					.println("Not all string variables set. Training will not commence");
			return false;
		}

		// Initialise the neat class
		Neat.initbase();

		// Import the parameters to be used by NEAT
		status = importParameters(parameterFileName);
		if (!status) {
			return false;
		}

		// Save imported parameters to new file
		// Can be used when debugging
		writeParametersToFile(debugParameterFileName);

		// Run experiments
		System.out.println("Start experiment " + nameOfExperiment);
		experimentSession(agent, numberOfGenerations, trainingSets,
				winnerPercentageThreshold, maxDifficulty, populationFile,
				minDifficulty, startingNumberOfLevels);

		return status;
	}

	/**
	 * Starts the experiment
	 * 
	 * @param starterGenomeFileName
	 * @param generations
	 * @throws IOException
	 */

	private void experimentSession(MasterAgent agent, int generations,
			String[] trainingSets, double winnerPercentageThreshold,
			int maxDifficulty) throws IOException {

		// Open the file with the starter genome data
		// Create starter genome
		agent.createBrain();
		Genome starterGenome = agent.getBrain().getGenome();

		// Start experiments
		for (int expCount = 0; expCount < Neat.p_num_runs; expCount++) {
			runExperiment(starterGenome, generations, trainingSets,
					winnerPercentageThreshold, maxDifficulty);
		}

	}

	/**
	 * Starts the experiment
	 * 
	 * @param starterGenomeFileName
	 * @param generations
	 * @throws IOException
	 */

	private void experimentSession(MasterAgent agent, int generations,
			String[] trainingSets, double winnerPercentageThreshold,
			int maxDifficulty, String populationFile, int minDifficulty, int startingNumberOfLevels)
			throws IOException {

		// Open the file with the starter genome data
		// Create starter genome
		agent.createBrain();

		// Start experiments
		for (int expCount = 0; expCount < Neat.p_num_runs; expCount++) {
			runExperiment(populationFile, generations, trainingSets,
					winnerPercentageThreshold, maxDifficulty, minDifficulty, startingNumberOfLevels);
		}

	}

	private void runExperiment(Genome starterGenome, int maxGenerations,
			String[] trainingSets, double winnerPercentageThreshold,
			int maxDifficulty) throws IOException {
		String mask6 = "000000";
		DecimalFormat fmt6 = new DecimalFormat(mask6);

		// Create population
		System.out.println("Spawning population from starter genome");
		Population pop = new Population(starterGenome, Neat.p_pop_size);

		// Verify population
		System.out.println("Verifying spawned population");
		pop.verify();

		// Run experiment
		System.out.println("Starting evolution");
		int difficultyLevel = 0;
		int gen = 1;
		int levelsTrainingOn = 1;
		double maxFitness = Double.NEGATIVE_INFINITY;
		int generationsWithoutIncrease = 0;

		do {
			// Create folder to save winners and generation info
			winnerFolder = rootWinnerFolder + delimiter + "Dif "
					+ difficultyLevel + " levels " + levelsTrainingOn;
			testAndCreate(winnerFolder);

			generationInfoFolder = rootGenerationInfoFolder + delimiter
					+ "Dif " + difficultyLevel + " levels " + levelsTrainingOn;
			testAndCreate(generationInfoFolder);

			System.out.print("\n---------------- E P O C H  < " + gen
					+ " >--------------");
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			String[] levelsToTrain = buildTrainingSet(trainingSets,
					difficultyLevel, levelsTrainingOn);

			int numberOfWinners = goThroughEpoch(pop, gen, filenameEpochInfo,
					levelsToTrain);

			if (numberOfWinners > 0) {
				if (enoughWinnersInPopulation(pop, winnerPercentageThreshold,
						numberOfWinners)) {
					if (levelsTrainingOn == trainingSets.length) {
						System.out.println();
						System.out.println("Changing difficulty level to "
								+ difficultyLevel);
						difficultyLevel++;
						levelsTrainingOn = 1;
						System.out.println();
					} else {
						System.out.println();
						System.out.println("Adding new level");
						levelsTrainingOn++;
						System.out.println();
					}

				}
			}

			gen++;
		} while (gen <= maxGenerations && difficultyLevel <= maxDifficulty);

		// Prints information about the last generation
		System.out.print("\n  Population : innov num   = "
				+ pop.getCur_innov_num()); // Prints the current number of
											// innovations
		System.out.print("\n             : cur_node_id = "
				+ pop.getCur_node_id()); // Current number of nodes (??)
		// Writes population info to file for the last population
		pop.print_to_filename(lastPopulationInfoFileName);
	}

	private void runExperiment(String populationFile, int maxGenerations,
			String[] trainingSets, double winnerPercentageThreshold,
			int maxDifficulty, int minDifficulty, int startingNumberOfLevels) throws IOException {
		String mask6 = "000000";
		DecimalFormat fmt6 = new DecimalFormat(mask6);

		// Create population
		System.out.println("Spawning population from starter genome");
		Population pop = new Population(populationFile, false);

		// Verify population
		System.out.println("Verifying spawned population");
		pop.verify();

		// Run experiment
		System.out.println("Starting evolution");
		int difficultyLevel = minDifficulty;
		int gen = 1;
		int levelsTrainingOn = startingNumberOfLevels;

		do {
			// Create folder to save winners and generation info
			winnerFolder = rootWinnerFolder + delimiter + "Dif "
					+ difficultyLevel + " levels " + levelsTrainingOn;
			testAndCreate(winnerFolder);

			generationInfoFolder = rootGenerationInfoFolder + delimiter
					+ "Dif " + difficultyLevel + " levels " + levelsTrainingOn;
			testAndCreate(generationInfoFolder);

			System.out.print("\n---------------- E P O C H  < " + gen
					+ " >--------------");
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			String[] levelsToTrain = buildTrainingSet(trainingSets,
					difficultyLevel, levelsTrainingOn);

			int numberOfWinners = goThroughEpoch(pop, gen, filenameEpochInfo,
					levelsToTrain);

			if (numberOfWinners > 0) {
				if (enoughWinnersInPopulation(pop, winnerPercentageThreshold,
						numberOfWinners)) {
					if (levelsTrainingOn == trainingSets.length) {
						difficultyLevel++;
						levelsTrainingOn = 1;
						System.out.println();
						System.out.println("Changing difficulty level to "
								+ difficultyLevel);
						System.out.println();						
						//Reset fitness counters
						//Makes sure that delta coding only happens when really necessary
						pop.setHighest_last_changed(0); 
						pop.setHighest_fitness(Double.NEGATIVE_INFINITY);
						
					} else {
						System.out.println();
						System.out.println("Adding new level");
						levelsTrainingOn++;
						System.out.println();
					}

				}
			}

			gen++;
		} while (gen <= maxGenerations && difficultyLevel <= maxDifficulty);

		// Prints information about the last generation
		System.out.print("\n  Population : innov num   = "
				+ pop.getCur_innov_num()); // Prints the current number of
											// innovations
		System.out.print("\n             : cur_node_id = "
				+ pop.getCur_node_id()); // Current number of nodes (??)
		// Writes population info to file for the last population
		pop.print_to_filename(lastPopulationInfoFileName);
	}

	private String[] buildTrainingSet(String[] allSets, int difficultyLevel,
			int numberOfLevels) {
		String[] result = new String[numberOfLevels];
		int addedLevels = 0;
		for (int i = 0; i < allSets.length; i++) {
			result[i] = allSets[i] + " -ld " + difficultyLevel;
			addedLevels++;
			if (addedLevels == numberOfLevels) {
				return result;
			}
		}

		return result;
	}

}
