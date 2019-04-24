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

public class FancyCommandStationGUI extends JFrame {
	
	private CommandStationCore commandStation;

	private static int MAXLINES = 100;

	private JFrame frame;
	private JTextField period;
	private JTextArea fromWifi;

	public FancyCommandStationGUI(CommandStationCore commandStation) {
		this.commandStation = commandStation;
		createAndShowGUI();
	}

	
	/**
	 * Method to receive messages from the simulator
	 * 
	 * @param msg an incoming message.
	 */
	public void receiveMessage(String msg) {

		fromWifi.append(msg + "\n");
		fromWifi.setCaretPosition(fromWifi.getDocument().getLength());

		if (fromWifi.getLineCount() > MAXLINES) {

			try {
				int endLine1 = fromWifi.getLineEndOffset(1);
				fromWifi.replaceRange("", 0, endLine1);
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

	private void addComponentsToPane(Container mainPane) {
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
		this.buildMessageWindow(bottom);
		
		// Set the layout
		mainPanel.setLayout(new GridLayout(2,1));
		
		// Add the panels to the mainPane
		mainPane.add(mainPanel);
		mainPanel.add(top);
		mainPanel.add(bottom);
	}
	
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
	
	private void buildMessageWindow(Container panel) {
		
		// Set up the border
		TitledBorder title = new TitledBorder("Messages");
		((JComponent) panel).setBorder(title);
		
		// create a label to display text
		fromWifi = new JTextArea(20, 40);
		JScrollPane scrollPane = new JScrollPane(fromWifi);
		fromWifi.setEditable(false);
		panel.add(scrollPane);
	}

	
}
