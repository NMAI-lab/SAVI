package savi.jason_processing;
import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.infra.centralised.BaseCentralisedMAS;

import java.util.ArrayList;
import java.util.Random;
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ROBOT_model extends PApplet {

/********** CONSTANTS **********/
int	NUMBER_TREES = 60;
int NUMBER_HOUSES =15;
int NUMBER_THREATS =10; 
int X_PIXELS = 991;
int Y_PIXELS = 740;
int FRAME_RATE = 20;
int MAX_IN_X_VEL_THREAT = 20;
int MAX_IN_Y_VEL_THREAT = 20;
float MAX_SPEED = 60;

int PERCEPTION_DISTANCE = 300;


/************* Global Variables *******************/
int simTime;      // stores simulation time (in seconds) 
int simTimeDelta; // discrete-time step (in seconds)
boolean simPaused;// simulation paused or not
float collisionRadius; // collision detection radius (km)

UAS uas;  // single UAS
List<Tree> trees = new ArrayList<Tree>(); //List of trees
List<House> houses = new ArrayList<House>(); //List of houses
List<Threat> threats = new ArrayList<Threat>(); //List of threats


JasonMAS jasonAgents; // the BDI agent

public void settings() { size(X_PIXELS,Y_PIXELS, P2D);  smooth(8); } // let's assume a 2D environment

public static void main(String[] passedArgs) {
  String[] appletArgs = new String[] { "savi.jason_processing.ROBOT_model" };
  if (passedArgs != null) {
    PApplet.main(concat(appletArgs, passedArgs));
  } else {
    PApplet.main(appletArgs);
  }
}

/************* Main setup() ***********************/
// Main system initialization function
// called by Processing once on startup
//************************************************/
public void setup() {
  
  // let's assume a 2D environment
  
  // Initialization code goes here
  simTime = 0;      // seconds
  simTimeDelta = 1; // seconds
  simPaused = false;// not paused by default  
  collisionRadius = 9.26f; // km (9.26 km or 5 knots) ----- We tried 6 km or 3.24 knots but it caused not collisions at all
  
  //ag.run();
  
  uas = new UAS(0, new PVector(X_PIXELS/2,Y_PIXELS/2));
  
  Random rand = new Random();
    
  for(int i = 0; i < NUMBER_TREES; i++) //Put trees
  { //_PIXELS is the maximum and the 1 is our minimum.
    trees.add(new Tree(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1));
  }
  for(int i = 0; i < NUMBER_HOUSES; i++) //Put houses
  { //_PIXELS is the maximum and the 1 is our minimum.
    houses.add(new House(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1));
  }
  for(int i = 0; i < NUMBER_THREATS; i++) //Put threats
  { //_PIXELS is the maximum and the 1 is our minimum.
    threats.add(new Threat(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, rand.nextInt(MAX_IN_X_VEL_THREAT) + 1, rand.nextInt(MAX_IN_Y_VEL_THREAT) + 1, 1 + rand.nextFloat() * (MAX_SPEED - 1)));
  }
  
        // smoother rendering (optional)
  frameRate(FRAME_RATE); // max 60 draw() calls per real second. (Can make it a larger value for the simulation to go faster)
                  // At simTimeDelta=1 this means 1 second
                  // of real time = 1 minute of sim time.
                  //If the processor is not fast enough to maintain the specified rate, the frame rate will not be achieved 
  
  //	set up Jason BDI agents ================
  Map<String,AgentModel> agentList = new HashMap<String,AgentModel>();
  agentList.put("demo", uas);
  
  jasonAgents = new JasonMAS(agentList);
  
  jasonAgents.startAgents();
  //==========================================
  
}


/************* Main draw() ***********************/
// Main state update and visualization function
// called by Processing in an infinite loop
//************************************************/
public void draw(){
  if (simPaused) return; // don't change anything if sim is paused
  
  // 1. TIME UPDATE
  simTime += simTimeDelta; // simple discrete-time advance
  
  // 2. STATE UPDATE (SIMULATION)
  //----------------------------- 
  /*
  String dir ="";
  if (abs(deltax)>abs(deltay)){
	 if (deltax>0) 
		 dir = "west";  
	 else
		 dir = "east";
  } else {
	  if (deltay>0) 
			 dir = "north"; 
		 else
			 dir = "south";
	  }
	  
  String percept= "seeaircraft("+dir+")";*/
  
  //  2.3 UAS & THREATS position
  //uas.update(threats.get(1).position);
  uas.update(PERCEPTION_DISTANCE,threats,trees,houses);
  //CRIS: TO PROPERLY DEFINE, JUST TO MAKE SURE IT COMPILE MY CHANGES
  //GUILLERMO: MODIFIED THIS TO PERCEPT treats, trees and houses within the vision angle
  //and a vision distance
  
  for(int i = 0; i < NUMBER_THREATS; i++) //Put threats
  { //_PIXELS is the maximum and the 1 is our minimum.
    threats.get(i).update();
  }  
  
  // 3. VISUALIZATION
  //------------------
  background(240); // white background
  
  drawUAS(uas);
  
  for(int i = 0; i < NUMBER_TREES; i++) //Makes all trees on screen.
  {
    trees.get(i).drawTree();
  }
  for(int i = 0; i < NUMBER_HOUSES; i++) //Makes all trees on screen.
  {
    houses.get(i).drawHouse();
  }
  for(int i = 0; i < NUMBER_THREATS; i++) //Makes all trees on screen.
  {
    threats.get(i).drawThreat();
  }    
}


// Visualize
public void drawUAS(UAS uas){
	
  //PVector uasposition, double compassAngle	
  PVector p1;
	
  // Draw UAS
  stroke(0); 
  
  PShape s;
  s=loadShape("robot.svg");
  
  // translate to center image on uasposition.x, uasposition.y
  s.translate(-s.width/2,-s.height/2);
  
  // to adjust compassAngle to the image
  s.rotate((float) ((float)uas.getCompassAngle()+Math.PI/2));
  
  //draw image
  shape(s, uas.getPosition().x, uas.getPosition().y, 26, 26);
  	  
  noFill();
  
  //draw perception area
  arc(uas.getPosition().x, uas.getPosition().y, PERCEPTION_DISTANCE*2, PERCEPTION_DISTANCE*2,(float)uas.getCompassAngle()-(float)Math.PI/2, (float)uas.getCompassAngle()+(float)Math.PI/2);
  
  //draw circle on objects percepted
  ArrayList<VisibleItem> items = new ArrayList<VisibleItem>();
  
  items = uas.agentState.getCameraInfo(); 
  
  for(int i=0; i< items.size(); i++) {
	  
	  double angle = (uas.getCompassAngle()+items.get(i).getAngle());// % 2* Math.PI;
	  
	  double cosv = Math.cos(angle);
	  double sinv = Math.sin(angle);
  
	  p1 = new PVector(Math.round(cosv*items.get(i).getDistance())+uas.getPosition().x, Math.round(sinv*items.get(i).getDistance())+uas.getPosition().y);
	  
	  // draw circle over items visualized
	  ellipse(p1.x,p1.y, 26, 26);
	  
  }
  
  
}

//************ UTILITY FUNCTIONS *****************/
// These are general helper functions that don't
// belong to any particular class.
//************************************************/
// Reset the simulation
public void resetSimulation (){
  // Set simulation time to zero
  simTime = 0;
  
  // Set all UAS positions to initial
  uas.reset(); 
              
  
  //Reset threats
  for(int i = 0; i < NUMBER_THREATS; i++) //Makes all trees on screen.
  {
    threats.get(i).reset();
  } 
               
  // Unpause the simualtion
  simPaused = false;
}
// Pause the simulation
public void pauseSimulation(){
  simPaused = !simPaused;
}

//************** USER INPUT *****************************/
// These functions handle user input events
// See "Input" subsection in processing.org/reference
//************r*******************************************/
public void keyPressed(){  // handle keyboard input
  switch(key) {
    case 'r': case 'R': resetSimulation(); break; // reset the simulation
    case ' ':           pauseSimulation(); break; // pause the simulation
    
    
    default: break; // ignore any other key presses
  }
}



class Threat {
	  //-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------
	  int ID;
	  PVector position;
	  PVector velocity;     
	  float maxSpeed; 
	  Random rand;

	  //-----------------------------------------
	  // METHODS (functions that act on the data)
	  //-----------------------------------------
	  // Constructor: called when an object is created using
	  //              the "new" keyword. It's the only method
	  //              that doesn't have a type (not even void).
	  Threat(int id, int x, int y, int x_v, int y_v, float MS) {
	    // Initialize data values
	    ID = id;
	    position = new PVector(x,y);
	    velocity = new PVector(x_v, y_v); 
	    maxSpeed = MS;// the max speed 
	    rand = new Random();
	  }
  
  // State Update: Randomly move up, down, left, right, or stay in one place
  void update(){
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
    // Stay within the screen's boundaries
    position.x = constrain(position.x,0,width-1); //constrain: Constrains a value to not exceed a maximum and minimum value.
    position.y = constrain(position.y,0,height-1);      
  }
  
  // State reset
  void reset(){
     // Initialize data values
    position = new PVector(X_PIXELS/2,Y_PIXELS/2); //Assume that the initial position is at the center of the display window
    velocity = new PVector();  // same as new PVector(0,0)
  }
  
  // Visualize
  void drawThreat(){    
    // Draw Threat
	  PShape s;
	  stroke(0);
	  s = loadShape("warning.svg");
	  
	// translate to center image on sposition.x, position.y
	  s.translate(-s.width/2,-s.height/2);
	  
	  shape(s, position.x, position.y,10,10);      
  }
  
}

class Tree {
	  //-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------
	  int ID;
	  int X;
	  int Y;	  
	  //-----------------------------------------
	  // METHODS (functions that act on the data)
	  //-----------------------------------------
	  // Constructor: called when an object is created using
	  //              the "new" keyword. It's the only method
	  //              that doesn't have a type (not even void).
	  Tree(int id, int x, int y) {
	    // Initialize data values
	    ID = id;
	    X = x;
	    Y = y;    
	  }	     
	  // Visualize
	  public void drawTree(){	      
	    // Draw Tree
		  PShape s;
		  stroke(0);
		  s = loadShape("tree.svg");
		  
		// translate to center image on sposition.x, position.y
		  s.translate(-s.width/2,-s.height/2);
		  
		  shape(s, X, Y,15,15);    
	  }	  
	}

class House {
	  //-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------	  
	  int ID;
	  int X;
	  int Y;
	  //-----------------------------------------
	  // METHODS (functions that act on the data)
	  //-----------------------------------------
	  // Constructor: called when an object is created using
	  //              the "new" keyword. It's the only method
	  //              that doesn't have a type (not even void).
	  House(int id, int x, int y) {
	    // Initialize data values
	    ID = id;
	    X = x;
	    Y = y;
	  }	  
	  // Visualize
	  public void drawHouse(){	      
	    // Draw House
		  PShape s;
		  stroke(0);
		  s = loadShape("home.svg");
		  
		// translate to center image on sposition.x, position.y
		  s.translate(-s.width/2,-s.height/2);
		  
		  shape(s, X, Y,25,25);  
	  }	  
	}

}