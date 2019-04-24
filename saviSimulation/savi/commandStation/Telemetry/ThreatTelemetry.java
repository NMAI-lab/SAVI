package savi.commandStation.Telemetry;

public class ThreatTelemetry extends TelemetryItem {

	private static final int xIndex = 0;
	private static final int yIndex = 1;
	private static final int zIndex = 2;
	private static final int radiusIndex = 3;
	private static final int timeIndex = 4;

	private static final String typeMessage = "notifyThreat";

	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(ThreatTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}

	protected ThreatTelemetry(String message) {
		super(message);
	}

	public double getX() {
		return this.getParameters().get(ThreatTelemetry.xIndex);
	}

	public double getY() {
		return this.getParameters().get(ThreatTelemetry.yIndex);
	}

	public double getZ() {
		return this.getParameters().get(ThreatTelemetry.zIndex);
	}

	public double getRadius() {
		return this.getParameters().get(ThreatTelemetry.radiusIndex);
	}

	public double getTime() {
		return this.getParameters().get(ThreatTelemetry.timeIndex);
	}

}
