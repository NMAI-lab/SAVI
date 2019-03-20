package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UaVBehavior extends UxVBehavior {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)
	private static final double VERTICAL_SPEED = 0.1; // 0.1 something (whatever real-life distance this corresponds to)
	//***********************************************************//
	//I THINK IS BETTER TO HAVE THE ROBOTS ITS DATA AND THE SYNCAGENTSTATE ITS OWN.
	//IF WE WANT TO IMPLEMENTE MALFUNCTION OF SENSORS, THE INFO RECEIVED IN 
	//SYNCAGENTSTATE IS NOT THE REAL ONE
	//***********************************************************//
	private double verticalSpeedVal;
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	/**
	 * Constructor
	 * @param id
	 * @param type
	 * @param initialPosition
	 */
	public UaVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {	
		// Initialize data values
		super(id, type, initialPosition,reasoningCyclePeriod);
	}
	
	/**
	 * update
	 * process actions from the queue, update the UAS state variable and set the new perceptions
	 */
	@Override
	public void update(UxV uav, double simTime, int perceptionDistance, List<WorldObject> objects){
		//Process actions to update speedVal & compassAngle
		System.out.println("Updating UaV behavior: "+uav.type+" " + uav.ID);
		processAgentActions();
		
		//Update simTime
		double timeElapsed =  simTime - this.time; //elapsed time since last update 
		this.time = simTime;
		
		//Calculate new x,y position
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		PVector newpos = new PVector(Math.round(cosv*this.speedVal*timeElapsed), Math.round(sinv*this.speedVal*timeElapsed), 0);
		//Calculate new altitude
		newpos.z+=verticalSpeedVal;
		if(newpos.z<0) {
			newpos.z=0;
		}
		
		uav.setPosition(uav.getPosition().add(newpos));
		
		//Calculate visible items
		this.visibleItems = new ArrayList<CameraPerception>();
		
		//Calculate objects detected with camera	
		for (CameraPerception c: objectDetection(uav.position, objects, perceptionDistance)) {
			visibleItems.add(c);
		}	
			
		//Update percepts	
		updatePercepts(uav.position);
		//this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}

	/**
	 * Process the action in the queue to update the speedVal and compassAngle
	 */
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
			else if (action.equals( "thrust(up)")) 
				this.verticalSpeedVal = VERTICAL_SPEED;
			else if (action.equals("thrust(down)")) 
				this.verticalSpeedVal = -VERTICAL_SPEED;
			else if (action.equals("hover")) 
				this.verticalSpeedVal = 0;	
				
		}					
	}	
}