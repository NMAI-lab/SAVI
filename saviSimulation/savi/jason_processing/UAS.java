package savi.jason_processing;

import java.util.*;

import processing.core.*;

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
	//SyncAgentState agentState; --same
	PVector initialPosition;
	PVector position;
	double speedVal;
	double compasAngle;
	ArrayList<CameraPerception> visibleItems;
	double time;	
	double wifi;
	
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
	UAS(String id, String type, PVector initialPosition) {	
		// Initialize data values
		this.ID = id;
		this.type = type;
		this.initialPosition = initialPosition;
		wifi = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time		
		reset();		
	}

	public PVector getPosition() {
		return position;
	}

	public double getCompassAngle() {
		return this.compasAngle;
	}
	/**
	 * update
	 * process actions from the queue, update the UAS state variable and set the new perceptions
	 */
	public void update(double simTime, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects, List<UAS> uas_list){
		//Update simTime
		this.time = simTime;
		//Process actions to update speedVal & compassAngle
		processAgentActions();
		//Calculate new position
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);
		this.position.add(new PVector(Math.round(cosv*this.speedVal), Math.round(sinv*this.speedVal), 0));
		//Calculate visible items
		this.visibleItems = new ArrayList<CameraPerception>();
		//Calculate threats detected	
		for (CameraPerception c: objectDetection(objects, perceptionDistance)) {
			visibleItems.add(c);
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
		
		
		updatePercepts(); //Update percepts
		this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	
	/**
	 * Detect world objects & threats with the camera
	 */
	protected ArrayList<CameraPerception> objectDetection(List<WorldObject> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		for(int i=0; i<obj.size(); i++) {   
			//get relative position to UAS:
			float deltax = obj.get(i).position.x - getPosition().x;
			float deltay = obj.get(i).position.y - getPosition().y;
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
					visibleItems.add(new CameraPerception(obj.get(i).type, this.time, angle, 0, dist));
				} //else {
						//	System.out.println(obj.get(i).type + i + " not visible.");
						//}
			}		
		}
		return visibleItems;
	}
	/**
	 * Process the action in the queue to update the speedVal and compassAngle
	 */
	protected void processAgentActions(){
		List<String> toexec = agentState.getAllActions();   
		for (String action : toexec) {
			System.out.println("UAS doing:"+ action);
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
	 * Reset the UAS state
	 */
	public void reset(){
		this.position = initialPosition.copy();
		this.speedVal = 0;	
		this.time = 0;
		this.compasAngle = 0;
		agentState = new SyncAgentState();
		this.visibleItems = new ArrayList<CameraPerception>();
		updatePercepts();		
	}	
	/**
	 * Get UAS id
	 * @return
	 */
	public String getID() {		
		return ID;
	}	
	/**
	 * Update perception Snapshot in agent state
	 */
	protected void updatePercepts() {
		PerceptionSnapshot P = new PerceptionSnapshot();		
		//Add position
		P.addPerception(new PositionPerception(this.time, (double) this.position.x, (double) this.position.y, (double) this.position.z));
		//Add velocity
		P.addPerception(new VelocityPerception(this.time, Math.atan(this.position.x/this.position.y), 0, this.speedVal));
		//Add time
		P.addPerception(new TimePerception(this.time));
		//Add Visible items
		for(CameraPerception cpi : this.visibleItems) {
			P.addPerception(cpi);
		}
		agentState.setPerceptions(P);
	}
}