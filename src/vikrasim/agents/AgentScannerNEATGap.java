package vikrasim.agents;

import jNeatCommon.IOseq;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import vikrasim.agents.scanners.MasterScanner;
import vikrasim.agents.scanners.gapScanner;
import vikrasim.agents.scanners.MasterScanner.Dir;
import vikrasim.agents.scanners.MasterScanner.ScannerType;
import vikrasim.agents.scanners.Scanner;
import vikrasim.genomeFileCreation.FileCreater;
import jneat.evolution.Organism;
import jneat.neuralNetwork.Genome;
import jneat.neuralNetwork.NNode;
import jneat.neuralNetwork.Network;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class AgentScannerNEATGap extends MasterAgent implements Agent {
	ArrayList<MasterScanner> scanners;
	int scannerLength;
	int scannerHeight;
	
		
	public AgentScannerNEATGap(String agentName, String genomeFileName, int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight) {
		super(agentName, genomeFileName, zLevelEnemies, zLevelScene);
		
		this.scannerHeight = scannerHeight;
		this.scannerLength=scannerLength;
		
		this.create();
		
	}
	
	public AgentScannerNEATGap(String agentName, Organism brain, int zLevelEnemies, int zLevelScene, 
			int scannerLength, int scannerHeight) {
		super(agentName, brain, zLevelEnemies, zLevelScene);
		
		this.scannerHeight = scannerHeight;
		this.scannerLength = scannerLength;
	
	}
	
	public void createBrain(){
		String genomeFileName = this.genomeFileName;
		File f = new File(genomeFileName);
		if(!f.exists()) {
			FileCreater fc = new FileCreater();
			fc.createGenomeFile(genomeFileName, scanners.size() + 4, 6);
			fc = null;
		}
		f = null;
		makeBrain(genomeFileName);
	}
	
	@Override
	public void create() {
		addScanners(scannerLength, scannerHeight);		
	}
	
	private void addScanners(int length, int height){
		this.scanners = new ArrayList<>();
		
		//Add enemy radar
		scanners.add(new Scanner(length, height, Dir.NE, ScannerType.ENEMY));
		scanners.add(new Scanner(length, height, Dir.NW, ScannerType.ENEMY));
		scanners.add(new Scanner(length, height, Dir.SE, ScannerType.ENEMY));
		scanners.add(new Scanner(length, height, Dir.SW, ScannerType.ENEMY));
		
		//Add distance scanners
		scanners.add(new Scanner(length, 1, Dir.N, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.S, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.E, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.W, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.NE, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.NW, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.SE, ScannerType.ENVIRONMENT));
		scanners.add(new Scanner(length, 1, Dir.SW, ScannerType.ENVIRONMENT));
		
		//Add gap scanner
		scanners.add(new gapScanner(length, 1, null, ScannerType.ENVIRONMENT));
		
	}
	
	public void createGenomeFile(String genomeFileName){
		
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
	
	protected boolean convertToBoolean(double value){
		if(value < 0.5) return false;
		return true;
	}
	
	protected boolean propagateSignal(Network net, int net_depth, double[] inputValues){
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
	
	protected byte convertBooleanToByte(Boolean b){
		if (b) return 1;
		return 0;
	}
	protected double[] readSurroundings(){
		
		double[] result = new double[scanners.size()];
		
		
		for (int i = 0; i < scanners.size(); i++ ){
			MasterScanner s = scanners.get(i);
			result[i] = s.scan(enemies, levelScene);			
		}		
		
		return result;
	}
}
