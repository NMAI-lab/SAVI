package savi.jason_processing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import savi.StateSynchronization.CameraPerception;

public class SAVI_Processing extends PApplet implements SimView {
	
	/********** CONSTANTS THAT CANNOT BE LOADED FROM THE CONF FILE **********/
	public static int X_PIXELS;
	public static int Y_PIXELS;
	
	public static int FRAME_RATE;

	
	private SAVIWorld_model worldModel;
	
	private Map<String,PShape> modelImages;
	
	private List<String> visibleThings = Arrays.asList(new String [] {"threat", "FieldAntenna"});
	
	private Button playButton, stopButton;
	private PShape  play, pause, restart;
	private PImage backGround;

	
	public SAVI_Processing(SAVIWorld_model world, int frame_rate) {
		setWorldModel(world);
		FRAME_RATE=frame_rate;
	}

	public void settings() {
		size(X_PIXELS, Y_PIXELS, P3D);
		smooth(8);
	} 
	
	public void setWorldModel(SAVIWorld_model wm) {
		worldModel = wm;
		worldModel.addView(this);
	}
	
	
	public void setup() {
		frameRate(FRAME_RATE); // max 60 draw() calls per real second. (Can make it a larger value for the
		// simulation to go faster)
// simTimeDelta is now 1000/FRAME_RATE, meaning the simulation is in real-time
// if the processor can manage it.

// If the processor is not fast enough to maintain the specified rate, the frame
// rate will not be achieved
		playButton = new Button("play", width / 2 - 20, 10, 40, 40);
		stopButton = new Button("restart", width / 2 + 20, 10, 40, 40);

		play = loadShape("SimImages/play.svg");
		pause = loadShape("SimImages/pause.svg");
		restart = loadShape("SimImages/replay.svg");
		
		// load images for visualization
		
		modelImages = new HashMap<>();
		
		
		modelImages.put("tree", loadShape("SimImages/tree.svg"));
		modelImages.put("house", loadShape("SimImages/home.svg"));
		modelImages.put("threat", loadShape("SimImages/warning.svg"));
		modelImages.put("ugv", loadShape("SimImages/robot3.svg"));
		modelImages.put("uav", loadShape("SimImages/drone.svg"));
		modelImages.put("FieldAntenna", loadShape("SimImages/antenna.svg"));

		backGround = loadImage("SimImages/Major'sHillPark3D.jpg");
		
	}
	
	/**
	 * this actually drives the simulation
	 */
	public void draw() {
		worldModel.update(1000.0/FRAME_RATE);
		
	}
	
	public void drawOnce(boolean paused) {
		
		
		background(backGround);
		if (worldModel ==null) {
			return;
		}

		for (WorldObject wo : worldModel.getWorldObjects()) { // Makes all objects on screen.
			drawWorldObject(wo);
		}

		if(paused) {

			playButton.label = "play";

			playButton.drawButton();
			stopButton.drawButton();
		}else {
			playButton.label = "pause";

			playButton.drawButton();
			stopButton.drawButton();
		}
	}
	
	
	public void drawWorldObject(WorldObject wo) {
		
		if (wo instanceof UxV) {
			UxV uxv = (UxV) wo;
			
			PVector p1;
			stroke(0);

				//it's easier to load the image every time to rotate it to the compassAngle
			PShape image= modelImages.get(uxv.getType());
				
				// translate to center image on uasposition.x, uasposition.y
//					simulator.shapeMode(PConstants.CENTER);// didn't work
			image.translate(-image.width/2,-image.height/2);		
				
				// to adjust compassAngle to the image
			image.rotate((float) ((float)uxv.getBehavior().getCompassAngle()+Math.PI/2));

				//draw image
			shape(image, wo.position.x+wo.pixels/2, wo.position.y+wo.pixels/2, wo.pixels, wo.pixels);
			
			//undo rotation and translation
			image.rotate((-1)* (float) ((float)uxv.getBehavior().getCompassAngle()+Math.PI/2));
			image.translate(image.width/2,image.height/2);		
			
			text(Double.toString(wo.position.z+(wo.pixels/2))+"\n"+Double.toString(wo.position.z-(wo.pixels/2)), wo.position.x, wo.position.y);
			noFill();

				//draw perception area
			drawPerceptionArea(uxv);

				//draw circle on objects perceived
				double azimuth, elevation, perception, range, diam;
				for(CameraPerception cpi : uxv.getBehavior().getVisibleItems()){
					azimuth = cpi.getParameters().get(0);
					elevation = cpi.getParameters().get(1);
					range = cpi.getParameters().get(2);
					diam = cpi.getParameters().get(3);
					p1 = Geometry.absolutePositionFromPolar(azimuth, elevation, range, uxv.position, uxv.getBehavior().getCompassAngle());
					//double horiz_angle = (uxv.getBehavior().getCompassAngle()+cpi.getParameters().get(0));// % 2* Math.PI;
					//double cosv = Math.cos(horiz_angle);
					//double sinv = Math.sin(horiz_angle);
					//p1 = new PVector(Math.round(cosv*cpi.getParameters().get(2))+uxv.position.x, Math.round(sinv*cpi.getParameters().get(2))+uxv.position.y);
					// draw circle over items visualized
					ellipse(p1.x,p1.y, (float) diam*2, (float) diam*2);
				}
			}
		else {
		stroke(0);

		shapeMode(PConstants.CENTER);
		//simulator.shape(this.image, this.position.x, this.position.y,pixels,pixels);
		//show height lower and upper
		text(Double.toString(wo.position.z+(wo.pixels/2))+"\n"+Double.toString(wo.position.z-(wo.pixels/2)), wo.position.x, wo.position.y);
		
		if(visibleThings.contains(wo.getType())) {
			shape(modelImages.get(wo.getType()), wo.position.x, wo.position.y,wo.pixels,wo.pixels);

		}
		}
	}

	public void drawPerceptionArea(UxV uxv) {
		// calculate the projection of the perception distance on the ground (important for UAV)
		float groundPerceptionDistance = (float) Math.sqrt(uxv.perceptionDistance*uxv.perceptionDistance - uxv.position.z*uxv.position.z);
		arc(uxv.position.x, uxv.position.y, groundPerceptionDistance*2, groundPerceptionDistance*2, (float)(uxv.getBehavior().getCompassAngle()) - uxv.perceptionAngle/2, (float)(uxv.getBehavior().getCompassAngle()) + uxv.perceptionAngle/2);
	}

	
	/*public static void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "savi.jason_processing.SAVI_Processing" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}*/
	
	public void mousePressed() {

		if (playButton.MouseIsOver()) {
			worldModel.pauseSimulation();
		}

		if (stopButton.MouseIsOver()) {
			worldModel.resetSimulation();
		}
	}
	
	//************** USER INPUT *****************************/
	// These functions handle user input events
	// See "Input" subsection in processing.org/reference
	//************r*******************************************/
		public void keyPressed() { // handle keyboard input
			switch (key) {
			case 'r':
			case 'R':
				worldModel.resetSimulation();
				break; // reset the simulation
			case ' ':
				worldModel.pauseSimulation();
				break; // pause/unpause the simulation
			default:
				break; // ignore any other key presses
			}
		}
	
	
	
	//the Button class
	class Button {
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
