package vikrasim.evolution.training.evaluators;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import jneat.evolution.Organism;
import vikrasim.agents.MasterAgent;

public class NoJumpEvaluator extends MasterEvaluator {

	public NoJumpEvaluator(String levelParameters, MasterAgent agent) {
		super(levelParameters, agent);
	}
	
	@Override
	public boolean evaluate(Organism organism, String[] trainingSet) {
		boolean success = false;
		 
		 success = runSimulation(organism, trainingSet);
	  
		 return success; 
	}
	
	@Override
	public boolean evaluate(Organism organism) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	private boolean runSimulation(Organism organism, String[] trainingSet){
	    double totalFitness = 0;
		boolean finishedAllLevels = true;
		
	    for (int i = 0; i < trainingSet.length; i++){
			//Write parameters to use in simulation
			String options = trainingSet[i];
		    
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
		    boolean[] actions;
		    int numberOfJumps = 0;
		    
		    while (!environment.isLevelFinished() && ticks < maxTicks){	
		    	environment.tick(); //Execute one tick in the game //STC
		    	agent.integrateObservation(environment);
		    	actions = agent.getAction();
		        environment.performAction(actions);
		        if(actions[1]){
		        	numberOfJumps++;
		        }
		        ev = environment.getEvaluationInfoAsInts();
		        if (ev[0] > oldDistance){
		        	maxTicks += (ev[0] - oldDistance) * 10;
		        	oldDistance = ev[0];		        	
		        }
		        ticks++;
		    }
		    
		    //Blocks travelled 
		    //kills = ev[6]*0.75;
		    //coins = ev[10]*0.75;
		    //time = ev[11];
		    //shrooms = ev[9]*10;
		    mode = ev[7]+1;
		    distance = ev[0];
		    double localFitness = distance - numberOfJumps;
		    //localFitness += (kills + coins + shrooms + mode)/4;
		    int status = environment.getMarioStatus();
		    
		    if (status== Mario.STATUS_WIN){
		    	
		    	//double shell = ev[4];
		    	//double stomp = ev[5];
		    	//double fire = ev[3];
		    	//can be easily changed by uncomenting the above three
		    	
		    	//localFitness = localFitness ;//+ time + mode * 100;
		    	organism.setWinner(true);
		    } else{
		    	finishedAllLevels = false;
		    	organism.setWinner(false);
		    }
		    totalFitness += localFitness;
		    
	    }
		
		double fitness = totalFitness; // (double) trainingSet.length;
		
		organism.setFitness(fitness);

	    return finishedAllLevels;
	}

}
