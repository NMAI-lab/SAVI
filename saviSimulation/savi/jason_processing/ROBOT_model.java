package savi.jason_processing;
import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;
import savi.jason_processing.ROBOT_model.Button;

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
float MAX_SPEED = 20;
int PERCEPTION_DISTANCE = 300;
int WIFI_PERCEPTION_DISTANCE = 500;
int NUMBER_UAS = 3;


/************* Global Variables *******************/
int simTime;      // stores simulation time (in seconds) 
int simTimeDelta; // discrete-time step (in seconds)
boolean simPaused;// simulation paused or not

List<Tree> trees = new ArrayList<Tree>(); //List of trees
List<House> houses = new ArrayList<House>(); //List of houses
List<Threat> threats = new ArrayList<Threat>(); //List of threats
List <UAS> UAS_list = new ArrayList<UAS>(); //List of UAS 

JasonMAS jasonAgents; // the BDI agent

Button playButton,stopButton,pauseButton;
PShape play,pause,restart;

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
   
	playButton = new Button("play", width/2-20, 10, 40, 40);
	pauseButton = new Button("pause", width/2-20, 10, 40, 40);
	stopButton = new Button("restart", width/2+20, 10, 40, 40);
	
	play=loadShape("play.svg");
	pause=loadShape("pause.svg");
	restart=loadShape("replay.svg");
	
	Random rand = new Random();
	for(int i = 0; i < NUMBER_UAS; i++)  { //Put UAS
		//_PIXELS is the maximum and the 1 is our minimum
		//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
		UAS_list.add(new UAS(Integer.toString(i), "demo", new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1)));
	}    
	for(int i = 0; i < NUMBER_TREES; i++) { //Put trees
		//_PIXELS is the maximum and the 1 is our minimum.
		trees.add(new Tree(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1));
	}
	for(int i = 0; i < NUMBER_HOUSES; i++) { //Put houses
		//_PIXELS is the maximum and the 1 is our minimum.
		houses.add(new House(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1));
	}
	for(int i = 0; i < NUMBER_THREATS; i++) { //Put threats
		//_PIXELS is the maximum and the 1 is our minimum.
		threats.add(new Threat(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, rand.nextInt(MAX_IN_X_VEL_THREAT) + 1, rand.nextInt(MAX_IN_Y_VEL_THREAT) + 1, 1 + rand.nextFloat() * (MAX_SPEED - 1)));
	}
  
	// smoother rendering (optional)
	frameRate(FRAME_RATE); // max 60 draw() calls per real second. (Can make it a larger value for the simulation to go faster)
		// At simTimeDelta=1 this means 1 second
		// of real time = 1 minute of sim time.
		//If the processor is not fast enough to maintain the specified rate, the frame rate will not be achieved 
  
	//======= set up Jason BDI agents ================
	Map<String,AgentModel> agentList = new HashMap<String,AgentModel>();
	for(UAS uas: UAS_list) {//Create UAS agents
		agentList.put(uas.getID(), uas);
	}  
    jasonAgents = new JasonMAS(agentList);
    jasonAgents.startAgents();
    //==========================================
  
}

/************* Main draw() ***********************/
// Main state update and visualization function
// called by Processing in an infinite loop
//************************************************/
public void draw(){
	if(simPaused){
		
		background(240); // white background
		
		for(int i = 0; i < NUMBER_UAS; i++){ //Draw UAS agents
			drawUAS(UAS_list.get(i));
			UAS_list.get(i).getAgentState().pause();
		}

		for(int i = 0; i < NUMBER_TREES; i++){ //Makes all trees on screen.
			trees.get(i).drawTree();
		}
		
		for(int i = 0; i < NUMBER_HOUSES; i++){ //Makes all trees on screen.
			houses.get(i).drawHouse();
		}
		
		for(int i = 0; i < NUMBER_THREATS; i++){ //Makes all trees on screen.
		    threats.get(i).drawThreat();
		}
		
		playButton.label="play";
		
		playButton.drawButton();
		stopButton.drawButton();
		
		return; // don't change anything if sim is paused
	}
	
	// 1. TIME UPDATE
	simTime += simTimeDelta; // simple discrete-time advance
	// 2. STATE UPDATE (SIMULATION)
	for(int i = 0; i < NUMBER_UAS; i++){ //Create UAS agents
		UAS_list.get(i).getAgentState().run();
		UAS_list.get(i).update(PERCEPTION_DISTANCE,WIFI_PERCEPTION_DISTANCE, threats,trees,houses,UAS_list);
	}
	for(int i = 0; i < NUMBER_THREATS; i++){ //Put threats
		threats.get(i).update();
	}  
	// 3. VISUALIZATION
	//------------------
	background(240); // white background
	for(int i = 0; i < NUMBER_UAS; i++){ //Draw UAS agents
		drawUAS(UAS_list.get(i));	
	}  
    for(int i = 0; i < NUMBER_TREES; i++){ //Draw trees.
    	trees.get(i).drawTree();
    }
    for(int i = 0; i < NUMBER_HOUSES; i++){//Draw houses.
    	houses.get(i).drawHouse();
    }
    for(int i = 0; i < NUMBER_THREATS; i++) {//Draw Threats.
	  	threats.get(i).drawThreat();
    }
    
    playButton.label="pause";
	
	playButton.drawButton();
	stopButton.drawButton();
}


public void mousePressed(){
	
	if(playButton.MouseIsOver()){
		pauseSimulation();
	}
	
	if(stopButton.MouseIsOver()){
		resetSimulation();
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
public void resetSimulation(){
	// Set simulation time to zero
	simTime = 0;

	trees.removeAll(trees);
	houses.removeAll(houses);
	threats.removeAll(threats);
  
	setup();
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


//the Button class
public class Button{
	String label; // button label
	float x;      // top left corner x position
	float y;      // top left corner y position
	float w;      // width of button
	float h;      // height of button

	// constructor
	Button(String labelB, float xpos, float ypos, float widthB, float heightB){
		label = labelB;
		x = xpos;
		y = ypos;
		w = widthB;
		h = heightB;
	}

	void drawButton(){
		shapeMode(CORNER);
		
		if(label.contentEquals("play")){
			//s=loadShape("play.svg");
			shape(play, x, y, w, h);
		}
		else if(label.contentEquals("restart")){
			//s=loadShape("replay.svg");
			shape(restart, x, y, w, h);
		}
		else {
			//s=loadShape("pause.svg");
			shape(pause, x, y, w, h);
		}
	}

	boolean MouseIsOver(){
		if (mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h)){
			return true;
		}
		return false;
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
		//shapeMode(CENTER);
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
		//shapeMode(CENTER);
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
		//shapeMode(CENTER);
		shape(s, X, Y,25,25);  
	}	  
}

}