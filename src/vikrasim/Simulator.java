package vikrasim;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class Simulator {

	public static void main(String[] args)
	{
	    //Write parameters to use in simulation
		String options = "-ls 20 -ld 0 -lco off -lb off -le off -lhb off -lg off -ltb off -lhs off -lde off -vis on";
	    System.out.print(options);
	    
	    //Create new environment with chosen parameters
	    Environment environment = MarioEnvironment.getInstance();
	    environment.reset(options);
	    
	    //Create new agent
//	    Agent agent = new ForwardAgent();
//	    Agent agent = new OurBasicAgent("This rocks");
	    String file = "D:\\Users\\Simon\\Documents\\GitHub\\MarioProject\\NEAT data\\Training data\\Mario 1\\starterGenome.txt";
//	    String file = "D:\\Users\\Simon\\Documents\\GitHub\\MarioProject\\NEAT data\\Training data\\Mario 1\\Winners\\Mario 1_win 0";
	    Agent agent = new AgentNEAT("This rocks", file);
	    
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
