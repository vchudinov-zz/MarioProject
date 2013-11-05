package vikrasim.agents;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class OurBasicAgent extends BasicMarioAIAgent implements Agent {
	public OurBasicAgent(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	//Array with possible actions the agent can perform
	protected boolean action[] = new boolean[Environment.numberOfKeys];

	@Override
	public boolean[] getAction() {
				
		if (isMarioAbleToJump){
			action[Mario.KEY_JUMP] = true;
			action[Mario.KEY_RIGHT] = true;
			action[Mario.KEY_SPEED] = true;
		} else {
			action[Mario.KEY_JUMP] = false;
			action[Mario.KEY_RIGHT] = true;
		}
		return action;
	}

}
