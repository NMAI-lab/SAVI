package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UAS extends AgentModel {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

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
	PShape uasShape;
	
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
	UAS(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {	
		// Initialize data values
		super(reasoningCyclePeriod);
		this.ID = id;
		this.type = type;
		this.initialPosition = initialPosition;
		wifi = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time
//		uasShape=loadShape("SimImages/robot.svg");
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
		//Process actions to update speedVal & compassAngle
		processAgentActions();
		//Calculate new position
		double cosv = Math.cos(this.compasAngle);
		double sinv = Math.sin(this.compasAngle);

		//Update simTime
		double timeElapsed =  simTime - this.time; //elapsed time since last update 
		this.time = simTime;

		this.position.add(new PVector(Math.round(cosv*this.speedVal*timeElapsed), Math.round(sinv*this.speedVal*timeElapsed), 0));
		//Calculate visible items
		this.visibleItems = new ArrayList<CameraPerception>();
		//Calculate objects detected with camera	
		for (CameraPerception c: objectDetection(objects, perceptionDistance)) {
			visibleItems.add(c);
		}	
		//Calculate UAS detected with camera
		for (CameraPerception c: UASDetection(uas_list, perceptionDistance)) {
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
		//this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	
	/**
	 * Detect world objects & threats with the camera
	 */
	protected ArrayList<CameraPerception> objectDetection(List<WorldObject> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		for(WorldObject wo:obj) {   
			//get relative position to UAS:
            List<Double> polar = Geometry.relativePositionPolar(wo.position, this.position, this.compasAngle);
            
			//calculate distance
            double azimuth = polar.get(Geometry.AZIMUTH);
            double elevation = polar.get(Geometry.ELEVATION);
            double dist = polar.get(Geometry.DISTANCE);
            if ((azimuth < Math.PI/2. || azimuth > 3* Math.PI/2.)&&(dist <perceptionDistance) ) {
					//it's visible 
					visibleItems.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist));
			} 
		}
		return visibleItems;
	}
	
	/**
	 * Detect other UAS with the camera
	 * TODO: Eliminate this method as the UAS will become a WorldObject.
	 */
	protected ArrayList<CameraPerception> UASDetection(List<UAS> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		for(UAS uas:obj) {   
			if(! this.ID.equals(uas.getID())) {
				//get relative position to UAS:
				List<Double> polar = Geometry.relativePositionPolar(uas.position, this.position, this.compasAngle);
	            
	            //calculate distance
	            double azimuth = polar.get(Geometry.AZIMUTH);
	            double elevation = polar.get(Geometry.ELEVATION);
	            double dist = polar.get(Geometry.DISTANCE);
	            if ((azimuth < Math.PI/2. || azimuth > 3* Math.PI/2.)&&(dist <perceptionDistance) ) {
						//it's visible 
						visibleItems.add(new CameraPerception(uas.type, this.time, azimuth, elevation, dist));
				}
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
	 * Get UAS type
	 * @return
	 */
	public String getType() {		
		return type;
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