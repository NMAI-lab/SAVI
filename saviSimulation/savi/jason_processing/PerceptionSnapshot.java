package savi.jason_processing;

import java.util.ArrayList;

public class PerceptionSnapshot {

	private ArrayList<Perception> perceptionList;

	public PerceptionSnapshot() {
		this.perceptionList = new ArrayList<Perception>();
	}
	
	public void addPerception(Perception newPerception) {
		this.perceptionList.add(newPerception);
	}
	
	public ArrayList<Perception> getPerceptionList() {
		return new ArrayList<Perception>(this.perceptionList);
	}
}
