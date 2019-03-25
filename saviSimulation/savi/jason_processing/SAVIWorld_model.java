package savi.jason_processing;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import savi.jason_processing.SAVIWorld_model.Button;
import savi.StateSynchronization.*;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.infra.centralised.BaseCentralisedMAS;

public class SAVIWorld_model extends PApplet {

	/********** CONSTANTS TO BE LOADED FROM CONFIG FILE **********/
	private int NUMBER_TREES;
	private int NUMBER_HOUSES;
	private int NUMBER_THREATS;
	private int FRAME_RATE;
	private double MAX_SPEED;
	private int UGV_PERCEPTION_DISTANCE;
	private int UAV_PERCEPTION_DISTANCE;
	private int RANDOM_SEED;
	private double REASONING_CYCLE_PERIOD;
	private int TREE_SIZE;
	private int HOUSE_SIZE;
	private int THREAT_SIZE;
	private int ANTENNA_SIZE;
	private int UAV_SIZE;
	private int UGV_SIZE;
	private int NUMBER_UAV;
	private int NUMBER_UGV;
	private double SENSORS_ERROR_PROB;
	private double SENSORS_ERROR_STD_DEV;
	private double WIFI_PERCEPTION_DISTANCE;
	private double WIFI_ERROR_PROB;
	/********** CONSTANTS THAT CANNOT BE LOADED FROM THE CONF FILE **********/
	public final int X_PIXELS = 874;
	public final int Y_PIXELS = 699;
	//int Z_PIXELS = 500;

	// TimeStamp file names
	private long lastCycleTimeStamp;
	private String timeStampFileName;

	private static Logger logger = Logger.getLogger(SAVIWorld_model.class.getName());

	// Load Parameters
	private FileInputStream in = null;
	private Properties modelProps = new Properties();

	/************* Global Variables *******************/
	private double simTime; // stores simulation time (in milliseconds)
	private double simTimeDelta; // discrete-time step (in milliseconds)
	private boolean simPaused;// simulation paused or not

	private List<WorldObject> objects = new ArrayList<WorldObject>();// List of world objects
	FieldAntenna consoleProxy; //this is an antenna placed somewhere in the area that allows the console to communicate with the agents via wifi

	List<WifiAntenna> wifiParticipants = new LinkedList<WifiAntenna>(); // the UAS and the antenna

	private JasonMAS jasonAgents; // the BDI agents

	private Button playButton, stopButton;
	private PShape ugvImage, treeImage, houseImage, threatImage, play, pause, restart, antennaImage, uavImage;
	private PImage backGround;
	
	public void settings() {
		size(X_PIXELS, Y_PIXELS, P3D);
		smooth(8);
	} // 3D environment

