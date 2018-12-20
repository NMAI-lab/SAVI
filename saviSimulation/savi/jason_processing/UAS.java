package savi.jason_processing;

import java.util.ArrayList;

import java.util.List;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import savi.jason_processing.ROBOT_model.House;
import savi.jason_processing.ROBOT_model.Threat;
import savi.jason_processing.ROBOT_model.Tree;

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
		double wifi = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time
		agentState.setPosition(position); //value type is PVector
		agentState.setSpeedAngle(0.0); //TODO: calculate velocity angle + magnitude
		agentState.setSpeedValue(0.0); //TODO
		agentState.setCompassAngle(-Math.PI/2); //TODO calculate direction we're facing

		agentState.setCameraInfo(new ArrayList<VisibleItem>()); //TODO: calculate what we can see
		ArrayList<String> mes2share = new ArrayList<String>();
		//mes2share.add(("HelloIAm(" + ID+")")); //TODO: messages cannot be arbitrary strings, they need to be well-formed agentspeak
		//agentState.setMessages2Share(mes2share);
		agentState.setMessagesRead( new ArrayList<String>());	    
	}

	public PVector getPosition() {
		return agentState.getPosition();
	}

	public double getCompassAngle() {
		return agentState.getCompassAngle();
	}

	// State Update: Read actions from queue, execute them
	// also includes coordinates of threat.
	public void update(int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<Threat> threats, List<Tree> trees, List<House> houses, List<UAS> uas_list){
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
		compassAngle = compassAngle % 2* Math.PI;  
		agentState.setCompassAngle(compassAngle);
		agentState.setSpeedValue(speedValue);   
		//calculate what we can see  
		List<VisibleItem> things = new ArrayList<VisibleItem>();
		List<String> messages = new ArrayList<String>();
		//Calculate threats detected
		for(int i=0; i<threats.size(); i++) {   
			//get relative position of aircraft to UAS:
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
					things.add(new VisibleItem("tree", angle, dist)); 	
				}
			}		
		}
		//Calculate UAS detected for wifi communication 
		for(int i=0; i<uas_list.size(); i++) { 
			//get relative position of UAS to UAS:
			float deltax = uas_list.get(i).getPosition().x - getPosition().x;
			float deltay = uas_list.get(i).getPosition().x - getPosition().y;
			//calculate distance
			double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
			if(dist<WIFI_PERCEPTION_DISTANCE) {
				for(String message:uas_list.get(i).agentState.getMessages2Share()) { 
					messages.add(message);
				}	 
			}		
		}

		//calculate trees detected

		for(int i=0; i<trees.size(); i++) { 

			//get relative position of aircraft to object:
			float deltax = trees.get(i).X - getPosition().x;
			float deltay = trees.get(i).Y - getPosition().y;

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
					things.add(new VisibleItem("tree", angle, dist));  
				}
			}	 
		} 

		for(int i=0; i<houses.size(); i++) { 
			//get relative position of aircraft to object:
			float deltax = houses.get(i).X - getPosition().x;
			float deltay = houses.get(i).Y - getPosition().y;
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
					things.add(new VisibleItem("house", angle, dist));  
				}	  
			}  
		}		 
		agentState.setCameraInfo(things); 
		agentState.setMessagesRead(messages);
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