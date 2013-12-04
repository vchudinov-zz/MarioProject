package vikrasim.evolution.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.AgentScannerNEATSlow;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.evaluators.IncrementalEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.NoJumpEvaluator;
import vikrasim.evolution.training.trainers.AverageTrainer;
import vikrasim.evolution.training.trainers.IncrementalTrainer;
import vikrasim.genomeFileCreation.FileCreater;

public class ExternalTC extends Console {

	public ExternalTC(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}
	
	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		String nameOfExperiment = args[0];
		int maxNumberOfGenerations = Integer.parseInt(args[1]);
		double winnerPercentageThreshold = Double.parseDouble(args[2]);
		
		//Info about agent (if used)
		int zLevelEnemies = Integer.parseInt(args[3]);
		int zLevelScene = Integer.parseInt(args[4]);
		int scannerLength = Integer.parseInt(args[5]);
		int scannerHeight = Integer.parseInt(args[6]);
		
		//Data files
		String rootDataFolder = args[7];
		
		//Training set data
		String trainingSetFile = args[8];
		int startDifficulty = Integer.parseInt(args[9]);;
		int maxDifficulty = Integer.parseInt(args[10]);
		int numberOfDifferentLevels = Integer.parseInt(args[11]);
		
		//Type of evaluator
		String typeOfEvaluator = args[12];
		
		//Agent
		String agentType = args[13];
		
		ExternalTC tc = new ExternalTC(nameOfExperiment, maxNumberOfGenerations, false, 0.01, rootDataFolder);
		
		tc.createMissingParameterFile();
		
		String[][] trainingSets = tc.createTrainingSets(trainingSetFile, startDifficulty, maxDifficulty, numberOfDifferentLevels);
		
		//Setup agent
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight,agentType);
		tc.train(agent, winnerPercentageThreshold, trainingSets, typeOfEvaluator);
	}	
	
	
	private String[][] createTrainingSets(String trainingSetFile, int startDifficulty, int maxDifficulty, int numberOfDifferentLevels ){
		
		
		String[] levels = getLevelParameters(trainingSetFile);
		
		

		String[][] s = new String[maxDifficulty][levels.length
				* numberOfDifferentLevels];

		for (int i = 0; i < maxDifficulty; i++) {
			for (int j = 0; j < levels.length; j++) {
				for (int k = 0; k < numberOfDifferentLevels; k++) {
					s[i][j * numberOfDifferentLevels + k] = levels[j] + " -ld "
							+ (i + startDifficulty) + " -ls " + ((k + 1) * 10);
				}
			}
		}
		
		return s;
	}
	
	private String[] getLevelParameters(String fileName){
		BufferedReader br;
		String s ="";
		String[] levels = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				s = s + line + ";";
			}
			levels = s.split(";");
			br.close();
			   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return levels;		
	}
	
	private void train(MasterAgent agent, double winnerPercentageThreshold, String[][] trainingSets, String typeOfEvaluator) throws IOException{
		String levelParameters = "";
		
		//Create evaluator
		MasterEvaluator evaluator =null;
		if (typeOfEvaluator.equalsIgnoreCase("AverageEvaluator")){
			evaluator = new AverageEvaluator(levelParameters, agent);
			
		} else if (typeOfEvaluator.equalsIgnoreCase("IncrementalEvaluator")){
			evaluator = new IncrementalEvaluator(levelParameters, agent);
			
		} else if (typeOfEvaluator.equalsIgnoreCase("NoJumpEvaluator")){
			evaluator = new NoJumpEvaluator(levelParameters, agent);
		} 
		
				
		//Create trainer
		String delimiter = new File("").separator;
		IncrementalTrainer t = new IncrementalTrainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, evaluator, delimiter);
				
		//Train network
		t.trainNetwork(trainingSets, winnerPercentageThreshold, agent);
	}
	
	private MasterAgent setupAgent(int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight, String agentType){		
		MasterAgent agent = null;
		if (agentType.equalsIgnoreCase("AgentScannerNEAT")){
			agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
		} else if (agentType.equalsIgnoreCase("AgentScannerNEATGapScanner")){
			agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
		} else if (agentType.equalsIgnoreCase("AgentScannerNEATSlow")){
			agent = new AgentScannerNEATSlow(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		}
			
		return agent;
	}
	
	private void createMissingParameterFile(){
		//Create filecreater
		FileCreater fc = new FileCreater();
		
		//Test to see if genome exists
		String path = parameterFileName;
		
		File f = new File(path);
		if(!f.exists()) {
			fc.createParametersFile(path);
		}
		f = null;
		fc = null;		
	}
	
	
	        

}
