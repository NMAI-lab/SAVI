package savi.jason_processing;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
//import savi.jason_processing.SAVIWorld_model.Button;
import savi.StateSynchronization.*;
import savi.agentBehaviour.SimpleJasonAgent;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.infra.centralised.BaseCentralisedMAS;

public class SAVIWorld_model {

	/********** CONSTANTS TO BE LOADED FROM CONFIG FILE **********/
	public static int NUMBER_TREES;
	public static int NUMBER_HOUSES;
	public static int NUMBER_THREATS;
	
	public static double MAX_SPEED;
	public static int UGV_PERCEPTION_DISTANCE;
	public static int UAV_PERCEPTION_DISTANCE;
	public static int RANDOM_SEED;
	public static double REASONING_CYCLE_PERIOD;
	public static int TREE_SIZE;
	public static int HOUSE_SIZE;
	public static int THREAT_SIZE;
	public static int ANTENNA_SIZE;
	public static int UAV_SIZE;
	public static int UGV_SIZE;
	public static int NUMBER_UAV;
	public static int NUMBER_UGV;
	public static double SENSORS_ERROR_PROB;
	public static double SENSORS_ERROR_STD_DEV;
	public static double WIFI_PERCEPTION_DISTANCE;
	public static double WIFI_ERROR_PROB;
	
	public static int X_PIXELS;
	public static int Y_PIXELS;
	//int Z_PIXELS = 500;

	// TimeStamp file names
	private long lastCycleTimeStamp;
	private String timeStampFileName;

	private static Logger logger = Logger.getLogger(SAVIWorld_model.class.getName());

	
	/************* Global Variables *******************/
	private double simTime; // stores simulation time (in milliseconds)
	private double simTimeDelta; // discrete-time step (in milliseconds)
	private boolean simPaused;// simulation paused or not

	private List<WorldObject> objects = new ArrayList<WorldObject>();// List of world objects
	FieldAntenna consoleProxy; //this is an antenna placed somewhere in the area that allows the console to communicate with the agents via wifi

	List<WifiAntenna> wifiParticipants = new LinkedList<WifiAntenna>(); // the UAS and the antenna

	private JasonMAS jasonAgents; // the BDI agents
	
	private List<SimView> views = new ArrayList<>();

	
	public void addView(SimView v) {
		views.add(v);
	}
	

	/************* Main setup() ***********************/
// Main system initialization function

//************************************************/
	public SAVIWorld_model() {
		setup();
		}
	
