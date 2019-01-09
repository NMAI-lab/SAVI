package savi.jason_processing;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;

public class Perception {
	private String perceptionName;
	private long versionID;
	private List<Double> parameters;
	private final double similarity = 0.02; 	// Parameters must be 0.02% similar for comparison
	
	/**
	 * Default constructor is not useful
	 */
	@SuppressWarnings("unused")
	private Perception() {}
	
	/**
	 * Constructor
	 * @param newSensorType
	 * @param newVersionID
	 * @param newParameters
	 */
	public Perception(String perceptionName, long newVersionID, List<Double> newParameters) {
		this.perceptionName = new String(perceptionName);
		this.versionID = newVersionID;
		this.parameters = new ArrayList<Double>(newParameters);
	}
	
	/**
	 * Get the perception name
	 * @return
	 */
	public String getPerceptionName() {
		return new String(this.perceptionName);
	}
	
	
	public long getVersionID() {
		return this.versionID;
	}
	
	public List<Double> getParameters() {
		return new ArrayList<Double>(this.parameters);
	}
	
	
	public Boolean comparePerceptionType(Perception otherPerception) {
		return this.perceptionName.equals(otherPerception.getPerceptionName());		
	}
	
	/**
	 * Compare another perception to this perception. 
	 * Returns the % difference between the two most different parameters.
	 * Closer to 0 is more similar.
	 */
	public double getDifference(Perception otherPerception) {
		double maxDifference = 0;
		
		List<Double> otherParameters = otherPerception.getParameters();
		List<Double> myParameters = this.getParameters();
		
		// If the perceptions are from a different sensor, they are 100% different
		if (!comparePerceptionType(otherPerception)) {
			maxDifference = 1;
			return maxDifference;
		}
		
		// If there is a difference number of parameters, they are 100% different
		if (myParameters.size() != otherParameters.size()) {
			maxDifference = 1;
			return maxDifference;
		}
		
		for (int i = 0; i < myParameters.size(); i++) {
			double currentDifference = calculateDifference(myParameters.get(i), otherParameters.get(i));
			if (Math.abs(currentDifference) > Math.abs(maxDifference)) {
				maxDifference = currentDifference;
			}
		}
		
		return maxDifference;
	}
	
	/**
	 * Overwrite the equals method. Version ID does not matter!
	 * @param otherPerception
	 * @return
	 */
	public Boolean equals(Perception otherPerception) {
		return this.comparePerceptionType(otherPerception) && 
				this.parameters.equals(otherPerception.getParameters());
	}
	
	/**
	 * Check if the is within the similarity distance for two similar perceptions
	 * @param otherPerception
	 * @return
	 */
	public Boolean checkSimilar(Perception otherPerception) {
		if (Math.abs(this.getDifference(otherPerception)) < this.similarity) {
			return true;			
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the % difference calculation between a and b
	 * @param a
	 * @param b
	 * @return
	 */
	private double calculateDifference(double a, double b) {
		return (Math.abs(a - b))/((a + b)/2);
	}
	
	/**
	 * Convert the perception to a Literal for passing to the Jason agent.
	 * @return
	 */
	public Literal getLiteral() {
		String perceptString = new String(this.perceptionName);
		perceptString = perceptString + "(";
		for (int i = 0; i < this.parameters.size(); i++) {
			perceptString = perceptString+this.parameters.get(i).toString();
			if (i < this.parameters.size() - 1) {
				perceptString = perceptString + ",";
			} else {
				perceptString = perceptString + ")";
			}
		}
		return Literal.parseLiteral(perceptString);
	}
}
