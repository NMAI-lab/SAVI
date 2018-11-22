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
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ROBOT_model extends PApplet {

/*

Author: Ifeoluwa Oyelowo
#Modified version by Cristina Ruiz
#Modified version by Alan Davoust


*/

/************* Global Variables *******************/
int simTime;      // stores simulation time (in seconds) 
int simTimeDelta; // discrete-time step (in seconds)
boolean simPaused;// simulation paused or not
float collisionRadius; // collision detection radius (km)
//PImage backgroundMap; // satellite image of the area

Aircraft aircraft;  // single aircraft
Aircraft aircraft2;  // single aircraft
UAS uas;  // single UAS
Tree[] trees = new Tree[60];
House[] houses = new House[15];

JasonMAS jasonAgents; // the BDI agent


public void settings() {  size(991,740, P3D);  smooth(8); }

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
  
   // let's assume a 3D environment
  
  // Initialization code goes here
  simTime = 0;      // seconds
  simTimeDelta = 1; // seconds
  simPaused = false;// not paused by default
  
  collisionRadius = 9.26f; // km (9.26 km or 5 knots) ----- We tried 6 km or 3.24 knots but it caused not collisions at all

  
  //ag.run();
  
  
  aircraft = new Aircraft(0, "AircraftFlightData.csv");  // Aircraft's constructor is called
  aircraft2 = new Aircraft(2, "AircraftFlightData2.csv"); 
  //uas = new UAS(0, new PVector(2*width/3,height/3));            // UAS's constructor is called
  uas = new UAS(0, new PVector(width/2,height/2)); 

  for(int i = 0; i < trees.length; i++) //Put trees
  { 
	  int a = (int)(random (991));
	 int b = (int)(random (740));
    trees[i] = new Tree(i, a, b);
  }
  
  for(int i = 0; i < houses.length; i++) //Put trees
  { 
	  int a = (int)(random (991));
	 int b = (int)(random (740));
    houses[i] = new House(i, a, b);
  }
  
  
  //backgroundMap = loadImage("OttawaAirport_v1.PNG"); // load image file
  
        // smoother rendering (optional)
  frameRate(20); // max 60 draw() calls per real second. (Can make it a larger value for the simulation to go faster)
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
  //  2.1 Detect Collision (based on previous timestep)
  //boolean collided = detectCollision(uas.position, aircraft.position, collisionRadius); 
  boolean collided = detectCollision(); 
  
  //  2.2 Aircraft position
  aircraft.update(simTime);
  aircraft2.update(simTime);
  
  
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
  
  //  2.3 UAS position
  uas.update(aircraft2.position);
  
  
  // 3. VISUALIZATION
  //------------------
  background(240); // white background
 // image(backgroundMap,0,0); // sat map
  
  //aircraft.drawPath("curve");
  //aircraft.drawAircraft();
  //aircraft2.drawPath("curve");
  //aircraft2.drawAircraft();
  drawUAS(uas.getPosition(), uas.getCompassAngle());
  for(int i = 0; i < trees.length; i++) //Makes all trees on screen.
  {
    trees[i].drawTree();
  }
  for(int i = 0; i < houses.length; i++) //Makes all trees on screen.
  {
    houses[i].drawHouse();
  }
  // 4. Debugging/Output data
  displayDebugText(collided);
  
  //stop simulation when the aircraft reaches the end of the path
  //We don't care about the UAS because it will go on forever (it doesnt have a destination but is random walking) 
 
    //When aircraft.timeIndex > aircraft.flightPath.length-2, this means that we have simulated the last data point of the aircraft path
    //i.e the data point at flightTime.length - 1 (recall we simulate flightPath[timeIndex+1] when timeIndex = flightTime.length - 2
    
   if((aircraft.timeIndex > aircraft.flightPath.length-3) && (aircraft2.timeIndex > aircraft2.flightPath.length-3)){
      noLoop();
      jasonAgents.stopAgents(); 
   }
   
}


