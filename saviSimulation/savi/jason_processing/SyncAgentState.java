package savi.jason_processing;

import java.util.*;

import processing.core.PVector;

public class SyncAgentState {


	private Map<String,Double> perceptionData;		// Hash map of the perception parameters
	private long counter;							// Counter used for tracking changes to state data
	private PVector position;						// Vector for the agent's position. TODO: incorporate this into the map somehow
	private boolean pauseSignal;					// For tracking the pause signal
	private ArrayList<VisibleItem> cameraInfo;		// List of camera info
	private ArrayList<String> messages2Share;		// List of camera info
	private ArrayList<String> messagesRead;		// TODO: ID & message

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
		this.messages2Share = new ArrayList<String>();
		this.messagesRead = new ArrayList<String>();

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

	public synchronized ArrayList<String> getMessages2Share() {
		ArrayList<String> myCopy = new ArrayList<String>();
		myCopy.addAll(messages2Share);
		return myCopy;
	}

	public synchronized ArrayList<String> getMessagesRead() {
		ArrayList<String> myCopy = new ArrayList<String>();
		myCopy.addAll(messagesRead);
		return myCopy;
	}

	public synchronized void setCameraInfo(List<VisibleItem> cameraInfo) {
		this.incrementCounter();
		this.cameraInfo.clear();
		this.cameraInfo.addAll(cameraInfo);
	}

	public synchronized void setMessages2Share(List<String> messages2Share) {
		this.incrementCounter();
		this.messages2Share.clear();
		this.messages2Share.addAll(messages2Share);
	}

	public synchronized void setMessagesRead(List<String> messagesRead) {
		this.incrementCounter();
		this.messagesRead.clear();
		this.messagesRead.addAll(messagesRead);
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

	//TODO: use this to pause the agents [not working yet]
	public synchronized void pause() {
		pauseSignal = !pauseSignal;
	}

}
