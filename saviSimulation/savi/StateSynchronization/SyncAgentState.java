package savi.StateSynchronization;

import java.util.*;

public class SyncAgentState {
	private PerceptionSnapshot perceptions;		// Perceptions for the agent
	private LinkedList<String> actions;			// Actions that the agent wants to execute
	private Queue<String> msgOut;				// Messages that the agent is sending
	private Queue<String> msgIn;				// Messages for the agent

	private double reasoningCyclePeriod;		// How much time it should take to do a reasoning cycle - meaning that this much
												// time must elapse after a perception is received before a new perception can be received,
												// any actions can be allowed out of the reasoning cycle, and any mail sent
	private double lastTimeStamp;				// Last time stamp for a perception or mail check - used for limiting the reasoning cycles
	
	/**
	 *  Constructor for the SyncAgentState class.
	 */
	public SyncAgentState() {
		this(0);		// Set a default value for reasoningCyclePeriod
	}
	
	
	/**
	 *  Constructor for the SyncAgentState class.
	 */
	public SyncAgentState(double reasoningCyclePeriod) {
		this.perceptions = null;
		this.actions = new LinkedList<String>();
		this.msgOut = new LinkedList<String>();
		this.msgIn = new LinkedList<String>();
		this.reasoningCyclePeriod = reasoningCyclePeriod;
	}
	
	
	/**
	 * Check if the reasoning cycle is complete
	 * Used in order to verify if an action or message is allowed out of the agent
	 * or if the agent can access new perceptions or messages.
	 * @param time
	 * @return
	 */
	public boolean reasoningComplete() {
		if (this.getLatestPerceptionTimeStamp() == -1) {
			return true;
		}
		
		double currentTime = this.getLatestPerceptionTimeStamp();
		double reasoningEnd = this.lastTimeStamp + this.reasoningCyclePeriod;
		
		if (currentTime > reasoningEnd) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Update the perceptions.
	 * @param newSnapsot
	 */
	public synchronized void setPerceptions(PerceptionSnapshot newSnapsot) {
		this.perceptions = new PerceptionSnapshot(newSnapsot);
		notify(); //notifies agent that new perceptions are available.
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
			//while(!this.reasoningComplete());							// Replace this with wait() / notify() technique
			this.lastTimeStamp = this.perceptions.getLatestTimeStamp();
			return new PerceptionSnapshot(this.perceptions);
		}
	}
	
	/**
	 * Get the perceptions. THis version waits until fresh perceptions are available (defined as: their timestamp is different from ' last' ).
	 * @return
	 */
	public synchronized PerceptionSnapshot getPerceptions(double last) {
		while(this.perceptions.getLatestTimeStamp()== last)
			try {
				System.out.println(" Waiting for new perceptions...");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (this.perceptions == null) {
			return null;
		} else {
			//while(!this.reasoningComplete());							// Replace this with wait() / notify() technique
			this.lastTimeStamp = this.perceptions.getLatestTimeStamp();
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
		msgIn.clear();
		return myCopy;
	}


	public synchronized void setMsgOut(String msgOut) {
		//while(!this.reasoningComplete());							// Replace this with wait() / notify() technique
		this.msgOut.add(msgOut);
	}

	public synchronized void setMsgIn(String msgIn) {
		this.msgIn.add(msgIn);
	}

	public synchronized void addAction(String action) {
		//while(!this.reasoningComplete());							// Replace this with wait() / notify() technique
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
