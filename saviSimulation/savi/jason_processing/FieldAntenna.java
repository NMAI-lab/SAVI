package savi.jason_processing;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import processing.core.PShape;
import processing.core.PVector;

public class FieldAntenna extends WorldObject implements Communicator, ActionListener{
	
	

	WifiAntenna antenna;
	
	private List<String> outbox = new LinkedList<String>();
	
	//TODO: add list of messages here, then implement message delivery
	private JFrame frame;
	private JTextField commands;
	private JLabel fromWifi;
	
	public FieldAntenna(int id, PVector position, SAVIWorld_model sim, int size, PShape image, double wifiProbWorking) {
		
		super(id, position, size, "FieldAntenna", sim, image);
		this.position = position;
		antenna = new WifiAntenna(id, this, wifiProbWorking);
		
		createAndShowGUI();
		

	}
	
	public void update(List<WifiAntenna> wifiParticipants) {
		antenna.update(wifiParticipants);
		
	}
	private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("ConsoleDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        
        // create a label to display text 
        fromWifi = new JLabel("Nothing heard on wifi");
  
        // create a new button 
        JButton button = new JButton("submit"); 
        button.setSize(50, 20);
   
        // addActionListener to button 
        button.addActionListener(this); 
  
        // create a object of JTextField with 16 columns 
        commands = new JTextField(64);
  
        
        // add buttons and textfield to panel 
        frame.getContentPane().add(commands, BorderLayout.NORTH);
        frame.getContentPane().add(fromWifi, BorderLayout.SOUTH); 
        frame.getContentPane().add(button, BorderLayout.CENTER); 
  
      
   
      //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    } 
  
    // if the button is pressed 
    public synchronized void actionPerformed(ActionEvent e) 
    { 
       
            // set the text of the label to the text of the field 
            outbox.add(commands.getText()); 
  
            // set the text of field to blank 
            commands.setText("  "); 
            
        
    } 

	

	@Override
	public PVector getPosition() {
		return position;
	}

	
	@Override
	public void receiveMessage(String msg) {
		
	fromWifi.setText(msg);
		
		
	}

	@Override
	public synchronized List<String> getOutgoingMessages() {
		// This is where messages should be read from the console
		List<String> outcopy = outbox;
		outbox = new LinkedList<String>();
		return outcopy; 
	}

	@Override
	public WifiAntenna getAntennaRef() {	
		return antenna;
	}

}
