package savi.jason_processing;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;

/**
 * Snapshot of perceptions. Contains a list of perceptions.
 * @author patrickgavigan
 *
 */
public class PerceptionSnapshot {

	private ArrayList<Perception> perceptionList;	// List of perceptions in the snapshot

	public PerceptionSnapshot() {
		this.perceptionList = new ArrayList<Perception>();
	}
	
	/**
	 * 
	 * @param snapshot
	 */
	public PerceptionSnapshot(PerceptionSnapshot snapshot) {
		this.perceptionList = snapshot.getPerceptionList();
	}
	
	/**
	 * 
	 * @param newPerception
	 */
	public void addPerception(Perception newPerception) {
		if (newPerception != null) {
			this.perceptionList.add(newPerception);
		}
	}

	/**
	 * 
	 * @param newPerceptions
	 */
	public void addPerceptionsFromSnapshot(PerceptionSnapshot newPerceptions) {
		List<Perception> newPerceptionList = new ArrayList<Perception>(newPerceptions.getPerceptionList());
		for (int i = 0; i < newPerceptionList.size(); i++) {
			this.addPerception(newPerceptionList.get(i));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Perception> getPerceptionList() {
		return new ArrayList<Perception>(this.perceptionList);
	}
	
	
	/**
	 * Get the versionID of the most recent data in the snapshot
	 * @return
	 */
	public long getLatestVersion() {
		long latestVersion = -1;
		
		for (int i = 0; i < this.perceptionList.size(); i++) {
			long currentID = this.perceptionList.get(i).getVersionID();
			if (currentID > latestVersion) {
				latestVersion = currentID;
			}
		}
		return latestVersion;		
	}
	
	
	/**
	 * Gets the most similar perception from the store and returns it.
	 * Removes this perception from the snapshot
	 * @param percept
	 * @return The most similar perception, null if none are similar enough
	 */
	public Perception pullSimilarPerception(Perception otherPerception) {
		Perception similar = null;
		double difference = 100;
		
		for (int i = 0; i < this.perceptionList.size(); i++) {
			if (this.perceptionList.get(i).isSimilar(otherPerception)) {
				double currentDifference = this.perceptionList.get(i).getDifference(otherPerception);
				if (Math.abs(currentDifference) < Math.abs(difference)) {
					difference = currentDifference;
					similar = this.perceptionList.get(i);
					this.perceptionList.remove(i);
				}
			}
		}
		return similar;
	}
	
	/**
	 * Get the output literals for this snapshot
	 * @return
	 */
	public List<Literal> getLiterals() {
		List<Literal> outputLiterals = new ArrayList<Literal>();
		for (int i = 0; i < this.perceptionList.size(); i++) {
			outputLiterals.add(this.perceptionList.get(i).getLiteral());
		}
		return outputLiterals;
	}
}