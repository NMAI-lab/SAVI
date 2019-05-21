package savi.simulation.behaviour;

import java.util.LinkedList;
import java.util.List;

import processing.core.PVector;
import savi.simulation.Communicator;
import savi.simulation.SAVIWorld_model;

public class WifiAntenna {
	
	int ID;
	double wifiProbFailing = 1;
	Communicator communicator; //the source and destination of messages exchanged over the wifi
	private static double wifiPerceptionDistance;
	
	public WifiAntenna(int id, Communicator communicator, double failureProb) {
		
		this.ID = id;
		this.communicator = communicator;
		this.wifiProbFailing= failureProb;
		
		//this.wifiPerceptionDistance=0;
	}
	
	public void update(List<WifiAntenna> wifiParticipants) {
		sendMessages(communicator.getOutgoingMessages(), wifiParticipants);
	}
	

	protected void sendMessages(List<String> outgoingMessages, List<WifiAntenna> others) {
		
		if (outgoingMessages.isEmpty())
			return;
		
		List<WifiAntenna> receivers = new LinkedList<WifiAntenna>();
		
		//figure out who will receive the messages
		for(WifiAntenna other: others) {
			
			//calculate distance
			double dist  = this.getPosition().dist(other.getPosition());
			if (dist < WifiAntenna.wifiPerceptionDistance //reachable
				&& this.ID!=other.getID()	//not the same
			    && isWifiFailing(this.wifiProbFailing)	//wifi working
			    && other.isWifiFailing(this.wifiProbFailing)) 			//TODO make use of probability for this and line above
				
				receivers.add(other);
		}
		
		//transmit messages to all receivers
		for(String msg: outgoingMessages) {
			for(WifiAntenna other: receivers) {
				other.receiveMessage(msg);			
			}
		}				
		outgoingMessages.clear(); //empty list of outgoing messages

		
	}

	public PVector getPosition() {
		
		return communicator.getPosition();
	}

	
	public int getID() {
		
		return ID;
	}

	
	public void receiveMessage(String msg) {
		communicator.receiveMessage(msg);
	}

	// takes probability parameter between 0 and 1 
	protected boolean isWifiFailing(double probability) { 
		return (SAVIWorld_model.rand.nextDouble()>probability); //prob is prob of error
	}
	
	public static void setPerceptionDistance(double value) {
		wifiPerceptionDistance=value;
	}
	
}
