package vikrasim.competition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import competition.cig.sergeykarakovskiy.SergeyKarakovskiy_JumpingAgent;
import competition.gic2010.gameplay.sergeykarakovskiy.SergeyKarakovskiy_ForwardAgent;
import vikrasim.agents.GapAgent;
import vikrasim.agents.MasterAgent;
import vikrasim.competition.evaluators.Evaluator;
import vikrasim.competition.evaluators.FormularSimon;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.agents.controllers.RandomAgent;
import ch.idsia.agents.controllers.ScaredAgent;
import ch.idsia.agents.learning.MediumMLPAgent;
import ch.idsia.agents.learning.MediumSRNAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;


public class Judge {

	public static void main(String[] args) {

		Judge judge = new Judge();
		judge.startCompetition();

	}

	private void startCompetition() {

		// Create levels
		String[] possibleLevels = getPossibleLevels();
		String[] levels = createLevelsToTestOn(1000, possibleLevels, 0, 2);

		// Get list of participants
		Vector<Agent> agentList = readyAgents();

		// Run competition
		double[][] results = new double[levels.length][agentList.size()];

		for (int i = 0; i < levels.length; i++) {
			if ( i % 10 == 0){
				System.out.println("Training on level " + (i) + " out of " + levels.length);
			}			
			String testLevel = levels[i];
			for (int j = 0; j < agentList.size(); j++) {
				Agent a = agentList.get(j);
				results[i][j] = runSimulation(a, testLevel);
			}
		}
		
		//Save results from competition
		String fileName = "D:\\Users\\Simon\\Documents\\MarioFun\\Competition results.csv";
		writeResultsToFile(fileName, agentList, results);

	}

	private double runSimulation(Agent a, String levelOptions) {
		// Create new environment with chosen parameters
		Environment environment = MarioEnvironment.getInstance();
		environment.reset(levelOptions);
		while (!environment.isLevelFinished()) {
			environment.tick(); // Execute one tick in the game (I think) //STC
			a.integrateObservation(environment);
			environment.performAction(a.getAction());
		}
		
		//Test if agent completed the level
		Evaluator eval = new FormularSimon();
		
		return eval.evaluate(environment);
	}

	private String[] getPossibleLevels() {
		// Viktors levels
		String flatNoBlock = 		"-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = 		"-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = 			"-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = 			"-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds = 			"-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = 			"-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = 	"-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything = 		"-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";

		String[] possibleLevels = { flatNoBlock, flatBlocks, withCoins,
				withGaps, withTubes };

		return possibleLevels;
	}

	private String[] createLevelsToTestOn(int totalNumberOfLevels,
			String[] levelsToChooseFrom, int minDifficulty, int maxDifficulty) {
		Random rand = new Random();
		String[] levels = new String[totalNumberOfLevels];
		int numberOfDifferentLevels = levelsToChooseFrom.length;
		int difficulty;
		int levelIndex;

		for (int i = 0; i < totalNumberOfLevels; i++) {
			difficulty = rand.nextInt(maxDifficulty + 1) + minDifficulty;
			levelIndex = rand.nextInt(numberOfDifferentLevels);
			levels[i] = levelsToChooseFrom[levelIndex] + " -ld " + difficulty;
		}

		return levels;
	}

	private Vector<Agent> readyAgents() {
		Vector<Agent> agentList = new Vector<>();

		// Create gap agent
		String file = "D:\\Users\\Simon\\Documents\\MarioFun\\NEAT data\\Training data\\Training environment No Jump 3\\Winners\\Dif 2 levels 13\\Training environment No Jump 3 gen 310 best";
		MasterAgent agentGap = new GapAgent("AgentGap", file, 1, 1, 7, 7);
		agentGap.createBrain();
		agentList.add(agentGap);

		// Create scared agent
		Agent agentScared = new ScaredAgent();
		agentScared.setName("AgentScared");
		agentList.add(agentScared);

		// Create Forward agent
		Agent agentForward = new ForwardAgent();
		agentForward.setName("AgentForward");
		agentList.add(agentForward);

		// Create ForwardJumpingAgent
		Agent agentForwardJumping = new ForwardJumpingAgent();
		agentForwardJumping.setName("AgentForwardJumping");
		agentList.add(agentForwardJumping);

		// Create RandomAgent
		Agent agentRandom = new RandomAgent();
		agentRandom.setName("AgentRandom");
		agentList.add(agentRandom);
		
		return agentList;
	}

	private void writeResultsToFile(String filename, Vector<Agent> agents,
			double[][] fitness) {
		// Test if file exists
		File file = new File(filename);
		if (!file.exists()) {
			try {
				// Try creating the file
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(filename, true)));
			// Write basic info to file
			String s = "Level;";
			for (Agent a : agents) {
				s = s + a.getName() + ";";
			}
			s = s.substring(0, s.length() - 1);
			out.println(s);

			// Write fitness values
			for (int i = 0; i < fitness.length; i++) {
				s = i + ";";
				for (int j = 0; j < fitness[i].length; j++) {
					s = s + fitness[i][j] + ";";
				}
				s = s.substring(0, s.length() - 1);
				out.println(s);
			}
			out.close();
		} catch (IOException e) {
			// oh noes!
		}

	}

}
