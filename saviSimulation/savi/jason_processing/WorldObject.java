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
		
		int pixels;
			//-----------------------------------------
			// METHODS (functions that act on the data)
			//-----------------------------------------
			// Constructor: called when an object is created using
			//              the "new" keyword. It's the only method
			//              that doesn't have a type (not even void).
		WorldObject(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim) {
			// Initialize data values
			ID = id;
			position=pos;
			type=Type;
			simulator = sim;
			//this.image = image;
			this.pixels = pixels;
		}  
		
		/**
		 * returns the object type: tree, house, UGV, etc.
		 * @return
		 */
		public String getType() {
			return type;
		}
		
		public void update(double simtime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
			// most worldObjects do nothing
		}


		public PVector getPosition() {
			
			return position;
		}
}