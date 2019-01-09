package savi.jason_processing;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;

/**
 * Used for tracking the perceptions sent to the agent.
 * @author patrickgavigan
 *
 */
public class PerceptionHistory {
	
	// The previous perception snapshot
	private PerceptionSnapshot previousPerception;
	
	// The last version of these perceptions that were sent to the agent
	private PerceptionSnapshot previousPerceptionSent;
	
	
	/**
	 * Default Constructor
	 */
	public PerceptionHistory() {
		this.previousPerception = new PerceptionSnapshot();
		this.previousPerceptionSent = new PerceptionSnapshot();
	}
	
	
	/**
	 * Updates the perception history and updates the agent. Looks for things that are sufficiently 
	 * different in order to send the update to the agent
	 */
	public List<Literal> updatePerceptions(PerceptionSnapshot newPerception) {
		
		// Make the empty perceptionUpdate list for the output.
		List<Literal> perceptionUpdate = new ArrayList<Literal>();
		
		// Check if the perceptions are up to date. If so, return empty list
		if (this.lastPerceptionId == perceptionId) {
			return perceptionUpdate;
		}
		
		// Check if there are differences. Something I saw that isn't there anymore, something that was there that moved a little, change in speed
		int numPerceptionsToUpdate = this.perceptionList.size();
		List<Boolean> updateNeeded = this.initializeUpdateNeededList(numPerceptionsToUpdate);		// Updates that are needed
		
		
		for (int i = 0; i < numPerceptionsToUpdate; i++) {
			int numNewPerceptions = newPerceptionList.size();
			Literal oldPerception = this.perceptionList.get(i);
			int oldArity = oldPerception.getArity();
			String oldFunctor = oldPerception.getFunctor();
			
			for (int j = 0; j < numNewPerceptions; j++) {
				Literal newPerception = newPerceptionList.get(j);
				int newArity = newPerception.getArity();
				String newFunctor = newPerception.getFunctor();
				// Check if the functor is the same
				if (oldFunctor.equals(newFunctor) && (oldArity == newArity)) {
					// Check if the terms are the same
					
					for (int k = 0; k < oldArity; k++) {
						if ((oldPerception.getTerm(k).equals(newPerception.getTerm(k)))) {
							
						}
					}
				}
			}
			
		}
		
		return perceptionUpdate;
		
	}
	
	/**
	 * 
	 * @param length
	 * @return
	 */
	private List<Boolean> initializeUpdateNeededList(int length) {
		List<Boolean> updateNeeded = new ArrayList<Boolean>();
		for (int i = 0; i < length; i++) {
			updateNeeded.add(false);
		}
		return updateNeeded;
	}
	
}
