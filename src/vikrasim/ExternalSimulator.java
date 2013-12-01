package vikrasim;


import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.MasterAgent;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.agents.controllers.ScaredShooty;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class ExternalSimulator {

	public static void main(String[] args) throws IOException
	{
		String LevelParameters = args[0];
		String levelDifficulty = args[1];
		String levelSeed = args[2];
		int zLevelEnemies = Integer.parseInt(args[3]);
		int zLevelScene = Integer.parseInt(args[4]);
		int scannerLength = Integer.parseInt(args[5]);
		int scannerHeight = Integer.parseInt(args[6]);
		String genomeFile = args[7];

	    
	    //Create new environment with chosen parameters
	    Environment environment = MarioEnvironment.getInstance();
	    String options = LevelParameters + " -ld " + levelDifficulty + " -ls " + levelSeed;
	    environment.reset(options);
	    
	    //Create new agent
	    String file = genomeFile;
	    MasterAgent agent = new AgentScannerNEAT("This rocks", file,zLevelEnemies,zLevelScene,scannerLength,scannerHeight);
	    agent.createBrain();
	    
	   
	    while (!environment.isLevelFinished())
	    {
	        environment.tick(); //Execute one tick in the game (I think) //STC
	        agent.integrateObservation(environment);
	        environment.performAction(agent.getAction());
	    }
	    
	    System.out.println("Evaluation Info:");
	    int[] ev = environment.getEvaluationInfoAsInts();
	    
	    for (int  i : ev){
	    	System.out.print(i + "  ");
	    }
	    System.exit(0);
	}

}
