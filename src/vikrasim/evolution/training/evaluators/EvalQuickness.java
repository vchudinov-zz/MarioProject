 package vikrasim.evolution.training.evaluators;

import java.util.Vector;

import vikrasim.agents.AgentNEAT;
import vikrasim.agents.MasterAgent;
import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import jneat.evolution.Organism;
import jneat.neuralNetwork.NNode;
import jneat.neuralNetwork.Network;


public class EvalQuickness extends MasterEvaluator{
	
	public EvalQuickness(String levelParameters, MasterAgent agent) {
		super(levelParameters, agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Evaluates an organism and returns true if the organism has found and answer
	 * @param organism
	 * @return
	 */
	 public boolean evaluate(Organism organism){
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
		    while (!environment.isLevelFinished() && ticks < maxTicks)
		    {
		        environment.tick(); //Execute one tick in the game (I think) //STC
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
		    int timeLeft = ev[11];
		    organism.setFitness(fitness);
		    
		    int status = environment.getMarioStatus();
		    
		    boolean isWinner = false;
		    if (status== Mario.STATUS_WIN){
		    	fitness += timeLeft;
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

	@Override
	public boolean evaluate(Organism organism, String[] trainingSet) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
		 
	 

}
