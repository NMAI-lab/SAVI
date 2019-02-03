package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;

public class VelocityPerception extends Perception {

	public VelocityPerception(double timeStamp, double bearing, double pitch, double speed) {
		super("velocity", null, timeStamp, ((ArrayList<Double>) Arrays.asList(new Double[] {bearing, pitch, speed})));
	}
}
