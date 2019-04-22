package savi.commandStation;

public class VelocityTelemetry extends TelemetryItem {

	private static final int bearingIndex = 0;
	private static final int pitchIndex = 1;
	private static final int speedIndex = 2;
	private static final int timeIndex = 3;

	private static final String typeMessage = "notifyPosition";

	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(VelocityTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}

	protected VelocityTelemetry(String message) {
		super(message);
	}

	public double getBearing() {
		return this.getParameters().get(VelocityTelemetry.bearingIndex);
	}

	public double getPitch() {
		return this.getParameters().get(VelocityTelemetry.pitchIndex);
	}

	public double getSpeed() {
		return this.getParameters().get(VelocityTelemetry.speedIndex);
	}

	public double getTime() {
		return this.getParameters().get(VelocityTelemetry.timeIndex);
	}

}