	public void setup() {
		
		simPaused = false;// not paused by default

		
		
		//Load objects on scenario from objects file
		try {
			String filePath = new File("").getAbsolutePath();
			filePath = filePath + "/objects.cfg";
			logger.fine(filePath);
			/// read file as stream:
			Files.lines(Paths.get(filePath)).forEach(strLine -> {
				// do this for each line in file:
				Arrays.stream(strLine.split(",")).mapToInt(Integer::parseInt).toArray();
				  int out[] = Arrays.stream(strLine.split(","))
						  			.mapToInt(Integer::parseInt)
						  			.toArray();

				  			  
				  objects.add(new WorldObject(1, new PVector(out[0], out[1], out[2]/2),
						  out[2], "tree", this));
			});
			
			
			
		} catch (FileNotFoundException e) {
			logger.severe("File not found");
		} catch (Exception e) {
			logger.severe("Exception occurred");
		}
		
		
		Random rand = new Random();
		// ======= Jason BDI agents ================
		Map<String, AgentModel> agentList = new HashMap<String, AgentModel>();
		//====================================================

		if (RANDOM_SEED != -1) {
			rand = new Random(RANDOM_SEED);
		}
		
		for(int i = 0; i < NUMBER_UAV; i++)  { //Put UaV
			//_PIXELS is the maximum and the 1 is our minimum
			//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
			UaV uav = new UaV(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, UAV_SIZE/2), UAV_SIZE, 
					this, REASONING_CYCLE_PERIOD, UAV_PERCEPTION_DISTANCE, SENSORS_ERROR_PROB, SENSORS_ERROR_STD_DEV, WIFI_ERROR_PROB);
			wifiParticipants.add(uav.getAntennaRef());
			objects.add(uav);
			agentList.put(((UxV)uav).getBehavior().getID(), ((UxV)uav).getBehavior());//Create UaV agent
		}
		for(int i = NUMBER_UAV; i < NUMBER_UAV+NUMBER_UGV; i++)  { //Put UgV 
			// The way the for loop is set up is to make sure all the UxVs have different ids
			//_PIXELS is the maximum and the 1 is our minimum
			//TODO: right now agents are initialized with strings "0", "1", "2", ... as identifiers and a fixed type "demo" which matches their asl file name. This should be configurable...
			UgV ugv= new UgV(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, UGV_SIZE/2), UGV_SIZE,
					this, REASONING_CYCLE_PERIOD, UGV_PERCEPTION_DISTANCE, SENSORS_ERROR_PROB, SENSORS_ERROR_STD_DEV, WIFI_ERROR_PROB);
			wifiParticipants.add(ugv.getAntennaRef());
			objects.add(ugv);
			agentList.put(((UxV)ugv).getBehavior().getID(), ((UxV)ugv).getBehavior());//Create UgV agent
		}

		for (int i = 0; i < NUMBER_THREATS; i++) { // Put threats
			// _PIXELS is the maximum and the 1 is our minimum.
			objects.add(new Threat(i, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, THREAT_SIZE/2), RANDOM_SEED, MAX_SPEED,
					THREAT_SIZE, "threat", this));
		}
		
		//set classes values
		WifiAntenna.setPerceptionDistance(WIFI_PERCEPTION_DISTANCE);
		WifiAntenna.setSeed(RANDOM_SEED);
		UxVBehavior.setSeed(RANDOM_SEED);
		
		consoleProxy = new FieldAntenna(NUMBER_UAV+NUMBER_UGV+1, new PVector(rand.nextInt(X_PIXELS) + 1, rand.nextInt(Y_PIXELS) + 1, ANTENNA_SIZE/2), this, ANTENNA_SIZE, WIFI_ERROR_PROB);
		objects.add(consoleProxy);
		logger.fine("console proxy coordinates:"+consoleProxy.position.x+" / "+consoleProxy.position.y);
		wifiParticipants.add(consoleProxy.getAntennaRef());

		// smoother rendering (optional)
		

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
	
	/*private boolean isSpaceOccupied(WorldObject obj, List<WorldObject> objects) {
		return true;
	}*/
	
	
	
	/************* Main update() ***********************/
// Main state update and visualization function
// called by Processing in an infinite loop
	//************************************************/
	public void update(double simTimeDelta) {
		if (simPaused) {

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

		logger.info("== SAVIWorld_Model update() == at:" + simTime);
		// 2. STATE UPDATE (SIMULATION)
		for (WorldObject wo : objects) { // Update all world objects
			wo.update(simTime, simTimeDelta, objects, wifiParticipants);
		}
		// 3. VISUALIZATION
		// ------------------
		//		background(240); // white background

		for (SimView view: views)
			view.drawOnce();
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

			logger.fine("pausing simulation!-------===================================================");
			for (WorldObject wo : objects) { // unpause all agents
				if (wo instanceof UxV) {
					//((UxV) wo).getBehavior().pauseAgent();
				}
			}
		} else { // the sim was paused, unpause it
			logger.fine("resuming simulation!-------");
			for (WorldObject wo : objects) { // pause all agents
				if (wo instanceof UxV) {
					((UxV) wo).getBehavior().unPauseAgent();
				}	
			}
		}
		simPaused = !simPaused;
	}



	public List<WorldObject> getWorldObjects() {
		
		return objects;
	}



}
