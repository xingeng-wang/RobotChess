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
 * Class: CreateGame
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * Displays the sub-menu showing options for game set up, including
 * number of players playing, number of AI players, board size, and more.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class CreateGame extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** The size of font used on this panel. */
	static final int FONT_SIZE = 12;
	
	/** The width of buttons used on this panel. */
	static final int BUTTON_WIDTH = 200;
	
	/** The height of the buttons used on this panel. */
	static final int BUTTON_HEIGHT = 50;
	
	/** The amount of space used between buttons on this panel. */
	static final int SPACER = 35;
	
	/**
	 * Constructs the CreateGame panel
	 * @param width the width of the panel
	 * @param height the height of the panel
	 * @param al the actionListener used for each of the buttons in the panel
	 */
	public CreateGame(int width, int height, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, getHeight() / 5)));
		
		JLabel title = new JLabel("Create Game");
		title.setFont(new Font("Arial", Font.BOLD, 40));
		title.setForeground(Color.BLACK);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(title, BorderLayout.NORTH);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		DisplayPanel subPanel = new DisplayPanel();
		subPanel.setLayout(new GridLayout(4, 1));
		
		JButton two = new JButton("Two Players");
		two.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		two.setBackground(Color.WHITE);
		two.setForeground(Color.BLACK);
		two.setActionCommand("two");
		two.addActionListener(al);
		two.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(two);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton three = new JButton("Three Players");
		three.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		three.setBackground(Color.WHITE);
		three.setForeground(Color.BLACK);
		three.setActionCommand("three");
		three.addActionListener(al);
		three.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(three);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton six = new JButton("Six Players");
		six.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		six.setBackground(Color.WHITE);
		six.setForeground(Color.BLACK);
		six.setActionCommand("six");
		six.addActionListener(al);
		six.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(six);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		JButton back = new JButton("Back");
		back.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		back.setBackground(Color.WHITE);
		back.setForeground(Color.BLACK);
		back.setActionCommand("mainMenu");
		back.addActionListener(al);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(back);
		
		add(subPanel);
	}
}