// Visualize
public void drawUAS(PVector uasposition, double compassAngle){
  // Draw collision detection radius
  noStroke();
  fill(220,140,220,40);
  ellipse(uasposition.x, uasposition.y, collisionRadius, collisionRadius);
 
    
  // Draw UAS
  stroke(0);
  //fill(255,255,255);
  //ellipse(uasposition.x, uasposition.y, 13, 13);
  
  PShape s;
  
  //triangle(uasposition.x+10, uasposition.y,uasposition.x,uasposition.y+10,uasposition.x+20,uasposition.y+10);
  s = loadShape("airplane.svg");
  
  //s.scale((float) 0.1);
  //rotate to CompassAngle
  s.rotate((float) ((float) compassAngle+(float)Math.PI/2.0));
  //s.rotate((float) compassAngle);
  //shape(s);
  shape(s, uasposition.x, uasposition.y, 26, 26);
  //triangle(uasposition.x+10, uasposition.y,uasposition.x,uasposition.y+10,uasposition.x+20,uasposition.y+10);
  
  System.out.println("UAS at"+ uasposition.x +","+ uasposition.y);
  
  
  
  
}

//************ UTILITY FUNCTIONS *****************/
// These are general helper functions that don't
// belong to any particular class.
//************************************************/
// Detect collision between a UAS and an Aircraft
public boolean detectCollision(){
  //If the aircraft and UAS are within a collisionRadius distance and their Altitudes are within 200 ft (60.96 m)
  //Note that the Altitude in the data is in feet 
  //(I did not have to scale the altitude here because it is provided in the data so there is no need for it)
  //Then there is a potential collision 
   if ( ((aircraft.position.dist(uas.getPosition()) <= collisionRadius)) || 
         ((aircraft2.position.dist(uas.getPosition()) <= collisionRadius)) ||
         (aircraft2.position.dist(aircraft.position) <= collisionRadius)) {
      return true;
   }else{
      return false;
   }
}


/* Detect collision between positions a and b given a radius
boolean detectCollision(PVector a, PVector b, float radius){
    return (a.dist(b) <= radius);  // returns true or false
}
*/

public void displayDebugText(boolean collided){
    // print() statements are convenient.
  // you could also render text directly onto the screen
  // to avoid repetitive print statements
  fill(0,50); // transparent black
  rect(0,0,250,80);
  fill(200); // white
  textSize(12);
  text("Simulation Time (seconds): " + simTime, 10, 20);
  String formattedTime = ((simTime/3600) % 24) + ":" +  // hour
                         ((simTime/60) % 60)   + ":" +  // min
                         simTime % 60;                  // sec
  text("Simulation Time (h:m:s): " + formattedTime, 10, 40);
  
  if(collided) fill(255,0,0); // red
  text("Collision detected: " + (collided?"Yes":"No"), 10, 60);
  
  surface.setTitle("UAS Simulation | FPS: " + PApplet.parseInt(frameRate)); // show framerate in window title
}

