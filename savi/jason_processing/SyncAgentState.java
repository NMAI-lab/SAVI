package savi.jason_processing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PVector;

public class SyncAgentState {
	
	private PVector position;
	private double speedAngle;
	private double speedValue;
	
	private boolean pauseSignal;
	
	private double compassAngle;
	
	private ArrayList<VisibleItem> cameraInfo;
	
	private LinkedList<String> actions;
	
	public synchronized double getSpeedValue() {
		return speedValue;
	}

	public synchronized double getCompassAngle() {
		return compassAngle;
	}

	public synchronized void setSpeedValue(double speedValue) {
		this.speedValue = speedValue;
	}
	
	public synchronized void setCompassAngle(double compassAngle) {
		this.compassAngle = compassAngle;
	}
	
	/**
	 * returns a shallow copy of the list of visible items that are seen
	 * @return
	 */
	public synchronized ArrayList<VisibleItem> getCameraInfo() {
		ArrayList<VisibleItem> myCopy = new ArrayList<VisibleItem>();
		myCopy.addAll(cameraInfo);
		return myCopy;
	}

	public synchronized void setCameraInfo(List<VisibleItem> cameraInfo) {
		this.cameraInfo.clear();
		this.cameraInfo.addAll(cameraInfo);
	}
	
	public synchronized void addAction(String action) {
		this.actions.add(action);
	}
	
	/**
	 * get next action
	 * @return the next action in the queue (which is removed from the queue)
	 */
	public synchronized String getAction() {
		return this.actions.poll();
	}
	
	/**gets all the actions in the queue (assumption is that all will be processed in single simulation step) 
	 * in future they could have an associated timestamp
	 * */
	public synchronized List<String> getAllActions() {
		List<String> myCopy = new LinkedList<String>();
		myCopy.addAll(this.actions);
		this.actions.clear(); 
		return myCopy;
	}

	
	public synchronized PVector getPosition() {
		return position.copy();
	}
	
	public synchronized void setPosition(PVector position) {
		this.position = position;
	}
	
	public synchronized void setSpeedAngle(double angle) {
		this.speedAngle = angle;
	}
	
	public synchronized double getSpeedAngle() {
		return speedAngle;
	}

	//TODO: use this to pause the agents [not working yet]
	public synchronized void pause() {
		pauseSignal = !pauseSignal;
	}
	
	public SyncAgentState() {
	  cameraInfo = new ArrayList<VisibleItem>(); 
	  actions = new LinkedList<String>();
	  pauseSignal = false;
	  
	}
}