	public static void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "savi.jason_processing.SAVIWorld_model" };
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
		/*** LOAD SIM PARAMETERS ****/
		try {
			String filePath = new File("").getAbsolutePath();
			filePath = filePath + "/config.cfg";
			System.out.println(filePath);
			File inFile = new File(filePath);
			in = new FileInputStream(inFile);
			modelProps.load(in);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (Exception e) {
			System.out.println("Exception occurred");
		}
		NUMBER_TREES = Integer.parseInt(modelProps.getProperty("NUMBER_TREES"));
		NUMBER_HOUSES = Integer.parseInt(modelProps.getProperty("NUMBER_HOUSES"));
		NUMBER_THREATS = Integer.parseInt(modelProps.getProperty("NUMBER_THREATS"));
		// X_PIXELS = Integer.parseInt(modelProps.getProperty("X_PIXELS"));
		// Y_PIXELS = Integer.parseInt(modelProps.getProperty("Y_PIXELS"));
		FRAME_RATE = Integer.parseInt(modelProps.getProperty("FRAME_RATE"));
		MAX_SPEED = (double) Double.parseDouble(modelProps.getProperty("MAX_SPEED"));
		UAV_PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("UAV_PERCEPTION_DISTANCE"));
		UGV_PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("UGV_PERCEPTION_DISTANCE"));
		RANDOM_SEED = Integer.parseInt(modelProps.getProperty("RANDOM_SEED"));
		REASONING_CYCLE_PERIOD = (double) Double.parseDouble(modelProps.getProperty("REASONING_CYCLE_PERIOD"));
		TREE_SIZE = Integer.parseInt(modelProps.getProperty("TREE_SIZE"));
		HOUSE_SIZE = Integer.parseInt(modelProps.getProperty("HOUSE_SIZE"));
		THREAT_SIZE = Integer.parseInt(modelProps.getProperty("THREAT_SIZE"));
		ANTENNA_SIZE = Integer.parseInt(modelProps.getProperty("ANTENNA_SIZE"));
		NUMBER_UGV = Integer.parseInt(modelProps.getProperty("NUMBER_UGV"));
		NUMBER_UAV = Integer.parseInt(modelProps.getProperty("NUMBER_UAV"));
		UGV_SIZE = Integer.parseInt(modelProps.getProperty("UGV_SIZE"));
		UAV_SIZE = Integer.parseInt(modelProps.getProperty("UAV_SIZE"));
		SENSORS_ERROR_PROB = (double) Double.parseDouble(modelProps.getProperty("SENSORS_ERROR_PROB"));
		SENSORS_ERROR_STD_DEV = (double) Double.parseDouble(modelProps.getProperty("SENSORS_ERROR_STD_DEV"));
		WIFI_ERROR_PROB = (double) Double.parseDouble(modelProps.getProperty("WIFI_ERROR_PROB"));
		WIFI_PERCEPTION_DISTANCE = Double.parseDouble(modelProps.getProperty("WIFI_PERCEPTION_DISTANCE"));
		// Initialization code goes here
		simTime = 0; // seconds
		simTimeDelta = 1000 / FRAME_RATE; // milliseconds
		simPaused = false;// not paused by default

		playButton = new Button("play", width / 2 - 20, 10, 40, 40);
		stopButton = new Button("restart", width / 2 + 20, 10, 40, 40);

		play = loadShape("SimImages/play.svg");
		pause = loadShape("SimImages/pause.svg");

		// load images for visualization
		restart = loadShape("SimImages/replay.svg");
		treeImage = loadShape("SimImages/tree.svg");
		houseImage = loadShape("SimImages/home.svg");
		play = loadShape("SimImages/play.svg");
		pause = loadShape("SimImages/pause.svg");
		restart = loadShape("SimImages/replay.svg");
		threatImage = loadShape("SimImages/warning.svg");
		ugvImage = loadShape("SimImages/robot.svg");
		uavImage = loadShape("SimImages/airplane.svg");
		antennaImage = loadShape("SimImages/antenna.svg");

		backGround = loadImage("SimImages/OttawaAirport_874_699.PNG");
		
		Random rand = new Random();
		// ======= Jason BDI agents ================
		Map<String, AgentModel> agentList = new HashMap<String, AgentModel>();
		//====================================================
				
		for(int i = 0; i < NUMBER_UAV; i++)  { //Put UaV
			//_PIXELS is the maximum and the 1 is our minimum
			//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
			if(RANDOM_SEED != -1) {
				rand = new Random(RANDOM_SEED+i);
			}	
				UaV uav = new UaV(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, UAV_SIZE/2), UAV_SIZE,"demo", 
						this, uavImage, REASONING_CYCLE_PERIOD, "drone", UAV_PERCEPTION_DISTANCE, SENSORS_ERROR_PROB, SENSORS_ERROR_STD_DEV, WIFI_ERROR_PROB);
				wifiParticipants.add(uav.getAntennaRef());
				objects.add(uav);
				agentList.put(((UxV)uav).getBehavior().getID(), ((UxV)uav).getBehavior());//Create UaV agent
			}
		for(int i = NUMBER_UAV; i < NUMBER_UAV+NUMBER_UGV; i++)  { //Put UgV 
			// The way the for loop is set up is to make sure all the UxVs have different ids
			//_PIXELS is the maximum and the 1 is our minimum
			//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
			if(RANDOM_SEED != -1) {
				rand = new Random(RANDOM_SEED+i);
			}	
				UgV ugv= new UgV(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, UGV_SIZE/2), UGV_SIZE,"demo",
						this, ugvImage, REASONING_CYCLE_PERIOD, "robot", UGV_PERCEPTION_DISTANCE, SENSORS_ERROR_PROB, SENSORS_ERROR_STD_DEV, WIFI_ERROR_PROB);
				wifiParticipants.add(ugv.getAntennaRef());
				objects.add(ugv);
				agentList.put(((UxV)ugv).getBehavior().getID(), ((UxV)ugv).getBehavior());//Create UgV agent
		}
		for (int i = 0; i < NUMBER_TREES; i++) { // Put trees
			// _PIXELS is the maximum and the 1 is our minimum.
			if (RANDOM_SEED != -1) {
				rand = new Random(2 * RANDOM_SEED + i);
			}
			objects.add(new WorldObject(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, TREE_SIZE/2),
					TREE_SIZE, "tree", this, treeImage));
		}
			// _PIXELS is the maximum and the 1 is our minimum.
		for (int i = 0; i < NUMBER_HOUSES; i++) { // Put houses
			if (RANDOM_SEED != -1) {
				rand = new Random(3 * RANDOM_SEED + i);
			}
			objects.add(new WorldObject(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, HOUSE_SIZE/2),
					HOUSE_SIZE, "house", this, houseImage));
		}
		for (int i = 0; i < NUMBER_THREATS; i++) { // Put threats
			// _PIXELS is the maximum and the 1 is our minimum.
			if (RANDOM_SEED != -1) {
				rand = new Random(4 * RANDOM_SEED + i);
			}
			objects.add(new Threat(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, THREAT_SIZE/2), RANDOM_SEED, MAX_SPEED,
					THREAT_SIZE, "threat", this, threatImage));
		}
		
		//set classes values
		WifiAntenna.setPerceptionDistance(WIFI_PERCEPTION_DISTANCE);
		WifiAntenna.setSeed(RANDOM_SEED);
		UxVBehavior.setSeed(RANDOM_SEED);
		
		consoleProxy = new FieldAntenna(NUMBER_UAV+NUMBER_UGV+1, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, ANTENNA_SIZE/2), this, ANTENNA_SIZE, antennaImage, WIFI_ERROR_PROB);
		objects.add(consoleProxy);
		wifiParticipants.add(consoleProxy.getAntennaRef());

		// smoother rendering (optional)
		frameRate(FRAME_RATE); // max 60 draw() calls per real second. (Can make it a larger value for the
								// simulation to go faster)
		// simTimeDelta is now 1000/FRAME_RATE, meaning the simulation is in real-time
		// if the processor can manage it.

		// If the processor is not fast enough to maintain the specified rate, the frame
		// rate will not be achieved

		// ======= set up Jason BDI agents ================
		jasonAgents = new JasonMAS(agentList);
		jasonAgents.startAgents();		
		// ==========================================
		// Set up the cycle length logfile
		this.timeStampFileName = "SimulationTimeStamps.log";
		this.lastCycleTimeStamp = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.timeStampFileName));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}		
	/************* Main draw() ***********************/
