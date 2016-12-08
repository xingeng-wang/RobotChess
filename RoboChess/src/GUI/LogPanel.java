/*
 * University of Saskatchewan 
 * CMPT 370 - Intermediate Software Engineering
 * Team B5:
 *     Lin, Yuchen
 * 	   Nelson, Jordan
 * 	   Park, Ryan
 * 	   Wang, Xingeng
 * 	   Van Heerde, Willie
 * 
 * Class: LogPanel
 */
package GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Displays log messages for the player to see from the server. Cleared upon a new 
 * game turn.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class LogPanel extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** The size of the font used in this panel. */
	static final int FONT_SIZE = 10; // TODO: specify scope
	
	/** The area the log is displayed on. */
	JTextArea log; // TODO: specify scope
	
	/**
	 * Constructs the log panel.
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public LogPanel(int width, int height) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		
		//Create a text area to display the logs on
		log = new JTextArea(35, 17);
		log.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(log);
		
		//Add constraints on the dimensions of the panel
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridwidth = GridBagConstraints.REMAINDER;
		
		//Add space vertically and vertically if more space is needed
		cons.fill = GridBagConstraints.VERTICAL;
		
		cons.weightx = 1.0;
		cons.weighty = 1.0;
		add(scrollPane, cons);
	}
	
	/**
	 * Post a log on the panel
	 * @param logMsg the message to post
	 */
	public void post(String logMsg) {
		/* Display the log to the screen. */
		log.append(logMsg + "\n");
		
		/* 
		 * Make sure that the new log message is shown on screen if it displays
		 * bellow the bottom of the panel
		 */
		log.setCaretPosition(log.getDocument().getLength());
	}
	
	/**
	 * Clears the log of messages
	 */
	public void clearLog() {
		log.setText("");
	}
	
	public static void main(String args[]) {
		//Create and set up the window.
        CreateFrame frame = new CreateFrame(400, 600, "TEST");
 
        LogPanel log = new LogPanel(400, 600);
        //Add contents to the window.
        frame.getFrame().add(log);
 
        //Display the window.
        frame.getFrame().pack();
        frame.getFrame().setVisible(true);
        
        int i = 0;
        while (true) {
        	i++;
        	log.post("Player " + i + " was hit!");
        	try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
					
			}
        	
        	if (i == 60) {
        		log.clearLog();
        		i = 0;
        	}
        }
	}
}
