package savi.jason_processing;
import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;
import savi.StateSynchronization.VisibleItem;
import savi.jason_processing.ROBOT_model.Button;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.infra.centralised.BaseCentralisedMAS;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream; 

public class ROBOT_model extends PApplet {

	/********** CONSTANTS TO BE LOADED FROM CONFIG FILE**********/
	int	NUMBER_TREES;
	int NUMBER_HOUSES;
	int NUMBER_THREATS; 
	int FRAME_RATE;
	int MAX_IN_X_VEL_THREAT;
	int MAX_IN_Y_VEL_THREAT;
	double MAX_SPEED;
	int PERCEPTION_DISTANCE;
	int WIFI_PERCEPTION_DISTANCE;
	int NUMBER_UAS;
	int RANDOM_SEED;
	/********** CONSTANTS THAT CANNOT BE LOADED FROM THE CONF FILE **********/
	int X_PIXELS = 900;
	int Y_PIXELS = 700;
	
	//Load Parameters
	FileInputStream in = null;	
	Properties modelProps = new Properties();
		
		/************* Global Variables *******************/
	int simTime;      // stores simulation time (in seconds) 
	int simTimeDelta; // discrete-time step (in seconds)
	boolean simPaused;// simulation paused or not

	List<WorldObject> objects = new ArrayList<WorldObject>();//List of world objects  
	List<Threat> threats = new ArrayList<Threat>(); //List of threats
	List <UAS> UAS_list = new ArrayList<UAS>(); //List of UAS 

	JasonMAS jasonAgents; // the BDI agents

