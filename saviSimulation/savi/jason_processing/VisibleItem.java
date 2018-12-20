package savi.jason_processing;

public class VisibleItem {
	private String label;
	private double angle;
	private double distance;
	
	public VisibleItem(String l, double a, double d) {
		label = l;
		angle = a;
		distance = d;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getType() { //TODO: perhaps exploit some ontology of the world here
		return label;
	}
	
	public String toPercept() {
		return label+"("+angle+","+distance+")";
	}
}
