package savi.commandStation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;


public class FancyCommandStationGUI implements ActionListener{
	
	private static int MAXLINES = 100;
	
	private JFrame frame;
	private JTextField commands;
	private JTextArea fromWifi;
	
	private CommandStationConnector connector; // connection to the rest of the simulation
	private String id; // identifier to use in messages
	
	public FancyCommandStationGUI(CommandStationConnector connector, String ID) {
		this.id = ID;
		this.connector = connector;
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		
        //Create and set up the window.
        frame = new JFrame("ConsoleDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        
        // create a label to display text 
        fromWifi = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(fromWifi); 
        fromWifi.setEditable(false);
  
        // create a new button 
        JButton followTargetButton = new JButton("followTarget"); 
        followTargetButton.setSize(50, 20);
        
        // create a new button 
        JButton button = new JButton("achieve"); 
        button.setSize(50, 20);
   
        // addActionListener to button 
        button.addActionListener(this); 
        followTargetButton.addActionListener(this);
  
        // create a object of JTextField with 16 columns 
        commands = new JTextField(40);
  
        
        // add buttons and textfield to panel 
        frame.getContentPane().add(commands, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.SOUTH); 
        frame.getContentPane().add(button, BorderLayout.CENTER); 
        frame.getContentPane().add(followTargetButton, BorderLayout.WEST);
  
      
   
      //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    } 
  
    // if the button is pressed 
    public void actionPerformed(ActionEvent e) { 

		long mid= System.currentTimeMillis()%20000; //this is a really crude message id!
		String message;
		
		// Deal with the type of button that was pressed
    	if (e.getActionCommand().equals("achieve")) {
       
            // set the text of the label to the text of the field
    		message = "<"+mid+","+this.id+",achieve,BROADCAST,"+commands.getText()+">";
    	} else {	// "followTarget"
    		message = "<"+mid+","+this.id+",achieve,BROADCAST,followTarget>";
    	}
    	
		// send the message to the simulator
        connector.messageOut(message); 

        // set the text of field to blank 
        commands.setText("  "); 
            
        
    } 

	
    /**
     * Method to receive messages from the simulator
     * @param msg an incoming message.
     */
	public void receiveMessage(String msg) {
		
	fromWifi.append(msg+"\n");
	fromWifi.setCaretPosition(fromWifi.getDocument().getLength());
	
	if (fromWifi.getLineCount()>MAXLINES) {
		
		try {
			int endLine1 = fromWifi.getLineEndOffset(1);
			fromWifi.replaceRange("", 0, endLine1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
		
		
	}

}
