package vikrasim.agents;

import jNeatCommon.IOseq;

import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Vector;

import vikrasim.agents.scanners.Scanner;
import vikrasim.agents.scanners.Scanner.Dir;
import vikrasim.agents.scanners.Scanner.ScannerType;
import vikrasim.genomeFileCreation.FileCreater;
import jneat.evolution.Organism;
import jneat.neuralNetwork.Genome;
import jneat.neuralNetwork.NNode;
import jneat.neuralNetwork.Network;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class AgentScannerNEATSlow extends AgentScannerNEAT implements Agent {
	ArrayList<Scanner> scanners;
	int scannerLength;
	int scannerHeight;
	ArrayList<double[]> inputQueue;
	
		
	public AgentScannerNEATSlow(String agentName, String genomeFileName, int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight) {
		super(agentName, genomeFileName, zLevelEnemies, zLevelScene, scannerHeight, scannerHeight);
		
		this.scannerHeight = scannerHeight;
		this.scannerLength=scannerLength;
		
		create();
		
		this.inputQueue =  new ArrayList<>();
	}
	
	public AgentScannerNEATSlow(String agentName, Organism brain, int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight) {
		super(agentName, brain, zLevelEnemies, zLevelScene, scannerHeight, scannerHeight);
		
		this.scannerHeight = scannerHeight;
		this.scannerLength = scannerLength;
		this.inputQueue =  new ArrayList<>();
	
	}	
	
	@Override
	public boolean[] getAction() {
		
		double[] observations = readSurroundings();
		int numberOfObservations = observations.length;
		double[] inputs = new double[numberOfObservations + 5];
		for (int i = 0; i < numberOfObservations; i++){ 
			inputs[i] = observations[i];
		}
		
		inputs[numberOfObservations + 0] = convertBooleanToByte(isMarioAbleToJump);
		inputs[numberOfObservations + 1] = convertBooleanToByte(isMarioOnGround);
		inputs[numberOfObservations + 2] = convertBooleanToByte(isMarioAbleToShoot);
		inputs[numberOfObservations + 3] = marioMode;
		
		inputs[numberOfObservations + 4] = 1; //Bias
		
		inputQueue.add(inputs);
		
		Network network = brain.net;
		boolean success = false;
		if (inputQueue.size() >= 3){
			inputs = inputQueue.get(0);
			success = propagateSignal(network, network.max_depth(), inputs);
			inputQueue.remove(0);
		}
		
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
	
	
}
