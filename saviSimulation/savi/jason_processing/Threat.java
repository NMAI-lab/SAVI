package savi.jason_processing;

import java.util.List;
import java.util.Random;

import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class Threat extends WorldObject{
	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------
	double maxSpeed;
	Random rand;
	double movingAngle = 0;
	PVector nextRandomDestination;
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	// Constructor: called when an object is created using
	//              the "new" keyword. It's the only method
	//              that doesn't have a type (not even void).
	

	Threat(int id, PVector initialPosition, int seed, double MS, int pxSize, String type, SAVIWorld_model world, PShape img) {
		// Initialize data values
		super(id, initialPosition, pxSize, type, world, img);
		rand = new Random();
		if(seed != -1) {
			rand = new Random(seed + this.ID);
		}		
		maxSpeed = MS;// the max speed 
		this.movingAngle = (double) (rand.nextInt(10)*(Math.PI/4));
		
		setRandomDestination();
		
	  }

	// State Update: Randomly move up, down, left, right, or stay in one place
	@Override
	public void update(double simTime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants){
		
		double speedValue = this.maxSpeed *(1 - 0.6*rand.nextDouble()); // speed between 0.4 * maxSpeed and MaxSpeed
		
		PVector temp = nextRandomDestination.copy().sub(position).setMag((float) (speedValue * timestep));// new PVector(Math.round(cosv*speedValue * timestep ), Math.round(sinv*speedValue * timestep));
		// add a bit of noise to the movement
		float noisex = (float) (speedValue * timestep *0.4*(rand.nextFloat()-1));
		float noisey = (float) (speedValue * timestep *0.4*(rand.nextFloat()-1));
		temp.add(noisex, noisey, 0);
		position.add(temp);		
		
		if (position.dist(nextRandomDestination)<5) { //we have arrived at our (random) destination
			setRandomDestination(); //set a new one.
		}
	}


	// State reset
	void reset(int X_PIXELS, int Y_PIXELS){
		// Initialize data values
		position = new PVector(X_PIXELS/2,Y_PIXELS/2,0); //Assume that the initial position is at the center of the display window
	}

	
	private void setRandomDestination() {
		int rx = rand.nextInt(simulator.X_PIXELS);
		int ry = rand.nextInt(simulator.Y_PIXELS);
		nextRandomDestination = new PVector(rx,ry,pixels/2);
		System.out.println("Threat heading to position "+rx+" / "+ry );
	}
	
	@Override
	public void draw(PVector position) {
		simulator.stroke(0);

		simulator.shapeMode(PConstants.CENTER);
		simulator.shape(this.image, this.position.x, this.position.y, this.pixels, this.pixels);
		//show height lower and upper
		simulator.text(Double.toString(position.z+(this.pixels/2))+"\n"+Double.toString(position.z-(this.pixels/2)), position.x, position.y);
	}


}
