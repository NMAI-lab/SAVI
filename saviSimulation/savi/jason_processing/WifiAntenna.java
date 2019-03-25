package savi.jason_processing;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import processing.core.PVector;

public class WifiAntenna {
	
	int ID;
	double wifiProbWorking = 1;
	Communicator communicator; //the source and destination of messages exchanged over the wifi
	private static Random rand = new Random();
	private static double wifiPerceptionDistance;
	
	public WifiAntenna(int id, Communicator communicator, double wifiProbWorking) {
		
		this.ID = id;
		this.communicator = communicator;
		this.wifiProbWorking= wifiProbWorking;
		
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
			    && isWifiFailing(this.wifiProbWorking)	//wifi working
			    && other.isWifiFailing(this.wifiProbWorking)) 			//TODO make use of probability for this and line above
				
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
		if(rand.nextDouble()>probability) { 
			return true;
		}else {
			return false;
		}	
	}
	
	public static void setPerceptionDistance(double value) {
		wifiPerceptionDistance=value;
	}
	
	public static void setSeed(int seed) {
		if(seed != -1) {
			rand = new Random(seed);
		}
	}
	
}