	Button playButton,stopButton,pauseButton;
	PShape robot,tree,house,threat,play,pause,restart;

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
	/***LOAD SIM PARAMETERS ****/
	try {
		String filePath = new File("").getAbsolutePath();
		filePath = filePath + "/config.cfg";
		System.out.println(filePath);
		File inFile = new File(filePath);
		in = new FileInputStream(inFile);
		modelProps.load(in);
		}
	catch (FileNotFoundException  e) {
		System.out.println("File not found");
	}catch (Exception  e) {
		System.out.println("Exception occurred");
	}
	NUMBER_TREES = Integer.parseInt(modelProps.getProperty("NUMBER_TREES"));
	NUMBER_HOUSES = Integer.parseInt(modelProps.getProperty("NUMBER_HOUSES"));
	NUMBER_THREATS = Integer.parseInt(modelProps.getProperty("NUMBER_TREES"));
	//X_PIXELS = Integer.parseInt(modelProps.getProperty("X_PIXELS"));
	//Y_PIXELS = Integer.parseInt(modelProps.getProperty("Y_PIXELS"));
	FRAME_RATE = Integer.parseInt(modelProps.getProperty("FRAME_RATE"));
	MAX_IN_X_VEL_THREAT = Integer.parseInt(modelProps.getProperty("MAX_IN_X_VEL_THREAT"));
	MAX_IN_Y_VEL_THREAT = Integer.parseInt(modelProps.getProperty("MAX_IN_Y_VEL_THREAT"));
	MAX_SPEED = (double) Integer.parseInt(modelProps.getProperty("MAX_SPEED"));
	PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("PERCEPTION_DISTANCE"));
	WIFI_PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("WIFI_PERCEPTION_DISTANCE"));
	NUMBER_UAS = Integer.parseInt(modelProps.getProperty("NUMBER_UAS"));
	RANDOM_SEED = Integer.parseInt(modelProps.getProperty("RANDOM_SEED"));
	// let's assume a 2D environment
	// Initialization code goes here
	simTime = 0;      // seconds
	simTimeDelta = 1; // seconds
	simPaused = false;// not paused by default  
   
	playButton = new Button("play", width/2-20, 10, 40, 40);
	pauseButton = new Button("pause", width/2-20, 10, 40, 40);
	stopButton = new Button("restart", width/2+20, 10, 40, 40);
	
	play=loadShape("SimImages/play.svg");
	pause=loadShape("SimImages/pause.svg");
	restart=loadShape("SimImages/replay.svg");

	//load images for visualization
	tree=loadShape("SimImages/tree.svg");
	house=loadShape("SimImages/home.svg");
	play=loadShape("SimImages/play.svg");
	pause=loadShape("SimImages/pause.svg");
	restart=loadShape("SimImages/replay.svg");
	threat=loadShape("SimImages/warning.svg");
	
	Random rand = new Random();
	for(int i = 0; i < NUMBER_UAS; i++)  { //Put UAS
		//_PIXELS is the maximum and the 1 is our minimum
		//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
		if(RANDOM_SEED != -1) {
			rand = new Random(RANDOM_SEED+i);
		}
		UAS_list.add(new UAS(Integer.toString(i), "demo", new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1)));
	}    
	for(int i = 0; i < NUMBER_TREES; i++) { //Put trees
		//_PIXELS is the maximum and the 1 is our minimum.
		if(RANDOM_SEED != -1) {
			rand = new Random(2*RANDOM_SEED+i);
		}
		objects.add(new WorldObject(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1), "tree"));
	}
	for(int i = 0; i < NUMBER_HOUSES; i++) { //Put houses
		//_PIXELS is the maximum and the 1 is our minimum.
		if(RANDOM_SEED != -1) {
			rand = new Random(3*RANDOM_SEED+i);
		}
		objects.add(new WorldObject(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1), "house"));
	}
	for(int i = 0; i < NUMBER_THREATS; i++) { //Put threats
		//_PIXELS is the maximum and the 1 is our minimum.
		if(RANDOM_SEED != -1) {
			rand = new Random(4*RANDOM_SEED+i);
		}
		threats.add(new Threat(i, rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, RANDOM_SEED, MAX_SPEED, "threat"));
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

			for(UAS uasi: UAS_list){ //Draw UAS agents
				drawUAS(uasi);
				//uasi.getAgentState().pause(); TODO: why was this pause() called?
			}

			for(int i = 0; i < objects.size(); i++){ //Makes all trees on screen.
			  drawObject(objects.get(i));
			}
		
			for(int i = 0; i < NUMBER_THREATS; i++){ //Makes all trees on screen.
				drawThreat(threats.get(i));
			}
      
			playButton.label="play";

			playButton.drawButton();
			stopButton.drawButton();

			return; // don't change anything if sim is paused
		}
	
		// 1. TIME UPDATE
	simTime += simTimeDelta; // simple discrete-time advance
	// 2. STATE UPDATE (SIMULATION)
	for(UAS uasi:UAS_list){ //Create UAS agents
		//uasi.getAgentState().run();
		uasi.update(PERCEPTION_DISTANCE,WIFI_PERCEPTION_DISTANCE, threats,objects,UAS_list);
	}
	for(int i = 0; i < NUMBER_THREATS; i++){ //Put threats
		threats.get(i).update(width,height, RANDOM_SEED);
	}  
	// 3. VISUALIZATION
	//------------------
	background(240); // white background
	for(int i = 0; i < NUMBER_UAS; i++){ //Draw UAS agents
		drawUAS(UAS_list.get(i));	
	}  
    
	for(int i = 0; i < objects.size(); i++){ //Draw trees.
		drawObject(objects.get(i));
	}
    
	for(int i = 0; i < NUMBER_THREATS; i++) {//Draw Threats.
		drawThreat(threats.get(i));
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
	s=loadShape("SimImages/robot.svg");

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


//Visualize
public void drawThreat(Threat oneThreat){
	// Draw Threat
	stroke(0);
	shapeMode(CENTER);
	
	shape(threat,oneThreat.position.x, oneThreat.position.y,10,10);
}



public void drawObject(WorldObject object){
	// Draw Object
	stroke(0);

	shapeMode(CENTER);
	// translate to center image on sposition.x, position.y
	//s.translate(-s.width/2,-s.height/2);
	
	if(object.type.contentEquals("house")){
		shape(house, object.position.x, object.position.y,25,25);
	}
	if(object.type.contentEquals("tree")){
		shape(tree, object.position.x, object.position.y,15,15);
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

	objects.removeAll(objects);
	threats.removeAll(threats);
	UAS_list.removeAll(UAS_list);
	
	setup();
	// Unpause the simulation =" use other method to ensure that agents are also unpaused
	pauseSimulation();
}
// Pause the simulation
public void pauseSimulation(){
	if(!simPaused) { //the sim is NOT paused and we want to pause it
		
		System.out.println("pausing simulation!-------===================================================");
		for(UAS uasi:UAS_list){ //unpause all agents
			uasi.pauseAgent();			
		}
	} else { //the sim was paused, unpause it
		System.out.println("resuming simulation!-------");
		for(UAS uasi:UAS_list){ //pause all agents
			uasi.unPauseAgent();			
		}	
	}
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


}