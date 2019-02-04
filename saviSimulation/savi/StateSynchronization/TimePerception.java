
package savi.StateSynchronization;

import java.util.ArrayList;
import java.util.List;

public class TimePerception extends Perception {

	public TimePerception(double timeStamp) {
		super("time", null, timeStamp, null);
	}
	
	protected TimePerception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		super(perceptionName, type, newTimeStamp, (new ArrayList<Double>(newParameters)), isLost);
	}
	
	public Perception clone() {
		return (Perception)(new TimePerception(this.getPerceptionName(), this.getPerceptionType(), this.getTimeStamp(), this.getParameters(), this.isLost()));
	}
}