// Main state update and visualization function
// called by Processing in an infinite loop
//************************************************/
	public void draw() {
		if (simPaused) {
		//	background(240); // white background
	//		bg.resize(X_PIXELS, Y_PIXELS);
		background(backGround);

			for (WorldObject wo : objects) { // Makes all objects on screen.
				wo.draw(wo.position);
			}

			playButton.label = "play";

			playButton.drawButton();
			stopButton.drawButton();

			return; // don't change anything if sim is paused
		}

		// 1. TIME UPDATE
		simTime += simTimeDelta; // simple discrete-time advance

		long currentSystemTime = System.currentTimeMillis();
		long simulationCycleTime = currentSystemTime - this.lastCycleTimeStamp;
		this.lastCycleTimeStamp = currentSystemTime;

		// Write the timestamp to the timestamp logfile
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.timeStampFileName, true));
			writer.append((new Long(simulationCycleTime)).toString());
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("== SAVIWorld_Model draw() == at:" + simTime);
		// 2. STATE UPDATE (SIMULATION)
		for (WorldObject wo : objects) { // Update threats
			wo.update(simTime, simTimeDelta, objects, wifiParticipants);
		}
		// 3. VISUALIZATION
		// ------------------
//		background(240); // white background
		background(backGround);
		
		for (WorldObject wo : objects) { // Makes all objects on screen.
			wo.draw(wo.position);
		}

		playButton.label = "pause";

		playButton.drawButton();
		stopButton.drawButton();
	}

	public void mousePressed() {

		if (playButton.MouseIsOver()) {
			pauseSimulation();
		}

		if (stopButton.MouseIsOver()) {
			resetSimulation();
		}
	}

//************ UTILITY FUNCTIONS *****************/
// These are general helper functions that don't
// belong to any particular class.
//************************************************/
// Reset the simulation
	public void resetSimulation() {
		// Set simulation time to zero
		simTime = 0;

		objects.removeAll(objects);

		setup();
		// Unpause the simulation =" use other method to ensure that agents are also
		// unpaused
		pauseSimulation();
	}

// Pause the simulation
	public void pauseSimulation() {
		if (!simPaused) { // the sim is NOT paused and we want to pause it

			System.out.println("pausing simulation!-------===================================================");
			for (WorldObject wo : objects) { // unpause all agents
				if (wo instanceof UxV) {
					//((UxV) wo).getBehavior().pauseAgent();
				}
			}
		} else { // the sim was paused, unpause it
			System.out.println("resuming simulation!-------");
			for (WorldObject wo : objects) { // pause all agents
				if (wo instanceof UxV) {
					((UxV) wo).getBehavior().unPauseAgent();
				}	
			}
		}
		simPaused = !simPaused;
	}

//************** USER INPUT *****************************/
// These functions handle user input events
// See "Input" subsection in processing.org/reference
//************r*******************************************/
	public void keyPressed() { // handle keyboard input
		switch (key) {
		case 'r':
		case 'R':
			resetSimulation();
			break; // reset the simulation
		case ' ':
			pauseSimulation();
			break; // pause the simulation
		default:
			break; // ignore any other key presses
		}
	}

//the Button class
	public class Button {
		String label; // button label
		float x; // top left corner x position
		float y; // top left corner y position
		float w; // width of button
		float h; // height of button

		// constructor
		Button(String labelB, float xpos, float ypos, float widthB, float heightB) {
			label = labelB;
			x = xpos;
			y = ypos;
			w = widthB;
			h = heightB;
		}

		void drawButton() {
			shapeMode(CORNER);

			if (label.contentEquals("play")) {
				// s=loadShape("play.svg");
				shape(play, x, y, w, h);
			} else if (label.contentEquals("restart")) {
				// s=loadShape("replay.svg");
				shape(restart, x, y, w, h);
			} else {
				// s=loadShape("pause.svg");
				shape(pause, x, y, w, h);
			}
		}

		boolean MouseIsOver() {
			if (mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h)) {
				return true;
			}
			return false;
		}

	}

}
