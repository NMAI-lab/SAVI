package savi.commandStation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/** Miminal connector to hold a peer-to-peer socket connection
 * Currently only works for a connection on the same machine.
 */
public class SocketConnector {
	
	private int myPort;
	private int otherPort;
	
	private MessageHandler inboxHandler;
	
	public SocketConnector(MessageHandler msgHandler, int myPort,
			int otherPort) {
		
		inboxHandler = msgHandler;
		this.myPort = myPort;
		this.otherPort = otherPort;
		
	}



	public void messageOut(String msg) {
		System.out.println("Message OUT: "+msg);
		//get data for message
		byte [] data = msg.getBytes();
         
        try {
        	DatagramSocket socket = new DatagramSocket();
        	socket.setBroadcast(true);

        	InetAddress aHost = InetAddress.getLocalHost();

        	DatagramPacket packet = new DatagramPacket(
        			data,
        			data.length,
        			aHost,
        			otherPort);

        	socket.send(packet);
        	socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
	
	/***
	 * listens for incoming messages on port commandStationPort
	 */
	public void listenForMessages() {
	    (new Thread() {
	        @Override
	        public void run() {

	                //byte data[] = new byte[0];
	                DatagramSocket socket = null;
	                try {
	                    socket = new DatagramSocket(myPort);

	                } catch (SocketException ex) {
	                    ex.printStackTrace();
	                }
	                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
	                
	                String message;
	                while (true) {
	                	try {
	                		socket.receive(packet);
	                		message=new String(packet.getData());
	                		inboxHandler.messageIn(message);


	                	} catch (IOException ex) {
	                		ex.printStackTrace();
	                	}

	                }
	            }

	    }).start();
	
	}

}
