package savi.commandStation;

public class TelemetryFetcher extends Thread {

	private long telemetryPeriod;				// Period for how often to check the telemetry in ms (anything less than minTelemetryPeriod means never)
	private static long minTelemetryPeriod = 100;
	private long lastTelemetryRequest;			// The last time telemetry was requested
	private CommandStationCore commandStation;	// Linkage to the command station
	
	public TelemetryFetcher(CommandStationCore commandStation) {
		this(0, commandStation);
	}
	
	public TelemetryFetcher(long period, CommandStationCore commandStation) {
		this.setTelemetryPeriod(period);
		this.lastTelemetryRequest = 0;
		this.commandStation = commandStation;
		//this.run();
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
					commandStation.sendMessage("BROADCAST", "achieve", "sendTelemetry");
					try {
						Thread.sleep(this.telemetryPeriod);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
