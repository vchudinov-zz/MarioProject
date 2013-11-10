package vikrasim.evolution.training.evaluators;

import jneat.evolution.Organism;
import vikrasim.CSVWriter;
import vikrasim.agents.MasterAgent;

public abstract class MasterEvaluator {
	protected String levelParameters;
	protected MasterAgent agent;
	protected CSVWriter writer;
	
	public MasterEvaluator(String levelParameters, MasterAgent agent){
		this.levelParameters = levelParameters;
		this.agent = agent;
	}
	
	public abstract boolean evaluate(Organism organism);
	
	public void setLevelParameters(String levelParameters ){
		this.levelParameters = levelParameters;
	}
}
