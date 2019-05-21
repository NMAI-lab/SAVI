package savi.commandStation;

import savi.commandStation.Telemetry.*;

public class CommandStationCore implements MessageHandler {
	
	
	public static final int simPort = 9090;
	public static final int commandStationPort = 9091;
	
	
	
	private SocketConnector socketConnector; 	// connection to the rest of the simulation
	private String id; 							// identifier to use in messages
	
	private TelemetryFetcher telemetryFetcher;	// class in a separate thread that generates messages for fetching telemetry
	private CommandStationGUI gui;			// GUI for the command station


	
	/**
	 * Constructor for the core of the command station
	 * @param connector 	The connector to the rest of the simulation
	 * @param ID			The agent ID for the command station (address for messages)
	 */
	public CommandStationCore(String ID)  {
		// Set message ID and connecter parameters
		this.id = ID;
		this.socketConnector =  new SocketConnector(this, commandStationPort, simPort);
		socketConnector.listenForMessages(); // start the "server" on the socket

		
		// Build the GUI (remove the parameters)
		gui = new CommandStationGUI(this);
		
		// Build the telemetry fetcher
		this.telemetryFetcher = new TelemetryFetcher(this);
	}
	
	public void setTelemetryPeriod(long period) {
		this.telemetryFetcher.setTelemetryPeriod(period);
	}
	
	
	/**
	 * Send the message to the agents
	 * 
	 * @param destination - ID of the agent or BROADCAST for all agents
	 * @param messageType - message type, "achieve", "tell", or any other JASON supported message types
	 * @param parameter - The message in AgentSpeak
	 */
	public synchronized void sendMessage(String destination, String messageType, String parameter) {

		// this is a really crude message id!
		long mid = System.currentTimeMillis() % 20000; 
		
		// Build the message
		String message = "<" + mid + "," + this.id + "," + messageType + "," + destination + "," + parameter + ">";
		
		// Send the message
		socketConnector.messageOut(message);
	}
		
	
	/**
	 * Method to receive messages from the simulator
	 * 
	 * @param msg an incoming message.
	 */
	@Override
	public void messageIn(String msg) {


		// Parse the message and get a telemetryItem
		TelemetryItem item = TelemetryItem.generateTelemetryItem(msg);

		// Parse the message
		String parsedMessage;
		if (item instanceof ThreatTelemetry) {
			ThreatTelemetry threatItem = (ThreatTelemetry)item;
			parsedMessage = threatItem.toString();
		} else if (item instanceof PositionTelemetry) {
			PositionTelemetry positionItem = (PositionTelemetry)item;
			parsedMessage = positionItem.toString();
		} else if (item instanceof VelocityTelemetry) {
			VelocityTelemetry velocityItem = (VelocityTelemetry)item;
			parsedMessage = velocityItem.toString();
		} else {
			parsedMessage = item.toString();
			//parsedMessage = "";
		}

		// Send to the GUI
		gui.receiveMessage(msg, parsedMessage);
	}
	

	public static void main(String[] args) {
		new CommandStationCore("commander");
	}
	
}

