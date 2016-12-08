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
 * Class: StatsPanel
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import Player.PlayerInfo;

/**
 * Displays player statistics at the end of a game, showing various things such as
 * tiles moved, damage dealt, etc.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class StatsPanel extends DisplayPanel {
	public static final long serialVersionUID = 1;
	
	/** The size of the font used in this panel. */
	static final int FONT_SIZE = 40;
	
	/** The width of each button used in this panel. */
	static final int BUTTON_WIDTH = 200;
	
	/** The height of each button used in this panel. */
	static final int BUTTON_HEIGHT = 50;
	
	/** The space in pixels used between each button. */
	static final int SPACER = 35;
	
	public StatsPanel(int width, int height, PlayerInfo stats, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, height / 5)));
		
		JLabel label = new JLabel("Player " + stats.getPlayerID() + "'s Statistics");
		label.setFont(new Font("Arial", Font.BOLD, 40));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);
		
		add(Box.createRigidArea(new Dimension(0, height / 10)));
		
		DisplayPanel subPanel = new DisplayPanel();
		subPanel.setLayout(new GridLayout(1, 4));
		
		JLabel stat1 = new JLabel("Damage Dealt: " + stats.getDamageDealt());
		stat1.setFont(new Font("Arial", Font.PLAIN, 20));
		stat1.setForeground(Color.BLACK);
		stat1.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(stat1);
		
		JLabel stat2 = new JLabel("Damage Taken: " + stats.getDamageTaken());
		stat2.setFont(new Font("Arial", Font.PLAIN, 20));
		stat2.setForeground(Color.BLACK);
		stat2.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(stat2);
		
		JLabel stat3 = new JLabel("Robots Destroyed: " + stats.getRobotsDestroyed());
		stat3.setFont(new Font("Arial", Font.PLAIN, 20));
		stat3.setForeground(Color.BLACK);
		stat3.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(stat3);
		
		JLabel stat4 = new JLabel("Tiles Traveled: " + stats.getTilesMoved());
		stat4.setFont(new Font("Arial", Font.PLAIN, 20));
		stat4.setForeground(Color.BLACK);
		stat4.setAlignmentX(Component.CENTER_ALIGNMENT);
		subPanel.add(stat4);
		
		add(subPanel);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		DisplayPanel navPanel = new DisplayPanel();
		navPanel.setLayout(new GridLayout(3, 1));
		
		JButton prev = new JButton("Prev");
		prev.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		prev.setBackground(Color.WHITE);
		prev.setForeground(Color.BLACK);
		prev.setActionCommand("prev");
		prev.addActionListener(al);
		prev.setAlignmentX(Component.CENTER_ALIGNMENT);
		navPanel.add(prev);
		
		JButton back = new JButton("Back");
		back.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		back.setBackground(Color.WHITE);
		back.setForeground(Color.BLACK);
		back.setActionCommand("back");
		back.addActionListener(al);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		navPanel.add(back);
		
		JButton next = new JButton("Next");
		next.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		next.setBackground(Color.WHITE);
		next.setForeground(Color.BLACK);
		next.setActionCommand("next");
		next.addActionListener(al);
		next.setAlignmentX(Component.CENTER_ALIGNMENT);
		navPanel.add(next);
		
		add(navPanel);
	}
}
