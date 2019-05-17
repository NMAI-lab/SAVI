package savi.commandStation;

/**
 * Interface goes with SocketConnector class. 
 * Provides call-back method to handle incoming messages (from the socket)
 * 
 *
 */
public interface MessageHandler {

	/** receive an incoming message from the SocketConnector*/
	void messageIn(String msg);
	
}
