 package vikrasim.evolution.training.evaluators;

import java.util.Vector;

import vikrasim.AgentNEAT;
import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import jneat.evolution.Organism;
import jneat.neuralNetwork.NNode;
import jneat.neuralNetwork.Network;


public class MyMarioEvaluator {
	
	private String levelParameters;
	
	public MyMarioEvaluator(String levelParameters){
		this.levelParameters = levelParameters;
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
		    Agent agent = new AgentNEAT("This rocks", organism);
		    
		    while (!environment.isLevelFinished())
		    {
		        environment.tick(); //Execute one tick in the game (I think) //STC
		        agent.integrateObservation(environment);
		        environment.performAction(agent.getAction());
		    }
		    
		    int[] ev = environment.getEvaluationInfoAsInts();
		    //Blocks travelled 
		    double fitness = ev[0];
		    
		    //Time left
		    int timeLeft = ev[11];
		    
		    organism.setFitness(fitness);
		    boolean isWinner = false;
		    if (environment.isLevelFinished() && timeLeft > 0){
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
