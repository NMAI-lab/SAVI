package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;

import processing.core.PShape;
import processing.core.PVector;

public class FieldAntenna extends WorldObject implements Communicator{
	
	WifiAntenna antenna;
	
	double wifi = 100;
	//TODO: add list of messages here, then implement message delivery
	
	public FieldAntenna(int id, PVector position, SAVIWorld_model sim, int size, PShape image, double wifiProbWorking) {
		
		super(id, position, size, "FieldAntenna", sim, image);
		this.position = position;
		antenna = new WifiAntenna(id, this, wifiProbWorking);
	}
	
	public void update(List<WifiAntenna> wifiParticipants) {
		antenna.update(wifiParticipants);
	}
	

	@Override
	public PVector getPosition() {
		return position;
	}

	
	@Override
	public void receiveMessage(String msg) {
		System.out.println("Console proxy received a message on the wifi:"+msg);	
	}

	@Override
	public List<String> getOutgoingMessages() {
		// This is where messages should be read from the console
		return new LinkedList<String>(); 
	}

	@Override
	public WifiAntenna getAntennaRef() {	
		return antenna;
	}

}
