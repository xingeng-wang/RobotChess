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
 * Class: ControlPanel
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * A class that displays a panel on the bottom of the main screen, allowing players to input
 * their wish to move, fire, end their turn, and see the stats for their current robot.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class ControlPanel extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** Buttons used within the panel used in multiple classes */
	private JButton move, fire, endTurn, back, confirm;
	
	/**
	 * Constructs the ControlPanel class.
	 * @param width the width of the panel
	 * @param height the height of the panel
	 * @param al the ActionListener used for each of the buttons
	 */
	public ControlPanel(int width, int height, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setFocusable(true);
		
		DisplayPanel buttonPanel = new DisplayPanel();
		buttonPanel.setLayout(new GridLayout(2, 2));
		
		/* Create the 4 left-hand buttons in the control panel */
		
		//Create move button
		move = new JButton("Move");
		move.setSize(width / 5, height / 2);
		move.setBackground(Color.WHITE);
		move.setForeground(Color.BLACK);
		move.setActionCommand("move");
		move.addActionListener(al);
		
		//Create fire button
		fire = new JButton("Fire");
		fire.setSize(width / 5, height / 2);
		fire.setBackground(Color.WHITE);
		fire.setForeground(Color.BLACK);
		fire.setActionCommand("fire");
		fire.addActionListener(al);
		
		//Create end turn button
		endTurn = new JButton("End Turn");
		endTurn.setSize(width / 5, height / 2);
		endTurn.setBackground(Color.WHITE);
		endTurn.setForeground(Color.BLACK);
		endTurn.setActionCommand("endTurn");
		endTurn.addActionListener(al);
		
		//Create back button
		back = new JButton("Back");
		back.setSize(width / 5, height / 2);
		back.setBackground(Color.WHITE);
		back.setForeground(Color.BLACK);
		back.setActionCommand("back");
		back.addActionListener(al);
		
		//add buttons to grid
		buttonPanel.add(move);
		buttonPanel.add(fire);
		buttonPanel.add(endTurn);
		buttonPanel.add(back);
		
		//add grid to panel
		add(buttonPanel, BorderLayout.WEST);
		
		/* Create the confirm button on the right hand side of the control panel. */
		
		DisplayPanel confirmPanel = new DisplayPanel();
		confirmPanel.setLayout(new GridLayout(1, 1));
		
		//Create confirm button
		confirm = new JButton("Confirm");
		confirm.setSize(width / 5, height);
		confirm.setBackground(Color.WHITE);
		confirm.setForeground(Color.BLACK);
		confirm.setActionCommand("confirm");
		confirm.addActionListener(al);
		
		//add it to the panel
		confirmPanel.add(confirm);
		
		//add the panel to the main panel
		add(confirmPanel, BorderLayout.EAST);
	}
	
	/**
	 * A function which updates the buttons on the control panel based on which ones have
	 * already been passed.
	 * @param: move the move button was pressed
	 * @param: fire the fire button was pressed
	 * @param: endTurn the endTurn button was pressed
	 */
	public void updateButtons(boolean moveClicked, boolean fireClicked, boolean endTurnClicked) {
		if (moveClicked || fireClicked || endTurnClicked) {
			move.setEnabled(false);
			fire.setEnabled(false);
			endTurn.setEnabled(false);
			back.setEnabled(true);
			confirm.setEnabled(true);
		} else {
			move.setEnabled(true);
			fire.setEnabled(true);
			endTurn.setEnabled(true);
			back.setEnabled(false);
			confirm.setEnabled(false);
		}
	}
	
	/**
	 * Updates the stats panel based on parameters given.
	 * @param remainingHealth the health the robot has remaining
	 * @param movementPoints the movement points the robot has remaining
	 * @param range the range the robot can shoot
	 * @param attackDamage the damage the robot gives when attacking
	 */
	public void updateStats(int remainingHealth, int movementPoints, int range, int attackDamage) {
		/* Create the stat's area in the center of the control panel. */
		
		DisplayPanel statsPanel = new DisplayPanel();
		statsPanel.setLayout(new GridLayout(2, 2));
		
		/* Create label for remaining health */
		String rhealth = "Remaining Health: " + Integer.toString(remainingHealth);
		JLabel health = new JLabel(rhealth, JLabel.CENTER);
		
		/* Create label for remaining movement points */
		String rMvmP = "Movement Points: " + Integer.toString(movementPoints);
		JLabel mvmP = new JLabel(rMvmP, JLabel.CENTER);
		
		/* Create label for range */
		String rangeStr = "Range: " + Integer.toString(range);
		JLabel robRange = new JLabel(rangeStr, JLabel.CENTER);
		
		/* Create label for attack damage */
		String atckDam = "Attack Damage: " + Integer.toString(attackDamage);
		JLabel damage = new JLabel(atckDam, JLabel.CENTER);
		
		/* Add the labels to the stats panel */
		statsPanel.add(health);
		statsPanel.add(mvmP);
		statsPanel.add(robRange);
		statsPanel.add(damage);
		
		/* Add the stats panel to the main panel */
		add(statsPanel, BorderLayout.CENTER);
		statsPanel.repaint();
	}
	
	public static void main(String[] args) {
		//Create and set up the window.
        CreateFrame frame = new CreateFrame(700, 150, "TEST");
 
        ControlPanel control = new ControlPanel(700, 150, null);
        control.updateStats(2, 3, 3, 1);
        //Add contents to the window.
        frame.getFrame().add(control);
 
        //Display the window.
        //frame.getFrame().pack();
        frame.getFrame().setVisible(true);
        control.updateButtons(false, false, false);
	}
}
