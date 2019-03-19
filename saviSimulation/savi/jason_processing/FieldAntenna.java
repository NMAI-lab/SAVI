package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;

import processing.core.PShape;
import processing.core.PVector;

public class FieldAntenna extends WorldObject implements Communicator{
	
	
	PVector position;
	WifiAntenna antenna;
	
	double wifi = 100;
	//TODO: add list of messages here, then implement message delivery
	
	public FieldAntenna(int id, PVector position, SAVIWorld_model sim, int size, PShape image) {
		
		//int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image
		
		super(id, position, size, "FieldAntenna", sim, image);
		
		this.position = position;
		antenna = new WifiAntenna(id, this);
		
	}
	
	public void update(int wifiPerceptionDistance, List<WifiAntenna> wifiParticipants) {
		antenna.update(wifiPerceptionDistance, wifiParticipants);
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
