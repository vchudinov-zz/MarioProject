package vikrasim.evolution.console;

import java.io.File;
import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.AverageEvaluator;
import vikrasim.evolution.training.evaluators.EvalQuickness;
import vikrasim.evolution.training.evaluators.FormulaEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;
import vikrasim.evolution.training.trainers.AutomatedTrainer;
import vikrasim.evolution.training.trainers.AverageTrainer;
import vikrasim.evolution.training.trainers.SimpleTrainer;
import vikrasim.genomeFileCreation.FileCreater;

public class AutoAverageTC extends Console {

	public AutoAverageTC(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}
	
	public static void main(String[] args) throws IOException {
		for (int size = 1; size <= 9; size ++){
			//Info about experiment
			String nameOfExperiment = "Training sensor size " + size;
			int maxNumberOfGenerations = 300;
			boolean stopOnFirstGoodOrganism = false;
			double errorThreshold = 0.1;
			double winnerPercentageThreshold = 0.20;
			
			//Info about agent (if used)
			int zLevelEnemies = 2;
			int zLevelScene = 1;
			int scannerLength = size;
			int scannerHeight = size;
			
			//Simon Laptop
			//String rootDataFolder = "C:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
			
			//Simon Desktop
			String rootDataFolder = "D:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
			
			//Victor
			//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
			
			//Krasimira
			//String rootDataFolder = new File("").getAbsolutePath() + "/NEAT data";
			
		
			AutoAverageTC tc = new AutoAverageTC(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold, rootDataFolder);
			
			tc.createMissingParameterFile();
			
			MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
			tc.train(agent, winnerPercentageThreshold);
			
			tc = null;
		}
	}	
	
	
	private String[][] createTrainingSets(){
		String noEnemies = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds ="-vis off -lb on -lca off -lco off -lde on -le off -lf off -lg off -lhs off -ltb off";
		String withEnemies = "-vis off -lco on -lb on -lhb off -lg off -ltb off -lhs off -lde off";
		String everything ="-vis off";
		
		String[] levels = {withGaps};
		int startDifficulty = 0;
		int maxDifficulty = 10;
		int numberOfDifferentLevels = 1;
		
		String[][] s = new String[maxDifficulty][levels.length * numberOfDifferentLevels];
		
		for (int i = 0; i < maxDifficulty; i++){
			for (int j = 0; j < levels.length; j++){
				for (int k = 0; k < numberOfDifferentLevels; k++){
					s[i][j*numberOfDifferentLevels+k] = levels[j] + " -ld " + (i+startDifficulty) + " -ls " + ((k+1) * 12);
				}				
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
		t.trainNetwork(trainingSets, winnerPercentageThreshold);
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
