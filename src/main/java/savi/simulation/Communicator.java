package savi.simulation;

import java.util.List;

import processing.core.PVector;

public interface Communicator {
	
	
	/**
	 * get physical position of this thing that the wifi antenna is attached to
	 * @return
	 */
	public PVector getPosition();
	
	/**
	 * get outgoing messages
	 */
	public List<String> getOutgoingMessages();

	/**
	 * receive a message: called by another communicator
	 * @param msg the incoming message 
	 */
	public void receiveMessage(String msg);
	
	/**
	 * gets a reference to the wifi antenna associated with this communicator
	 *  @return the wifi antenna
	 */
	public WifiAntenna getAntennaRef();
	

}
