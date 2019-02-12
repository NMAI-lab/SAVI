package savi.StateSynchronization;

import java.util.*;

public class SyncAgentState {
	private PerceptionSnapshot perceptions;		// Perceptions for the agent
	private LinkedList<String> actions;			// Actions that the agent wants to execute
	private Queue<String> msgOut;				// Messages that the agent is sending
	private Queue<String> msgIn;				// Messages for the agent

	/**
	 *  Constructor for the SyncAgentState class.
	 */
	public SyncAgentState() {
		this.perceptions = null;
		this.actions = new LinkedList<String>();
		this.msgOut = new LinkedList<String>();
		this.msgIn = new LinkedList<String>();
	}
	
	
	/**
	 * Update the perceptions.
	 * @param newSnapsot
	 */
	public synchronized void setPerceptions(PerceptionSnapshot newSnapsot) {
		this.perceptions = new PerceptionSnapshot(newSnapsot);
	}
	
	
	/**
	 * Get the time stamp of the most recent perception in the snapshot
	 * @return
	 */
	public synchronized double getLatestPerceptionTimeStamp() {
		if (this.perceptions == null) {
			return -1;
		} else {
			return this.perceptions.getLatestTimeStamp();
		}
	}
	
	/**
	 * Get the perceptions.
	 * @return
	 */
	public synchronized PerceptionSnapshot getPerceptions() {
		if (this.perceptions == null) {
			return null;
		} else {
			return new PerceptionSnapshot(this.perceptions);
		}
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


	public synchronized void setMsgOut(String msgOut) {
		this.msgOut.add(msgOut);
	}

	public synchronized void setMsgIn(String msgIn) {
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
}
