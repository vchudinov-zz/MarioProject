package vikrasim.evolution.training.trainers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import jneat.Neat;
import jneat.evolution.Organism;
import jneat.evolution.Population;
import jneat.evolution.Species;
import jneat.neuralNetwork.Genome;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.MasterEvaluator;

public class AverageTrainer {
	String parameterFileName;
	String debugParameterFileName;
	String starterGenomeFileName;
	String currentGenomeFileName;
	String genomeBackupFileName;
	String lastPopulationInfoFileName;
	String generationInfoFolder;
	String winnerFolder;
	String rootGenerationInfoFolder;
	String rootWinnerFolder;
	String nameOfExperiment;
	String delimiter;
	String rootWinnerFileName;
	int numberOfGenerations;
	MasterEvaluator evaluator;
	double maxFitnessThisGeneration;

	public AverageTrainer(String parameterFileName,
			String debugParameterFileName, String genomeFileName,
			String genomeBackupFileName, String lastPopulationInfoFileName,
			String generationInfoFolder, String winnerFolder,
			String nameOfExperiment, int numberOfGenerations,
			MasterEvaluator evaluator, String delimiter) {

		this.parameterFileName = parameterFileName;
		this.debugParameterFileName = debugParameterFileName;
		this.starterGenomeFileName = genomeFileName;
		this.genomeBackupFileName = genomeBackupFileName;
		this.lastPopulationInfoFileName = lastPopulationInfoFileName;
		this.rootGenerationInfoFolder = generationInfoFolder;
		this.rootWinnerFolder = winnerFolder;
		this.nameOfExperiment = nameOfExperiment;
		this.numberOfGenerations = numberOfGenerations;
		this.evaluator = evaluator;
		this.delimiter = delimiter;
	}

	public AverageTrainer() {

	}

