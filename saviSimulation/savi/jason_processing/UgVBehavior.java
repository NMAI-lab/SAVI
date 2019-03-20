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
	public UgVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {	
		// Initialize data values
		super(id, type, initialPosition,reasoningCyclePeriod);
	}

	/**
	 * update
	 * process actions from the queue, update the UAS state variable and set the new perceptions
	 */
	public void update(double simTime, int perceptionDistance, List<WorldObject> objects){
		//Process actions to update speedVal & compassAngle
		processAgentActions();
		
		//Update simTime
		double timeElapsed =  simTime - this.time; //elapsed time since last update 
		this.time = simTime;
		
		//Calculate new position
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		this.position.add(new PVector(Math.round(cosv*this.speedVal*timeElapsed), Math.round(sinv*this.speedVal*timeElapsed), 0));
		
		//Calculate visible items
		this.visibleItems = new ArrayList<CameraPerception>();
		
		//Calculate objects detected with camera	
		for (CameraPerception c: objectDetection(objects, perceptionDistance)) {
			visibleItems.add(c);
		}	

		//Update percepts	
		updatePercepts();
		//this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
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
	
	
}