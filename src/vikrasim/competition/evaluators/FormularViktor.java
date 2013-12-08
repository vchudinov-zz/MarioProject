package vikrasim.competition.evaluators;

import ch.idsia.benchmark.mario.environments.Environment;

public class FormularViktor implements Evaluator {

	@Override
	public double evaluate(Environment environment) {
		double kills,coins,time,shrooms,mode,distance;
		int ev[] = environment.getEvaluationInfoAsInts();
		double localFitness = ev[0];
	    kills = ev[6]*0.8;
	    coins = ev[10]*0.75;
	    shrooms = ev[9]*10;
	    //mode = ev[7]+1;
	    localFitness += kills + coins + shrooms;
	    
	    return localFitness;
	}

}
