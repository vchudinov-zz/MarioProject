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
import vikrasim.agents.AgentScannerNEAT;
import vikrasim.agents.GapAgent;
import vikrasim.agents.MasterAgent;
import vikrasim.competition.evaluators.EvalLength;
import vikrasim.competition.evaluators.Evaluator;
import vikrasim.competition.evaluators.FormularSimon;
import vikrasim.competition.evaluators.EvalSurvival;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.agents.controllers.RandomAgent;
import ch.idsia.agents.controllers.ScaredAgent;
import ch.idsia.agents.learning.MediumMLPAgent;
import ch.idsia.agents.learning.MediumSRNAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class ComplexJudge {

	public static void main(String[] args) {

		ComplexJudge judge = new ComplexJudge();
		judge.startCompetition("Big test");

	}

	private void startCompetition(String competitionName) {
		int numberOfLevels = 1000; // Number of different levels to test on
		int minDifficulty = 0; // Min possible difficulty
		int maxDifficulty = 3; // Max possible difficulty
		String baseResultFileName;
		String baseCompetitionFolder = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Results\\"
				+ competitionName;
		String baseResultFolder;
		String[] levels;

		// Get list of participants
		Vector<BasicMarioAIAgent> agentList = readyAgents();

		// Testing on tube level with enemies
		for (int dif = minDifficulty; dif <= maxDifficulty; dif++) {
			// Create levels
			String[] possibleLevels = { "-vis off -lb on -lca off -lco on -lde off -lf off -lg on -lhs off -ltb on" };
			levels = createLevelsToTestOn(numberOfLevels, possibleLevels, dif,
					dif);
			double[][][] results = runCompetitionRound(agentList, levels);

			baseResultFolder = baseCompetitionFolder + "\\Tube with enemies";
			baseResultFileName = baseResultFolder + "\\ld " + dif;
			testAndCreate(baseResultFolder);
			writeAllResultsToFile(baseResultFileName, agentList, results);
		}

		// Testing on tube level without enemies
		for (int dif = minDifficulty; dif <= maxDifficulty; dif++) {
			// Create levels
			String[] possibleLevels = { "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb on" };
			levels = createLevelsToTestOn(numberOfLevels, possibleLevels, dif,
					dif);
			double[][][] results = runCompetitionRound(agentList, levels);
			baseResultFolder = baseCompetitionFolder + "\\Tube without enemies";
			baseResultFileName = baseResultFolder + "\\ld " + dif;
			testAndCreate(baseResultFolder);

			writeAllResultsToFile(baseResultFileName, agentList, results);
		}

		// Testing on Gap level without enemies
		for (int dif = minDifficulty; dif <= maxDifficulty; dif++) {
			// Create levels
			String[] possibleLevels = { "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off" };
			levels = createLevelsToTestOn(numberOfLevels, possibleLevels, dif,
					dif);
			double[][][] results = runCompetitionRound(agentList, levels);
			baseResultFolder = baseCompetitionFolder + "\\Gap without enemies";
			baseResultFileName = baseResultFolder + "\\ld " + dif;
			testAndCreate(baseResultFolder);
			writeAllResultsToFile(baseResultFileName, agentList, results);
		}

		// Testing on Gap level with enemies
		for (int dif = minDifficulty; dif <= maxDifficulty; dif++) {
			// Create levels
			String[] possibleLevels = { "-vis off -lb on -lca off -lco on -lde off -lf off -lg on -lhs off -ltb off" };
			levels = createLevelsToTestOn(numberOfLevels, possibleLevels, dif,
					dif);
			double[][][] results = runCompetitionRound(agentList, levels);
			baseResultFolder = baseCompetitionFolder + "\\Gap with enemies";
			baseResultFileName = baseResultFolder + "\\ld " + dif;
			testAndCreate(baseResultFolder);
			writeAllResultsToFile(baseResultFileName, agentList, results);
		}

		// Testing a standard mario game
		for (int dif = minDifficulty; dif <= maxDifficulty; dif++) {
			// Create levels
			String[] possibleLevels = { "-vis off" };
			levels = createLevelsToTestOn(numberOfLevels, possibleLevels, dif,
					dif);
			double[][][] results = runCompetitionRound(agentList, levels);
			baseResultFolder = baseCompetitionFolder + "\\Standard mario";
			baseResultFileName = baseResultFolder + "\\ld " + dif;
			testAndCreate(baseResultFolder);
			writeAllResultsToFile(baseResultFileName, agentList, results);
		}

	}

	private double[][][] runCompetitionRound(
			Vector<BasicMarioAIAgent> agentList, String[] levels) {
		double[][][] results = new double[3][levels.length][agentList.size()];

		for (int i = 0; i < levels.length; i++) {
			if (i % 10 == 0) {
				System.out.println("Training on level " + (i) + " out of "
						+ levels.length);
			}
			String testLevel = levels[i];
			for (int j = 0; j < agentList.size(); j++) {

				BasicMarioAIAgent a = agentList.get(j);
				double[] simResults = runSimulation(a, testLevel);
				// Save survival(0), length(1) and jumps(2)
				for (int k = 0; k < 3; k++) {
					results[k][i][j] = simResults[k];
				}
			}
		}

		return results;
	}

	private double[] runSimulation(BasicMarioAIAgent a, String levelOptions) {
		double[] results = new double[3];
		int numberOfJumps = 0;
		boolean onGroundBefore = true;
		boolean onGroundNow = true;
		// Create new environment with chosen parameters
		Environment environment = MarioEnvironment.getInstance();
		environment.reset(levelOptions);
		while (!environment.isLevelFinished()) {
			environment.tick(); // Execute one tick in the game (I think) //STC
			a.integrateObservation(environment);
			environment.performAction(a.getAction());

			// Test if jump occurred
			onGroundNow = a.isMarioOnGround;
			if (!onGroundNow && onGroundBefore) {
				numberOfJumps++;
			}
			onGroundBefore = onGroundNow;
		}

		Evaluator eval;
		// Write survival
		eval = new EvalSurvival();
		results[0] = eval.evaluate(environment);

		// Write length
		eval = new EvalLength();
		results[1] = eval.evaluate(environment);

		// Write number of jumps
		results[2] = numberOfJumps;

		// Test if agent completed the level
		return results;
	}

	private String[] getPossibleLevels() {
		// Viktors levels
		String flatNoBlock = "-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = "-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds = "-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = "-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = "-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything = "-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";

		String[] possibleLevels = { everything };

		return possibleLevels;
	}

	private String[] createLevelsToTestOn(int totalNumberOfLevels,
			String[] levelsToChooseFrom, int minDifficulty, int maxDifficulty) {
		Random rand = new Random();
		String[] levels = new String[totalNumberOfLevels];
		int numberOfDifferentLevels = levelsToChooseFrom.length;
		int difficulty;
		int levelIndex;
		int levelSeed;

		for (int i = 0; i < totalNumberOfLevels; i++) {
			difficulty = rand.nextInt(maxDifficulty + 1) + minDifficulty;
			levelIndex = rand.nextInt(numberOfDifferentLevels);
			levelSeed = rand.nextInt();
			levels[i] = levelsToChooseFrom[levelIndex] + " -ld " + difficulty
					+ " -ls " + levelSeed;
		}

		return levels;
	}

	private void addOurAgent(String file, String name, int lenght, int height,
			Vector<BasicMarioAIAgent> agentList) {
		MasterAgent agentGap = new GapAgent(name, file, 1, 1, lenght, height);
		agentGap.createBrain();
		agentList.add(agentGap);
	}

	private Vector<BasicMarioAIAgent> readyAgents() {
		Vector<BasicMarioAIAgent> agentList = new Vector<>();
		String file;
		String name;
		int lenght;
		int height;

		// Create our agents

		// Krasimira
		// Gap 1
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Krasimira\\Gap Experiment gen 48 best";
		name = "Gap1";
		lenght = 7;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Simon
		// Gap 2

		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\1 Special design RewardNoJump 2 gen 163 best";
		name = "Gap2";
		lenght = 7;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Gap 3
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\2 Special design NoJump gen 566 best";
		name = "Gap3";
		lenght = 7;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Gap 4
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\3 Training environment cont gen 172 best";
		name = "Gap4";
		lenght = 7;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Gap 5
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\4 Training environment No Jump 3 gen 310 best";
		name = "Gap5";
		lenght = 7;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Viktor
		// Gap 6
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Viktor\\Gap Experiment gen 77 best";
		name = "Gap6";
		lenght = 8;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Gap 7
		file = "D:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Viktor\\Mario_NEAT_Agent_Final gen 234 best";
		name = "Gap7";
		lenght = 8;
		height = 7;
		addOurAgent(file, name, lenght, height, agentList);

		// Create scared agent
		BasicMarioAIAgent agentScared = new ScaredAgent();
		agentScared.setName("AgentScared");
		agentList.add(agentScared);

		// Create Forward agent
		BasicMarioAIAgent agentForward = new ForwardAgent();
		agentForward.setName("AgentForward");
		agentList.add(agentForward);

		// Create ForwardJumpingAgent
		BasicMarioAIAgent agentForwardJumping = new ForwardJumpingAgent();
		agentForwardJumping.setName("AgentForwardJumping");
		agentList.add(agentForwardJumping);

		// Create RandomAgent
		BasicMarioAIAgent agentRandom = new RandomAgent();
		agentRandom.setName("AgentRandom");
		agentList.add(agentRandom);

		return agentList;
	}

	private void writeAllResultsToFile(String filename,
			Vector<BasicMarioAIAgent> agents, double[][][] fitness) {
		// Write survival
		String f = filename + " survival.csv";
		writeResultsToFile(f, agents, fitness[0]);

		// Write length
		f = filename + " length.csv";
		writeResultsToFile(f, agents, fitness[1]);

		// Write survival
		f = filename + " jumps.csv";
		writeResultsToFile(f, agents, fitness[2]);
	}

	private void writeResultsToFile(String filename,
			Vector<BasicMarioAIAgent> agents, double[][] fitness) {
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

	private void testAndCreate(String folderPath) {
		File f = new File(folderPath);
		f.mkdirs();
	}

}
