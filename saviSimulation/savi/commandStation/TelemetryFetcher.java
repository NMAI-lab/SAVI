package savi.commandStation;

public class TelemetryFetcher extends Thread {

	private long telemetryPeriod;		// Period for how often to check the telemetry in ms (anything less than minTelemetryPeriod means never)
	private static long minTelemetryPeriod = 10;
	private long lastTelemetryRequest;	// The last time telemetry was requested
	
	public TelemetryFetcher() {
		this(0);
	}
	
	public TelemetryFetcher(long period) {
		this.setTelemetryPeriod(period);
		this.lastTelemetryRequest = 0;
	}
	
	synchronized public void setTelemetryPeriod(long period) {
		if (period < this.minTelemetryPeriod) {
			this.telemetryPeriod = 0;
		} else {
			this.telemetryPeriod = period;
		}
	}
	
	synchronized private long getLastTelemetryTime() {
		return this.lastTelemetryRequest;
	}
	
	synchronized private long getTelemetryPeriod() {
		return this.telemetryPeriod;
	}
	
	public void run() {
		// fetch telemetry every telemetryPeriod ms.
		long currentTime = System.currentTimeMillis();
		
		// Repeat telemetry ping forever
		while(true) {
			
			// Only ping telemetry if period is greater than 0
			if (this.getTelemetryPeriod() > 0) {
				
				// Is it time to ping telemetry?
				if (currentTime > (this.getLastTelemetryTime() + this.getTelemetryPeriod())) {
					
					// Ping  telemetry
				}
			}
		}
	}
}
