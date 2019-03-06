package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public class UxV extends WorldObject {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	protected UxVBehavior uxvBehavior;
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
	public UxV(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image, double reasoningCyclePeriod) {			
		// Initializes UAS as WorldObject
		super(id, pos, pixels, Type, sim, image);
		// Initializes Behaviuor
		this.uxvBehavior = new UxVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod);
	}
	
	@Override
	public void update(double simtime, double timestep, int perceptionDistance, int WIFI_PERCEPTION_DISTANCE,  List<WorldObject> objects) {
		this.uxvBehavior.update(simtime, perceptionDistance, WIFI_PERCEPTION_DISTANCE, objects);
	}
	
	public UxVBehavior getBehavior() {		
		return uxvBehavior;
	}
	
	@Override
	public PVector getPosition() {		
		return this.getBehavior().getPosition();
	}
	
	@Override
	public void draw() {
		PVector p1;
		
		simulator.stroke(0);

		//it's easier to load the image every time to rotate it to the compassAngle
		image=simulator.loadShape("SimImages/robot.svg");
		
		// translate to center image on uasposition.x, uasposition.y
		//	simulator.shapeMode(PConstants.CENTER); didn't work
		image.translate(-image.width/2,-image.height/2);		
		
		// to adjust compassAngle to the image
		image.rotate((float) ((float)this.getBehavior().getCompassAngle()+Math.PI/2));

		//draw image
		simulator.shape(image, this.getBehavior().getPosition().x, this.getBehavior().getPosition().y, 26, 26);

		simulator.noFill();

		//draw perception area
		simulator.arc(this.getBehavior().getPosition().x, this.getBehavior().getPosition().y, simulator.PERCEPTION_DISTANCE*2, simulator.PERCEPTION_DISTANCE*2,(float)this.getBehavior().getCompassAngle()-(float)Math.PI/2, (float)this.getBehavior().getCompassAngle()+(float)Math.PI/2);

		//draw circle on objects percepted
		for(CameraPerception cpi : this.getBehavior().getVisibleItems()){
			double angle = (this.getBehavior().getCompassAngle()+cpi.getParameters().get(0));// % 2* Math.PI;
			double cosv = Math.cos(angle);
			double sinv = Math.sin(angle);
			p1 = new PVector(Math.round(cosv*cpi.getParameters().get(2))+this.getBehavior().getPosition().x, Math.round(sinv*cpi.getParameters().get(2))+this.getBehavior().getPosition().y); 
			// draw circle over items visualized
//			simulator.ellipse(p1.x,p1.y, cpi.getParameters().get(3).floatValue(), cpi.getParameters().get(3).floatValue());
			simulator.ellipse(p1.x,p1.y, 20, 20);
		}
		
		
	}
	
	
}