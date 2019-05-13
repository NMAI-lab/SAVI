package savi.savimason;

import savi.jason_processing.SAVIWorld_model;
import savi.jason_processing.WorldObject;
import sim.engine.SimState;
import sim.engine.Steppable;

public class MasonWrapper implements Steppable {

	private SAVIWorld_model savimodel;
	
	
	public MasonWrapper(SAVIWorld_model world) {
		savimodel = world;
	}


	@Override
	public void step(SimState arg0) {
		MasonWorldModel masonWorld = (MasonWorldModel) arg0;
		savimodel.update(masonWorld.getTimeStep());
	}


	public void reset() {
		savimodel.setup();
		
	}

}
