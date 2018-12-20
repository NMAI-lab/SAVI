package savi.jason_processing;

import java.util.HashMap;
import java.util.Map;

public class JasonMAS {


	private Map<String,SimpleJasonAgent> theJasonAgents;

	public JasonMAS() {
		this(new HashMap<String,AgentModel>()); 
	}

	/**
	 * Creates the Jason MAS Builder
	 * For now there's only one type of Jason agent in the sense of its capabilities towards the environment (go, stop, turn right, turn left)
	 * but each agent can have its plans. The plans should be in a file type.asl where type is the agent attribute "type".
	 * @param agents
	 */
	public JasonMAS(Map<String, AgentModel> agents) {
		theJasonAgents = new HashMap<String, SimpleJasonAgent>();
		AgentModel am;
		for (String AgId: agents.keySet()) {
			am = agents.get(AgId);
			theJasonAgents.put(AgId, new SimpleJasonAgent(AgId,am.getType(), am.getAgentState()));
		}

	}


	public void startAgents() {
		for (String AgId: theJasonAgents.keySet()) {
			Thread t1 = new Thread(theJasonAgents.get(AgId));
			t1.start();
		}
	}

	/**
	 * TODO: check what this actually does...
	 */
	void stopAgents() {
		for (String AgId: theJasonAgents.keySet()) {
			theJasonAgents.get(AgId).stop();
		}

	}
}
