package savi.commandStation.Telemetry;

import java.util.ArrayList;
import java.util.List;

import jason.asSemantics.Message;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

public class TelemetryItem {

	private Message jasonMessage;
	private String receiver;
	private String sender;
	private String type;
	private List<Double> parameters;
	private String vehicleType;

	
	/**
	 * Static generator method - use this instead of the constructor
	 * @param message
	 * @return
	 */
	public static TelemetryItem generateTelemetryItem(String message) {
		TelemetryItem tempItem = new TelemetryItem(message);
		if (ThreatTelemetry.isType(tempItem)) {
			return new ThreatTelemetry(message);
		} else if (PositionTelemetry.isType(tempItem)) {
			return new PositionTelemetry(message);
		} else if (VelocityTelemetry.isType(tempItem)) {
			return new VelocityTelemetry(message);
		} else {
			return tempItem;
		}
	}

	/**
	 * Default constructor - protected, generator method should be used.
	 * @param message
	 */
	protected TelemetryItem(String message) {
		try {
			// Interpret the message as a jason message
			this.jasonMessage = Message.parseMsg(message);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		// Extract relevant information
		this.receiver = new String(jasonMessage.getReceiver());
		this.sender = new String(jasonMessage.getSender());

		Literal content = (Literal) jasonMessage.getPropCont();
		this.type = content.getFunctor();

		List<Term> termList = content.getTerms();
		this.parameters = new ArrayList<Double>();
		this.vehicleType = new String("Not Set");
		for (Term currentTerm : termList) {
			if (currentTerm.isNumeric()) {
				// Deal with special case for negative numbers (number is in brackets for some reason)
				if (currentTerm.toString().contains("-")) {
					String negativeString = currentTerm.toString();
					this.parameters.add(Double.parseDouble(negativeString.substring(1, negativeString.length()-1)));
				// Positive numbers are not in brackets
				} else {
					this.parameters.add(Double.parseDouble(currentTerm.toString()));
				}
			// Deal with the only parameter that isn't a number
			} else {
				this.vehicleType = new String(currentTerm.toString());
			}
		}
	}
	
	/**
	 * Return human readable version
	 */
	public String toString() {
		String msg = "Generic telemetry from " + this.getSender() + " to " + this.getReceiver() + " containing " ;
		
		// Append the parameters
		if (this.getParameters() == null) {
			msg = msg + "no doubles";
		} else {
			msg = msg + this.getParameters().toString();
		}
		
		// Append the type
		if (this.getVehicleType() == null) {
			msg = msg + " and no no vehicle type";
		} else {
			msg = msg + " vehicle type " + this.getVehicleType();
		}
		
		return msg;
	}

	
	/**
	 * Getter
	 * @return
	 */
	public Message getMessage() {
		if (this.jasonMessage == null) {
			return null;
		} else {
			return jasonMessage.clone();
		}
	}

	/**
	 * Getter
	 * @return
	 */
	public String getReceiver() {
		if (this.receiver == null) {
			return null;
		} else {
			return new String(this.receiver);
		}
	}

	/**
	 * Getter
	 * @return
	 */
	public String getSender() {
		if (this.sender == null) {
			return null;
		} else {
			return new String(this.sender);
		}
	}

	/**
	 * Getter
	 * @return
	 */
	public String getType() {
		if (this.type == null) {
			return null;
		} else {
			return new String(this.type);
		}
	}

	/**
	 * Getter
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
	 * Getter
	 * @return
	 */
	public String getVehicleType() {
		if (this.vehicleType == null) {
			return null;
		} else {
			return new String(this.vehicleType);
		}
	}
}
