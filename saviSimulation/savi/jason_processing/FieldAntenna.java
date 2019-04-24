package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;

import processing.core.PShape;
import processing.core.PVector;
import savi.commandStation.*;

public class FieldAntenna extends WorldObject implements Communicator, CommandStationConnector{
	
	

	WifiAntenna antenna;
	
	private List<String> outbox = new LinkedList<String>();
	
	//private CommandStationGUI commandStation;
	//private FancyCommandStation commandStationGUI;
	private CommandStationCore commandStation;
	
	
	public FieldAntenna(int id, PVector position, SAVIWorld_model sim, int size, PShape image, double wifiProbFailure) {
		
		super(id, position, size, "FieldAntenna", sim, image);
		this.position = position;
		antenna = new WifiAntenna(id, this, wifiProbFailure);
		
		//commandStation = new CommandStationGUI(this, String.valueOf(this.ID)); 
		//commandStation = new FancyCommandStationGUI(this, String.valueOf(this.ID));
		commandStation = new CommandStationCore(this, String.valueOf(this.ID));
		
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
	public void draw(PVector position) {
		simulator.shape(this.image, this.position.x, this.position.y,pixels,pixels);
	}

	
	@Override
	public void receiveMessage(String msg) {
		commandStation.receiveMessage(msg);
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
