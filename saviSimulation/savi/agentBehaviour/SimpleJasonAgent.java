package savi.agentBehaviour;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import savi.StateSynchronization.*;



import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Example of an agent that only uses Jason BDI engine. It runs without all
 * Jason IDE stuff. (see Jason FAQ for more information about this example)
 *
 * The class must extend AgArch class to be used by the Jason engine.
 */
public class SimpleJasonAgent extends AgArch implements Runnable {
	private static final String broadcastID = "BROADCAST";
	
	private String name;
	private SyncAgentState agentState;
	private boolean running;
	private static Logger logger = Logger.getLogger(SimpleJasonAgent.class.getName());

	private double lastPerceptionId;		// ID of the last perception received
	private boolean firstPerception;	// Flag for noting if any perceptions have ever been received (deal with the first ID issue)
	private PerceptionHistory perceptHistory;
	private String perceptionLogFileName;
	
	public SimpleJasonAgent(String id, String type, SyncAgentState modelAgentState) { //need to make the UAS class public so that the AgArch can refer back to it
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
		} catch (Exception e) {
			System.err.println("Error setting up logger:" + e);
		}
    	
		// Set parameters for the first perception ID
		this.lastPerceptionId = 0;
		this.firstPerception = true;
		this.perceptHistory = new PerceptionHistory();
		agentState = modelAgentState;
		running = false;

		// set up the Jason agent
		try {
			Agent ag = new Agent();
			new TransitionSystem(ag, null, null, this);
			this.name = id;
            ag.initAg("savi/asl/"+type+".asl");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Init error", e);
		}
		
		// Set up the perception logfile
		this.perceptionLogFileName = "PerceptionLog_" + this.name + ".log";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.perceptionLogFileName));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("I'm a Jason Agent and I'm starting");
		
		try {
			running= true;
			
			while (isRunning()) {
				// calls the Jason engine to perform one reasoning cycle
				logger.fine("Reasoning....");
				getTS().reasoningCycle();
                
				if (getTS().canSleep()) {
                	sleep();
				}
			}
			logger.fine("Agent "+getAgName()+" stopped.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Run error", e);
		}
	}
	
	public String getAgName() {
		return name;
	}
	
	/**
     * Check if there is fresh data to perceive. Don't want to read the same perception data more than once
     * @return
     */
	boolean checkForFreshPerception() {
		boolean freshData = false;		// Return flag - default is false (perception is not fresh)
		double currentPerceptId = agentState.getLatestPerceptionTimeStamp();
    	
		// Is this the first time perceiving? Is the perception ID different from the last perception?
		if ((this.firstPerception) || (currentPerceptId != this.lastPerceptionId)) {
			this.firstPerception = false;	// No longer the first perception
			freshData = true;				// There is fresh data
			this.lastPerceptionId = currentPerceptId;	// Update the perception ID
		}

		// Return the result
		return freshData;
	}
	
	// this method just add some perception for the agent
	@Override
	public List<Literal> perceive() {
		
		// This line will need to go away once the SIM side handles this.
		//agentState.buildSnapshot();
		
		// Get the perceptions from agentState
		PerceptionSnapshot currentPerceptions = new PerceptionSnapshot(this.agentState.getPerceptions());
		this.lastPerceptionId = currentPerceptions.getLatestTimeStamp();
		
		// Update the history, get the list of literals to send to the agent
		List<Literal> perceptionLiterals = new ArrayList<Literal>(this.perceptHistory.updatePerceptions(currentPerceptions));
		
		System.out.println("Agent " + getAgName() + " Perceiving perception "+ this.agentState.getCounter());
		System.out.println(perceptionLiterals.toString());
		
		// Write the perceptions to the perception logfile
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.perceptionLogFileName, true));
			for (Literal current : perceptionLiterals) {
				writer.append(current.toString() + " ");
			}
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return perceptionLiterals;
	}
	
	/**
	 * This method gets the agent actions. This is called back by the agent code
	 */ 
	@Override
	public void act(ActionExec action) {
		// Get the action term
		Structure actionTerm = action.getActionTerm();
		
		// Log the action
		getTS().getLogger().info("Agent " + getAgName() + " is doing: " + actionTerm);
		System.out.println("MYAgent " + getAgName() + " is doing: " + actionTerm);
		
		// Define the action string
		String actionString = "";
		
		// Define terms for possible actions (should move these to private class parameters)
		Term left = Literal.parseLiteral("turn(left)");
		Term right = Literal.parseLiteral("turn(right)");
		Term go = Literal.parseLiteral("thrust(on)");
		Term stop = Literal.parseLiteral("thrust(off)");
        
		// Check what action is being performed, update actionString accordingly.
		if (actionTerm.equals(left)) {
			actionString = "turn(left)";
		}
		else if (actionTerm.equals(right)) 
			actionString = "turn(right)";
		else if (actionTerm.equals(go)) 
			actionString = "thrust(on)";
		else if (actionTerm.equals(stop))
			actionString = "thrust(off)";
		
		// Add the action to agentState
		agentState.addAction(actionString);
		
		// Set that the execution was OK and flag it as complete.
		action.setResult(true);
		actionExecuted(action);
	}
	
	@Override
	public boolean canSleep() {
		return !this.checkForFreshPerception();
	}

	@Override
	public boolean isRunning() {
		return running;
	}
    
	@Override
	public void stop() {
		running = false;
	}
	
	// a very simple implementation of sleep
	public void sleep() {
		System.out.println("Snoozing");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Send message to another agent (via simulated wifi).
	 */
	@Override
	public void sendMsg(jason.asSemantics.Message m) throws Exception {
		// Make sure sender parameter is set
        if (m.getSender() == null)  m.setSender(getAgName());
		
        // Put the message in the wifi queue
        this.agentState.setMsgOut(m.toString());
	}
	
	/**
	 * in case agent is sleeping
	 * TODO: in case we get problems of agents not waking up on messages, this is what to use!!
	 * */
	public void wakeAgent() {
        wakeUpSense();
    }

	@Override
	public void broadcast(jason.asSemantics.Message m) throws Exception {
		m.setReceiver(broadcastID);
		this.sendMsg(m);
	}
	
	@Override
	public void checkMail() {
		Circumstance circ = getTS().getC();
		Queue<String> messages = new LinkedList<String>();
		messages = this.agentState.getMsgIn();
		for(String messageString:messages) {
			try {
				 Message currentMessage = Message.parseMsg(messageString);
				 if (currentMessage.getReceiver().equals(broadcastID) || currentMessage.getReceiver().equals(this.getAgName()))
					 circ.addMsg(currentMessage);
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}
}
