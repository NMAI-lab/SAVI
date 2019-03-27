package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CompassAnglePerception extends Perception {

	public CompassAnglePerception(double timeStamp, double angle) {
		super("compass", null, timeStamp, (new ArrayList<Double>(Collections.singletonList(angle))));
	}

	protected CompassAnglePerception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		super(perceptionName, type, newTimeStamp, (new ArrayList<Double>(newParameters)), isLost);
	}
	
	public Perception clone() {
		return (Perception)(new CompassAnglePerception(this.getPerceptionName(), this.getPerceptionType(), this.getTimeStamp(), this.getParameters(), this.isLost()));
	}
	
}