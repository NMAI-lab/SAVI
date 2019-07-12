package savi.simulation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import processing.core.PApplet;
import savi.commandStation.CommandStationCore;
import savi.simulation.processing.SAVI_Processing;
import savi.util.ResourceManager;

public class SAVI_Controller {
		
	private static Logger logger = Logger.getLogger(SAVI_Controller.class.getName());
	
	public static void main(String [] args) {
		
			
		InputStream in = null;
		Properties modelProps = new Properties();
		
		/*** LOAD SIM PARAMETERS ****/
		try {

			in = ResourceManager.getResourceStream("/config.cfg");
			modelProps.load(in);
		} catch (FileNotFoundException e) {
			logger.fine("File not found");
		} catch (Exception e) {
			logger.fine("Exception occurred");
			System.out.println();
		}
		
		
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
		String[] appletArgs = new String[] { "savi.simulation.processing.SAVI_Processing" };
		PApplet processingView = new SAVI_Processing(world, FRAME_RATE);
		//processingView.noLoop();
		PApplet.runSketch(appletArgs, processingView);
		
		new CommandStationCore("commander");
		
	}

}
