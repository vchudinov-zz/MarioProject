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
import vikrasim.competition.evaluators.EvalLength;
import vikrasim.competition.evaluators.Evaluator;
import vikrasim.competition.evaluators.FormularSimon;
import vikrasim.competition.evaluators.EvalSurvival;
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
		int numberOfLevels = 1000; //Number of different levels to test on
		int minDifficulty = 2; //Min possible difficulty
		int maxDifficulty = 2; //Max possible difficulty
		String fileName;
		Evaluator eval;		
		
		// Create levels
		String[] possibleLevels = getPossibleLevels();
		String[] levels = createLevelsToTestOn(numberOfLevels, possibleLevels, minDifficulty, maxDifficulty);		

		// Get list of participants
		Vector<Agent> agentList = readyAgents();

		// Run competition, testing survival
		eval = new EvalSurvival(); //Evaluator used to score agents
		double[][] resultsSurvival = runCompetitionRound(agentList, levels, eval);
		fileName = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\ResultsCompetition results Survival ld2 everything.csv";
		writeResultsToFile(fileName, agentList, resultsSurvival);
		
		//Run competition, testing score (length of levels)
		eval = new EvalLength(); //Evaluator used to score agents
		double[][] resultsScore = runCompetitionRound(agentList, levels, eval);
		fileName = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\ResultsCompetition results Length ld2 everything.csv";
		writeResultsToFile(fileName, agentList, resultsScore);
	}
	
	private double[][] runCompetitionRound(Vector<Agent> agentList, String[] levels,Evaluator eval ){
		double[][] results = new double[levels.length][agentList.size()];

		for (int i = 0; i < levels.length; i++) {
			if ( i % 10 == 0){
				System.out.println("Training on level " + (i) + " out of " + levels.length);
			}			
			String testLevel = levels[i];
			for (int j = 0; j < agentList.size(); j++) {
				Agent a = agentList.get(j);
				results[i][j] = runSimulation(a, testLevel, eval);
			}
		}
		
		return results;
	}

	private double runSimulation(Agent a, String levelOptions, Evaluator eval) {
		// Create new environment with chosen parameters
		Environment environment = MarioEnvironment.getInstance();
		environment.reset(levelOptions);
		while (!environment.isLevelFinished()) {
			environment.tick(); // Execute one tick in the game (I think) //STC
			a.integrateObservation(environment);
			environment.performAction(a.getAction());
		}
		
		//Test if agent completed the level
		return eval.evaluate(environment);
	}

	private String[] getPossibleLevels() {
		// Viktors levels
		String flatNoBlock = 		"-vis off -lb off -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String flatBlocks = 		"-vis off -lb on -lca off -lco off -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withCoins = 			"-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg off -lhs off -ltb off";
		String withGaps = 			"-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb off";
		String deadEnds = 			"-vis off -lb on -lca off -lco on -lde on -le off -lf off -lg on -lhs off -ltb off";
		String withTubes = 			"-vis off -lb on -lca off -lco on -lde off -le off -lf off -lg on -lhs off -ltb on";
		String withFrozenEnemies = 	"-vis off -lb on -lca off -lco on -lde on -le on -lf off -lg on -lhs off -ltb on -fc on";
		String everything = 		"-vis off -lb on -lca on -lco on -lde on -lf off -lg on -lhs on -ltb on";

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
			levels[i] = levelsToChooseFrom[levelIndex] + " -ld " + difficulty + " -ls " + levelSeed;
		}

		return levels;
	}
	
	private void addOurAgent(String file, String name, int lenght, int height,Vector<Agent> agentList ){
		MasterAgent agentGap = new GapAgent(name, file, 1, 1, lenght, height);
		agentGap.createBrain();
		agentList.add(agentGap);
	}

	private Vector<Agent> readyAgents() {
		Vector<Agent> agentList = new Vector<>();
		String file;
		String name;
		int lenght;
		int height;
	
		//Create our agents
		/*	
		//Krasimira
			file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Krasimira\\Gap Experiment gen 48 best";
			name ="Gap1";
			lenght = 7;
			height = 7;
			addOurAgent(file, name, lenght, height, agentList);
		*/
			//Simon
				//Agent 1
			/*
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\1 Special design RewardNoJump 2 gen 163 best";
				name ="Gap2";
				lenght = 7;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);
				
				//Agent 2
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\2 Special design NoJump gen 566 best";
				name ="Gap3";
				lenght = 7;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);
				*/
				//Agent 3
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\3 Training environment cont gen 172 best";
				name ="Gap4";
				lenght = 7;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);
			/*
				//Agent 4
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Simon\\4 Training environment No Jump 3 gen 310 best";
				name ="Gap5";
				lenght = 7;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);
		
		/*
			//Viktor
				//Agent 5
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Viktor\\Gap Experiment gen 77 best";
				name ="Gap6";
				lenght = 8;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);
				
				//Agent 6
				file = "C:\\Users\\Simon\\Dropbox\\Mario Project\\Agents\\Viktor\\Mario_NEAT_Agent_Final gen 234 best";
				name ="Gap7";
				lenght = 8;
				height = 7;
				addOurAgent(file, name, lenght, height, agentList);

		// Create scared agent
		Agent agentScared = new ScaredAgent();
		agentScared.setName("AgentScared");
		agentList.add(agentScared);

		// Create Forward agent
		Agent agentForward = new ForwardAgent();
		agentForward.setName("AgentForward");
		agentList.add(agentForward);
*/
		// Create ForwardJumpingAgent
		Agent agentForwardJumping = new ForwardJumpingAgent();
		agentForwardJumping.setName("AgentForwardJumping");
		agentList.add(agentForwardJumping);
/*
		// Create RandomAgent
		Agent agentRandom = new RandomAgent();
		agentRandom.setName("AgentRandom");
		agentList.add(agentRandom);
		*/
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
