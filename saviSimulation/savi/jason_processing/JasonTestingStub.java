package savi.jason_processing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

public class JasonTestingStub extends AgArch implements Runnable {

	private String name;
	private static Logger logger = Logger.getLogger(SimpleJasonAgent.class.getName());
	private String testingFilename;		// ID of the last perception received
	private String nextPercept;


	public JasonTestingStub(String aslFileName, String testingFilename) { 
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("testLogging.properties"));
		} catch (Exception e) {
			System.err.println("Error setting up logger:" + e);
		}

		// Set parameters for the first perception ID
		this.testingFilename = testingFilename;

		nextPercept= null;

		//myModel= model;
		// set up the Jason agent
		try {
			Agent ag = new Agent();
			new TransitionSystem(ag, null, null, this);
			this.name = aslFileName;

			ag.initAg(aslFileName);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Init error", e);
		}
	}


	public static void main(String[] params) {

		if (params.length < 2) {
			System.err.println("Usage: java JasonTestingStub asl_file_name.asl, percepts_file.txt\n In the percepts file, each line should be the set of percepts for one reasoning cycle, separated by tabs or spaces. e.g. aircraft(0,10) tree(2.1,200)" );
			System.exit(1);
		}

		JasonTestingStub tester = new JasonTestingStub(params[0], params[1]);

		tester.run();
		System.exit(0);

	}

	public void run(){
		System.out.println("Starting testing stub!");


		try {

			String line;

			BufferedReader reader = new BufferedReader(new FileReader(this.testingFilename));


			while ((line = reader.readLine()) != null) {     

				this.nextPercept = line; //save this to be retrieved by perceive() method

				// calls the Jason engine to perform one reasoning cycle
				logger.fine("Reasoning....");
				getTS().reasoningCycle();// sense();//reasoningCycle();

			}
			reader.close();
			logger.fine("Finished going through testing file.");
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


		List<Literal> l = new ArrayList<Literal>();
		System.out.println("------ Percepts:");	
		for (String item: nextPercept.split("\\s+")) { //split around any number of whitespace/tabs

			l.add(Literal.parseLiteral(item));
			System.out.println(item);

		}

		// Perceive agent's speed and speed direction

		return l;
	}

	// this method get the agent actions //this is called back by the agent code 
	@Override
	public void act(ActionExec action) {
		//System.out.println("MYAgent " + getAgName() + " is doing: " + action.getActionTerm());

		getTS().getLogger().info("Agent " + getAgName() + " is doing: " + action.getActionTerm());
		System.out.println("------ Testing Agent doing:\n " + action.getActionTerm());

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
		return true;
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
