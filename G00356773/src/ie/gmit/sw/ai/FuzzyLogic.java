package ie.gmit.sw.ai;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyLogic {

	private static final String FILE = "./resources/fuzzy/damage.fcl";

	public double getDamageDealt(double health, double distance) {
		FIS fis = FIS.load(FILE, true);
		FunctionBlock fb = fis.getFunctionBlock("damageDealt");
		fis.setVariable("health", health);
		fis.setVariable("distance", distance);
		fis.evaluate();

		Variable damage = fb.getVariable("damage");

		return damage.getValue();
	}

}
