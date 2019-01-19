/**
 * 
 */
package savi.agentBehaviour;

import java.util.List;
import java.util.Queue;

import jason.RevisionFailedException;
import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.Option;
import jason.asSyntax.Literal;

/**
 * @author Patrick
 *
 */
public class UxVAgent extends Agent {

	/**
	 * Default constructor, call super
	 */
	public UxVAgent() {
		super();
	}

	/**
	 * 
	 */
	public void initAg() {
		super.initAg();
	}

	/**
	 * Select the message that will be handled on this reasoning cycle.
	 * Placeholder for now. 
	 */
	public Message selectMessage(Queue<Message> messages) {
		return super.selectMessage(messages);
	}
	
	/**
	 * Select the event to focus on for this reasoning cycle.
	 * Placeholder for now.
	 */
	public Event selectEvent(Queue<Event> events) {
		return super.selectEvent(events);
	}
	
	/**
	 * Placeholder for now
	 */
	public Option selectOption(List<Option> options) {
		return super.selectOption(options);
	}
	
	/**
	 * Placeholder for now.
	 */
	public Intention selectIntention(Queue<Intention> intentions) {
		return super.selectIntention(intentions);
	}
	
	/**
	 * Placeholder for now
	 * Needs to check if the message is a proper message.
	 */
	public boolean socAcc(Message m) {
		return super.socAcc(m);
	}
	
	/**
	 * Placeholder for now
	 * @param percepts
	 * @return
	 */
	public int buf(List<Literal> percepts) {
		return super.buf(percepts);
	}
	
	/**
	 * Placeholder for now
	 */
	public List<Literal>[] brf(Literal beliefToAdd, Literal beliefToDel, Intention i) throws RevisionFailedException {
		return super.brf(beliefToAdd, beliefToDel, i);
	}
	
}
