package savi.commandStation;


import savi.jason_processing.FieldAntenna;

/**
 * Wraps a connection to the command station using a SocketConnector.
 * @author davoal01
 *
 */
public class CommandStationSocketConnector implements MessageHandler {

	public static final int SimPort = 9090;
	public static final int commandStationPort = 9091;
	
	//singleton
	private static CommandStationSocketConnector singleton;
	
	private SocketConnector socketConnector;
	private FieldAntenna fieldAntenna;
	
	public static CommandStationSocketConnector getCommandStationSocketConnector(FieldAntenna fa) {
		if (singleton ==null)
			singleton = new CommandStationSocketConnector(fa);
		else
			singleton.fieldAntenna = fa;
		
		return singleton;
	}
	
	private CommandStationSocketConnector(FieldAntenna fa) {		
		this.fieldAntenna = fa;
		this.socketConnector = new SocketConnector(this, SimPort, commandStationPort);
		this.socketConnector.listenForMessages();		
		
	}
	
	/**
	 * pass-through messages from simulation to command station
	 * @param msg
	 */
	public void messageToCommandStation(String msg) {
		socketConnector.messageOut(msg);
	}

	@Override
	public void messageIn(String msg) {
		fieldAntenna.messageOut(msg); // this message will be "out" into the simulation although it's "in" from the socket		
	}


}
