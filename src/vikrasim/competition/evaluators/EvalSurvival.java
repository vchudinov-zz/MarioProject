package vikrasim.competition.evaluators;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class EvalSurvival implements Evaluator {

	@Override
	public double evaluate(Environment environment) {
		int status = environment.getMarioStatus();

		if (status == Mario.STATUS_WIN) {
			return 1;
		} else {
			return 0;
		}
	}

}
