package savi.commandStation;

public class CommandStationCore {
	
	private CommandStationConnector connector; 	// connection to the rest of the simulation
	private String id; 							// identifier to use in messages
	
	private TelemetryFetcher telemetryFetcher;	// class in a separate thread that generates messages for fetching telemetry
	private FancyCommandStationGUI gui;

	public CommandStationCore(CommandStationConnector connector, String ID) {
		// Set message ID and connecter parameters
		this.id = ID;
		this.connector = connector;
		
		// Build the GUI (remove the parameters)
		gui = new FancyCommandStationGUI(this);
		
		// Build the telemetry fetcher
		this.telemetryFetcher = new TelemetryFetcher(this);
	}
	
	public void setTelemetryPeriod(long period) {
		this.telemetryFetcher.setTelemetryPeriod(period);
	}
	
	
	public synchronized void sendMessage(String destination, String messageType, String parameter) {

		// this is a really crude message id!
		long mid = System.currentTimeMillis() % 20000; 
		
		// Build the message
		String message = "<" + mid + "," + this.id + "," + messageType + "," + destination + "," + parameter + ">";
			
		// Send the message
		this.connector.messageOut(message);
	}
	
	
	/**
	 * Method to receive messages from the simulator
	 * 
	 * @param msg an incoming message.
	 */
	public void receiveMessage(String msg) {
		
		// Deal with receiving the message. Send to the GUI raw for now
		gui.receiveMessage(msg);
	}
}

