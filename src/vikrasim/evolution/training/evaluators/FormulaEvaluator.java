package vikrasim.evolution.training.evaluators;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import jneat.evolution.Organism;
import vikrasim.agents.MasterAgent;

public class FormulaEvaluator extends MasterEvaluator{

	public FormulaEvaluator(String levelParameters, MasterAgent agent) {
		super(levelParameters, agent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean evaluate(Organism organism) {
		boolean success = false;
		 
		 success = runSimulation(organism);
	  
		 return success; 
	}
	
	private boolean runSimulation(Organism organism){
	    //Write parameters to use in simulation
		String options = levelParameters;
	    
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
	   
	    while (!environment.isLevelFinished() && ticks < maxTicks)
	    {	environment.tick(); //Execute one tick in the game (I think) //STC
	        agent.integrateObservation(environment);
	        environment.performAction(agent.getAction());
	        int[] ev = environment.getEvaluationInfoAsInts();
	        if (ev[0] > oldDistance){
	        	maxTicks += (ev[0] - oldDistance) * 10;
	        	oldDistance = ev[0];		        	
	        }
	        ticks++;
	    }
	    
	    int[] ev = environment.getEvaluationInfoAsInts();
	    //Blocks travelled 
	    double fitness = ev[0];
	    
	    //Time left
	    //int timeLeft = ev[11];
	    //System.out.println("Getting " + ev[11]);
	    organism.setFitness(fitness);
	    
	    int status = environment.getMarioStatus();
	    
	    boolean isWinner = false;
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
	    	fitness += (kills + coins + shrooms + mode+time)/5;
	    	organism.setFitness(fitness);
	    	isWinner = true;
	    	organism.setWinner(true);
	    } else{
	    	isWinner = false;
	    	organism.setWinner(false);
	    }
	    
	    //environment = null;
	    //agent = null;


	    return isWinner;
	}

}
