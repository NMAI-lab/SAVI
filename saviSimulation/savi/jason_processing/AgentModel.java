package savi.jason_processing;

import java.util.concurrent.ConcurrentHashMap;

//import jason.asSyntax.Structure;

public abstract class AgentModel {

	protected String ID; 
	protected String type;

	protected SyncAgentState agentState; //visible to subclasses

	public SyncAgentState getAgentState() // to access it from the jason agent [during init]
	{
		return agentState;
	}

	public String getType() {
		
		return type;
	};

	//public void updateModel(Structure action);

}
