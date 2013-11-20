package vikrasim.agents;

import jneat.evolution.Organism;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;

public abstract class MasterAgent extends BasicMarioAIAgent implements Agent {
	Organism brain;
	String genomeFileName;

	public MasterAgent(String agentName, String genomeFileName, int zLevelEnemies, int zLevelScene) {
		super(agentName);
		
		this.zLevelEnemies = zLevelEnemies;
		this.zLevelScene = zLevelScene;
		
		this.genomeFileName=genomeFileName;
	}
	
	public MasterAgent(String agentName, Organism brain, int zLevelEnemies, int zLevelScene) {
		super(agentName);
		
		this.zLevelEnemies = zLevelEnemies;
		this.zLevelScene = zLevelScene;
		
		this.brain = brain;
	}
	
	public void setBrain(Organism brain){
		this.brain = brain;
	}
	
	public void setgenomeFileName(String genomeFileName){
		this.genomeFileName = genomeFileName;
	}
	
	public abstract void create();
	
	
	

}
