package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import processing.core.PVector;

public class WifiAntenna {
	
	int ID;
	double wifiProbWorking = 100; //TODO: actually make use of this as a probability in the message transmission
	Communicator communicator; //the source and destination of messages exchanged over the wifi
	
	public WifiAntenna(int id, Communicator communicator) {
		
		this.ID = id;
		this.communicator = communicator;
		
		
	}
	
	public void update(int wifiPerceptionDistance, List<WifiAntenna> wifiParticipants) {
		sendMessages(wifiPerceptionDistance, communicator.getOutgoingMessages(), wifiParticipants);
	}
	

	protected void sendMessages(int wifiPerceptionDistance, List<String> outgoingMessages, List<WifiAntenna> others) {
		
		List<WifiAntenna> receivers = new LinkedList<WifiAntenna>();
		
		//figure out who will receive the messages
		for(WifiAntenna other: others) {
			
			//calculate distance
			double dist  = this.getPosition().dist(other.getPosition());
			if (dist < wifiPerceptionDistance 		//reachable
				&& this.ID!=other.getID()	//not the same
			    && wifiProbWorking > 0				//wifi working
			    && other.getWifiValue()>0) 			//TODO make use of probability for this and line above
				
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

	public double getWifiValue() {
		
		return wifiProbWorking;
	}


	public int getID() {
		
		return ID;
	}

	
	public void receiveMessage(String msg) {
		communicator.receiveMessage(msg);
	}

}
