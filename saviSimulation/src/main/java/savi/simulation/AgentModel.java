package savi.simulation;

import savi.stateSynchronization.SyncAgentState;

import java.util.logging.Logger;

//import jason.asSyntax.Structure;

public abstract class AgentModel {
	private static final Logger logger = Logger.getLogger(AgentModel.class.getName());
	protected String ID; 
	protected String type;

	protected SyncAgentState agentState; //visible to subclasses
	
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
	
	
	protected void pauseAgent() {
		if (theAgentThread != null) {
			logger.fine("Agent "+ID+" paused-----");
			theAgentThread.suspend();
		
		}
	}
	protected void unPauseAgent() {
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
