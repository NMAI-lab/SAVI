package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.Arrays;


public class PositionPerception extends Perception {

	public PositionPerception(long timeStamp, double x, double y, double z) {
		super("position", null, timeStamp, ((ArrayList<Double>) Arrays.asList(new Double[] {x, y, z})));
	}
}