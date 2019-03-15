package savi.jason_processing;

import java.util.concurrent.ConcurrentHashMap;

import jason.architecture.AgArch;
import savi.StateSynchronization.SyncAgentState;

//import jason.asSyntax.Structure;

public abstract class AgentModel {

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
			System.out.println("Agent "+ID+" paused-----");
			theAgentThread.suspend();
		
		}
	}
	protected void unPauseAgent() {
		if (theAgentThread != null) {
				theAgentThread.resume();
			
			
		}
		System.out.println("Agent "+ID+" UNpaused ----");
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
