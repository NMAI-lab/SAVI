package savi.commandStation.Telemetry;

public class VelocityTelemetry extends TelemetryItem {

	private static final int bearingIndex = 0;
	private static final int pitchIndex = 1;
	private static final int speedIndex = 2;
	private static final int timeIndex = 3;

	private static final String typeMessage = "notifyVelocity";

	/**
	 * Check if the telemetry item should be of this type
	 * @param item
	 * @return
	 */
	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(VelocityTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Return a human readable string
	 */
	public String toString() {
		String msg = "Velocity - Bearing: " + this.getBearing() + " Pitch: " + this.getPitch() + " Speed: " + this.getSpeed() + " at " + this.getTime();
		
		if (this.getVehicleType() == null) {
			msg = msg + " vehicle type unknown.";
		} else {
			msg = msg + " vehicle type : " + this.getVehicleType() + ".";
		}
		return msg;
	}

	/**
	 * Protected constructor - use the generator
	 * @param message
	 */
	protected VelocityTelemetry(String message) {
		super(message);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getBearing() {
		return this.getParameters().get(VelocityTelemetry.bearingIndex);
	}
	
	/**
	 * Getter
	 * @return
	 */
	public double getPitch() {
		return this.getParameters().get(VelocityTelemetry.pitchIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getSpeed() {
		return this.getParameters().get(VelocityTelemetry.speedIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getTime() {
		return this.getParameters().get(VelocityTelemetry.timeIndex);
	}

}