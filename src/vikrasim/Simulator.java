package vikrasim;

import java.io.IOException;

import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.GapAgent;
import vikrasim.agents.MasterAgent;
import ch.idsia.agents.Agent;
import ch.idsia.agents.AgentsPool;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.agents.controllers.ScaredShooty;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class Simulator {

	public static void main(String[] args) throws IOException {
		// Viktors levels
		String flatNoBlock = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = "-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis on -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds = "-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = "-vis on -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = "-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything = "-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";

		// Write parameters to use in simulation
		String options = "-vis on -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		options = options;
		// options = "-ls 20 -vis on";
		System.out.print(options);

		// Create new environment with chosen parameters
		Environment environment = MarioEnvironment.getInstance();
		environment.reset(options);

		// Create new agent
		String file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\5 Triathlon 3 gen 732 best";
		MasterAgent agent = new AgentScannerNEAT("ThisRocks", file, 1, 1, 7, 7);
		agent.createBrain();

		// Visualize the agent
		GraphVizWriter writer = new GraphVizWriter();
		String targetFile = "D:\\Users\\Simon\\Dropbox\\MarioFun\\NEAT data\\Training data\\Special design RewardNoJump\\Vizualized.txt";
		writer.writer(file, targetFile, "WinnerDif4");

		// Agent agent = new HumanKeyboardAgent();
		while (!environment.isLevelFinished()) {
			environment.tick(); // Execute one tick in the game (I think) //STC
			agent.integrateObservation(environment);
			environment.performAction(agent.getAction());
		}

		System.out.println("Evaluation Info:");
		int[] ev = environment.getEvaluationInfoAsInts();

		for (int i : ev) {
			System.out.print(i + "  ");
		}
		System.exit(0);
	}

}
