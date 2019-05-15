package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UgVBehavior extends UxVBehavior {

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	//String ID; -- Note: moved to superclass 
	//String type; -- same
	//SyncAgentState agentState; --same
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
	public UgVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod,  double sensorsErrorProb, double sensorsErrorStdDev) {	
		// Initialize data values
		super(id, type, initialPosition,reasoningCyclePeriod, sensorsErrorProb, sensorsErrorStdDev);
	}

	protected void processAgentActions(){
		List<String> toexec = agentState.getAllActions();   
		for (String action : toexec) {
			logger.fine("[ process actions]UAS id="+this.ID+" doing:"+ action);
			if (action.equals("turn(left)")) //TODO: make these MOD 2 pi ? 
				this.compasAngle -= Math.PI/16.0;
				//Normalize compass angle between 0 and 2 Pi
				if(compasAngle<0) compasAngle+=2*Math.PI;
			else if (action.equals("turn(right)"))				
				this.compasAngle += Math.PI/16.0;
				//Normalize compass angle between 0 and 2 Pi
				if(compasAngle>2*Math.PI) compasAngle-=2*Math.PI;
			else if (action.equals( "thrust(on)")) 
				this.speedVal = SPEED;
			else if (action.equals("thrust(off)")) 
				this.speedVal = 0;  
		}
	}
	
	protected PVector calculateMovementVector (double timeElapsed) {
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		PVector movementVector = new PVector((float)(cosv*this.speedVal*timeElapsed), (float)(sinv*this.speedVal*timeElapsed), (float)0.0);
		movementVector.z=0;
		return movementVector;
	}

	
	protected boolean isObjectDetected (double azimuth, double elevation, double dist, double perceptionDistance) {
		return ((azimuth < Math.PI/2. || azimuth > 3* Math.PI/2.)&&(dist <perceptionDistance));	
	}
	
	
}
