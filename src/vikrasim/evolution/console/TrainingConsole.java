package vikrasim.evolution.console;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.evaluators.EvalQuickness;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;
import vikrasim.evolution.training.trainers.Trainer;

public class TrainingConsole extends Console {

	public TrainingConsole(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold);
	}



	public static void main(String[] args) {
		
		//Info about experiment
		//String nameOfExperiment = "Mario 1";
		String nameOfExperiment = "Mario Scanners";
		int maxNumberOfGenerations = 500;
		boolean stopOnFirstGoodOrganism = false;
		double errorThreshold = 0.1;
		
		//Info about agent (if used)
		int zLevelEnemies = 2;
		int zLevelScene = 2;
		int scannerLength = 3;
		int scannerHeight = 3;
		
	
		TrainingConsole tc = new TrainingConsole(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold);
		
		MasterAgent agent = tc.setupAgent(zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		tc.train(agent);
	}
	
	private MasterAgent setupAgent(int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight){		
		
		MasterAgent agent = new AgentScannerNEAT(nameOfExperiment, genomeFileName, zLevelEnemies, zLevelScene, scannerLength, scannerHeight);
		
		return agent;
	}
	
	
	
	public void train(MasterAgent agent){
		String levelParameters = "-vis off -ls 10";
		//levelParameters ="-ls 20 -vis off";
		//Create evaluator
		
		//MasterEvaluator evaluator = new MyMarioEvaluator(levelParameters, agent);
		MasterEvaluator evaluator = new EvalQuickness(levelParameters, agent);
				
		//Create trainer
		Trainer t = new Trainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,evaluator );
				
		//Train network
		t.trainNetwork();
	}
	
	        

}
