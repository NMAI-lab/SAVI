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