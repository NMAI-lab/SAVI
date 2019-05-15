package savi.jason_processing;

import java.util.logging.Logger;

import savi.StateSynchronization.SyncAgentState;
import savi.agentBehaviour.SimpleJasonAgent;

//import jason.asSyntax.Structure;

public abstract class AgentModel {

	protected String ID; 
	protected String type;

	protected SyncAgentState agentState; //visible to subclasses
	
	private static Logger logger = Logger.getLogger(SimpleJasonAgent.class.getName());
	
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
