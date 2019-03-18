package savi.stateSynchronization;

import java.util.ArrayList;
import java.util.List;

import jason.asSyntax.Literal;

/**
 * 
 * @author patrickgavigan
 *
 */
public abstract class Perception {
	private String perceptionName;
	private String perceptionType;
	private double timeStamp;
	private List<Double> parameters;
	private final double similarityThreshold = 0.02; 	// Parameters must be 2% similar for comparison
	private boolean perceptionLost = false;
	
	/**
	 * Default constructor is not useful
	 */
	protected Perception() {
		this.timeStamp = 0;
	}
	
	/**
	 * Constructor
	 * @param newSensorType
	 * @param newVersionID
	 * @param newParameters
	 */
	protected Perception(String perceptionName, double newTimeStamp, List<Double> newParameters) {
		this(perceptionName, null, newTimeStamp, newParameters);
	}
	
	/**
	 * 
	 * @param perceptionName
	 * @param type
	 * @param newTimeStamp
	 * @param newParameters
	 */
	protected Perception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters) {
		this(perceptionName, type, newTimeStamp, newParameters, false);
	}
	
	/**
	 * 
	 * @param perceptionName
	 * @param type
	 * @param newVersionID
	 * @param newParameters
	 */
	protected Perception(String perceptionName, String type, double newTimeStamp, List<Double> newParameters, boolean isLost) {
		this.perceptionName = new String(perceptionName);
		this.timeStamp = newTimeStamp;
		this.perceptionType = null;
		this.parameters = null;
		this.perceptionLost = isLost;
		
		if (newParameters != null) {
			this.parameters = new ArrayList<Double>(newParameters);
		}
		
		if (type != null) {
			this.perceptionType = new String(type);
		}
	}
	
	/**
	 * Abstract class, this is needed instead of a copy constructor
	 * @param other
	 */
	public abstract Perception clone();
	
	/**
	 * Changes the perceptionName to mark this perception as not being perceived anymore.
	 */
	public void perceptionLost() {
		if (!this.perceptionLost) {		// Can only lose a perception once
			this.perceptionName = this.perceptionName + "lost";
			this.perceptionLost = true;
		}
	}
	
	public boolean isLost() {
		return this.perceptionLost;
	}
	
	/**
	 * Get the perception name
	 * @return
	 */
	public String getPerceptionName() {
		if (this.perceptionName == null) {
			return null;
		} else {
			return new String(this.perceptionName);
		}
	} 
	
	/**
	 * Get the perception name
	 * @return
	 */
	public String getPerceptionType() {
		if (this.perceptionType == null) {
			return null;
		} else {
			return new String(this.perceptionType);
		}
	} 
	
	/**
	 * 
	 * @return
	 */
	public double getTimeStamp() {
		return this.timeStamp;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Double> getParameters() {
		if (this.parameters == null) {
			return null;
		} else {
			return new ArrayList<Double>(this.parameters);
		}
	}
	
	
	/**
	 * 
	 * @param otherPerception
	 * @return
	 */
	public boolean comparePerceptionType(Perception otherPerception) {
		// Check if they are both null, OK
		if ((this.getPerceptionType() == null) && (otherPerception.getPerceptionType() == null)) {
			return true;
		
		// If one of them is null, not OK	
		} else if ((this.getPerceptionType() == null) || (otherPerception.getPerceptionType() == null)) {
			return false;
			
		// Otherwise, check string equals
		} else {	
			return this.perceptionType.equals(otherPerception.getPerceptionType());
		}
	}
	
	/**
	 * 
	 * @param otherPerception
	 * @return
	 */
	public boolean comparePerceptionName(Perception otherPerception) {
		if ((this.getPerceptionName() == null) && (otherPerception.getPerceptionName() == null)) {
			return true;
		} else {
			return this.perceptionName.equals(otherPerception.getPerceptionName());
		}
	}
	
	/**
	 * Compare another perception to this perception. 
	 * Returns the % difference between the two most different parameters.
	 * Closer to 0 is more similar.
	 */
	public double getDifference(Perception otherPerception) {
		
		// First deal with cases where they are completely different
		double maxDifference = 1;
		List<Double> otherParameters;
		List<Double> myParameters;		
		
		// OtherPerception can't be null
		if (otherPerception == null) {
			return maxDifference;
		}
		
		// Check if the parameters for one is null but not the other one
		if ((otherPerception.getParameters() == null) != (this.getParameters() == null)) {
			return maxDifference;
		}		
		
		// Check if both are null
		if  ((otherPerception.getParameters() == null) && (this.getParameters() == null)) {
			otherParameters = null;
			myParameters = null;
		} else {
			otherParameters = otherPerception.getParameters();
			myParameters = this.getParameters();
		}

		
		// If the perceptions have a different name or type, they are 100% different
		if ((!this.comparePerceptionName(otherPerception)) || (!this.comparePerceptionType(otherPerception))) {
			return maxDifference;
		}

		// If the parameters lists are null, calculate difference on the timestamp
		if (otherParameters == null) {
			return maxDifference = calculateDifference(this.getTimeStamp(), otherPerception.getTimeStamp());
		}
		
		// If there is a difference number of parameters, they are 100% different
		if (myParameters.size() != otherParameters.size()) {
			return maxDifference;
		}
		
		// Finished all the 100% different cases.
		maxDifference = 0;
		
		// Otherwise, compare the other parameters
		for (int i = 0; i < myParameters.size(); i++) {
			double currentDifference = calculateDifference(myParameters.get(i), otherParameters.get(i));
			if (Math.abs(currentDifference) > Math.abs(maxDifference)) {
				maxDifference = currentDifference;
			}
		}
		
		return maxDifference;
	}
	
	/**
	 * Overwrite the equals method.
	 * @param otherPerception
	 * @return
	 */
	public boolean equals(Object otherPerception) {
		if (otherPerception instanceof Perception) {
			Perception p = (Perception)otherPerception;
			boolean sameType = this.comparePerceptionType(p);
			boolean sameName = this.comparePerceptionName(p);
			boolean sameTimeStamp = (this.getTimeStamp() == p.getTimeStamp());
			boolean sameParameters = false;
			
			if ((this.parameters != null) && (p.getParameters() != null)) {
				sameParameters = this.parameters.equals(p.getParameters());
			} else {
				sameParameters = (this.parameters == null) && (p.getParameters() == null);
			}
			
			return sameType && sameName && sameTimeStamp && sameParameters;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if otherPeception the is within the similarity distance for two similar perceptions
	 * @param otherPerception
	 * @return
	 */
	public boolean isSimilar(Perception otherPerception) {
		if (Math.abs(this.getDifference(otherPerception)) < this.similarityThreshold) {
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
	 * Format is: PerceptName(Param0,Param1...,TimeStamp, PerceptType)
	 * If any of these parameters is null, they are skipped. Can be as simple as:
	 * PerceptName(TimeStamp)
	 * @return	Perception as a Literal
	 */
	public Literal getLiteral() {
		// Start with the perception name
		String perceptString = new String(this.perceptionName);
		
		// Add the opening bracket
		perceptString = perceptString + "(";
		
		// Add the parameters, if we have any
		if (this.parameters != null) {
			for (int i = 0; i < this.parameters.size(); i++) {
				perceptString = perceptString + this.parameters.get(i).toString() + ",";
			}
		}

		// Add the timeStamp
		perceptString = perceptString + this.getTimeStamp();
		
		// Add the type, is we have one
		if (this.perceptionType != null) {
			perceptString = perceptString + "," + this.perceptionType + ")";
		} else {
			perceptString = perceptString + ")";
		}
		
		// Make the literal and return the result		
		return Literal.parseLiteral(perceptString.toLowerCase());
	}
	
	/**
	 * Implement the toString method
	 */
	public String toString() {
		return new String(this.getLiteral().toString());
	}
}
