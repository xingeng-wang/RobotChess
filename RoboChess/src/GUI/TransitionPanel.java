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
 * Class: TransitionPanel
 */
package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Displays a panel used during game transitions, such as loading and switching between players. 
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class TransitionPanel extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** The size of the font used in this panel. */
	private static final int FONT_SIZE = 20;
	
	/** The width of each button used in this panel. */
	private static final int BUTTON_WIDTH = 200;
	
	/** The height of each button used in this panel. */
	private static final int BUTTON_HEIGHT = 50;
	
	/**
	 * Constructs the transition panel.
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public TransitionPanel(int width, int height) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}
	
	/**
	 * Displays the player wait panel
	 * @param playerID the ID of the next player's turn
	 * @param al the actionListener to get player input from the panel
	 */
	public void playerWaitScreen(int playerID, ActionListener al) {
		removeAll();
		add(Box.createRigidArea(new Dimension(0, getHeight() / 3)));
		JLabel label = new JLabel("Player " + playerID + "'s Turn");
		label.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
		
		JButton ready = new JButton("Ready");
		ready.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		ready.setBackground(Color.WHITE);
		ready.setForeground(Color.BLACK);
		ready.setActionCommand("ready");
		ready.addActionListener(al);
		ready.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(ready);
	}
	
	/**
	 * Displays the AI wait screen
	 * @param playerID the ID of the AI playing
	 */
	public void AIWaitScreen(int playerID) {
		removeAll();
		add(Box.createRigidArea(new Dimension(0, getHeight() / 3)));
		
		JLabel label = new JLabel("AI Player " + playerID + "'s Turn. Please Wait.");
		label.setFont(new Font("Arial", Font.BOLD, 40));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
	}
	
	/**
	 * Displays a loading screen
	 */
	public void loadingScreen() {
		removeAll();
		add(Box.createRigidArea(new Dimension(0, getHeight() / 3)));
		
		JLabel label = new JLabel("Loading...");
		label.setFont(new Font("Arial", Font.BOLD, 40));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
	}
}
