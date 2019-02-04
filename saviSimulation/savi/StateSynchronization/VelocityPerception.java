package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VelocityPerception extends Perception {

	public VelocityPerception(double timeStamp, double bearing, double pitch, double speed) {
		super("velocity", null, timeStamp, (new ArrayList<Double>(Arrays.asList(new Double[] {bearing, pitch, speed}))));
	}
	
	protected VelocityPerception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		super(perceptionName, type, newTimeStamp, (new ArrayList<Double>(newParameters)), isLost);
	}
	
	public Perception clone() {
		return (Perception)(new VelocityPerception(this.getPerceptionName(), this.getPerceptionType(), this.getTimeStamp(), this.getParameters(), this.isLost()));
	}
}