	public boolean trainNetwork(String[][] trainingSets,
			double winnerPercentageThreshold, MasterAgent agent)
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
				winnerPercentageThreshold);

		return status;
	}

	protected void testAndCreate(String folderPath) {
		File f = new File(folderPath);
		f.mkdirs();
	}

	protected boolean testVariables() {
		if (!testSingleVariable(parameterFileName))
			return false;
		if (!testSingleVariable(debugParameterFileName))
			return false;
		if (!testSingleVariable(starterGenomeFileName))
			return false;
		if (!testSingleVariable(genomeBackupFileName))
			return false;
		if (!testSingleVariable(lastPopulationInfoFileName))
			return false;
		if (!testSingleVariable(rootGenerationInfoFolder))
			return false;
		if (!testSingleVariable(rootWinnerFolder))
			return false;
		if (!testSingleVariable(nameOfExperiment))
			return false;
		if (numberOfGenerations <= 0)
			return false;
		if (evaluator == null)
			return false;

		return true;
	}

	private boolean testSingleVariable(String s) {
		boolean status = !s.contentEquals("");
		return status;
	}

	protected boolean importParameters(String parameterFileName) {
		boolean status = Neat.readParam(parameterFileName);
		if (status) {
			System.out.println("Parameter read okay");
		} else {
			System.out.println("Error in parameter read");
		}

		return status;
	}

	protected void writeParametersToFile(String parameterFileName) {
		Neat.writeParam(parameterFileName);
	}

	/**
	 * Starts the experiment
	 * 
	 * @param starterGenomeFileName
	 * @param generations
	 * @throws IOException
	 */
	private void experimentSession(MasterAgent agent, int generations,
			String[][] trainingSets, double winnerPercentageThreshold)
			throws IOException {

		// Open the file with the starter genome data
		// Create starter genome
		agent.createBrain();
		Genome starterGenome = agent.getBrain().getGenome();

		// Start experiments
		for (int expCount = 0; expCount < Neat.p_num_runs; expCount++) {
			runExperiment(starterGenome, generations, trainingSets,
					winnerPercentageThreshold);
		}

	}

	/**
	 * Runs an experiment where populations are evolved from a basic genome
	 * 
	 * @param starterGenome
	 * @param maxGenerations
	 *            - Maximum number of generations
	 * @param stopOnFirstGoodOrganism
	 * @param maxNumberOfNoImprovement
	 * @return returns the last generation number
	 * @throws IOException
	 */
	private void runExperiment(Genome starterGenome, int maxGenerations,
			String[][] trainingSets, double winnerPercentageThreshold)
			throws IOException {
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

		do {
			// Create folder to save winners and generation info
			winnerFolder = rootWinnerFolder + delimiter + "Difficulty level "
					+ difficultyLevel;
			testAndCreate(winnerFolder);

			generationInfoFolder = rootGenerationInfoFolder + delimiter
					+ "Difficulty level " + difficultyLevel;
			testAndCreate(generationInfoFolder);

			System.out.print("\n---------------- E P O C H  < " + gen
					+ " >--------------");
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			int numberOfWinners = goThroughEpoch(pop, gen, filenameEpochInfo,
					trainingSets[difficultyLevel]);
			if (numberOfWinners > 0) {
				if (enoughWinnersInPopulation(pop, winnerPercentageThreshold,
						numberOfWinners)) {
					difficultyLevel++;
					System.out.println();
					System.out.println("Changing difficulty level to "
							+ difficultyLevel);
					System.out.println();
				}
			}
			gen++;
		} while (gen <= maxGenerations && difficultyLevel < trainingSets.length);

		// Prints information about the last generation
		System.out.print("\n  Population : innov num   = "
				+ pop.getCur_innov_num()); // Prints the current number of
											// innovations
		System.out.print("\n             : cur_node_id = "
				+ pop.getCur_node_id()); // Current number of nodes (??)
		// Writes population info to file for the last population
		pop.print_to_filename(lastPopulationInfoFileName);
	}

	protected boolean enoughWinnersInPopulation(Population pop,
			double percentageThreshold, int numberOfWinners) {
		int totalNumberOfOrganism = pop.organisms.size();

		double winnerPercentage = (double) numberOfWinners
				/ totalNumberOfOrganism;
		System.out.println();
		System.out.println("Winner percentage: " + winnerPercentage);

		if (winnerPercentage >= percentageThreshold) {
			return true;
		}

		return false;
	}

	/**
	 * Evolves a new generation for the population
	 * 
	 * @param pop
	 * @param generation
	 * @param filenameEpochInfo
	 * @return True if a winner has been found in the population. False
	 *         otherwise
	 * @throws IOException
	 */
	protected int goThroughEpoch(Population pop, int generation,
			String filenameEpochInfo, String[] trainingSet) throws IOException {
		boolean status = false;
		ArrayList<Organism> winners = new ArrayList<>();

		// Evaluate each organism to see if it is a winner
		boolean win = false;

		Iterator itr_organism;
		itr_organism = pop.organisms.iterator();

		while (itr_organism.hasNext()) {
			// point to organism
			Organism curOrganism = ((Organism) itr_organism.next());
			// evaluate
			status = evaluator.evaluate(curOrganism, trainingSet);
			// if is a winner , store a flag
			if (status) {
				win = true;
				winners.add(curOrganism);
			}
		} // Looping through all the organisms in the population

		// compute average and max fitness for each species
		Iterator itr_specie;
		itr_specie = pop.species.iterator();
		while (itr_specie.hasNext()) {
			Species curSpecie = ((Species) itr_specie.next());
			curSpecie.compute_average_fitness();
			curSpecie.compute_max_fitness();
		}

		// Print best organism to file
		if (winners.isEmpty()) {
			printBest(pop.organisms, generation);
		} else {
			printBest(winners, generation);
		}

		// Only print to file every print_every generations
		if (win || (generation % Neat.p_print_every) == 0) {
			pop.print_to_file_by_species(generationInfoFolder + delimiter
					+ filenameEpochInfo);
		}

		// if a winner exist, write to file
		if (win) {
			int cnt = 0;
			itr_organism = pop.getOrganisms().iterator();
			while (itr_organism.hasNext()) {
				Organism _organism = ((Organism) itr_organism.next());
				if (_organism.winner) {
					// System.out.print("\n   -WINNER IS #" +
					// _organism.genome.getGenome_id());
					_organism.getGenome().print_to_filename(
							winnerFolder + delimiter + nameOfExperiment
									+ "_win " + cnt);
					cnt++;
				}
			}
		}

		// wait an epoch and make a reproduction of the best species
		pop.epoch(generation);

		if (win) {
			System.out.print("\t\t** I HAVE FOUND A CHAMPION **");
		}

		return winners.size();
	}

	private Organism findBestOrganism(Iterable<Organism> list) {
		double maxFitness = Double.NEGATIVE_INFINITY;
		Organism best = null;
		for (Organism o : list) {
			double myFitness = o.getFitness();
			if (myFitness > maxFitness) {
				best = o;
				maxFitness = myFitness;
			}
		}
		return best;
	}

	private void printBest(Iterable<Organism> list, int generation)
			throws IOException {
		Organism best = findBestOrganism(list);
		maxFitnessThisGeneration = best.getFitness();

		System.out.println();
		System.out.println("Generation " + generation + " highest fitness: "
				+ maxFitnessThisGeneration);
		String filename = winnerFolder + delimiter + nameOfExperiment + " gen "
				+ generation + " best";
		best.getGenome().print_to_filename(filename);
		saveFitness(generation, maxFitnessThisGeneration);
		// System.out.println("CSV created");
	}

	private void saveFitness(int generation, double fitness) {
		String xNameFile = generationInfoFolder + delimiter + "popFitness.csv";

		// Test if file exists
		File file = new File(xNameFile);

		// Does the file already exist
		if (!file.exists()) {
			try {
				// Try creating the file
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		// Write to file
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(xNameFile, true)));
			out.println(generation + ";" + fitness);
			out.close();
		} catch (IOException e) {
			// oh noes!
		}
	}

	public void setParameterFileName(String parameterFileName) {
		this.parameterFileName = parameterFileName;
	}

	public void setDebugParameterFileName(String debugParameterFileName) {
		this.debugParameterFileName = debugParameterFileName;
	}

	public void setGenomeFileName(String genomeFileName) {
		this.currentGenomeFileName = genomeFileName;
	}

	public void setGenomeBackupFileName(String genomeBackupFileName) {
		this.genomeBackupFileName = genomeBackupFileName;
	}

	public void setLastPopulationInfoFileName(String lastPopulationInfoFileName) {
		this.lastPopulationInfoFileName = lastPopulationInfoFileName;
	}

	public void setGenerationInfoFolder(String generationInfoFolder) {
		this.generationInfoFolder = generationInfoFolder;
	}

	public void setWinnerFolder(String winnerFolder) {
		this.winnerFolder = winnerFolder;
	}

	public void setNumberOfGenerations(int numberOfGenerations) {
		this.numberOfGenerations = numberOfGenerations;
	}

	public void setNameOfExperiment(String nameOfExperiment) {
		this.nameOfExperiment = nameOfExperiment;
	}

}
