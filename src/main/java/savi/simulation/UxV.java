package savi.simulation;

import java.util.*;

import processing.core.*;


public abstract class UxV extends WorldObject implements Communicator {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	protected WifiAntenna wifiAntenna;
	protected UxVBehavior uxvBehavior;
	protected String imageName;
	protected float perceptionDistance;

	public float perceptionAngle;
	//***********************************************************//
	//I THINK IS BETTER TO HAVE THE ROBOTS ITS DATA AND THE SYNCAGENTSTATE ITS OWN.
	//IF WE WANT TO IMPLEMENTE MALFUNCTION OF SENSORS, THE INFO RECEIVED IN 
	//SYNCAGENTSTATE IS NOT THE REAL ONE
	//***********************************************************//
	
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	/**
	 * Constructor
	 * @param id
	 * @param type
	 * @param initialPosition
	 */
	public UxV(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, double reasoningCyclePeriod, int perceptionDistance, double perceptionAngle, double sensorsErrorProb, double sensorsErrorStdDev, double probWifiFailure) {			
		// Initializes UAS as WorldObject
		super(id, pos, pixels, Type, sim);
		//this.imageName=imageName;
		// Initializes Behaviuor
		//this.uxvBehavior = new UxVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod);
		this.perceptionAngle = (float) perceptionAngle;
		this.wifiAntenna = new WifiAntenna (id, this, probWifiFailure);
		this.perceptionDistance=perceptionDistance;
	}
	
	@Override
	public void update(double simtime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
		this.uxvBehavior.update(this, simtime, this.perceptionDistance, objects);
		this.wifiAntenna.update(wifiParticipants);
	}
	
	public UxVBehavior getBehavior() {		
		return uxvBehavior;
	}
	
	public void setPosition(PVector pos) {		
		this.position = pos;
	}	
	
	@Override
	public  List<String> getOutgoingMessages(){
		
		List<String> myMsgOutCopy = new LinkedList<String>();
		myMsgOutCopy.addAll(getBehavior().agentState.getMsgOutAll());
		
		return myMsgOutCopy;
	}

	@Override
	public void receiveMessage(String msg) {
		getBehavior().agentState.setMsgIn(msg);			
		
	}

	@Override
	public WifiAntenna getAntennaRef() {		
		return this.wifiAntenna;
	}
	
}