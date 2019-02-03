package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraPerception extends Perception {

	public CameraPerception(String perceptionName, double timeStamp, double azumuth, double elevation, double range) {
		this(perceptionName, new String("unknown"), timeStamp, azumuth, elevation, range);
	}
	
	public CameraPerception(String perceptionName, String type, double timeStamp, double azumuth, double elevation, double range) {
		super(perceptionName, type, timeStamp, ((ArrayList<Double>) Arrays.asList(new Double[] {azumuth, elevation, range})));
	}
}
