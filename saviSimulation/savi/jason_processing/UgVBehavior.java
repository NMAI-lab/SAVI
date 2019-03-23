package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UgVBehavior extends UxVBehavior {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

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
	public UgVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod,  double sensorsErrorProb, double sensorsErrorStdDev, int seed) {	
		// Initialize data values
		super(id, type, initialPosition,reasoningCyclePeriod, sensorsErrorProb, sensorsErrorStdDev, seed);
	}

	protected void processAgentActions(){
		List<String> toexec = agentState.getAllActions();   
		for (String action : toexec) {
			System.out.println("[ process actions]UAS id="+this.ID+" doing:"+ action);
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

	
	/**
	 * Detect world objects & threats with the camera
	 */
	protected ArrayList<CameraPerception> objectDetection(PVector mypos, List<WorldObject> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		ArrayList<CameraPerception> detectedItems = new ArrayList<CameraPerception>();
		double distance, oposite, tan, angle;
		
		for(WorldObject wo:obj) {
			//shouldn't detect itself. if not (UxV and himself)
			if( !((wo instanceof UxV) && this.ID.equals(((UxV)wo).getBehavior().getID())) ){
				
            	List<Double> polar = Geometry.relativePositionPolar(wo.getPosition(), mypos, this.compasAngle);
            
            	//calculate distance
            	double azimuth = polar.get(Geometry.AZIMUTH);
            	double elevation = polar.get(Geometry.ELEVATION);
            	double dist = polar.get(Geometry.DISTANCE);
            	if ((azimuth < Math.PI/2. || azimuth > 3* Math.PI/2.)&&(dist <perceptionDistance) ) {
					//it's visible 
					detectedItems.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist, wo.pixels/2));
					visibleItems.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist, wo.pixels/2));
            	}
			}	   	
		}

		visibleItems = removeCoveredObjects(detectedItems, visibleItems);
		return visibleItems;
	}
	
	protected PVector calculateMovementVector (double timeElapsed) {
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		PVector movementVector = new PVector((float)(cosv*this.speedVal*timeElapsed), (float)(sinv*this.speedVal*timeElapsed), (float)0.0);
		return movementVector;
	}

}