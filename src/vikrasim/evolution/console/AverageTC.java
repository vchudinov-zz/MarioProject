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

public class AverageTC extends Console {

	public AverageTC(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}
	
	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		String nameOfExperiment = "AverageTrainingSessions";
		int maxNumberOfGenerations = 500;
		boolean stopOnFirstGoodOrganism = false;
		double errorThreshold = 0.1;
		int maxNumberOfNoImprovement = 10;
		
		//Info about agent (if used)
		int zLevelEnemies = 2;
		int zLevelScene = 1;
		int scannerLength = 3;
		int scannerHeight = 3;
		
		//Simon Laptop
		String rootDataFolder = "C:\\Users\\Simon\\Documents\\MarioFun\\NEAT data";
		//Simon Desktop
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		//Victor
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		//Krasimira
		//String rootDataFolder = new File("").getAbsolutePath() + "/NEAT data";
		
	
		AverageTC tc = new AverageTC(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold, rootDataFolder);
		
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		tc.train(agent, maxNumberOfNoImprovement);
	}
	
	private MasterAgent setupAgent(int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight){		
		
		MasterAgent agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
		return agent;
	}
	
	private String[][] createTrainingSets(){
		String flatNoEnemies = "-vis off -ls 20 -lb off -lca off -lco off -lde off -le off -lf on -lg off -lhs off -ltb off";
		String flatWithGaps = "-vis off -ls 20 -lb off -lca off -lco off -lde off -le off -lf on -lg on -lhs off -ltb off";
		String deadEnds ="-vis off -ls 20 -lb off -lca off -lco off -lde on -le off -lf off -lg off -lhs off -ltb off";
		String flatWithEnemies = "-vis off -ls 20 -lb off -lca off -lco off -lde off -le on -lf on -lg off -lhs off -ltb off";
		//String everything ="-vis off -ls 20 -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";
		
		String[] levels = {deadEnds};
		
		int maxDifficulty = 10;
		
		String[][] s = new String[maxDifficulty][levels.length];
		
		for (int i = 0; i < maxDifficulty; i++){
			for (int j = 0; j < levels.length; j++){
				s[i][j] = levels[j] + " -ld " + i;
			}
		}
		
		return s;
	}	
	
	private void train(MasterAgent agent, int maxNumberOfNoImprovement) throws IOException{
		String levelParameters = "";
		
		//Create evaluator		
		AverageEvaluator evaluator = new AverageEvaluator(levelParameters, agent);
				
		//Create trainer
		String delimiter = new File("").separator;
		AverageTrainer t = new AverageTrainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, evaluator, delimiter);
				
		//Train network
		String[][] trainingSets = createTrainingSets();
		t.trainNetwork(trainingSets, maxNumberOfNoImprovement);
	}
	
	
	
	        

}
