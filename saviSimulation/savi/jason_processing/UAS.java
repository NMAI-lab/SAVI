package savi.jason_processing;

import java.util.ArrayList;
import java.util.List;

//import processing.core.PApplet;
import processing.core.PVector;


public class UAS extends AgentModel {
	  private static final double SPEED = 3; //we move by one unit / pixel at each timestep?

	//-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------
	  
	  int ID; 
	  
	  //SyncAgentState agentState; // contains all relevant info = It's in the superclass!
	  
	  PVector initialPosition; // to be able to reset
	  
	  
	  //-----------------------------------------
	  // METHODS (functions that act on the data)
	  //-----------------------------------------
	  // Constructor: called when an object is created using
	  //              the "new" keyword. It's the only method
	  //              that doesn't have a type (not even void).
	  UAS(int id, PVector initialPosition) {
	    // Initialize data values
	    ID = id;
	    
	    agentState = new SyncAgentState();
	    this.initialPosition = initialPosition;
	  
	    PVector position = initialPosition.copy(); //Assume that the initial position is at the center of the display window
	    
	    agentState.setPosition(position); //value type is PVector
	    agentState.setSpeedAngle(0.0); //TODO: calculate velocity angle + magnitude
	    agentState.setSpeedValue(0.0); //TODO
	    //agentState.setCompassAngle(Math.PI); //TODO calculate direction we're facing
	    agentState.setCompassAngle(0);
	    
	    agentState.setCameraInfo(new ArrayList<VisibleItem>()); //TODO: calculate what we can see
	    
	    
	    //maxSpeed = 0.051f; //  km/s  i.e 100 knots
	    //Altitude = 10000 ;//Assume the altitude is in feet
	    
	    //agent architecture
	    //BaseCentralisedMAS.getRunner().setupLogger();
	    
	    //ag = new SimpleJasonAgent(this);
	    
	    
	  }
	 
	  public PVector getPosition() {
		  return agentState.getPosition();
	  }
	  
	  public double getCompassAngle() {
		  return agentState.getCompassAngle();
	  }
	  
	  //// State Update: Read actions from queue, execute them
	  // also includes coordinates of threat.
	  public void update(PVector threat){


		  PVector position = (PVector) agentState.getPosition();
		  double speedValue = agentState.getSpeedValue();
		  double compassAngle = agentState.getCompassAngle(); //TODO for now speedAngle is always zero 
		  
		  List<String> toexec = agentState.getAllActions();
		  
		  System.out.print("---------number of agent actions: "+agentState.getAllActions().size());
		  
		  for (String action : toexec) {
			  System.out.println("UAS doing:"+ action);
			    if (action.equals("turn(left)")) //TODO: make these MOD 2 pi ? 
		        	compassAngle -= Math.PI/16.0;
		        else if (action.equals("turn(right)")) 
		        	compassAngle += Math.PI/16.0;
		        else if (action.equals( "thrust(on)")) 
		        	speedValue = SPEED;
		        else if (action.equals("thrust(off)")) 
		        	speedValue = 0;  
		  }
		  
		  
		  double movingAngle = compassAngle+agentState.getSpeedAngle();
		  
		  
		  double cosv = Math.cos(movingAngle);
		  double sinv = Math.sin(movingAngle);
		  
		  
		  //	TODO: calculate new position
		  PVector temp = new PVector(Math.round(cosv*speedValue), Math.round(sinv*speedValue));
		  position.add(temp);
		  
		  //put info back into agentstate
		  agentState.setPosition(position);
		  agentState.setCompassAngle(compassAngle);
		  agentState.setSpeedValue(speedValue);
		  
		  //		  
		  //}
		  //TODO: calculate what we can see
		 
		  System.out.println("Threat coordinates: ("+threat.x+", "+threat.y+")");
		  
		//get relative position of aircraft to UAS:
		  float deltax = threat.x - getPosition().x;
		  float deltay = threat.y - getPosition().y;
		  
		  //convert to polar
		  double dist     = Math.sqrt(deltax*deltax + deltay*deltay);
		  double theta = Math.atan2(deltay, deltax);
		  double angle = (theta - getCompassAngle()) % 2* Math.PI; //(adjust to 0, 2pi) interval
		  
		  
		  List<VisibleItem> things = new ArrayList<VisibleItem>();
		  
		  if (angle < Math.PI/2. || angle > 3* Math.PI/2.) { 
			//it's visible
			things.add(new VisibleItem("aircraft", angle, dist));  
			  
		  }
		  
		  agentState.setCameraInfo(things); 
	  }
	  
	  
	  
	  
	  
	  // State reset
	  public void reset(){
	     // Initialize data values
	    PVector position = initialPosition.copy(); //Assume that the initial position is at the center of the display window
	    agentState.setPosition(position);
	    agentState.setSpeedValue(0.0);
	    agentState.setCameraInfo(new ArrayList<VisibleItem>());
	  }
	 
	  
	
	 
	}