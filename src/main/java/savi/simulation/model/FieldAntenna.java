package savi.simulation.model;

import java.util.LinkedList;
import java.util.List;

import processing.core.PVector;
import savi.commandStation.*;
import savi.simulation.Communicator;
import savi.simulation.SAVIWorld_model;
import savi.simulation.behaviour.WifiAntenna;

public class FieldAntenna extends WorldObject implements Communicator, CommandStationConnector{
	
	

	WifiAntenna antenna;
	
	private List<String> outbox = new LinkedList<String>();
	
	private CommandStationSocketConnector toCommandStation;
	
	
	public FieldAntenna(int id, PVector position, SAVIWorld_model sim, int size, double wifiProbFailure) {
		
		super(id, position, size, "FieldAntenna", sim);
		this.position = position;
		antenna = new WifiAntenna(id, this, wifiProbFailure);
		
		toCommandStation = CommandStationSocketConnector.getCommandStationSocketConnector(this); 

	}
	
	@Override
	public void update(double simtime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
		antenna.update(wifiParticipants);
		
	}
		

	@Override
	public PVector getPosition() {
		return position;
	}
	

	
	@Override
	public void receiveMessage(String msg) {
		toCommandStation.messageToCommandStation(msg);
	}

	@Override
	public synchronized List<String> getOutgoingMessages() {
		// This is where messages should be read from the console
		if (outbox.isEmpty())
			return new LinkedList<String>();
		
		List<String> outcopy = outbox;
		outbox = new LinkedList<String>();
		return outcopy; 
	}

	@Override
	public WifiAntenna getAntennaRef() {	
		return antenna;
	}

	
	@Override
	public synchronized void messageOut(String msg) {
		outbox.add(msg);
		
	}

}
