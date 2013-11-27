package vikrasim.evolution.console;

import java.io.File;
import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.AboveAverageEvaluator;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.trainers.AverageTrainer;
import vikrasim.evolution.training.trainers.ImprovedTrainer;
import vikrasim.genomeFileCreation.FileCreater;

public class ImprovedTC extends Console{

	public ImprovedTC(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}
	
	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		String nameOfExperiment = "ImprovedTrainingSessions 6";
		int maxNumberOfGenerations = 500;
		boolean stopOnFirstGoodOrganism = false;
		double errorThreshold = 0.1;
		double winnerPercentageThreshold = 0.20;
		
		//Info about agent (if used)
		int zLevelEnemies = 2;
		int zLevelScene = 1;
		int scannerLength = 3;
		int scannerHeight = 3;
		
		//Simon Laptop
		//String rootDataFolder = "C:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
		
		//Simon Desktop
		String rootDataFolder = "D:\\eclipse\\Workspace\\MarioProject\\NEAT data";
		
		//Victor
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		
		//Krasimira
		//String rootDataFolder = new File("").getAbsolutePath() + "/NEAT data";
		
	
		ImprovedTC tc = new ImprovedTC(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold, rootDataFolder);
		
		tc.createMissingParameterFile();
		
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		tc.train(agent, winnerPercentageThreshold);
	}	
	
	
	private String[] createTrainingSets(){
		String flatNoBlock = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = "-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds ="-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = "-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = "-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything ="-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";
		
		//String[] levels = {flatNoBlock, flatBlocks, withGaps, deadEnds, withCoinsAndTubes, withFrozenEnemies, everything};
		String[] levels = {flatNoBlock, flatBlocks,withCoins, withGaps};
		
//		int maxDifficulty = 1;
//		
//		String[][] s = new String[levels.length][maxDifficulty];
//		
//		for (int i = 0; i < levels.length; i++){
//			for (int j = 0; j < maxDifficulty; j++){
//				s[i][j] = levels[i] + " -ld " + j;
//			}
//		}
//		String[][]z = new String[maxDifficulty]
		
//		return s;
		return levels;
	}		
	
	private void train(MasterAgent agent, double winnerPercentageThreshold) throws IOException{
		String levelParameters = "";
		
		//Create evaluator		
		AboveAverageEvaluator evaluator = new AboveAverageEvaluator(levelParameters, agent);
				
		//Create trainer
		String delimiter = new File("").separator;
		ImprovedTrainer t = new ImprovedTrainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, evaluator, delimiter);
				
		//Train network
		String[] trainingSets = createTrainingSets();
		t.trainNetwork(trainingSets, winnerPercentageThreshold,3,5);
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
