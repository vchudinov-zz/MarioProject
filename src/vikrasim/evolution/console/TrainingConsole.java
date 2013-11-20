package vikrasim.evolution.console;

import java.io.File;
import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.EvalQuickness;
import vikrasim.evolution.training.evaluators.FormulaEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;
import vikrasim.evolution.training.trainers.AutomatedTrainer;
import vikrasim.evolution.training.trainers.SimpleTrainer;

public class TrainingConsole extends Console {

	public TrainingConsole(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold, String rootDataFolder) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold, rootDataFolder );
	}



	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		//String nameOfExperiment = "Mario 1";
		//String nameOfExperiment = "Mario FormulaEval";
		String nameOfExperiment = "Mario FormulaEval";
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
		String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		//Simon Desktop
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		//Victor
		//String rootDataFolder = new File("").getAbsolutePath() + "\\NEAT data";
		//Krasimira
		//String rootDataFolder = new File("").getAbsolutePath() + "/NEAT data";
		
	
		TrainingConsole tc = new TrainingConsole(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold, rootDataFolder);
		
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		tc.train(agent, maxNumberOfNoImprovement);
	}
	
	private MasterAgent setupAgent(int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight){		
		
		MasterAgent agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
		return agent;
	}
	
	private String[] createScenarios(){
		int startDifficulty = 5;
		String[] s = {"-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++, 
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					  "-vis off -ls 20 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off -ld " + startDifficulty++,
					 };
		return s;
	}	
	
	public void train(MasterAgent agent, int maxNumberOfNoImprovement) throws IOException{
		//String levelParameters = "-vis off -ls 20";
		String levelParameters = "";
		//levelParameters ="-ls 20 -vis off";
		//Create evaluator
		
		//MasterEvaluator evaluator = new MyMarioEvaluator(levelParameters, agent);
		//MasterEvaluator evaluator = new EvalQuickness(levelParameters, agent);
		MasterEvaluator evaluator = new FormulaEvaluator(levelParameters, agent);
				
		//Create trainer
		//Trainer t = new Trainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,evaluator );
		String delimiter = new File("").separator;
		AutomatedTrainer t = new AutomatedTrainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,evaluator, delimiter );
		
		//Train network
		String[] scenarios = createScenarios();
		t.trainNetwork(scenarios, maxNumberOfNoImprovement);
	}
	
	
	
	        

}
