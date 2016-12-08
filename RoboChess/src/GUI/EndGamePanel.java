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
 * Class: EndGamePanel
 */
package GUI;

import java.awt.BorderLayout;
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
 * A panel which displays the end game screen, allowing players to play again, view statistics, or exit
 * to the main menu.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class EndGamePanel extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** Defines the font size used on the panel. */
	private static final int FONT_SIZE = 40;
	
	/** Defines the width of a button used on the panel. */
	private static final int BUTTON_WIDTH = 200;
	
	/** Defines the height of a button used on the panel.*/
	private static final int BUTTON_HEIGHT = 50;
	
	/** Defines the amount of extra space to use between buttons. */
	private static final int SPACER = 35;
	
	/**
	 * Constructs the EndGame panel.
	 * @param width the width of the panel
	 * @param height the height of the panel
	 * @param al the action listener used for the buttons on the panel
	 * @param winningPlayerID the id of the player who won, for display
	 */
	public EndGamePanel(int width, int height, ActionListener al, int winningPlayerID) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, height / 5)));
		
		JLabel label = new JLabel("Player " + winningPlayerID + " Wins!!!");
		label.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
		
		add(Box.createRigidArea(new Dimension(0, height / 10)));
		
		JButton playAgain = new JButton("Play Again");
		playAgain.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		playAgain.setBackground(Color.WHITE);
		playAgain.setForeground(Color.BLACK);
		playAgain.setActionCommand("playAgain");
		playAgain.addActionListener(al);
		playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(playAgain);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton statistics = new JButton("Statistics");
		statistics.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		statistics.setBackground(Color.WHITE);
		statistics.setForeground(Color.BLACK);
		statistics.setActionCommand("statistics");
		statistics.addActionListener(al);
		statistics.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(statistics);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton quit = new JButton("Quit");
		quit.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		quit.setBackground(Color.WHITE);
		quit.setForeground(Color.BLACK);
		quit.setActionCommand("quit");
		quit.addActionListener(al);
		quit.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(quit);
	}
	
	public static void main(String[] args) {
		CreateFrame frame = new CreateFrame(1000, 500, "TEST");
		DisplayPanel panel = new EndGamePanel(frame.getWidth(), frame.getHeight(), null, 2);
		frame.getFrame().getContentPane().removeAll();
		frame.getFrame().getContentPane().add(panel, BorderLayout.CENTER);
		frame.getFrame().getContentPane().validate();
		Component focusComponent = panel.getFocusComponent();
		if (focusComponent != null) {
			focusComponent.requestFocusInWindow();
		}
		frame.getFrame().setVisible(true);
	}
}
