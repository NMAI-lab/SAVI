import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

public class DebugEnvironment extends Environment {
	private static Logger logger;		// The logger
	private String testingFilename;		// File name for the test perceptions
	private Queue<String> perceptionList;
	BufferedReader reader;

	/**
	 * Initialize the environment
	 */
	public void init(String[] args) {
		// Initialize the logger.
		DebugEnvironment.logger = Logger.getLogger(DebugEnvironment.class.getName());
	
		// Set the input perception file
		this.testingFilename = "testPercepts.txt";
		
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("testLogging.properties"));
		} catch (Exception e) {
			logger.severe("Error setting up logger:" + e);
		}
		
		this.perceptionList = new LinkedList<String>();
		
		try {
			String line;
			// Open the file 
			this.reader = new BufferedReader(new FileReader(this.testingFilename));

			// Load the perceptions
			while ((line = reader.readLine()) != null) {     
				this.perceptionList.add(line);
			}

			// Done reading the file
			reader.close();
			logger.fine("Finished going through testing file.");
			
		} catch (Exception e) {
			logger.severe("Run error " + e.getMessage());
		}

		logger.info("Starting testing stub!");
	}


	/**
	 * Execute the action (print it to the console)
	 */
    public boolean executeAction(String ag, Structure action) {
		String actionMessage = "Agent " + ag + " is taking action " + action.toString(); 
		logger.info(actionMessage);

		// Update perceptions
		//updatePercepts();
		informAgsEnvironmentChanged();
		
		// Set that the execution was ok
		return true;
	}
    
    
	/**
	 * Add perceptions to the agent from the file
	 */
    public Collection<Literal> getPercepts(String ag) {
    	List<Literal> l = new ArrayList<Literal>();
		
		// Get the current perception
		String nextPercept = this.perceptionList.poll();
		
		if (nextPercept != null) {
			// Process the perception
			for (String item: nextPercept.split("\\s+")) { //split around any number of whitespace/tabs
				l.add(Literal.parseLiteral(item));
			}
		} else {
			// All done
			logger.info("All perceptions Processed.");
		}
		
		for (Literal p: l) {
			addPercept(p);
			logger.info("Perceiving " + p.toString());
		}
		
		Collection<Literal> returnList = super.getPercepts(ag);
		this.clearPercepts();
		return returnList;
	}
}
