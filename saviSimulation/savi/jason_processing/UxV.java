package savi.jason_processing;

import java.util.*;

import processing.core.*;

import processing.data.*;
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.*;


public abstract class UxV extends WorldObject implements Communicator {
	private static final double SPEED = 0.1; // 0.1 pixels (whatever real-life distance this corresponds to)

	//-----------------------------------------
	// DATA (or state variables)
	//-----------------------------------------  
	protected WifiAntenna wifiAntenna;
	protected UxVBehavior uxvBehavior;
	protected String imageName;
	protected int perceptionDistance;
	private static Random rand = new Random();
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
	public UxV(int id, PVector pos, int pixels, String Type, SAVIWorld_model sim, PShape image, double reasoningCyclePeriod, String imageName, int perceptionDistance, double sensorsErrorProb, double sensorsErrorStdDev, double probWifiFailure) {			
		// Initializes UAS as WorldObject
		super(id, pos, pixels, Type, sim, image);
		this.imageName=imageName;
		// Initializes Behaviuor
		//this.uxvBehavior = new UxVBehavior(Integer.toString(id), type, pos, reasoningCyclePeriod);
		this.wifiAntenna = new WifiAntenna (id, this, probWifiFailure);
		this.perceptionDistance=perceptionDistance;
	}
	
	@Override
	public void update(double simtime, double timestep, List<WorldObject> objects, List<WifiAntenna> wifiParticipants) {
		this.uxvBehavior.update(this, simtime, this.perceptionDistance, objects);
		this.wifiAntenna.update(wifiParticipants);
	}
	
	public UxVBehavior getBehavior() {		
		return uxvBehavior;
	}
	
	public void setPosition(PVector pos) {		
		this.position = pos;
	}
	
	@Override
	public void draw(PVector position) {
		PVector p1;
		simulator.stroke(0);

		//it's easier to load the image every time to rotate it to the compassAngle
		image=simulator.loadShape("SimImages/"+imageName+".svg");
		
		// translate to center image on uasposition.x, uasposition.y
		//	simulator.shapeMode(PConstants.CENTER); didn't work
		image.translate(-image.width/2,-image.height/2);		
		
		// to adjust compassAngle to the image
		image.rotate((float) ((float)this.getBehavior().getCompassAngle()+Math.PI/2));

		//draw image
		simulator.shape(image, position.x, position.y, pixels, pixels);
		//show height lower and upper
		simulator.text(Double.toString(getBehavior().compasAngle)+"\n"+Double.toString(position.z-(this.pixels/2)), position.x, position.y);
		simulator.noFill();

		//draw perception area
		drawPerceptionArea();

		//draw circle on objects percepted
		for(CameraPerception cpi : this.getBehavior().getVisibleItems()){
			double angle = (this.getBehavior().getCompassAngle()+cpi.getParameters().get(0));// % 2* Math.PI;
			double cosv = Math.cos(angle);
			double sinv = Math.sin(angle);
			p1 = new PVector(Math.round(cosv*cpi.getParameters().get(2))+this.position.x, Math.round(sinv*cpi.getParameters().get(2))+this.position.y);
			// draw circle over items visualized
			simulator.ellipse(p1.x,p1.y, cpi.getParameters().get(3).floatValue()*2, cpi.getParameters().get(3).floatValue()*2);
		}
	}
	
	
	public abstract void drawPerceptionArea();
	
	
	@Override
	public  List<String> getOutgoingMessages(){
		
		List<String> myMsgOutCopy = new LinkedList<String>();
		myMsgOutCopy.addAll(getBehavior().agentState.getMsgOutAll());
		
		return myMsgOutCopy;
	}

	@Override
	public void receiveMessage(String msg) {
		getBehavior().agentState.setMsgIn(msg);			
		
	}

	@Override
	public WifiAntenna getAntennaRef() {		
		return this.wifiAntenna;
	}
	
}