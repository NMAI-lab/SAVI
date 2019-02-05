package savi.jason_processing;

import java.util.List;

import processing.core.PVector;

public interface Communicator {
	
	
	/**
	 * 
	 * @return my geographical position
	 */
	public PVector getPosition();
	
	/**
	 * Do wifi communication
	 * @param others other communicators to talk to
	 */
	public void sendMessages(List<Communicator> others);

	/**
	 * receive a message: called by another communicator
	 * @param msglist the incoming messages 
	 */
	public void receiveMessages(List<String> msglist);
	
	/**
	 * 
	 * @return probability that my wifi is working
	 */
	public double getWifiValue();
	
	/**
	 * 
	 * @return my ID on the wifi
	 */
	public String getID();

}
