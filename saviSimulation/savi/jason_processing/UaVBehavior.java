package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UaVBehavior extends UxVBehavior {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)
	
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
	public UaVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {	
		// Initialize data values
		super(id, type, initialPosition,reasoningCyclePeriod);
	}
	
	/**
	 * update
	 * process actions from the queue, update the UAS state variable and set the new perceptions
	 */
	public void update(double simTime, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects){
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

		//Communicate through Wifi
		wifiCommunication(WIFI_PERCEPTION_DISTANCE, objects);
			
		//Update percepts	
		updatePercepts();
		//this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	
	/**
	 * Detect world objects & threats with the camera
	 */
	protected ArrayList<CameraPerception> objectDetection(List<WorldObject> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		for(WorldObject wo:obj) {
			//shouldn't detect itself. if not (uAS and himself)
			if( !((wo instanceof UaV) && this.ID.equals(((UaV)wo).getBehavior().getID())) ){
				
            	List<Double> polar = Geometry.relativePositionPolar(wo.getPosition(), this.position, this.compasAngle);
            
            	//calculate distance
            	double azimuth = polar.get(Geometry.AZIMUTH);
            	double elevation = polar.get(Geometry.ELEVATION);
            	double dist = polar.get(Geometry.DISTANCE);
            	if ((azimuth < Math.PI/2. || azimuth > 3* Math.PI/2.)&&(dist <perceptionDistance) ) {
					//it's visible 
					visibleItems.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist));
            	}
			}	
            	
		}
		return visibleItems;
	}
	/**
	 * Wifi Communication
	 */
	protected void wifiCommunication(int WIFI_PERCEPTION_DISTANCE, List<WorldObject> objects) {
		//Calculate UAS detected for wifi communication
				Queue<String> myMsgOutCopy = new LinkedList<String>();
				myMsgOutCopy = this.agentState.getMsgOutAll();
				for(WorldObject wo:objects) {
					if(wo instanceof UaV){
						//get relative position of UAS to UAS:
						float deltax = ((UaV)wo).getBehavior().getPosition().x - this.getPosition().x;
						float deltay = ((UaV)wo).getBehavior().getPosition().y - this.getPosition().y;
						//calculate distance
						double dist  = Math.sqrt(deltax*deltax + deltay*deltay);
						if(dist < WIFI_PERCEPTION_DISTANCE & wifiProbWorking > 0) {
							Queue<String> msg = new LinkedList<String>();
							msg = myMsgOutCopy;
							if(this.ID != ((UaV)wo).getBehavior().ID & ((UaV)wo).getBehavior().wifiProbWorking > 0 ) {
								while(!msg.isEmpty()) {
									((UaV)wo).getBehavior().agentState.setMsgIn(msg.poll());			
								}
							}				
						}		
					}
				}
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