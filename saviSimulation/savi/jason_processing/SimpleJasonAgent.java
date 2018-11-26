package savi.jason_processing;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
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
	//private AgentModel myModel;
	private SyncAgentState agentState;
	
	private boolean running; 

    private static Logger logger = Logger.getLogger(SimpleJasonAgent.class.getName());
    
    private Random rand;

    //public static void main(String[] a) {
        //BaseCentralisedMAS.getRunner().setupLogger();
        //SimpleJasonAgent ag = new SimpleJasonAgent();
        //ag.run();
    //}

    public SimpleJasonAgent(String id, SyncAgentState modelAgentState) { //need to make the UAS class public so that the AgArch can refer back to it
    	try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (Exception e) {
            System.err.println("Error setting up logger:" + e);
        }
    	
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
        	  Thread.sleep(1000);
        	  running= true;

            while (isRunning()) {
                // calls the Jason engine to perform one reasoning cycle
                logger.fine("Reasoning....");
                getTS().reasoningCycle();// sense();//reasoningCycle();
                Thread.sleep(2);
                
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

    // this method just add some perception for the agent
    @Override
    public List<Literal> perceive() {
    	System.out.println("Perceiving");
    	//TODO: prepare perception from current state of the world rather than just having the latest one here.
    	
        List<Literal> l = new ArrayList<Literal>();
        
        if (agentState.getCameraInfo().isEmpty())
        	System.out.println("(I see nothing)");
        	
        for (VisibleItem vi: agentState.getCameraInfo()) {
        	
        	l.add(Literal.parseLiteral(vi.toPercept()));
        	System.out.println(vi.toPercept());
        }
        	
        
        return l;
    }

    // this method get the agent actions //this is called back by the agent code 
    @Override
    public void act(ActionExec action) {
    	//System.out.println("MYAgent " + getAgName() + " is doing: " + action.getActionTerm());
    	
        getTS().getLogger().info("Agent " + getAgName() + " is doing: " + action.getActionTerm());
        System.out.println("MYAgent " + getAgName() + " is doing: " + action.getActionTerm());
        
        //pass changes to model
        //myModel.updateModel(action.getActionTerm());
       
        String actionString = "";

        Term left = Literal.parseLiteral("turn(left)");
        Term right = Literal.parseLiteral("turn(right)");
        Term go = Literal.parseLiteral("thrust(on)");
        Term stop = Literal.parseLiteral("thrust(off)");

        
        
        if (action.equals(left)) 
        	actionString = "turn(left)";
        else if (action.equals(right)) 
        	actionString = "turn(right)";
        else if (action.equals(go)) 
        	actionString = "thrust(on)";
        else if (action.equals(stop))
        	actionString = "thrust(off)";
        
        
        // === hack: for now act randomly
        if (rand.nextBoolean()) 
        	actionString = "thrust(on)";
        else if (rand.nextBoolean())
        	actionString = "thrust(off)";
        else if (rand.nextBoolean())
        	if (rand.nextBoolean())
            	actionString = "turn(right)";
        	else
        		actionString = "turn(left)";
        System.out.println("MYAgent " + getAgName() + " changed this to: " + actionString);
        // TODO: remove the above block once agent is coded (via Jason agentspeak) to do something useful 

        agentState.addAction(actionString);

        // set that the execution was ok
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
