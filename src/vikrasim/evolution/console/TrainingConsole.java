package vikrasim.evolution.console;

import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import vikrasim.evolution.training.Trainer;
import vikrasim.evolution.training.evaluators.EvalQuickness;
import vikrasim.evolution.training.evaluators.FormulaEvaluator;
import vikrasim.evolution.training.evaluators.MasterEvaluator;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;

public class TrainingConsole extends Console {

	public TrainingConsole(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold);
	}



	public static void main(String[] args) throws IOException {
		
		//Info about experiment
		//String nameOfExperiment = "Mario 1";
		//String nameOfExperiment = "Mario FormulaEval";
		String nameOfExperiment = "Mario Scanners";
		int maxNumberOfGenerations = 100;
		boolean stopOnFirstGoodOrganism = false;
		double errorThreshold = 0.1;
		
		//Info about agent (if used)
		int zLevelEnemies = 2;
		int zLevelScene = 1;
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
	
	
	
	public void train(MasterAgent agent) throws IOException{
		//String levelParameters = "-vis off -ls 20";
		String levelParameters = "-vis off -ls 20 -ld 0 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off";
		//levelParameters ="-ls 20 -vis off";
		//Create evaluator
		
		//MasterEvaluator evaluator = new MyMarioEvaluator(levelParameters, agent);
		//MasterEvaluator evaluator = new EvalQuickness(levelParameters, agent);
		MasterEvaluator evaluator = new FormulaEvaluator(levelParameters, agent);
				
		//Create trainer
		Trainer t = new Trainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,evaluator );
				
		//Train network
		t.trainNetwork();
	}
	
	        

}
