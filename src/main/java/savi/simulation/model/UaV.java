package savi.simulation.model;

import processing.core.*;
import savi.simulation.SAVIWorld_model;
import savi.simulation.behaviour.UaVBehavior;


public class UaV extends UxV {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)
	public static final double UAVPerceptionAngle = Math.PI *2;
	public static final String UAVTYPE = "uav";

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------
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
	public UaV(int id, PVector pos, int pixels, SAVIWorld_model sim, double reasoningCyclePeriod, int perceptionDistance, double sensorsErrorProb, double sensorsErrorStdDev, double probWifiFailing) {
		// Initializes UAS as WorldObject
		super(id, pos, pixels, UAVTYPE, sim, reasoningCyclePeriod, perceptionDistance, UAVPerceptionAngle, sensorsErrorProb, sensorsErrorStdDev, probWifiFailing);
		// Initializes Behaviuor
		this.uxvBehavior = new UaVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod, sensorsErrorProb, sensorsErrorStdDev);
	}
		
}