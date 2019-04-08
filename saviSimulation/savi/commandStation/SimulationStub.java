package savi.commandStation;

import java.util.Random;

public class SimulationStub implements CommandStationConnector {

	CommandStationGUI theGUI;
	
	
	public SimulationStub() {
		theGUI = new CommandStationGUI(this, "fakeID");
	}
	
	@Override
	public void messageOut(String msg) {
		System.out.println("Message OUT: "+msg);

	}

	public static void main(String[] args) {
		SimulationStub stub = new SimulationStub();
		stub.simulateMessages();

	}

	/***
	 * periodically sends out messages to the GUI to emulate the simulation
	 */
	private void simulateMessages() {
		Random rand = new Random();
		System.out.println("Simulation stub launched!");
		new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    
                    try {
						Thread.sleep(100+rand.nextInt(500)); // random sleep time between 0.1 and 0.6 sec
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    long mid= System.currentTimeMillis()%20000; //a message id!
                    int x = rand.nextInt(1000);
                    int y = rand.nextInt(1000);
                    int z = rand.nextInt(1000);
            		String message = "<"+mid+","+rand.nextInt(10)+",tell,BROADCAST,position("+x+","+y+","+z+")>";
                    theGUI.receiveMessage(message);
                }
            }
        }).start();
		
	}

}
