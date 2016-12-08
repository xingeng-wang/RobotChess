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
 * Class: MainMenu
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
 * Displays a main menu upon game startup. Allows a player to set up a game, view options, or exit.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class MainMenu extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** The size of the font used in this panel. */
	static final int FONT_SIZE = 40;
	
	/** The width of each button used in this panel. */
	static final int BUTTON_WIDTH = 200;
	
	/** The height of each button used in this panel. */
	static final int BUTTON_HEIGHT = 50;
	
	/** The space in pixels used between each button. */
	static final int SPACER = 35;
	
	/**
	 * Constructs the main menu panel
	 * @param width the width of the panel
	 * @param height the height of the panel
	 * @param al the action listener used to interpret user input from the panel
	 */
	public MainMenu(int width, int height, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, height / 5)));
		
		JLabel label = new JLabel("RoboChess");
		label.setFont(new Font("Arial", Font.BOLD, 40));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
		
		add(Box.createRigidArea(new Dimension(0, height / 10)));
		
		JButton newGame = new JButton("Create Game");
		newGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		newGame.setBackground(Color.WHITE);
		newGame.setForeground(Color.BLACK);
		newGame.setActionCommand("createGame");
		newGame.addActionListener(al);
		newGame.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(newGame);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton options = new JButton("Options");
		options.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		options.setBackground(Color.WHITE);
		options.setForeground(Color.BLACK);
		options.setActionCommand("options");
		options.addActionListener(al);
		options.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(options);
		
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
}
