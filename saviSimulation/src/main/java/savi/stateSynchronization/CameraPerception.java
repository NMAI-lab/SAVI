package savi.stateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraPerception extends Perception {

	public CameraPerception(String perceptionName, double timeStamp, double azumuth, double elevation, double range) {
		this(perceptionName, new String("unknown"), timeStamp, azumuth, elevation, range);
	}
	
	public CameraPerception(String perceptionName, String type, double timeStamp, double azumuth, double elevation, double range) {
		super(perceptionName, type, timeStamp, (new ArrayList<Double>(Arrays.asList(new Double[] {azumuth, elevation, range}))));
	}
	
	protected CameraPerception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		super(perceptionName, type, newTimeStamp, (new ArrayList<Double>(newParameters)), isLost);
	}
	
	public Perception clone() {
		return (Perception)(new CameraPerception(this.getPerceptionName(), this.getPerceptionType(), this.getTimeStamp(), this.getParameters(), this.isLost()));
	}
}
