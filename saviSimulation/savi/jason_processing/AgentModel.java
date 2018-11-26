package savi.jason_processing;

import java.util.concurrent.ConcurrentHashMap;

//import jason.asSyntax.Structure;

public abstract class AgentModel {
	
	int ID; 
	  
	protected SyncAgentState agentState; //visible to subclasses
	
	public SyncAgentState getAgentState() // to access it from the jason agent [during init]
		{
		return agentState;
		};
		
	//public void updateModel(Structure action);

		
		
		
}
