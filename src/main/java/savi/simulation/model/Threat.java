package savi.simulation.model;

import java.util.List;

import processing.core.PVector;
import savi.simulation.SAVIWorld_model;
import savi.simulation.behaviour.WifiAntenna;

public class Threat extends WorldObject{
	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------
	private double maxSpeed;
	private double movingAngle = 0;
	private PVector nextRandomDestination;
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	// Constructor: called when an object is created using
	//              the "new" keyword. It's the only method
	//              that doesn't have a type (not even void).
	

	public Threat(int id, PVector initialPosition, double MS, int pxSize, String type, SAVIWorld_model world) {
		// Initialize data values
		super(id, initialPosition, pxSize, type, world);
		maxSpeed = MS;// the max speed 
		this.movingAngle = (double) (SAVIWorld_model.rand.nextInt(10)*(Math.PI/4));
		
		setRandomDestination();
		
	  }

	// State Update: Randomly move up, down, left, right, or stay in one place
	@Override
	public void update(double simTime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants){
		double speedValue = this.maxSpeed *(1 - 0.6 * SAVIWorld_model.rand.nextDouble()); // speed between 0.4 * maxSpeed and MaxSpeed
		
		PVector temp = nextRandomDestination.copy().sub(position).setMag((float) (speedValue * timestep));// new PVector(Math.round(cosv*speedValue * timestep ), Math.round(sinv*speedValue * timestep));
		// add a bit of noise to the movement
		float noisex = (float) (speedValue * timestep *0.4*(SAVIWorld_model.rand.nextFloat()-1));
		float noisey = (float) (speedValue * timestep *0.4*(SAVIWorld_model.rand.nextFloat()-1));
		temp.add(noisex, noisey, 0);// adding 0 in z coordinate as it does not change the altitude

		//if there is no collision with an object update position
		if(!detectCollision(this, temp, this.pixels, objects)) {
			position.add(temp);
		}else {
			setRandomDestination(); //set a new one.
		}
		
		if (position.dist(nextRandomDestination)<5) { //we have arrived at our (random) destination
			setRandomDestination(); //set a new one.
		}
	}


	public boolean detectCollision(Threat t, PVector movement, int pixels, List<WorldObject> objects) {
		PVector aux = new PVector(0,0,0);
		aux.add(t.position);
		aux.add(movement);
		
		for(WorldObject wo : objects) {
			//if not itself
			if(!(this.getClass().equals(wo.getClass()) && wo.ID==t.ID)) {
				//if thre spheres of the objects intersect
				if(aux.dist(wo.getPosition())<=(wo.pixels/2+pixels/2)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	// State reset
	private void reset(int X_PIXELS, int Y_PIXELS){
		// Initialize data values
		position = new PVector(X_PIXELS/2,Y_PIXELS/2,pixels/2); //Assume that the initial position is at the center of the display window
	}

	
	private void setRandomDestination() {
		int rx = SAVIWorld_model.rand.nextInt(simulator.X_PIXELS);
		int ry = SAVIWorld_model.rand.nextInt(simulator.Y_PIXELS);
		nextRandomDestination = new PVector(rx,ry,pixels/2);
		logger.fine("Threat heading to position "+rx+" / "+ry );
	}
	
	


}
