package vikrasim.evolution.console;

import vikrasim.evolution.training.Trainer;
import vikrasim.evolution.training.evaluators.MyMarioEvaluator;

public class TrainingConsole extends Console {

	public TrainingConsole(String nameOfExperiment, int maxNumberOfGenerations,
			boolean stopOnFirstGoodOrganism, double errorThreshold) {
		super(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,
				errorThreshold);
	}



	public static void main(String[] args) {
		String nameOfExperiment = "Mario 1";
		int maxNumberOfGenerations = 100;
		boolean stopOnFirstGoodOrganism = true;
		double errorThreshold = 0.1;
		
		TrainingConsole tc = new TrainingConsole(nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism, errorThreshold);
		
		tc.train();
	}
	
	
	
	public void train(){
		String levelParameters = "-ls 20 -ld 0 -lco off -lb off -le off -lhb off -lg off -ltb off -lhs off -lde off -vis off";
		//levelParameters ="-ls 20 -vis off";
		//Create evaluator		
		MyMarioEvaluator evaluator = new MyMarioEvaluator(levelParameters);
				
		//Create trainer
		Trainer t = new Trainer(parameterFileName, debugParameterFileName, genomeFileName, genomeBackupFileName, lastPopulationInfoFileName, generationInfoFolder, winnerFolder, nameOfExperiment, maxNumberOfGenerations, stopOnFirstGoodOrganism,evaluator );
				
		//Train network
		t.trainNetwork();
	}
	
	        

}
