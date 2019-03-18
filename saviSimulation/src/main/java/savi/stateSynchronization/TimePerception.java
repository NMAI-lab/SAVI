
package savi.stateSynchronization;

public class TimePerception extends Perception {

	public TimePerception(double timeStamp) {
		super("time", null, timeStamp, null);
	}
	
	protected TimePerception(String perceptionName, double newTimeStamp, boolean isLost) {
		super(perceptionName, null, newTimeStamp, null, isLost);
	}
	
	public Perception clone() {
		return (Perception)(new TimePerception(this.getPerceptionName(), this.getTimeStamp(), this.isLost()));
	}
}
