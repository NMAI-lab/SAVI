package savi.jason_processing;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UAS extends AgentModel {
	private static final double SPEED = 3; //we move by one unit / pixel at each timestep?

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	//String ID; -- Note: moved to superclass 
	//String type; -- same
	PVector initialPosition;
	PVector position;
	double speedVal;
	double compasAngle;
	
	double wifi;
	//SyncAgentState agentState; // contains all relevant info = It's in the superclass!
	
	//***********************************************************//
	//I THINK IS BETTER TO HAVE THE ROBOTS ITS DATA AND THE SYNCAGENTSTATE ITS OWN.
	//IF WE WANT TO IMPLEMENTE MALFUNCTION OF SENSORS, THE INFO RECEIVED IN 
	//SYNCAGENTSTATE IS NOT THE REAL ONE
	//***********************************************************//
	
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	// Constructor: called when an object is created using the "new" keyword. It's the only method
	//              that doesn't have a type (not even void).
	UAS(String id, String type, PVector initialPosition) {	
		// Initialize data values
		this.ID = id;
		this.type = type;
		agentState = new SyncAgentState();
		this.position = initialPosition;
		this.initialPosition = initialPosition;
		this.speedVal = 0;
		this.compasAngle = 0;
		wifi = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time
		
		PerceptionSnapshot percepts = new PerceptionSnapshot();
		//Add position
		percepts.addPerception(new PositionPerception(0, (double) position.x, (double) position.y, (double) position.z));
		//Add velocity
		percepts.addPerception(new VelocityPerception(0, Math.atan(position.x/position.y), 0, speedVal));
		//Add time
		percepts.addPerception(new TimePerception(0));
		//Do not see anything			
		agentState.setPerceptions(percepts);
		
		//**TO DELETE **//
		//agentState.setSpeedAngle(0.0); //TODO: calculate velocity angle + magnitude
		//agentState.setSpeedValue(0.0); //TODO
		//agentState.setCompassAngle(-Math.PI/2); //TODO calculate direction we're facing

		//agentState.setCameraInfo(new ArrayList<VisibleItem>()); //TODO: calculate what we can see
		//ArrayList<String> mes2share = new ArrayList<String>();
		//mes2share.add(("HelloIAm(" + ID+")")); //TODO: messages cannot be arbitrary strings, they need to be well-formed agentspeak
		//agentState.setMessages2Share(mes2share);
		//agentState.setMsgIn( new Queue<String>());	  
		//**TO DELETE FINISHED**//
	}

	public PVector getPosition() {
		return position;
	}

	public double getCompassAngle() {
		return this.compasAngle;
	}

	// State Update: Read actions from queue, execute them and set new perceptions
	public void update(double simTime, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<Threat> threats, List<WorldObject> objects, List<UAS> uas_list){
		//**TO DELETE **//
		//PVector position = (PVector) agentState.getPosition();
		//double speedValue = agentState.getSpeedValue();
		//double compassAngle = agentState.getCompassAngle(); //TODO for now speedAngle is always zero 
		//**TO DELETE FINISHED**//
		
		List<String> toexec = agentState.getAllActions();   
		for (String action : toexec) {
			System.out.println("UAS doing:"+ action);
			if (action.equals("turn(left)")) //TODO: make these MOD 2 pi ? 
				this.compasAngle -= Math.PI/16.0;
			else if (action.equals("turn(right)")) 
				this.compasAngle += Math.PI/16.0;
			else if (action.equals( "thrust(on)")) 
				this.speedVal = SPEED;
			else if (action.equals("thrust(off)")) 
				this.speedVal = 0;  
		} 			
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		PerceptionSnapshot percepts = new PerceptionSnapshot();
		//Calculate, set and add position
		this.position.add(new PVector(Math.round(cosv*this.speedVal), Math.round(sinv*this.speedVal), 0));
		percepts.addPerception(new PositionPerception(simTime, (double) position.x, (double) position.y, (double) position.z));	
		//Add velocity
		percepts.addPerception(new VelocityPerception(simTime, Math.atan(position.x/position.y), 0, speedVal));
		//Add time
		percepts.addPerception(new TimePerception(simTime));
		//Normalize angle between 0 and 2 Pi
		//compassAngle = compassAngle % 2* Math.PI;
		if(compasAngle<0) compasAngle+=2*Math.PI;
		if(compasAngle>2*Math.PI) compasAngle-=2*Math.PI;
		//**TO DELETE **//
		//agentState.setCompassAngle(compasAngle);
		//agentState.setSpeedValue(speedValue);
		//**TO DELETE FINISHED**//
		//calculate what we can see  					
				
		//Calculate threats detected
		//System.out.println("UAS perception ---- threat number:"+threats.size());
		for(int i=0; i<threats.size(); i++) {   
			//get relative position to UAS:
			float deltax = threats.get(i).position.x - getPosition().x;
			float deltay = threats.get(i).position.y - getPosition().y;
			//calculate distance
			double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
			if(dist<perceptionDistance) {
				double theta = Math.atan2(deltay, deltax);
				double angle = (theta - this.compasAngle);// % 2* Math.PI; //(adjust to 0, 2pi) interval
				// to normalize between 0 to 2 Pi
				if(angle<0) angle+=2*Math.PI;
				if(angle>2*Math.PI) angle-=2*Math.PI;
				if (angle < Math.PI/2. || angle > 3* Math.PI/2.) {
					//it's visible 
					percepts.addPerception(new CameraPerception(threats.get(i).type, simTime, angle, 0, dist));
				} //else {
						//	System.out.println("threat " + i + " not visible.");
						//}
			}		
		}
		//calculate objects detected
		for(int i=0; i<objects.size(); i++) { 
			//get relative position of aircraft to object:
			float deltax = objects.get(i).position.x - getPosition().x;
			float deltay = objects.get(i).position.y - getPosition().y;
			//calculate distance
			double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
			if(dist<perceptionDistance) {
				double theta = Math.atan2(deltay, deltax);
				double angle = (theta - this.compasAngle);// % 2* Math.PI; //(adjust to 0, 2pi) interval
				// to normalize between 0 to 2 Pi
				//HERE IS THE PROBLEM WITH THE ANGLES, WE ONLY CHECK ONCE - 
				if(angle<0) angle+=2*Math.PI;
				if(angle>2*Math.PI) angle-=2*Math.PI;
				if (angle < Math.PI/2. || angle > 3* Math.PI/2.) {
					//it's visible 
					percepts.addPerception(new CameraPerception(objects.get(i).type, simTime, angle, 0, dist)); 
				}
			}	 
		} 
		
		//Calculate UAS detected for wifi communication
		Queue<String> myMsgOutCopy = new LinkedList<String>();
		myMsgOutCopy = this.agentState.getMsgOutAll();
		for(int i=0; i<uas_list.size(); i++) { 
			//get relative position of UAS to UAS:
			float deltax = uas_list.get(i).getPosition().x - getPosition().x;
			float deltay = uas_list.get(i).getPosition().x - getPosition().y;
			//calculate distance
			double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
			if(dist < WIFI_PERCEPTION_DISTANCE & wifi > 0) {
				Queue<String> msg = new LinkedList<String>();
				msg = myMsgOutCopy;
				if(this.ID != uas_list.get(i).ID & uas_list.get(i).wifi > 0 ) {
					while(!msg.isEmpty()) {
						uas_list.get(i).agentState.setMsgIn(msg.poll());			
					}
				}				
			}		
		}
		
		
		 
		agentState.setPerceptions(percepts);
		
		this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	// State reset
	public void reset(){
		// Initialize data values
		this.position = initialPosition.copy(); //Assume that the initial position is at the center of the display window
		this.speedVal = 0;	
		PerceptionSnapshot percepts = new PerceptionSnapshot();
		//Add position
		percepts.addPerception(new PositionPerception(0, (double) position.x, (double) position.y, (double) position.z));
		//Add velocity
		percepts.addPerception(new VelocityPerception(0, Math.atan(position.x/position.y), 0, speedVal));
		//Add time
		percepts.addPerception(new TimePerception(0));
		//Do not see anything			
		agentState.setPerceptions(percepts);
	}

	public String getID() {
		
		return ID;
	}




}