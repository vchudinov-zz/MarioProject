package vikrasim.evolution.training.trainers;

import jNeatCommon.IOseq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import jneat.Neat;
import jneat.evolution.Organism;
import jneat.evolution.Population;
import jneat.evolution.Species;
import jneat.neuralNetwork.Genome;
import vikrasim.CSVWriter;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;

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
	CSVWriter writer;
	double maxFitnessThisGeneration;
	
	public AverageTrainer(String parameterFileName, String debugParameterFileName, String genomeFileName, 
			String genomeBackupFileName, String lastPopulationInfoFileName, String generationInfoFolder, 
			String winnerFolder, String nameOfExperiment, int numberOfGenerations, 
			AverageEvaluator evaluator, String delimiter){
		
		this.parameterFileName=parameterFileName;
		this.debugParameterFileName=debugParameterFileName;
		this.starterGenomeFileName=genomeFileName;
		this.genomeBackupFileName=genomeBackupFileName;
		this.lastPopulationInfoFileName=lastPopulationInfoFileName;
		this.rootGenerationInfoFolder = generationInfoFolder;
		this.rootWinnerFolder = winnerFolder;
		this.nameOfExperiment=nameOfExperiment;
		this.numberOfGenerations=numberOfGenerations;
		this.evaluator = evaluator;	
		this.delimiter=delimiter;
		writer = new CSVWriter();
	}
	
	public AverageTrainer(){
		
	}
	
	public boolean trainNetwork(String[][] scenarios) throws IOException{
		boolean status;
		
		//Test if all variables have been set
		if (!testVariables()){
			System.out.println("Not all string variables set. Training will not commence");
			return false;
		}
		
		//Initialise the neat class
		Neat.initbase();
		
		//Import the parameters to be used by NEAT
		status = importParameters(parameterFileName);
		if (!status){
			return false;
		}
		
		//Save imported parameters to new file
		//Can be used when debugging		
		writeParametersToFile(debugParameterFileName);
		
		//Run experiments
		System.out.println("Start experiment " + nameOfExperiment);
		int lastGeneration = -1;
		
		for (int difficultyLevel = 0; difficultyLevel < scenarios.length;difficultyLevel++){
			System.out.println();
			System.out.println("*****  Start difficulty level " + (difficultyLevel) + "  *****");
			System.out.println();
			
			for (int levelNumber = 0; levelNumber < scenarios[difficultyLevel].length; levelNumber++){
				
				//Find starter genome
				if (levelNumber == 0 && difficultyLevel == 0){
					currentGenomeFileName = this.starterGenomeFileName;
				}else {
					currentGenomeFileName = winnerFolder +  delimiter + nameOfExperiment + " gen " + lastGeneration + " best";
				}			
				
				//Create folder to save winners and generation info
				winnerFolder = rootWinnerFolder + delimiter + "Difficulty level " + difficultyLevel;
				testAndCreate(winnerFolder);
				
				generationInfoFolder = rootGenerationInfoFolder + delimiter + "Difficulty level " + difficultyLevel;
				testAndCreate(generationInfoFolder);
				
				//Run experiment session			
				lastGeneration = experimentSession(currentGenomeFileName, numberOfGenerations);
			}
		}
		
		
		return status;
	}
	
	private void testAndCreate(String folderPath){
		File f = new File(folderPath);
		f.mkdirs();
	}
		
	private boolean testVariables(){
		 if (!testSingleVariable(parameterFileName)) return false;
		 if (!testSingleVariable(debugParameterFileName)) return false;
		 if (!testSingleVariable(starterGenomeFileName)) return false;
		 if (!testSingleVariable(genomeBackupFileName)) return false;
		 if (!testSingleVariable(lastPopulationInfoFileName)) return false;
		 if (!testSingleVariable(rootGenerationInfoFolder)) return false;
		 if (!testSingleVariable(rootWinnerFolder)) return false;
		 if (!testSingleVariable(nameOfExperiment)) return false;
		 if (numberOfGenerations <= 0) return false;
		 if (evaluator == null) return false;
		
		return true;
	}
	
	private boolean testSingleVariable(String s){
		boolean status = !s.contentEquals("");
		return status;
	}
	
	private boolean importParameters (String parameterFileName){
		boolean status = Neat.readParam(parameterFileName);
		if (status){
			System.out.println("Parameter read okay");
		} else{
			System.out.println("Error in parameter read");
		}
		
		return status;
	}
	
	private void writeParametersToFile(String parameterFileName){
		Neat.writeParam(parameterFileName);
	}
	
	/**
	 * Starts the experiment
	 * @param starterGenomeFileName
	 * @param generations
	 * @throws IOException 
	 */
	private int experimentSession (String starterGenomeFileName, int generations, String[] trainingSet) throws IOException{
		
		//Open the file with the starter genome data
		IOseq starterGenomeFile = new IOseq(starterGenomeFileName);
		boolean ret = starterGenomeFile.IOseqOpenR();
		int lastGeneration = -1;
		if (ret){
			//Create starter genome
			Genome starterGenome = createGenome(starterGenomeFile);
			
			//Start experiments			
			for (int expCount = 0; expCount < Neat.p_num_runs; expCount++){
				lastGeneration = runExperiment(starterGenome, generations, trainingSet);
			}
			
		} else{
			System.out.println("Error during opening of " + starterGenomeFileName);
			starterGenomeFile.IOseqCloseR();
			return -1;
		}
		
		starterGenomeFile.IOseqCloseR();
		return lastGeneration;
	}
	/**
	 * Reads a file and creates a genome based on the data in that file
	 * @param starterGenomeFile
	 * @return
	 */
	private Genome createGenome (IOseq starterGenomeFile){
		String curWord;
		
		System.out.println("Read starter genome");
		
		//Read file
		String line = starterGenomeFile.IOseqRead();
		StringTokenizer st = new StringTokenizer(line);
		
		//Skip first word in file
		curWord = st.nextToken();
		
		//Read ID of the genome
		curWord = st.nextToken();
		int id = Integer.parseInt(curWord);
		
		//Create the genome
		System.out.println("Create genome id " + id);
		Genome startGenome = new Genome (id,starterGenomeFile);
		
		//Backup initial genome
		//Probably used for debugging
		startGenome.print_to_filename(genomeBackupFileName);
				
		return startGenome;
		
	}

	/**
	 * Runs an experiment where populations are evolved from a basic genome
	 * @param starterGenome
	 * @param maxGenerations - Maximum number of generations
	 * @param stopOnFirstGoodOrganism
	 * @param maxNumberOfNoImprovement
	 * @return returns the last generation number
	 * @throws IOException
	 */
	private int runExperiment(Genome starterGenome, int maxGenerations, String[] trainingSet) throws IOException{
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
		double highestFitness = 0.0;
		int lastGenWithImprovement = -1;
		int lastGeneration = 0;
		for (int gen = 1; gen <= maxGenerations; gen++){
			System.out.print("\n---------------- E P O C H  < " + gen+" >--------------");
			
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			boolean status = goThroughEpoch(pop, gen, filenameEpochInfo, trainingSet);			
		}
		
		//Prints information about the last generation 
		System.out.print("\n  Population : innov num   = " + pop.getCur_innov_num()); //Prints the current number of innovations
		System.out.print("\n             : cur_node_id = " + pop.getCur_node_id());  //Current number of nodes (??)
		//Writes population info to file for the last population 
		pop.print_to_filename(lastPopulationInfoFileName);
		writer.Writer(lastPopulationInfoFileName);
		
		return lastGeneration;
	}
	
	/**
	 * Evolves a new generation for the population
	 * @param pop
	 * @param generation
	 * @param filenameEpochInfo
	 * @return True if a winner has been found in the population. False otherwise
	 * @throws IOException 
	 */
	private boolean goThroughEpoch(Population pop, int generation, String filenameEpochInfo, String[] trainingSet) throws IOException{
		boolean status = false;
		ArrayList<Organism> winners = new ArrayList<>();
		
		//Evaluate each organism to see if it is a winner
		boolean win = false;
		
		Iterator itr_organism;
		itr_organism = pop.organisms.iterator();
		
		while (itr_organism.hasNext()){
			//point to organism
			Organism curOrganism = ((Organism) itr_organism.next());
			//evaluate 
			status = evaluator.evaluate(curOrganism, trainingSet);
			// if is a winner , store a flag
			if (status){
				win = true;
				winners.add(curOrganism);
			}
		 } //Looping through all the organisms in the population
		
		//compute average and max fitness for each species
		Iterator itr_specie;
		itr_specie = pop.species.iterator();
		while (itr_specie.hasNext()) {
			Species curSpecie = ((Species) itr_specie.next());
			curSpecie.compute_average_fitness();
			curSpecie.compute_max_fitness();
		}
		
		//Print best organism to file
		if (winners.isEmpty()){
			printBest(pop.organisms, generation);
		} else {
			printBest(winners, generation);			
		}
		 
		// Only print to file every print_every generations
		if (win || (generation % Neat.p_print_every) == 0){
			pop.print_to_file_by_species(generationInfoFolder + delimiter + filenameEpochInfo);
		}
		  
		// if a winner exist, write to file	   
		if (win) {
			int cnt = 0;
			itr_organism = pop.getOrganisms().iterator();
			while (itr_organism.hasNext()) {
				Organism _organism = ((Organism) itr_organism.next());
				if (_organism.winner){
					//System.out.print("\n   -WINNER IS #" + _organism.genome.getGenome_id());
					_organism.getGenome().print_to_filename(winnerFolder +  delimiter + nameOfExperiment + "_win " + cnt);
					cnt++;
				}
			}
		}
		
		// wait an epoch and make a reproduction of the best species
		pop.epoch(generation);
		
		if (win){
			System.out.print("\t\t** I HAVE FOUND A CHAMPION **");
			return true;
		} else { 
			return false;
		}		
	}
	
	private Organism findBestOrganism(Iterable<Organism> list){
		double maxFitness = Double.NEGATIVE_INFINITY;
		Organism best = null;
		for (Organism o : list){
			double myFitness = o.getFitness();
			if (myFitness > maxFitness){
				best = o;
				maxFitness = myFitness;
			}
		}
		return best;
	}
	
	private void printBest(Iterable<Organism> list, int generation) throws IOException{
		Organism best = findBestOrganism(list);
		maxFitnessThisGeneration = best.getFitness();
		
		System.out.println();
		System.out.println("Generation " + generation + " highest fitness: " + maxFitnessThisGeneration);
		String filename = winnerFolder +  delimiter + nameOfExperiment + " gen " + generation + " best";
		best.getGenome().print_to_filename(filename);
		writer.WriterOfOne(filename);
		saveFitness(generation, maxFitnessThisGeneration);
		//System.out.println("CSV created");
	    }
	
	private void saveFitness(int generation, double fitness){
		String xNameFile = generationInfoFolder + delimiter + "popFitness.csv";
		
		//Test if file exists
		File file = new File(xNameFile); 

		// Does the file already exist 
		if(!file.exists()) 
		{ 
		  try 
		  { 
		    // Try creating the file 
		    file.createNewFile(); 
		  } 
		  catch(IOException ioe) 
		  { 
		    ioe.printStackTrace(); 
		  } 
		}
		
		//Write to file
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(xNameFile, true)));
		    out.println(generation + ";" + fitness);
		    out.close();
		} catch (IOException e) {
		    //oh noes!
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


	public void setEvaluator(MyMarioEvaluator evaluator) {
		this.evaluator = evaluator;
	}
}
