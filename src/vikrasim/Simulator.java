package vikrasim;


import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.agents.controllers.ScaredShooty;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class Simulator {

	public static void main(String[] args) throws IOException
	{
		String noEnemies = "-vis on -ls 20 -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis on -lb off -lca off -lco off -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds ="-vis on -ls 20 -lb off -lca off -lco off -lde on -le off -lf off -lg off -lhs off -ltb off";
		String withEnemies = "-vis on -lco on -lb on -lhb off -lg off -ltb off -lhs off -lde off";
		String everything ="-vis on -ls 20 -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";
		
		//Write parameters to use in simulation
		String options = withGaps + " -ld 2 -ls 80";
		System.out.print(options);
	    
	    //Create new environment with chosen parameters
	    Environment environment = MarioEnvironment.getInstance();
	    environment.reset(options);
	    
	    //Create new agent
	    String file = "D:\\Users\\Simon\\Documents\\MarioFun\\NEAT data\\Training data\\Training sensor size 4 v2\\testGenome.txt";
	    Agent agent = new AgentScannerNEAT("This rocks", file,2,1,5,5);
	    //Agent agent = new HumanKeyboardAgent();

	    
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
