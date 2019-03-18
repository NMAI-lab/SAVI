package savi.stateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PositionPerception extends Perception {

	public PositionPerception(double timeStamp, double x, double y, double z) {
		super("position", null, timeStamp, (new ArrayList<Double>(Arrays.asList(new Double[] {x, y, z}))));
	}
	
	protected PositionPerception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		super(perceptionName, type, newTimeStamp, (new ArrayList<Double>(newParameters)), isLost);
	}
	
	public Perception clone() {
		return (Perception)(new PositionPerception(this.getPerceptionName(), this.getPerceptionType(), this.getTimeStamp(), this.getParameters(), this.isLost()));
	}
	
}