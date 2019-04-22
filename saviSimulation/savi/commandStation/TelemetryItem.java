package savi.commandStation;

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

	public static TelemetryItem getTelemetryItem(String message) {
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
				this.parameters.add(Double.parseDouble(currentTerm.toString()));
			} else {
				this.vehicleType = new String(currentTerm.toString());
			}
		}
	}

	public Message getMessage() {
		if (this.jasonMessage == null) {
			return null;
		} else {
			return jasonMessage.clone();
		}
	}

	public String getReceiver() {
		if (this.receiver == null) {
			return null;
		} else {
			return new String(this.receiver);
		}
	}

	public String getSender() {
		if (this.sender == null) {
			return null;
		} else {
			return new String(this.sender);
		}
	}

	public String getType() {
		if (this.type == null) {
			return null;
		} else {
			return new String(this.type);
		}
	}

	public List<Double> getParameters() {
		if (this.parameters == null) {
			return null;
		} else {
			return new ArrayList<Double>(this.parameters);
		}
	}

	public String getVehicleType() {
		if (this.vehicleType == null) {
			return null;
		} else {
			return new String(this.vehicleType);
		}
	}
}
