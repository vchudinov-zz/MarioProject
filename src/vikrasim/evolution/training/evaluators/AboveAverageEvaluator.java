package vikrasim.evolution.training.evaluators;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import jneat.evolution.Organism;
import vikrasim.agents.MasterAgent;

public class AboveAverageEvaluator extends MasterEvaluator {

	public AboveAverageEvaluator(String levelParameters, MasterAgent agent) {
		super(levelParameters, agent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean evaluate(Organism organism, String[] trainingSet) {
		boolean success = false;
		 
		// success = runSimulation(organism, trainingSet);
	  
		 return success; 
	}
	
	@Override
	public boolean evaluate(Organism organism, String options, int runs) {
		boolean success = false;
		 
		success = runSimulation(organism, options, runs);
	  
		return success;
	}

	private boolean runSimulation(Organism organism, String levelOptions, int runs){
	    double totalFitness = 0;
		boolean finishedAllLevels = true;
		int seed = 10;
	    for (int i = 0; i < runs; i++){
	    	String options = levelOptions + " -ls " + seed*i;
		    
		    //Create new environment with chosen parameters
		    Environment environment = MarioEnvironment.getInstance();
		    environment.reset(options);
		    
		    //Create new agent based on organism
		    agent.setBrain(organism);
		    agent.create();
		    
		    int ticks = 0;
		    int maxTicks = 500;
		    int oldDistance = 0;
		    double kills,coins,time,shrooms,mode,distance;
		    //shell,stomp,fire;
		    int[] ev = null;
		    while (!environment.isLevelFinished() && ticks < maxTicks){	
		    	environment.tick(); //Execute one tick in the game //STC
		    	agent.integrateObservation(environment);
		        environment.performAction(agent.getAction());
		        ev = environment.getEvaluationInfoAsInts();
		        if (ev[0] > oldDistance){
		        	maxTicks += (ev[0] - oldDistance) * 10;
		        	oldDistance = ev[0];		        	
		        }
		        ticks++;
		    }
		    
		    //Blocks travelled 
		    double localFitness = ev[0];
		    
		    int status = environment.getMarioStatus();
		    
		    if (status== Mario.STATUS_WIN){
		    	kills = ev[6]*0.75;
		    	coins = ev[10]*0.75;
		    	time = ev[11];
		    	shrooms = ev[9]*10;
		    	mode = (ev[7]+1)*100;
		    	distance = ev[0];
		    	//double shell = ev[4];
		    	//double stomp = ev[5];
		    	//double fire = ev[3];
		    	//can be easily changed by uncomenting the above three
		    	localFitness += (kills + coins + shrooms + mode+time)/5;
		    	organism.setWinner(true);
		    } else{
		    	finishedAllLevels = false;
		    	organism.setWinner(false);
		    }
		    totalFitness += localFitness;
		    
	    }
		
		double fitness = totalFitness / (double) runs;
		
		organism.setFitness(fitness);

	    return finishedAllLevels;
	}

	@Override
	public boolean evaluate(Organism organism) {
		// TODO Auto-generated method stub
		return false;
	}

	

	
}
