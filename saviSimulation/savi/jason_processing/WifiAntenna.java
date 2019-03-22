package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import processing.core.PVector;

public class WifiAntenna {
	
	int ID;
	double wifiProbWorking = 1; //TODO: actually make use of this as a probability in the message transmission
	int wifiPerceptionDistance;
	Communicator communicator; //the source and destination of messages exchanged over the wifi
	int seed;
	
	public WifiAntenna(int id, Communicator communicator, int wifiPerceptionDistance, double wifiProbWorking, int seed) {
		
		this.ID = id;
		this.communicator = communicator;
		this.wifiPerceptionDistance = wifiPerceptionDistance;
		this.wifiProbWorking= wifiProbWorking;
		this.seed = seed;
	}
	
	public void update(List<WifiAntenna> wifiParticipants) {
		sendMessages(communicator.getOutgoingMessages(), wifiParticipants);
	}
	

	protected void sendMessages(List<String> outgoingMessages, List<WifiAntenna> others) {
		
		List<WifiAntenna> receivers = new LinkedList<WifiAntenna>();
		
		//figure out who will receive the messages
		for(WifiAntenna other: others) {
			
			//calculate distance
			double dist  = this.getPosition().dist(other.getPosition());
			if (dist < this.wifiPerceptionDistance 		//reachable
				&& this.ID!=other.getID()	//not the same
			    && isWifiFailing(this.wifiProbWorking, this.seed)	//wifi working
			    && other.isWifiFailing(this.wifiProbWorking, this.seed)) 			//TODO make use of probability for this and line above
				
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
	protected boolean isWifiFailing(double probability, int seed) {
		Random rand = new Random();
		if(seed != -1) {
			rand = new Random(seed);
		}
		if(rand.nextDouble()<probability) {
			return true;
		}else {
			return false;
		}	
	}
	
}
