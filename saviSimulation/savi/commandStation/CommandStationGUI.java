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


public class CommandStationGUI implements ActionListener{
	
	
	private static int MAXLINES = 100;
	
	
	
	private JFrame frame;
	private JTextField commands;
	private JTextArea fromWifi;
	
	private CommandStationConnector connector; // connection to the rest of the simulation
	private String id; // identifier to use in messages
	
	public CommandStationGUI(CommandStationConnector connector, String ID) {
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
        JButton button = new JButton("submit"); 
        button.setSize(50, 20);
   
        // addActionListener to button 
        button.addActionListener(this); 
  
        // create a object of JTextField with 16 columns 
        commands = new JTextField(40);
  
        
        // add buttons and textfield to panel 
        frame.getContentPane().add(commands, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.SOUTH); 
        frame.getContentPane().add(button, BorderLayout.CENTER); 
  
      
   
      //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    } 
  
    // if the button is pressed 
    public void actionPerformed(ActionEvent e) 
    { 
       
            // set the text of the label to the text of the field
    		long mid= System.currentTimeMillis()%20000; //this is a really crude message id!
    		String message = "<"+mid+","+this.id+",tell,BROADCAST,"+commands.getText()+">";
    		
            connector.messageOut(message); 
  
            // set the text of field to blank 
            commands.setText("  "); 
            
        
    } 

	

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
