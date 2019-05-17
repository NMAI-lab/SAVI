package savi.jason_processing;

import java.util.*;
import java.util.logging.Logger;

import processing.core.*;

import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import savi.StateSynchronization.*;
import savi.agentBehaviour.SimpleJasonAgent;


public abstract class UxVBehavior extends AgentModel {
	protected static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)
	
	protected static Logger logger = Logger.getLogger(UxVBehavior.class.getName());
	
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
	public UxVBehavior(String id, String type, PVector initialPosition, double reasoningCyclePeriod, double sensorsErrorProb, double sensorsErrorStdDev) {	
		// Initialize data values
		super(reasoningCyclePeriod);
		this.ID = id;
		this.type = type;
		this.initialPosition = initialPosition;
		this.speedVal = 0;
		this.time = 0;
		this.compasAngle = 0;
		//this.agentState = new SyncAgentState();
		this.visibleItems = new ArrayList<CameraPerception>();
		updatePercepts(initialPosition);		
		this.sensorsErrorProb = sensorsErrorProb;
		this.sensorsErrorStdDev = sensorsErrorStdDev;
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

	public void update(UxV uxv, double simTime, float perceptionDistance, List<WorldObject> objects){
		PVector movementVector = new PVector();
		
		//Process actions to update speedVal & compassAngle & verticalPosition(if applies)
		processAgentActions();
		
		//Update simTime
		double timeElapsed =  simTime - this.time; //elapsed time since last update 
		this.time = simTime;
		
		//Calculate new x,y position and z if its flying
		movementVector = calculateMovementVector(timeElapsed);		
		uxv.setPosition(uxv.getPosition().add(movementVector));
		
		//If the movement led to z<0 the position in z should be 0
		if(uxv.getPosition().z<0) {
			uxv.setPosition(new PVector(uxv.getPosition().x,uxv.getPosition().y,0));
		}
		
		//Calculate visible items
		this.visibleItems = new ArrayList<CameraPerception>();
		
		//Calculate objects detected with camera	
		for (CameraPerception c: objectDetection(uxv.getPosition(), objects, perceptionDistance)) {
			visibleItems.add(c);
		}
			
		//Update percepts	
		updatePercepts(uxv.position);
		//this.notifyAgent(); //this interrupts the Jason if it was sleeping while waiting for a new percept.
	}
	
	//to be Overriden
	protected abstract void processAgentActions();
	protected abstract boolean isObjectDetected (double azimuth, double elevation, double dist, double perceptionDistance);
	protected abstract PVector calculateMovementVector (double timeElapsed);
	
	
	
	/**
	 * Removes covered objects from uncoveredObjects list
	*/
	protected ArrayList<CameraPerception> objectDetection (PVector mypos, List<WorldObject> allObjects, double perceptionDistance){
		ArrayList<CameraPerception> uncoveredObjects = new ArrayList<CameraPerception>(); 
		ArrayList<CameraPerception> detectedObjects = new ArrayList<CameraPerception>(); 
		
		for(WorldObject wo:allObjects) {
			//shouldn't detect itself. if not (UxV and himself)
			if( !((wo instanceof UxV) && this.ID.equals(((UxV)wo).getBehavior().getID())) ){
				
            	List<Double> polar = Geometry.relativePositionPolar(wo.getPosition(), mypos, this.compasAngle);
            
            	//calculate distance
            	double azimuth = polar.get(Geometry.AZIMUTH);
            	double elevation = polar.get(Geometry.ELEVATION);
            	double dist = polar.get(Geometry.DISTANCE);
            	if (isObjectDetected (azimuth, elevation, dist, perceptionDistance) ) {
					//it's visible 
					detectedObjects.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist, wo.pixels/2));
					uncoveredObjects.add(new CameraPerception(wo.type, this.time, azimuth, elevation, dist, wo.pixels/2));
            	}
			}	   	
		}

		uncoveredObjects = removeCoveredObjects(detectedObjects, uncoveredObjects);
		return uncoveredObjects;
	}
	
	
	
	/**
	 * Removes covered objects from uncoveredObjects list
	*/
	protected ArrayList<CameraPerception> removeCoveredObjects (ArrayList<CameraPerception> allObjects, ArrayList<CameraPerception> uncoveredObjects){
		double distance, oposite, angle, tan;
		
		for(CameraPerception di:allObjects) {
			for(int i=0; i<uncoveredObjects.size();i++) {
				//to calculate visual angle covered by the object
				distance = di.getParameters().get(2);
				//angle deviation from centroid = radius
				oposite = di.getParameters().get(3);
				//math to calculate angle cover
				tan=oposite/distance;
				angle=Math.abs(Math.atan(tan));
						
				//if object is covered by di remove
				//if is covered by azimuth angle
				if(di.getParameters().get(0)+angle > uncoveredObjects.get(i).getParameters().get(0) 
					&& di.getParameters().get(0)-angle < uncoveredObjects.get(i).getParameters().get(0) ) {
					//if it is covered by elevation angle
					if(di.getParameters().get(1)+angle > uncoveredObjects.get(i).getParameters().get(1) 
						&& di.getParameters().get(1)-angle < uncoveredObjects.get(i).getParameters().get(1) ){
						//if it's at a mayor distance
						if(di.getParameters().get(2) < uncoveredObjects.get(i).getParameters().get(2)) {
							uncoveredObjects.remove(uncoveredObjects.get(i));
						}
					}
				}
			}	
		}
		return uncoveredObjects;
	}
	
	
	/**
	 * Update perception Snapshot in agent state
	 */
	protected void updatePercepts(PVector mypos) {
		PerceptionSnapshot P = new PerceptionSnapshot();
		double azimuth, elevation, range;
		//if position sensor is failing will return an error
		PVector myposWithError = getPositionWithError(mypos,sensorsErrorProb);
		double compassAngleWithError = this.compasAngle;

		if(isSensorFailing(sensorsErrorProb))
			compassAngleWithError = calculateFailureValue(compasAngle, sensorsErrorStdDev);
		
		// to normalize between 0 to 2 Pi                                                                
		compasAngle = Geometry.normalize02PI(compasAngle);
		
		P.addPerception(new PositionPerception(this.time, (double)myposWithError.x, (double)myposWithError.y, (double)myposWithError.z));

		//Add velocity
		P.addPerception(new VelocityPerception(this.time, compassAngleWithError, 0, this.speedVal));

		//Add time
		P.addPerception(new TimePerception(this.time));
		
		//Add Visible items
				for(CameraPerception cpi : this.visibleItems) {
						if(isSensorFailing(sensorsErrorProb)) {
							azimuth = calculateFailureValue(cpi.getParameters().get(0), this.sensorsErrorStdDev);
							elevation = calculateFailureValue(cpi.getParameters().get(1), this.sensorsErrorStdDev);
							range = calculateFailureValue(cpi.getParameters().get(2), this.sensorsErrorStdDev);
							
							
							//azimuth=Geometry.normalize02PI(azimuth);
							//elevation=Geometry.normalizeMinusPIPI(elevation);
							double az_el [] = Geometry.normalizePolar(azimuth, elevation);
							azimuth = az_el[0];
							elevation = az_el[1];

							cpi.getParameters().set(0, azimuth);
							cpi.getParameters().set(1, elevation);
							cpi.getParameters().set(2, range);
						}	
					P.addPerception(cpi);
				}
				
		agentState.setPerceptions(P);
	}
	
	
	/**
	 * Makes with a random probability, a random error on the UxV position perceived
	 */
	protected PVector getPositionWithError(PVector position, double sensorErrorProb) {
		PVector positionWithError = position.copy();
		if(isSensorFailing(sensorErrorProb)) {
			positionWithError.x = (float)calculateFailureValue((double)position.x, this.sensorsErrorStdDev);
			positionWithError.y = (float)calculateFailureValue((double)position.y, this.sensorsErrorStdDev);
			positionWithError.z = (float)calculateFailureValue((double)position.z, this.sensorsErrorStdDev);
		}	
		return positionWithError;
	}
	
	
	
	// takes probability parameter between 0 and 1 
	protected boolean isSensorFailing (double probability) {
		return (SAVIWorld_model.rand.nextDouble()<probability);
	}

	
	// Generate random value for a normal distribution (mean, stdDev)
	// nextGaussian returns a value for the normal distribution (0,1)
	// multiply for the stdDev to get error and this error value is added to the mean
	protected double calculateFailureValue (double mean, double stdDev) {
		return ((SAVIWorld_model.rand.nextGaussian()*stdDev)+mean);
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