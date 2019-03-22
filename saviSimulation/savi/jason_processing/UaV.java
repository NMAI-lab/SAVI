package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UaV extends UxV {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

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
	public UaV(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image, double reasoningCyclePeriod,String imageName, int perceptionDistance) {			
		// Initializes UAS as WorldObject
		super(id, pos, pixels, Type, sim, image, reasoningCyclePeriod, imageName, perceptionDistance);
		// Initializes Behaviuor
		uxvBehavior = new UaVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod);
	}
}