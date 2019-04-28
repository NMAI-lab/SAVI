package savi.commandStation;

public class TelemetryFetcher extends Thread {

	private long telemetryPeriod;				// Period for how often to check the telemetry in ms (anything less than minTelemetryPeriod means never)
	private static long minTelemetryPeriod = 1000;
	private CommandStationCore commandStation;	// Linkage to the command station
	
	public TelemetryFetcher(CommandStationCore commandStation) {
		this(0, commandStation);
	}
	
	public TelemetryFetcher(long period, CommandStationCore commandStation) {
		this.commandStation = commandStation;
		this.setTelemetryPeriod(period);	// Set the period and start the thread if needed
	}
	
	synchronized public void setTelemetryPeriod(long period) {
		if (period < this.minTelemetryPeriod) {
			this.telemetryPeriod = 0;
		} else {
			this.telemetryPeriod = period;
			this.start();
		}
	}
	
	synchronized private long getTelemetryPeriod() {
		return this.telemetryPeriod;
		
	}
	
	public void run() {
		
		
		// Repeat telemetry ping forever
		while(true) {
			
			// Only ping telemetry if period is greater than 0
			if (this.getTelemetryPeriod() > 0) {
				
				// Ping  telemetry
				commandStation.sendMessage("BROADCAST", "achieve", "sendTelemetry");
					
				// Sleep until next time to ping
				try {
					Thread.sleep(this.getTelemetryPeriod());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// We don't need to ping telemetry automatically (should never get here)
				return;
			}
		}
	}
}
