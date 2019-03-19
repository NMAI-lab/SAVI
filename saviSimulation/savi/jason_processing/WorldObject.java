package savi.jason_processing;

import java.util.List;
import java.util.Random;

import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class WorldObject{
		//-----------------------------------------
		// DATA (or state variables)
		//-----------------------------------------
		int ID;
		protected PVector position;
		String type;
		SAVIWorld_model simulator;
		PShape image;
		int pixels;
			//-----------------------------------------
			// METHODS (functions that act on the data)
			//-----------------------------------------
			// Constructor: called when an object is created using
			//              the "new" keyword. It's the only method
			//              that doesn't have a type (not even void).
		WorldObject(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image) {
			// Initialize data values
			ID = id;
			position=pos;
			type=Type;
			simulator = sim;
			this.image = image;
			this.pixels = pixels;
		}  
		

		public void draw() {
			simulator.stroke(0);

			simulator.shapeMode(PConstants.CENTER);
			simulator.shape(this.image, this.position.x, this.position.y,pixels,pixels);

		}
		
		public void update(double simtime, double timestep, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
			// most worldObjects do nothing
		}


		public PVector getPosition() {
			// TODO Auto-generated method stub
			return position;
		}


}