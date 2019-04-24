package savi.commandStation.Telemetry;

public class PositionTelemetry extends TelemetryItem {

	private static final int xIndex = 0;
	private static final int yIndex = 1;
	private static final int zIndex = 2;
	private static final int timeIndex = 3;

	private static final String typeMessage = "notifyPosition";

	public static boolean isType(TelemetryItem item) {
		if (item.getType().equals(PositionTelemetry.typeMessage)) {
			return true;
		} else {
			return false;
		}
	}

	protected PositionTelemetry(String message) {
		super(message);
	}

	public double getX() {
		return this.getParameters().get(PositionTelemetry.xIndex);
	}

	public double getY() {
		return this.getParameters().get(PositionTelemetry.yIndex);
	}

	public double getZ() {
		return this.getParameters().get(PositionTelemetry.zIndex);
	}

	public double getTime() {
		return this.getParameters().get(PositionTelemetry.timeIndex);
	}

}