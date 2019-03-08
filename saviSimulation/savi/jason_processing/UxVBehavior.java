package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UxVBehavior extends AgentModel {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	//String ID; -- Note: moved to superclass 
	//String type; -- same
	//SyncAgentState agentState; --same
	protected PVector initialPosition;
	protected PVector position;
	protected double speedVal;
	protected double compasAngle;
	protected ArrayList<CameraPerception> visibleItems;
	protected double time;	
	protected double wifiProbWorking;
	
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
	public UxVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod) {	
		// Initialize data values
		super(reasoningCyclePeriod);
		this.ID = id;
		this.type = type;
		this.initialPosition = initialPosition;
		wifiProbWorking = 100; //Probability of having the wifi working 0-100
		//TODO: "break the wifi during simulation time		
		this.position = initialPosition.copy();
		this.speedVal = 0;	
		this.time = 0;
		this.compasAngle = 0;
		agentState = new SyncAgentState();
		this.visibleItems = new ArrayList<CameraPerception>();
		updatePercepts();
	}

	public PVector getPosition() {
		return position;
	}
	public ArrayList<CameraPerception> getVisibleItems() {
		return visibleItems;
	}

	public double getCompassAngle() {
		return this.compasAngle;
	}
	/**
	 * update
	 * process actions from the queue, update the UAS state variable and set the new perceptions
	 */

	//To be override on derived classes
	public void update(double simTime, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects){
	}
	
	/**
	 * Detect world objects & threats with the camera
	 */
	protected ArrayList<CameraPerception> objectDetection(List<WorldObject> obj, int perceptionDistance) {
		ArrayList<CameraPerception> visibleItems = new ArrayList<CameraPerception>();
		ArrayList<CameraPerception> detectedItems = new ArrayList<CameraPerception>();
		double distance, oposite, tan, angle;
		
		for(WorldObject wo:obj) {
			//shouldn't detect itself. if not (UxV and himself)
			if( !((wo instanceof UxV) && this.ID.equals(((UxV)wo).getBehavior().getID())) ){
				
            	List<Double> polar = Geometry.relativePositionPolar(wo.getPosition(), this.position, this.compasAngle);
            
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
		
		//remove objects covered by others on the visualization
		for(CameraPerception di:detectedItems) {
			for(int i=0; i<visibleItems.size();i++) {
				//to calculate visual angle covered by the object
				distance = di.getParameters().get(2);
				//angle deviation from centroid = radius
				oposite = di.getParameters().get(3);
				//math to calculate angle cover
				tan=oposite/distance;
				angle=Math.abs(Math.atan(tan));
				
				//if object is covered by di remove
				//if is covered by azimuth angle
				if(di.getParameters().get(0)+angle > visibleItems.get(i).getParameters().get(0) && di.getParameters().get(0)-angle < visibleItems.get(i).getParameters().get(0) ) {
					//if it is covered by elevation angle
					if(di.getParameters().get(1)+angle > visibleItems.get(i).getParameters().get(1) && di.getParameters().get(1)-angle < visibleItems.get(i).getParameters().get(1) ){
						//if it's at a mayor distance
						if(di.getParameters().get(2) < visibleItems.get(i).getParameters().get(2)) {
							visibleItems.remove(visibleItems.get(i));
						}
					}
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
					//if not same class and id
					if(!((this.ID ==((UxV)wo).getBehavior().ID) &&(this.getClass().equals(((UxV)wo).getClass())))) {
						//if Wifi is working
						if(((UxV)wo).getBehavior().wifiProbWorking > 0 ) {
						//if not same id and same class amnd wifi is working	
							while(!msg.isEmpty()) {
								((UaV)wo).getBehavior().agentState.setMsgIn(msg.poll());			
							}
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
}