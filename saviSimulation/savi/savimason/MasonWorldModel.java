package savi.savimason;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import processing.core.PApplet;
import savi.commandStation.CommandStationCore;
import savi.jason_processing.SAVIWorld_model;
import savi.jason_processing.SAVI_Processing;
import sim.engine.SimState;

public class MasonWorldModel extends SimState {

	private MasonWrapper model;
	
	private double timestep;
	
	public MasonWorldModel(long seed, SAVIWorld_model world, double timestep) {
		super(seed);
		
		model = new MasonWrapper(world);
		this.timestep = timestep;
		
	}

	public double getTimeStep() {
		return timestep;
	}
	
	public void start() {
			super.start();
			model.reset();
			schedule.scheduleRepeating(model, timestep);
	}

	
	public static void main(String [] args) {
		FileInputStream in = null;
		Properties modelProps = new Properties();
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



		SAVIWorld_model.NUMBER_TREES = Integer.parseInt(modelProps.getProperty("NUMBER_TREES"));
		SAVIWorld_model.NUMBER_HOUSES = Integer.parseInt(modelProps.getProperty("NUMBER_HOUSES"));
		SAVIWorld_model.NUMBER_THREATS = Integer.parseInt(modelProps.getProperty("NUMBER_THREATS"));
		SAVIWorld_model.X_PIXELS = Integer.parseInt(modelProps.getProperty("X_PIXELS"));
		SAVIWorld_model.Y_PIXELS = Integer.parseInt(modelProps.getProperty("Y_PIXELS"));	
		SAVI_Processing.X_PIXELS = SAVIWorld_model.X_PIXELS;
		SAVI_Processing.Y_PIXELS = SAVIWorld_model.Y_PIXELS;
		SAVIWorld_model.MAX_SPEED = (double) Double.parseDouble(modelProps.getProperty("MAX_SPEED"));
		SAVIWorld_model.UAV_PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("UAV_PERCEPTION_DISTANCE"));
		SAVIWorld_model.UGV_PERCEPTION_DISTANCE = Integer.parseInt(modelProps.getProperty("UGV_PERCEPTION_DISTANCE"));
		SAVIWorld_model.RANDOM_SEED = Integer.parseInt(modelProps.getProperty("RANDOM_SEED"));
		SAVIWorld_model.REASONING_CYCLE_PERIOD = (double) Double.parseDouble(modelProps.getProperty("REASONING_CYCLE_PERIOD"));
		SAVIWorld_model.TREE_SIZE = Integer.parseInt(modelProps.getProperty("TREE_SIZE"));
		SAVIWorld_model.HOUSE_SIZE = Integer.parseInt(modelProps.getProperty("HOUSE_SIZE"));
		SAVIWorld_model.THREAT_SIZE = Integer.parseInt(modelProps.getProperty("THREAT_SIZE"));
		SAVIWorld_model.ANTENNA_SIZE = Integer.parseInt(modelProps.getProperty("ANTENNA_SIZE"));
		SAVIWorld_model.NUMBER_UGV = Integer.parseInt(modelProps.getProperty("NUMBER_UGV"));
		SAVIWorld_model.NUMBER_UAV = Integer.parseInt(modelProps.getProperty("NUMBER_UAV"));
		SAVIWorld_model.UGV_SIZE = Integer.parseInt(modelProps.getProperty("UGV_SIZE"));
		SAVIWorld_model.UAV_SIZE = Integer.parseInt(modelProps.getProperty("UAV_SIZE"));
		SAVIWorld_model.SENSORS_ERROR_PROB = (double) Double.parseDouble(modelProps.getProperty("SENSORS_ERROR_PROB"));
		SAVIWorld_model.SENSORS_ERROR_STD_DEV = (double) Double.parseDouble(modelProps.getProperty("SENSORS_ERROR_STD_DEV"));
		SAVIWorld_model.WIFI_ERROR_PROB = (double) Double.parseDouble(modelProps.getProperty("WIFI_ERROR_PROB"));
		SAVIWorld_model.WIFI_PERCEPTION_DISTANCE = Double.parseDouble(modelProps.getProperty("WIFI_PERCEPTION_DISTANCE"));

		SAVIWorld_model world = new SAVIWorld_model();
		
		int FRAME_RATE = Integer.parseInt(modelProps.getProperty("FRAME_RATE"));
		
		String[] appletArgs = new String[] { "savi.jason_processing.SAVI_Processing" };
		//PApplet processingView = new SAVI_Processing(world, FRAME_RATE);
		//processingView.noLoop();
		//PApplet.runSketch(appletArgs, processingView);

		//new CommandStationCore("commander");
		
		
		int jobs = 1; // let’s do 100 runs
		MasonWorldModel masonWorld = new MasonWorldModel(SAVIWorld_model.RANDOM_SEED, world, 1000.0/FRAME_RATE);
		masonWorld.nameThread();
		for(int job = 0; job < jobs; job++)
		{
			masonWorld.setJob(job);
			masonWorld.start();
		do
		if (!masonWorld.schedule.step(masonWorld)) break;
		while(masonWorld.schedule.getSteps() < 5000);
		masonWorld.finish();
		}
		System.exit(0);
	}
}
