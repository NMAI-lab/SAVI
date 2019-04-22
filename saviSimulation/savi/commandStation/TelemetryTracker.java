package savi.commandStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelemetryTracker {
	
//	private Map agentTelemetry;

	public TelemetryTracker() {
	//	this.agentTelemetry = new HashMap<String, AgentTelemetryData>();
	}

	public void update(String messageString) {
		TelemetryItem item = TelemetryItem.getTelemetryItem(messageString);
		
		
		
		//this.agentTelemetry.put();
		
		

		/*
		//try {
		//	Message jasonMessage = Message.parseMsg(messageString);
		//	String receiver = jasonMessage.getReceiver();
		//	String sender = jasonMessage.getSender();
		//	Literal content = (Literal)jasonMessage.getPropCont();
		//	String functor = content.getFunctor();

		//	Term term0 = content.getTerm(0);
		//	double term0AsDouble = Double.parseDouble(term0.toString());
						
			System.out.println("-------");
			System.out.println("This is the message stuff");
			System.out.println(messageString);
			System.out.println(item.getReceiver());
			System.out.println(item.getSender());
			System.out.println(item.getType());
			System.out.println(item.getVehicleType());
			System.out.println(item.getParameters().toString());
			System.out.println("That was the message stuff");
			//System.out.println("-------");
			//.equals(broadcastID)
			//if (currentMessage.getReceiver().equals(broadcastID) || currentMessage.getReceiver().equals(this.getAgName())) {
			//	circ.addMsg(currentMessage);
			//}
		//} catch (Exception e) {
		//	e.printStackTrace(System.out);
		//}
		if (item instanceof ThreatTelemetry) {
			ThreatTelemetry threatItem = (ThreatTelemetry)item;
			System.out.println("Threat X: " + threatItem.getX());
			//System.out.println("-------");
		}
		
		System.out.println("-------");
			*/
	}

}
