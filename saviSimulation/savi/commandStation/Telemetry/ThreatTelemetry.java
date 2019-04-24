package savi.commandStation.Telemetry;

public class ThreatTelemetry extends TelemetryItem {

	private static final int xIndex = 0;
	private static final int yIndex = 1;
	private static final int zIndex = 2;
	private static final int radiusIndex = 3;
	private static final int timeIndex = 4;

	private static final String typeMessage = "notifyThreat";

	/**
	 * Check if the telemetry item should be of this type
	 * @param item
	 * @return
	 */
	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(ThreatTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Return a human readable string
	 */
	public String toString() {
		String msg = "Threat - X: " + this.getX() + " Y: " + this.getY() + " Z: " + this.getZ() + " radius " + this.getRadius() + " at " + this.getTime();
		
		if (this.getVehicleType() == null) {
			msg = msg + " vehicle type unknown.";
		} else {
			msg = msg + " vehicle type : " + this.getVehicleType() + ".";
		}
		return msg;
	}

	/**
	 * Protected constrictor, use the generator
	 * @param message
	 */
	protected ThreatTelemetry(String message) {
		super(message);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getX() {
		return this.getParameters().get(ThreatTelemetry.xIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getY() {
		return this.getParameters().get(ThreatTelemetry.yIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getZ() {
		return this.getParameters().get(ThreatTelemetry.zIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getRadius() {
		return this.getParameters().get(ThreatTelemetry.radiusIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getTime() {
		return this.getParameters().get(ThreatTelemetry.timeIndex);
	}
}
