package savi.commandStation.Telemetry;

public class PositionTelemetry extends TelemetryItem {

	private static final int xIndex = 0;
	private static final int yIndex = 1;
	private static final int zIndex = 2;
	private static final int timeIndex = 3;

	private static final String typeMessage = "notifyPosition";

	/**
	 * Check if the telemetry item should be of this type
	 * @param item
	 * @return
	 */
	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(PositionTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return a human readable string
	 */
	public String toString() {
		String msg = "Position - X: " + this.getX() + " Y: " + this.getY() + " Z: " + this.getZ() + " at " + this.getTime();
		
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
	protected PositionTelemetry(String message) {
		super(message);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getX() {
		return this.getParameters().get(PositionTelemetry.xIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getY() {
		return this.getParameters().get(PositionTelemetry.yIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getZ() {
		return this.getParameters().get(PositionTelemetry.zIndex);
	}

	/**
	 * Getter
	 * @return
	 */
	public double getTime() {
		return this.getParameters().get(PositionTelemetry.timeIndex);
	}

}