package savi.commandStation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import savi.jason_processing.FieldAntenna;

public class CommandStationSocketConnector implements MessageHandler {

	public static final int SimPort = 9090;
	public static final int commandStationPort = 9091;
	
	private SocketConnector socketConnector;
	private FieldAntenna fieldAntenna;
	
	public CommandStationSocketConnector(FieldAntenna fa) {		
		this.fieldAntenna = fa;
		this.socketConnector = new SocketConnector(this, SimPort, commandStationPort);
		this.socketConnector.listenForMessages();		
		
	}
	
	public void messageToCommandStation(String msg) {
		socketConnector.messageOut(msg);
	}

	@Override
	public void messageIn(String msg) {
		fieldAntenna.messageOut(msg); // this message will be "out" into the simulation although it's "in" from the socket		
	}


}
