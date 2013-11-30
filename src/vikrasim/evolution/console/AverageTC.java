package vikrasim.evolution.console;

import java.io.File;
import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.trainers.AverageTrainer;
import vikrasim.genomeFileCreation.FileCreater;

public class AverageTC extends Console {

	public AverageTC(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}
	
	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		String nameOfExperiment = "Testing new NEAT";
		int maxNumberOfGenerations = 500;
		boolean stopOnFirstGoodOrganism = false;
		double errorThreshold = 0.1;
		double winnerPercentageThreshold = 0.01;
		
		//Info about agent (if used)
		int zLevelEnemies = 1;
		int zLevelScene = 1;
		int scannerLength = 7;
		int scannerHeight = 7;
		
		//Simon Laptop
		//String rootDataFolder = "C:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
		
		//Simon Desktop
		String rootDataFolder = "D:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
		
		//Victor
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		
		//Krasimira
		//String rootDataFolder = new File("").getAbsolutePath() + "/NEAT data";
		
	
		AverageTC tc = new AverageTC(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold, rootDataFolder);
		
		tc.createMissingParameterFile();
		
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		tc.train(agent, winnerPercentageThreshold);
	}	
	
	
	private String[][] createTrainingSets(){
		//Old levels
		/*
		String noEnemies = "-vis off -ls 20 -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis off -ls 20 -lb off -lca off -lco off -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds ="-vis off -ls 20 -lb off -lca off -lco off -lde on -le off -lf off -lg off -lhs off -ltb off";
		String withEnemies = "-vis off -ls 20 -lb off -lca off -lco off -lde off -lf off -lg off -lhs off -ltb off";
		String everything ="-vis off -ls 20 -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";
		*/
		
		//Viktors levels
		String flatNoBlock = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = "-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds ="-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = "-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = "-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything ="-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";
		
		String[] levels = {withGaps};
		
		int maxDifficulty = 10;
		
		String[][] s = new String[maxDifficulty][levels.length];
		
		for (int i = 0; i < maxDifficulty; i++){
			for (int j = 0; j < levels.length; j++){
				s[i][j] = levels[j] + " -ld " + i;
			}
		}
		
		return s;
	}		
	
	private void train(MasterAgent agent, double winnerPercentageThreshold) throws IOException{
		String levelParameters = "";
		
		//Create evaluator		
		AverageEvaluator evaluator = new AverageEvaluator(levelParameters, agent);
				
		//Create trainer
		String delimiter = new File("").separator;
		AverageTrainer t = new AverageTrainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, evaluator, delimiter);
				
		//Train network
		String[][] trainingSets = createTrainingSets();
		t.trainNetwork(trainingSets, winnerPercentageThreshold, agent);
	}
	
	private MasterAgent setupAgent(int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight){		
		
		MasterAgent agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
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
