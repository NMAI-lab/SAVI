package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UAS extends WorldObject {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	UASBehavior uasBehavior;
	
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
	UAS(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image, double reasoningCyclePeriod) {			
		// Initializes UAS as WorldObject
		super(id, pos, pixels, Type, sim, image);
		// Initializes Behaviuor
		this.uasBehavior = new UASBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod);	
	}
	
	@Override
	public void update(double simtime, double timestep, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects) {
		this.uasBehavior.update(simtime, perceptionDistance, WIFI_PERCEPTION_DISTANCE, objects);
	}
	
	public UASBehavior getBehavior() {		
		return uasBehavior;
	}
	
	@Override
	public PVector getPosition() {		
		return this.getBehavior().getPosition();
	}
	
	
}