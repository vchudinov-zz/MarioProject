package vikrasim.competition.evaluators;

import ch.idsia.benchmark.mario.environments.Environment;

public class FormularSimon implements Evaluator {

	@Override
	public double evaluate(Environment environment) {
		double kills,coins,time,shrooms,mode,distance;
		int ev[] = environment.getEvaluationInfoAsInts();
		double localFitness = ev[0];
		
		time = ev[11];
	    mode = ev[7]+1;
	    distance = ev[0];
	    localFitness = distance + time + mode * 100;;
	    
	    return localFitness;
	    
	}

}
