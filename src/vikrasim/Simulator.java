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
		String options = "-lf off -zs 1 -vis on -ld 0";
	    System.out.print(options);
	    
	    //Create new environment with chosen parameters
	    Environment environment = MarioEnvironment.getInstance();
	    environment.reset(options);
	    
	    //Create new agent
//	    Agent agent = new ForwardAgent();
	    Agent agent = new OurBasicAgent("This rocks");
	    
	    while (!environment.isLevelFinished())
	    {
	        environment.tick(); //Execute one tick in the game (I think) //STC
	        agent.integrateObservation(environment);
	        environment.performAction(agent.getAction());
	    }
	    
	    System.out.println("Evaluation Info:");
	    int[] ev = environment.getEvaluationInfoAsInts();
	    for (int anEv : ev)
	    {
	        System.out.print(anEv + ", ");
	    }
//	        }
	    System.exit(0);
	}

}
