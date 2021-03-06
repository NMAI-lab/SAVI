package savi.simulation.model;

import java.util.logging.Logger;
import savi.StateSynchronization.SyncAgentState;

public abstract class AgentModel {

	protected String ID; 
	protected String type;

	protected SyncAgentState agentState; //visible to subclasses
	
	private static Logger logger = Logger.getLogger(AgentModel.class.getName());
	
	public AgentModel() {
		this(0);
	}
	
	public AgentModel(double reasoningCyclePeriod) {
		this.agentState = new SyncAgentState(reasoningCyclePeriod);
	}
	
	protected Thread theAgentThread;
	
	public void setAgentThread(Thread t) {
		theAgentThread = t;
	}
	
	
	public void pauseAgent() {
		if (theAgentThread != null) {
			logger.fine("Agent "+ID+" paused-----");
			theAgentThread.suspend();
		
		}
	}
	public void unPauseAgent() {
		if (theAgentThread != null) {
				theAgentThread.resume();
			
			
		}
		logger.fine("Agent "+ID+" UNpaused ----");
	}
	
	
	protected void notifyAgent() {
		if (theAgentThread != null) {
			theAgentThread.interrupt();
		}
	}

	public SyncAgentState getAgentState() // to access it from the jason agent [during init]
	{
		return agentState;
	}

	public String getType() {
		
		return type;
	}

	public Thread getAgentThread() {
		
		return theAgentThread;
	};

	//public void updateModel(Structure action);

}
