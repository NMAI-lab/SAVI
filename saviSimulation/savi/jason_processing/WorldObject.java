package savi.jason_processing;

import java.util.Random;

import processing.core.PShape;
import processing.core.PVector;

public class WorldObject{
		//-----------------------------------------
		// DATA (or state variables)
		//-----------------------------------------
		int ID;
		PVector position;
		String type;
			//-----------------------------------------
			// METHODS (functions that act on the data)
			//-----------------------------------------
			// Constructor: called when an object is created using
			//              the "new" keyword. It's the only method
			//              that doesn't have a type (not even void).
		WorldObject(int id, PVector pos, String Type) {
			// Initialize data values
			ID = id;
			position=pos;
			type=Type;
		}  
}
	

class Threat extends WorldObject{
	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------
	PVector velocity;
	float maxSpeed;
	Random rand;
	//-----------------------------------------
	// METHODS (functions that act on the data)
	//-----------------------------------------
	// Constructor: called when an object is created using
	//              the "new" keyword. It's the only method
	//              that doesn't have a type (not even void).
	
	Threat(int id, int x, int y, int x_v, int y_v, float MS, String type) {
		// Initialize data values
		super(id,new PVector(x,y),type);
		velocity = new PVector(x_v, y_v);
		maxSpeed = MS;// the max speed 
		rand = new Random();
	  }

	// State Update: Randomly move up, down, left, right, or stay in one place
	void update(int width,int height){
		int stepsize = 2;
		PVector temp = new PVector();
		// The size of the neighborhood depends on the range in this line 
		// -1 = left/down, 0 = stay at your spot, 1 = right/up
		// To obtain -1, 0 or 1 use int(random(-2,2))
		while (temp.mag() == 0){
			temp = new PVector(stepsize * (rand.nextInt(2) + -2), stepsize * (rand.nextInt(2) + -2));
		}
	
		position.add(temp);    
		velocity = temp.limit(maxSpeed);//need to cross-check this    
		//Stay within the screen's boundaries
		//position.x = constrain(position.x,0,width-1); //constrain: Constrains a value to not exceed a maximum and minimum value.
		//position.y = constrain(position.y,0,height-1);
		
		position.x = (position.x > width-1) ? width-1 : (position.x < 0 ? 0 : position.x);
		position.y = (position.y > height-1) ? height-1 : (position.y < 0 ? 0 : position.y);
	}


	// State reset
	void reset(int X_PIXELS, int Y_PIXELS){
		// Initialize data values
		position = new PVector(X_PIXELS/2,Y_PIXELS/2); //Assume that the initial position is at the center of the display window
		velocity = new PVector();  // same as new PVector(0,0)
	}


}