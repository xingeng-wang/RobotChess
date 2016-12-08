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
 * Class: Options
 */
package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * Displays a sub-menu allowing players to view and set various options, including debug mode.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class Options extends DisplayPanel implements ItemListener {
	public static final long serialVersionUID = 1;
	
	/** Defines the font size used on the panel. */
	private static final int FONT_SIZE = 40;
	
	/** Defines the width of buttons used on the panel.  */
	private static final int BUTTON_WIDTH = 200;
	
	/** Defines the height of a button used on the panel. */
	private static final int BUTTON_HEIGHT = 50;
	
	/** Defines extra space used between each button on the panel. */
	private static final int SPACER = 35;
	
	
	/** Static variable options to pass to server */
	public static boolean DEBUG_MODE;
	public boolean SPECTATOR_MODE;
	
	/** check boxes used in this class */
	JCheckBox debug, spectator;
	
	/**
	 * Constructs the options panel
	 * @param width the width of the panel
	 * @param height the height of the panel
	 * @param al the action listener used to get user input from the panel
	 */
	public Options(int width, int height, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//Set the defualt option values
		DEBUG_MODE = false;
		
		add(Box.createRigidArea(new Dimension(0, height / 5)));
		
		JLabel label = new JLabel("Options");
		label.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
		
		add(Box.createRigidArea(new Dimension(0, height / 10)));
		
		spectator = new JCheckBox("Spectator Mode");
		spectator.setSelected(false);
		spectator.addItemListener(this);
		add(spectator);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		debug = new JCheckBox("Debug Mode");
		debug.setSelected(false);
		debug.addItemListener(this);
		add(debug);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton back = new JButton("Back");
		back.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		back.setBackground(Color.WHITE);
		back.setForeground(Color.BLACK);
		back.setActionCommand("mainMenu");
		back.addActionListener(al);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(back);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getItemSelectable() == debug && e.getStateChange() == ItemEvent.SELECTED) {
			DEBUG_MODE = true;
		} else {
			DEBUG_MODE = false;
		}
		
		if(e.getItemSelectable() == spectator && e.getStateChange() == ItemEvent.SELECTED) {
			DEBUG_MODE = true;
		} else {
			DEBUG_MODE = false;
		}
		
	}
}
