package savi.jason_processing;

import java.util.Random;

import processing.core.PVector;

class Threat extends WorldObject{
	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------
	double maxSpeed;
	Random rand;
	double movingAngle = 0;
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	// Constructor: called when an object is created using
	//              the "new" keyword. It's the only method
	//              that doesn't have a type (not even void).
	

	Threat(int id, int x, int y, int seed, double MS, String type) {
		// Initialize data values
		super(id,new PVector(x,y),type);
		rand = new Random();
		if(seed != -1) {
			rand = new Random(seed + this.ID);
		}		
		maxSpeed = MS;// the max speed 
		this.movingAngle = (double) (rand.nextInt(10)*(Math.PI/4));	
		
	  }

	// State Update: Randomly move up, down, left, right, or stay in one place
	void update(){
		
		double speedValue = (double) (rand.nextFloat() * this.maxSpeed);
		//double speedValue = 0;
		//double speedValue = this.maxSpeed ;	
		this.movingAngle = this.movingAngle + (double) (rand.nextInt(2)*(Math.PI/4));	
		double cosv = Math.cos(movingAngle);
		double sinv = Math.sin(movingAngle);  
		//calculate new position
		PVector temp = new PVector(Math.round(cosv*speedValue), Math.round(sinv*speedValue));
		position.add(temp);		
	}


	// State reset
	void reset(int X_PIXELS, int Y_PIXELS){
		// Initialize data values
		position = new PVector(X_PIXELS/2,Y_PIXELS/2); //Assume that the initial position is at the center of the display window
	}


}
