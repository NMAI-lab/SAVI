package savi.jason_processing;

import java.util.List;

import processing.core.PVector;

public class FieldAntenna implements Communicator{
	
	String ID;
	PVector position;
	double wifi = 100;
	
	public FieldAntenna(String id, PVector position) {
		
		this.ID = id;
		this.position = position;
		
	}
	
	public void update(List<Communicator> uas_list) {
		sendMessages(uas_list);
	}
	

	@Override
	public void sendMessages(List<Communicator> others) {
		
	}

	@Override
	public PVector getPosition() {
		
		return position;
	}

	@Override
	public double getWifiValue() {
		
		return wifi;
	}

	@Override
	public String getID() {
		
		return ID;
	}

	@Override
	public void receiveMessages(List<String> msglist) {
		for (String msg: msglist) {
			System.out.println("Console proxy received a message on the wifi:"+msg);
		}
		
	}

}
