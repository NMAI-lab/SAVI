package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraPerception extends Perception {

	public CameraPerception(String perceptionName, long timeStamp, double azumuth, double elevation, double range) {
		this(perceptionName, null, timeStamp, azumuth, elevation, range);
	}
	
	public CameraPerception(String perceptionName, String type, long timeStamp, double azumuth, double elevation, double range) {
		super(perceptionName, type, timeStamp, ((ArrayList<Double>) Arrays.asList(new Double[] {azumuth, elevation, range})));
	}
}
