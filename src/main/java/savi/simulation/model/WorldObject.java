package savi.simulation.model;

import java.util.List;
import java.util.logging.Logger;

import processing.core.PVector;
import savi.simulation.SAVIWorld_model;
import savi.simulation.behaviour.WifiAntenna;

public class WorldObject{
		//-----------------------------------------
		// DATA (or state variables)
		//-----------------------------------------
		protected int ID;
		protected PVector position;
		protected String type;
		protected SAVIWorld_model simulator;
		
		protected static Logger logger = Logger.getLogger(WorldObject.class.getName());
		
		int pixels;
			//-----------------------------------------
			// METHODS (functions that act on the data)
			//-----------------------------------------
			// Constructor: called when an object is created using
			//              the "new" keyword. It's the only method
			//              that doesn't have a type (not even void).
		public WorldObject(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim) {
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
			return this.type;
		}
		
		public void update(double simtime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
			// most worldObjects do nothing
		}


		public PVector getPosition() {
			
			return position;
		}

		public int getPixels() {
			return this.pixels;
		}
}