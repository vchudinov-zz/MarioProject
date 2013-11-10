package vikrasim;


import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class Simulator {

	public static void main(String[] args) throws IOException
	{
	    //Write parameters to use in simulation
		String options = "-vis on -ls 20 -ld 5 -lco on -lb on -le on -lhb on -lg off -ltb off -lhs off -lde off";
		//options = "-ls 20 -vis on";
		System.out.print(options);
	    
	    //Create new environment with chosen parameters
	    Environment environment = MarioEnvironment.getInstance();
	    environment.reset(options);
	    
	    //Create new agent
//	    Agent agent = new ForwardAgent();
//	    Agent agent = new OurBasicAgent("This rocks");
//	    String file = "D:\\Users\\Simon\\Documents\\GitHub\\MarioProject\\NEAT data\\Training data\\Mario 1\\Testing\\genomeToTest.txt";
//	    String file = "D:\\Users\\Simon\\Documents\\GitHub\\MarioProject\\NEAT data\\Training data\\Mario 1\\Winners\\Mario 1_win 0";
//	    String file = "C:\\Users\\Simon\\Documents\\Git Repository\\MarioProject\\NEAT data\\Training data\\Mario 1\\Winners\\Mario 1_win 0";
//	    String file = "D:\\eclipse\\Workspace\\MarioProject\\NEAT data\\Training data\\Mario Scanners\\Winners\\Mario Scanners gen 15 best";
	    String file = "D:\\Users\\Simon\\Documents\\GitHub\\MarioProject\\NEAT data\\Training data\\Mario FormulaEval\\starterGenome.txt";
	    Agent agent = new AgentScannerNEAT("This rocks", file,2,2,3,3);
	    
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
