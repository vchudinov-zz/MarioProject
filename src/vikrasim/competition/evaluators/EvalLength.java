package vikrasim.competition.evaluators;

import ch.idsia.benchmark.mario.environments.Environment;

public class EvalLength implements Evaluator {

	@Override
	public double evaluate(Environment environment) {
		int ev[] = environment.getEvaluationInfoAsInts();
		
	    double distance = ev[0];
	    double localFitness = distance;
	    
	    return localFitness;
	    
	}

}
