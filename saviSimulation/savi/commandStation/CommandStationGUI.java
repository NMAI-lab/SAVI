package savi.commandStation;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

public class CommandStationGUI extends JFrame {
	
	private CommandStationCore commandStation;		// Core of the command station - where the logic is implemented

	private static int MAXLINES = 100;

	private JFrame frame;
	private JTextField period;
	private JTextArea fromWifi;
	private JTextArea parsedMessages;

	public CommandStationGUI(CommandStationCore commandStation) {
		this.commandStation = commandStation;
		createAndShowGUI();
	}

	
	/**
	 * Method for displaying messages received by the CommandStationCore
	 * 
	 * @param 	rawMessage 		An incoming message, raw format
	 * @param	parsedMessage	Parsed version of the message
	 */
	public void receiveMessage(String rawMessage, String parsedMessage) {

		// Deal with the raw message
		fromWifi.append(rawMessage + "\n");
		fromWifi.setCaretPosition(fromWifi.getDocument().getLength());

		if (fromWifi.getLineCount() > MAXLINES) {

			try {
				int endLine1 = fromWifi.getLineEndOffset(1);
				fromWifi.replaceRange("", 0, endLine1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		// Deal with the parsed message
		parsedMessages.append(parsedMessage + "\n");
		parsedMessages.setCaretPosition(parsedMessages.getDocument().getLength());

		if (parsedMessages.getLineCount() > MAXLINES) {

			try {
				int endLine1 = parsedMessages.getLineEndOffset(1);
				parsedMessages.replaceRange("", 0, endLine1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Get the telemetry period from the text box and send it to the command station
	 */
	private void setTelemetryPeriod() {
		int value = Integer.parseInt(period.getText());
        period.setText(Integer.toString(value));
        this.commandStation.setTelemetryPeriod((long)value);
	}

	private void createAndShowGUI() {

		// Create and set up the window.
		frame = new JFrame("ConsoleDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addComponentsToPane(frame); 

		// Display the window.
		frame.pack();
		frame.setVisible(true);

	}
	
	
	/**
	 * Build the display
	 * @param mainPane
	 */
	private void addComponentsToPane(Container mainPane) {
		// Set up the panels
		Container mainPanel = new JPanel();
		
		// Build the left part of the panel (the parsed display)
		Container left = new JPanel();
		this.buildParsedMessageWindow(left);
		
		// Build the right part of the display
		Container right = new JPanel();
		this.addComponentsToCommandPane(right);
		
		// Set the layout of the final display
		mainPanel.setLayout(new GridLayout(1,2));
		
		// Add the panels to the mainPane
		mainPane.add(mainPanel);
		mainPanel.add(left);
		mainPanel.add(right);
		
	}
	

	/**
	 * Builds the command pane
	 * @param mainPane
	 */
	private void addComponentsToCommandPane(Container mainPane) {
		// Set up the panels
		Container mainPanel = new JPanel();
		
		// Build the top part of the panel
		Container top = new JPanel();
		top.setLayout(new GridLayout(2,1));
		Container telemetryPanel = new JPanel();
		Container missionPanel = new JPanel();

		// Call the panel builder functions
		getTelemetryPanel(telemetryPanel);
		getMissionPanel(missionPanel);
		
		top.add(telemetryPanel);
		top.add(missionPanel);
		
		// Build the bottom part of the panel
		Container bottom = new JPanel();
		this.buildRawMessageWindow(bottom);
		
		// Set the layout
		mainPanel.setLayout(new GridLayout(2,1));
		
		// Add the panels to the mainPane
		mainPane.add(mainPanel);
		mainPanel.add(top);
		mainPanel.add(bottom);
	}
	
	/**
	 * Build the telemetry panel (for setting telemetry settings)
	 * @param panel
	 */
	private void getTelemetryPanel(Container panel) {

		// Set up the border
		TitledBorder title = new TitledBorder("Telemetry Settings");
		((JComponent) panel).setBorder(title);

		// Set the layout
		panel.setLayout(new GridLayout(2,2));

		JLabel label = new JLabel("Telemetry period in ms (0 for no telemetry ping):");
	    panel.add(label); 
		
		JButton button;
		button = new JButton("Set telemetry period");
		panel.add(button);
		button.addActionListener(e -> {this.setTelemetryPeriod();});		// Set the period for telemetry to be sent

		// Make the text field for the period
		period = new JTextField(); 
		period.setText(Integer.toString(0));
	    panel.add(period); 
		
		button = new JButton("Request single telemetry");
		panel.add(button);
		
		// Request a single telemetry message be sent from all agents
		button.addActionListener(e -> {this.commandStation.sendMessage("BROADCAST", "achieve", "sendTelemetry");});
	}

	
	/**
	 * Build the mission loading panel
	 * @param panel
	 */
	private void getMissionPanel(Container panel) {

		// Set up the border
		TitledBorder title = new TitledBorder("Load Mission");
		((JComponent) panel).setBorder(title);

		// Set the layout
		panel.setLayout(new GridLayout(2,1));

		JButton button;

		button = new JButton("Patrol");
		panel.add(button);
		
		// Request a patrol from agents (UAVs can do this)
		button.addActionListener(e -> {this.commandStation.sendMessage("BROADCAST", "achieve", "patrol");});

		button = new JButton("Follow Target");
		panel.add(button);
		
		// Request a patrol from agents (UGVs can do this)
		button.addActionListener(e -> {this.commandStation.sendMessage("BROADCAST", "achieve", "followTarget");});
	}

	/**
	 * Build a message window for the raw messages to be displayed
	 * @param panel
	 */
	private void buildRawMessageWindow(Container panel) {
		
		// Set up the border
		TitledBorder title = new TitledBorder("Raw Messages");
		((JComponent) panel).setBorder(title);
		
		// create a label to display text
		fromWifi = new JTextArea(20, 80);
		JScrollPane scrollPane = new JScrollPane(fromWifi);
		fromWifi.setEditable(false);
		panel.add(scrollPane);
	}
	
	
	/**
	 * Build a message window for the raw messages to be displayed
	 * @param panel
	 */
	private void buildParsedMessageWindow(Container panel) {
		
		// Set up the border
		TitledBorder title = new TitledBorder("Parsed Messages");
		((JComponent) panel).setBorder(title);
		
		// create a label to display text
		parsedMessages = new JTextArea(60, 80);
		JScrollPane scrollPane = new JScrollPane(parsedMessages);
		parsedMessages.setEditable(false);
		panel.add(scrollPane);
	}
}
