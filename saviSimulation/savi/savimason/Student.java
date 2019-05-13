package savi.savimason; 

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class Student implements Steppable {

	@Override
	public void step(SimState simstate) {
		
		Students students = (Students) simstate;
		
		Continuous2D yard = students.yard;
		MersenneTwisterFast random = students.random;
		
		yard.setObjectLocation(this,
				new Double2D(yard.getWidth() * (0.5 + random.nextDouble() - 0.5),
						yard.getHeight() * (0.5 + random.nextDouble() - 0.5)));
		
	}

}
