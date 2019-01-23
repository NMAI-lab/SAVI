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


public class UAS extends AgentModel {
	private static final double SPEED = 3; //we move by one unit / pixel at each timestep?

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	//String ID; -- Note: moved to superclass 
	//String type; -- same
	PVector initialPosition; // to be able to reset
	double wifi;
	//SyncAgentState agentState; // contains all relevant info = It's in the superclass!
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
		this.initialPosition = initialPosition;
		PVector position = initialPosition.copy(); //Assume that the initial position is at the center of the display window
		wifi = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time
		agentState.setPosition(position); //value type is PVector
		agentState.setSpeedAngle(0.0); //TODO: calculate velocity angle + magnitude
		agentState.setSpeedValue(0.0); //TODO
		agentState.setCompassAngle(-Math.PI/2); //TODO calculate direction we're facing

		agentState.setCameraInfo(new ArrayList<VisibleItem>()); //TODO: calculate what we can see
		//ArrayList<String> mes2share = new ArrayList<String>();
		//mes2share.add(("HelloIAm(" + ID+")")); //TODO: messages cannot be arbitrary strings, they need to be well-formed agentspeak
		//agentState.setMessages2Share(mes2share);
		//agentState.setMsgIn( new Queue<String>());	    
	}

	public PVector getPosition() {
		return agentState.getPosition();
	}

	public double getCompassAngle() {
		return agentState.getCompassAngle();
	}

	// State Update: Read actions from queue, execute them
	// also includes coordinates of threat.
	public void update(int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<Threat> threats, List<WorldObject> objects, List<UAS> uas_list){
		PVector position = (PVector) agentState.getPosition();
		double speedValue = agentState.getSpeedValue();
		double compassAngle = agentState.getCompassAngle(); //TODO for now speedAngle is always zero 
		List<String> toexec = agentState.getAllActions();   
		for (String action : toexec) {
			System.out.println("UAS doing:"+ action);
			if (action.equals("turn(left)")) //TODO: make these MOD 2 pi ? 
				compassAngle -= Math.PI/16.0;
			else if (action.equals("turn(right)")) 
				compassAngle += Math.PI/16.0;
			else if (action.equals( "thrust(on)")) 
				speedValue = SPEED;
			else if (action.equals("thrust(off)")) 
				speedValue = 0;  
		}  
		double movingAngle = compassAngle+agentState.getSpeedAngle();	  
		double cosv = Math.cos(movingAngle);
		double sinv = Math.sin(movingAngle);  
		//calculate new position
		PVector temp = new PVector(Math.round(cosv*speedValue), Math.round(sinv*speedValue));
		position.add(temp);
		//put info back into Agentstate
		agentState.setPosition(position);  
		//Normalize angle between 0 and 2 Pi
		//compassAngle = compassAngle % 2* Math.PI;
		if(compassAngle<0) compassAngle+=2*Math.PI;
		if(compassAngle>2*Math.PI) compassAngle-=2*Math.PI;
		agentState.setCompassAngle(compassAngle);
		agentState.setSpeedValue(speedValue);   
		//calculate what we can see  
		List<VisibleItem> things = new ArrayList<VisibleItem>();
		
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
				double angle = (theta - getCompassAngle());// % 2* Math.PI; //(adjust to 0, 2pi) interval
				// to normalize between 0 to 2 Pi
				if(angle<0) angle+=2*Math.PI;
				if(angle>2*Math.PI) angle-=2*Math.PI;
				if (angle < Math.PI/2. || angle > 3* Math.PI/2.) {
				//it's visible 
					things.add(new VisibleItem("threat", angle, dist)); 	
				} //else {
						//	System.out.println("threat " + i + " not visible.");
						//}
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
		
		//calculate objects detected
		for(int i=0; i<objects.size(); i++) { 
			//get relative position of aircraft to object:
			float deltax = objects.get(i).position.x - getPosition().x;
			float deltay = objects.get(i).position.y - getPosition().y;
			//calculate distance
			double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
			if(dist<perceptionDistance) {
				double theta = Math.atan2(deltay, deltax);
				double angle = (theta - getCompassAngle());// % 2* Math.PI; //(adjust to 0, 2pi) interval
				// to normalize between 0 to 2 Pi
				if(angle<0) angle+=2*Math.PI;
				if(angle>2*Math.PI) angle-=2*Math.PI;
				if (angle < Math.PI/2. || angle > 3* Math.PI/2.) {
					//it's visible 
					things.add(new VisibleItem(objects.get(i).type, angle, dist)); 
				}
			}	 
		} 
		 
		agentState.setCameraInfo(things); 
		
		this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	// State reset
	public void reset(){
		// Initialize data values
		PVector position = initialPosition.copy(); //Assume that the initial position is at the center of the display window
		agentState.setPosition(position);
		agentState.setSpeedValue(0.0);
		agentState.setCameraInfo(new ArrayList<VisibleItem>());
	}

	public String getID() {
		
		return ID;
	}




}