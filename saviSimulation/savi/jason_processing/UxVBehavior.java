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
	protected double speedVal;
	protected double compasAngle;
	protected ArrayList<CameraPerception> visibleItems;
	protected double time;	
	protected double sensorsErrorProb;
	protected double sensorsErrorStdDev;
	protected Random rand = new Random();
	protected int seed;
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
	public UxVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod, double sensorsErrorProb, double sensorsErrorStdDev, int seed) {	
		// Initialize data values
		super(reasoningCyclePeriod);
		this.ID = id;
		this.type = type;
		this.initialPosition = initialPosition;
		this.speedVal = 0;	
		this.time = 0;
		this.compasAngle = 0;
		this.agentState = new SyncAgentState();
		this.visibleItems = new ArrayList<CameraPerception>();
		updatePercepts(initialPosition);
		
		this.sensorsErrorProb = sensorsErrorProb;
		this.sensorsErrorStdDev = sensorsErrorStdDev;
		this.seed = seed;
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
	public void update(UxV uxv, double simTime, int perceptionDistance, List<WorldObject> objects){
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
	 * Process the action in the queue to update the speedVal and compassAngle
	 */
	protected void processAgentActions(){				
	}	
	
	/**
	 * Update perception Snapshot in agent state
	 */
	protected void updatePercepts(PVector mypos) {
		PerceptionSnapshot P = new PerceptionSnapshot();
		PVector positionWithError = new PVector();
		
		//if position sensor is failing
		if(isSensorFailing(sensorsErrorProb, seed)) {
			positionWithError.x = (float)calculateFailureValue((double)mypos.x, this.sensorsErrorStdDev);
			positionWithError.y = (float)calculateFailureValue((double)mypos.y, this.sensorsErrorStdDev);
			positionWithError.z = (float)calculateFailureValue((double)mypos.z, this.sensorsErrorStdDev);
				//add position
				P.addPerception(new PositionPerception(this.time, (double)positionWithError.x, (double)positionWithError.y, (double) positionWithError.z));
				//Add velocity
				P.addPerception(new VelocityPerception(this.time, Math.atan(positionWithError.x/positionWithError.y), 0, this.speedVal));			
		} else { //Value without error
				//add position
				P.addPerception(new PositionPerception(this.time, (double) mypos.x, (double) mypos.y, (double) mypos.z));
				//Add velocity
				P.addPerception(new VelocityPerception(this.time, Math.atan(mypos.x/mypos.y), 0, this.speedVal));
		}
		
		//Add time
		P.addPerception(new TimePerception(this.time));
		
		//Add Visible items
		for(CameraPerception cpi : this.visibleItems) {
			for(int i=0; i<cpi.getParameters().size(); i++) {
				if(isSensorFailing(sensorsErrorProb, seed)) {
					cpi.getParameters().set(i, calculateFailureValue(cpi.getParameters().get(i), this.sensorsErrorStdDev));
				}
			}	
			P.addPerception(cpi);
		}
		
		agentState.setPerceptions(P);
	}
	
	
	// takes probability parameter between 0 and 1 
	protected boolean isSensorFailing (double probability, int seed) {
		if(seed != -1) {
			rand = new Random(seed);
		}
		if(rand.nextDouble()<probability) {
			return true;
		}else {
			return false;
		}	
	}

	
	// generate random value for a normal distribution (mean, stdDev)
	protected double calculateFailureValue (double mean, double stdDev) {
		if(seed != -1) {
			rand = new Random(seed);
		}
		return ((rand.nextGaussian()*stdDev)+mean);
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