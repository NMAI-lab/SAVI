package savi.jason_processing;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.infra.centralised.BaseCentralisedMAS;
import processing.core.PVector;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
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
	
	private String name;
	private SyncAgentState agentState;
	private boolean running; 
    private static Logger logger = Logger.getLogger(SimpleJasonAgent.class.getName());
    private Random rand;
    private long lastPerceptionId;		// ID of the last perception received
    private boolean firstPerception;	// Flag for noting if any perceptions have ever been received (deal with the first ID issue)


    public SimpleJasonAgent(String id, SyncAgentState modelAgentState) { //need to make the UAS class public so that the AgArch can refer back to it
    	try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (Exception e) {
            System.err.println("Error setting up logger:" + e);
        }
    	
    	// Set parameters for the first perception ID
    	this.lastPerceptionId = 0;
    	this.firstPerception = true;
    	
    	rand= new Random(100L); //the agent will act randomly but always the same way across executions 
    	
    	agentState = modelAgentState;
    	
    	running =false;
    	
    	//myModel= model;
        // set up the Jason agent
        try {
            Agent ag = new Agent();
            new TransitionSystem(ag, null, null, this);
            this.name = id;
            
            ag.initAg(id+".asl");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
    }

    public void run(){
    	System.out.println("I'm a Jason Agent and I'm starting");
    	
    	
          try {
        	  //Thread.sleep(1000);
        	  running= true;

            while (isRunning()) {
                // calls the Jason engine to perform one reasoning cycle
                logger.fine("Reasoning....");
                getTS().reasoningCycle();// sense();//reasoningCycle();
                //Thread.sleep(100);
                
                if (getTS().canSleep())
                	
                	sleep();
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
    	long currentPerceptId = agentState.getCounter();
    	
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
    	  	    	
    	while(!this.checkForFreshPerception()) {}	// Busy wait for a fresh perception. TODO: is there a way to do this more elegantly? Better to suspend the thread if possible.
    	
    	System.out.println("Perceiving perception "+ this.lastPerceptionId);
    	
        List<Literal> l = new ArrayList<Literal>();
        
        if (agentState.getCameraInfo().isEmpty())
        	System.out.println("(I see nothing)");
        	
        for (VisibleItem vi: agentState.getCameraInfo()) {
        	//TODO: Fix percept
        	//l.add(Literal.parseLiteral(vi.toPercept()));
        	System.out.println(vi.toPercept());
        }
        
       for (String ms: agentState.getMessagesRead()) {
        	
        	l.add(Literal.parseLiteral(ms));
        	System.out.println(ms);
        }
        	
        // Perceive agent's speed and speed direction
        double speed = agentState.getSpeedValue();
        double speedAngle = agentState.getSpeedAngle();
        String speedDataPercept = "speedData("+speedAngle+","+speed+")";
        l.add(Literal.parseLiteral(speedDataPercept));
        System.out.println(speedDataPercept);
        
        return l;
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
        if (actionTerm.equals(left)) 
        	actionString = "turn(left)";
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
        return true;
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

    // Not used methods
    // This simple agent does not need messages/control/...
    @Override
    public void sendMsg(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void broadcast(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void checkMail() {
    }


}
