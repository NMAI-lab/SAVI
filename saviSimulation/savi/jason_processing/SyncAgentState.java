package savi.jason_processing;

import java.util.*;

import processing.core.PVector;

public class SyncAgentState {


	private Map<String,Double> perceptionData;		// Hash map of the perception parameters
	private long counter;							// Counter used for tracking changes to state data
	private PVector position;						// Vector for the agent's position. TODO: incorporate this into the map somehow
	private boolean pauseSignal;					// For tracking the pause signal
	private ArrayList<VisibleItem> cameraInfo;		// List of camera info
	private Queue<String> msgOut;		// List of camera info
	private Queue<String> msgIn;		// TODO: ID & message

	private LinkedList<String> actions;

	/**
	 *  Constructor for the SyncAgentState class.
	 */
	public SyncAgentState() {
		this.counter = 0;
		this.perceptionData = new HashMap<String,Double>();
		this.cameraInfo = new ArrayList<VisibleItem>(); 
		this.actions = new LinkedList<String>();
		this.pauseSignal = false;
		this.msgOut = new LinkedList<String>();
		this.msgIn = new LinkedList<String>();

	}

	/**
	 * Increment the counter - to be done whenever the perception data is updated
	 */
	private synchronized void incrementCounter() {
		// Increment the counter to identify that the perception data was refreshed.
		this.counter++;
		if (this.counter == (Long.MAX_VALUE - 1)) {
			this.counter = 0;
		}
	}

	/**
	 * Get the perception data item that corresponds to the itemKey
	 * @param itemKey	key for the perception data that is sought
	 * @return corresponding perception data
	 */
	public synchronized double getPerceptionDataItem(String itemKey) {
		return perceptionData.get(itemKey);
	}
	/**
	 * Get the current wifi value
	 * @return	double wifi
	 */

	/**
	 * Get the current counter value
	 * @return	int counter
	 */
	public synchronized long getCounter() {
		return this.counter;
	}


	/**
	 * Set the perception data item type
	 * @param itemKey
	 * @param item
	 */
	public synchronized void setPerceptionDataItem(String itemKey, double item) {
		this.incrementCounter();
		this.perceptionData.put(itemKey, item);
	}


	public synchronized double getSpeedValue() {
		return this.getPerceptionDataItem("speedValue");
	}

	public synchronized double getCompassAngle() {
		return this.getPerceptionDataItem("compassAngle");
	}

	public synchronized void setSpeedValue(double speedValue) {
		this.setPerceptionDataItem("speedValue", speedValue);
	}

	public synchronized void setCompassAngle(double compassAngle) {
		this.setPerceptionDataItem("compassAngle", compassAngle);
	}

	/**
	 * returns a shallow copy of the list of visible items that are seen
	 * TODO: Should this be a deep copy?
	 * @return
	 */
	public synchronized ArrayList<VisibleItem> getCameraInfo() {
		ArrayList<VisibleItem> myCopy = new ArrayList<VisibleItem>();
		myCopy.addAll(cameraInfo);
		return myCopy;
	}

	public synchronized String getMsgOut() {
		String msg;
		msg = msgOut.poll();
		return msg;
	}
	public synchronized Queue<String> getMsgOutAll() {
		Queue<String> myCopy = new LinkedList<String>();
		myCopy.addAll(msgOut);
		msgOut.clear();
		return myCopy;
	}
	public synchronized Queue<String> getMsgIn() {
		Queue<String> myCopy = new LinkedList<String>();
		myCopy.addAll(msgIn);
		return myCopy;
	}

	public synchronized void setCameraInfo(List<VisibleItem> cameraInfo) {
		this.incrementCounter();
		this.cameraInfo.clear();
		this.cameraInfo.addAll(cameraInfo);
	}

	public synchronized void setMsgOut(String msgOut) {
		this.incrementCounter();
		this.msgOut.add(msgOut);
	}

	public synchronized void setMsgIn(String msgIn) {
		this.incrementCounter();
		this.msgIn.add(msgIn);
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
		this.incrementCounter();
		this.position = position;
	}

	public synchronized void setSpeedAngle(double speedAngle) {
		this.setPerceptionDataItem("speedAngle", speedAngle);
	}

	public synchronized double getSpeedAngle() {
		return this.getPerceptionDataItem("speedAngle");
	}

	/* Update: pause handled in UAS class that just make the threads wait() then notifies them to unpause.
	//TODO: use this to pause the agents [not working yet]
	public synchronized void pause() {
		pauseSignal = true;
	}
	
	public synchronized void run() {
		pauseSignal = false;
	}
	
	public synchronized boolean checkPause() {
		return this.pauseSignal;
	}*/

}