// Reset the simulation
public void resetSimulation (){
  // Set simulation time to zero
  simTime = 0;
  
  // Set all UAS positions to initial
  uas.reset(); 
              
  
  //Reset aircraft position
  aircraft.reset(); 
  aircraft2.reset(); 
               
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

// Handle mouse move
public void mouseMoved(){
  // variables mouseX & mouseY have current mouse position
  // variables pmouseX & pmouseY have previous mouse position
}

// Handle mouse press
public void mousePressed(){
  // mouseButton variable tells you which button was pressed
}

// Handle mouse release
public void mouseReleased(){}

// Handle mouse pressed then released
public void mouseClicked(){}

// Handle mouse drag
public void mouseDragged(){} 
class Aircraft {
  //-----------------------------------------
  // DATA (or state variables)
  //-----------------------------------------
  int ID;
  PVector position;  // 2D: (longitude, latitude)
  PVector velocity; 
  PVector[] flightPath;
  PVector[] randflightPath; //flightpath tracks + random deviation 
                            //add randomness (+/- "randValue") to each point that make up the track
                            //randValue is a value in km
    
  int[] flightTime;       //has the real time for each flight path data point
                            //data will be read from the data file                         
  int timeIndex; //index for PVector flightPath that specifies when each data point was reached.
  float maxSpeed; // the max velocity for the aircraft

  //-----------------------------------------
  // METHODS (functions that act on the data)
  //-----------------------------------------
  // Constructor: called when an object is created using
  //              the "new" keyword. It's the only method
  //              that doesn't have a type (not even void).
  Aircraft(int id, String path) {
    // Initialize data values
    ID = id;
    position = new PVector(); 
    velocity = new PVector(); 
    loadFlightPath(path);
    timeIndex = 0;
    maxSpeed = 0.1f;// the max speed for the aircraft is about 100 m/s ie 0.1 km/s i.e 194.384 knots ( or nautical mile per hour) 
  }
  
  // Loads the flight path from a file
  public void loadFlightPath (String fileName){
    // Read XY coordinates from excel file
    Table table = loadTable(fileName,"header");
  
    //println(table.getRowCount() + " total rows in table"); 
    
    //Initialize flightPath and flightTime
    flightPath = new PVector[table.getRowCount()];
    flightTime = new int[table.getRowCount()];
    
    
    
    int i=0; // row counter
    
    for (TableRow row : table.rows()) {
      flightPath[i] = new PVector();
      // Note that processing's coordinates have the origin in upper left corner,
      // so we might have to flip the y-axis. It all depends on the source flight data
      // 1 pixel = 1 km = 2000 m so divide initial values from the file by 1000
      flightPath[i].x =  row.getFloat("X")/1000.0f;
      flightPath[i].y =  row.getFloat("Y")/1000.0f+100; // flip y axis as source data has origin in the lower left corner
      
      //The time for each flightPath data point is stored in the array flightTime; Time when each XY position in PVector flightPath was reached
      //The time in array flightTime is in seconds
      flightTime[i] = row.getInt("Time");
      
      
         
     // println(flightPath[i].x + " " + flightPath[i].y);
  
      i++;
    }
    
    //create the randflightPath PVector
    randflightPath = new PVector[flightPath.length];
    
    for(int j = 0; j < randflightPath.length; j++){
      
      randflightPath[j] = flightPath[j].copy();
      
      if (!((j == 0) || (j == randflightPath.length - 1))){ //Ensure that the start and end data points are the same as original data
      
      //Assume randValue = 11km for now
      //Track data points have a deviation in the range of +/- randValue 
      //PVector.random2D() is a random float PVector in the range of +/- 1 e.g. (-0.9456181, -0.32527903)
      
        randflightPath[j].add(PVector.mult(PVector.random2D(),11)); //changes random vector direction in 2 ways (+ or -)
        
        //Changes vector direction in 4 ways(++,--,+-,-+)
        //randflightPath[j].add(random(-11,11),random(-11,11));
        
      }
    }
    position = randflightPath[0].copy(); //let the initial position of the aircraft be the 1st data point in randflightPath
  } 
  
  // Update
  public void update(int time){
    
     // The following code uses the time (secs) values in flightTime[]
    int realTime = time ; // int(time*frameRate); //time for each flightPath data point in real time = simulation time / framerate
    
     // Find the flight coords that encompass that time
     //Assume realTime is always between flightTime[timeIndex] and flightTime[timeIndex+1]
    int timeBtwPoints ; //timeBtwPoints = realTime  
    int timeEnd ; //time for the next datapoint
    float timeFraction = 0.0f ;//interpolation fraction (value between 0 and 1). 
 
     timeBtwPoints = realTime;
       
    if(realTime == flightTime[timeIndex] ){
           position = randflightPath[timeIndex].copy();
           //Velocity calculation
           if(timeIndex != 0){ //if timeIndex = 0 , we at the 1st data point and velocity should be (0,0)
             velocity = PVector.sub(position,randflightPath[timeIndex-1]);  
             //limit the velocity to 0.1 km/s as that is more realistic
             velocity = PVector.div(velocity,(realTime-flightTime[timeIndex-1])).limit(maxSpeed);
           }
    }else if (realTime == flightTime[timeIndex+1]){
            position = randflightPath[timeIndex+1].copy();
            //Velocity calculation
           velocity = PVector.sub(position,randflightPath[timeIndex]);  
           //limit the velocity to 0.1 km/s as that is more realistic
           velocity = PVector.div(velocity,(realTime-flightTime[timeIndex])).limit(maxSpeed);
           
           if (timeBtwPoints == flightTime[timeIndex+1] && timeIndex < (flightTime.length - 2)){
            
            //Ensure that the max value of timeIndex is flight.length - 2 to avoid the array out of bounds error
            timeIndex++;
          }
          
          //Increment timeIndex to flightTime.length - 1 at the condition of the if statement
          //This is done so that the we know in the main when to simulate the next flight path
          //Also, it ensures that we simulate the flight path data point at flightTime.length - 1 
          //instead of stopping at the data point at flightTime.length - 2
          if(timeIndex == (flightTime.length - 2) && realTime == flightTime[timeIndex+1]){
              timeIndex++;
          }
     
    }else if (realTime > flightTime[timeIndex] && realTime < flightTime[timeIndex+1]){
      
       
       //scale the difference between flightTime[timeIndex] and timeBtwPoints to a value between 0.0 and 1.0
       // timeBtwPoints is a value between  flightTime[timeIndex] and flightTime[timeIndex+1]
       timeFraction = (timeBtwPoints - flightTime[timeIndex])/PApplet.parseFloat(flightTime[timeIndex+1] - flightTime[timeIndex]) ; 
    
      // Find position by simple linear interpolation (https://processing.org/reference/PVector_lerp_.html)
      position = PVector.lerp(randflightPath[timeIndex], randflightPath[timeIndex+1],timeFraction);
      
        //Velocity calculation
        velocity = PVector.sub(position,randflightPath[timeIndex]);
          //limit the velocity to 0.1 km/s as that is more realistic
        velocity = PVector.div(velocity,(realTime-flightTime[timeIndex])).limit(maxSpeed);
 
     }
    
    //println(velocity.mag());
  }
  
  //reset
  public void reset(){
    position = randflightPath[0].copy(); //aircraft position is now the initial data point of the path
    //position = new PVector(); //aircraft position is now a new PVector (a way for resetting the position)
    
    timeIndex = 0;
  }
  
  // Visualization
  public void drawPath(String type){
    // Set the visual properties of the path (fill and stroke color, etc.)
    noFill(); // no fill
    strokeWeight(5); // thickness 5px
    stroke(100,200,80,160);
    
    if (type=="curve"){
      // Visualize the flight path as a smooth curve (see processing.org/tutorials/curves/)
      beginShape();
      for (int j=0; j<flightPath.length; j++){
        curveVertex(flightPath[j].x, flightPath[j].y);
        if (j==0 || j==flightPath.length-1)  // repeat for first and last vertices
          curveVertex(flightPath[j].x, flightPath[j].y);
      }
      endShape();
    }
     
    else if(type=="line") {
      // Visualize the flight path as a line graph
      for (int j=0; j<flightPath.length-1; j++){
        line(flightPath[j].x, flightPath[j].y, flightPath[j+1].x, flightPath[j+1].y);
      }
    }
    
    // Source data markers
    fill(100,200,80);
    for (int j=0; j<flightPath.length-1; j++){
      ellipse(flightPath[j].x,flightPath[j].y, 7, 7);
    } 
  }
  
  public void drawAircraft(){
    // Draw collision detection radius
    noStroke();
    fill(220,140,220,40);
    ellipse(position.x, position.y, collisionRadius, collisionRadius);
    
    // Draw aircraft
    stroke(0); // black stroke 
    strokeWeight(1); // thickness 1px
    fill(0,250,240); // fill color
    ellipse(position.x, position.y, 13, 13); // draw radius = 10
  }
}

class Tree {
	  //-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------
	  
	  int ID;
	  int X;
	  int Y;
	  //PVector position; // 2D: (longitude, latitude)  
	  
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
	    //position = new PVector(x,y);
	    
	  }	  
	    
	  // Visualize
	  public void drawTree(){	      
	    // Draw Tree
		  PShape s;
		  stroke(0);
		  //triangle(uasposition.x+10, uasposition.y,uasposition.x,uasposition.y+10,uasposition.x+20,uasposition.y+10);
		  s = loadShape("tree.svg");
		  shape(s, X, Y,15,15);
	    
	    //fill(0,225,0);
	    //rect(X, Y, 5, 5);	    
	  }
	  
	}
class House {
	  //-----------------------------------------
	  // DATA (or state variables)
	  //-----------------------------------------
	  
	  int ID;
	  int X;
	  int Y;
	  //PVector position; // 2D: (longitude, latitude)  
	  
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
	    //position = new PVector(x,y);
	    
	  }	  
	    
	  // Visualize
	  public void drawHouse(){	      
	    // Draw Tree
		  PShape s;
		  stroke(0);
		  //triangle(uasposition.x+10, uasposition.y,uasposition.x,uasposition.y+10,uasposition.x+20,uasposition.y+10);
		  s = loadShape("home.svg");
		  shape(s, X, Y,25,25);
	    
	    //fill(0,225,0);
	    //rect(X, Y, 5, 5);	    
	  }
	  
	}

}




