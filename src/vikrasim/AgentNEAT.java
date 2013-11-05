package vikrasim;

import jNeatCommon.IOseq;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import jneat.evolution.Organism;
import jneat.neuralNetwork.Genome;
import jneat.neuralNetwork.NNode;
import jneat.neuralNetwork.Network;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class AgentNEAT extends BasicMarioAIAgent implements Agent {
	Organism brain;
	
	//Array with possible actions the agent can perform
	//protected boolean action[] = new boolean[Environment.numberOfKeys];
	
	public AgentNEAT(String agentName, String genomeFileName) {
		super(agentName);
		
		makeBrain(genomeFileName);
		
		this.zLevelEnemies = 2;
		this.zLevelScene = 2;
	}
	
	public AgentNEAT(String agentName, Organism brain) {
		super(agentName);
		
		this.brain=brain;
		
		this.zLevelEnemies = 2;
		this.zLevelScene = 2;
	}
	
	private void makeBrain(String genomeFileName){
		//Open the file with the genome data
		IOseq starterGenomeFile = new IOseq(genomeFileName);
		boolean ret = starterGenomeFile.IOseqOpenR();
		
		if (ret){
			//Create starter genome
			Genome testGenome = createGenome(starterGenomeFile);
			
			//Create organism
			brain = new Organism(0, testGenome, 1);
			
		} else{
			System.out.println("Error during opening of " + genomeFileName);
		}		
	}
	
	private Genome createGenome (IOseq starterGenomeFile){
		String curWord;
		
		System.out.println("Read genome");
			
		//Read file
		String line = starterGenomeFile.IOseqRead();
		StringTokenizer st = new StringTokenizer(line);
			
		//Skip first word in file
		curWord = st.nextToken();
		
		//Read ID of the genome
		curWord=st.nextToken();
		int id = Integer.parseInt(curWord);
			
		//Create the genome
		System.out.println("Create genome id " + id);
		Genome startGenome = new Genome (id,starterGenomeFile);
			
		return startGenome;
			
	}
	
	@Override
	public boolean[] getAction() {
		
		byte[] observations = readSurroundings();
		int numberOfObservations = observations.length;
		double[] inputs = new double[numberOfObservations + 5];
		for (int i = 0; i <= numberOfObservations; i++){ 
			inputs[i] = observations[i];
		}
		
		inputs[numberOfObservations + 1] = convertBooleanToByte(isMarioAbleToJump);
		inputs[numberOfObservations + 2] = convertBooleanToByte(isMarioOnGround);
		inputs[numberOfObservations + 3] = convertBooleanToByte(isMarioAbleToShoot);
		inputs[numberOfObservations + 4] = marioMode;
		
		inputs[numberOfObservations + 5] = 1; //Bias
		
		Network network = brain.net;
		boolean success = propagateSignal(network, network.max_depth(), inputs);
		if (success) {
			//Read the output value	
			Vector<NNode> outputNodes = network.getOutputs();
			for (int i = 0; i < outputNodes.size(); i++){
				action[i] = convertToBoolean(outputNodes.get(i).getActivation());
			}
			network.flush();
		}
		
		return action;
	}
	
	private boolean convertToBoolean(double value){
		if(value < 0.5) return false;
		return true;
	}
	
	private boolean propagateSignal(Network net, int net_depth, double[] inputValues){
		boolean success = false;
		 // first activation from sensor to first level of neurons
		 net.load_sensors(inputValues);
		 success = net.activate();
	 
		 // next activation until last level is reached !
		 // use depth to ensure relaxation
	 
		for (int relax = 0; relax <= net_depth; relax++){
			success = net.activate();
		}
		
		return success;
	 }
	
	private byte convertBooleanToByte(Boolean b){
		if (b) return 1;
		return 0;
	}
	private byte[] readSurroundings(){
		ArrayList<Byte> observations = new ArrayList<>();
		int counter = 0;
		//Mario is always in the middle of the 19 x 19 array in levelz Scene and enemies
		int marioX = 9;
		int marioY = 9;
		for (int x = -2; x < 3; x++){
			for (int y = -2; y < 3; y++){
				int obsX = marioX+x;
				int obsY = marioY+y;
				if (obsX < 0) obsX=0;
				if (obsX > 18) obsX=18;
				if (obsY < 0) obsY=0;
				if (obsY > 18) obsY=18;
				observations.add(levelScene[obsX][obsY]);
				//observations.add(enemies[obsX][obsY]);
				//observations.add(mergedObservation[obsX][obsY]);
				counter++;
			}
		}
		
		byte[] result = new byte[observations.size()];
		
		for (int i = 0; i < observations.size(); i++){
			result[i] = observations.get(i);
		}		
		
		return result;
	}
	
	
	
	

}
