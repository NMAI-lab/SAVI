package savi.simulation.model;

import processing.core.*;
import savi.simulation.SAVIWorld_model;
import savi.simulation.behaviour.UgVBehavior;


public class UgV extends UxV {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)
	private static final double UGVperceptionAngle = Math.PI;
	private static final String UGVType = "ugv";
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
	public UgV(int id, PVector pos, int pixels, SAVIWorld_model sim, double reasoningCyclePeriod, int perceptionDistance, double sensorsErrorProb, double sensorsErrorStdDev, double probWifiFailing) {
		// Initializes UAS as WorldObject
		super(id, pos, pixels, UGVType, sim, reasoningCyclePeriod, perceptionDistance, UGVperceptionAngle, sensorsErrorProb, sensorsErrorStdDev, probWifiFailing);
		// Initializes Behavior
		this.uxvBehavior = new UgVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod, sensorsErrorProb, sensorsErrorStdDev);
	}
	
	
	
	
	
}