package vikrasim.evolution.training.evaluators;

import jneat.evolution.Organism;
import vikrasim.agents.MasterAgent;

public abstract class MasterEvaluator {
	protected String levelParameters;
	protected MasterAgent agent;
	
	public MasterEvaluator(String levelParameters, MasterAgent agent){
		this.levelParameters = levelParameters;
		this.agent = agent;
	}
	
	public abstract boolean evaluate(Organism organism);
}
