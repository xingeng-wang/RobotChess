package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class SixPlayerPanel extends DisplayPanel {

	public static final long serialVersionUID = 1;
	
	/** The size of font used on this panel. */
	static final int FONT_SIZE = 12;
	
	/** The width of buttons used on this panel. */
	static final int BUTTON_WIDTH = 200;
	
	/** The height of the buttons used on this panel. */
	static final int BUTTON_HEIGHT = 50;
	
	/** The amount of space used between buttons on this panel. */
	static final int SPACER = 35;
	
	/** The textfield the number of human players is stored in. */
	public TextField humanPlyrs;
	
	public SixPlayerPanel(int width, int height, ActionListener al) {
		setSize(width, height);
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, getHeight() / 5)));
		
		JLabel title = new JLabel("Six Player Set Up");
		title.setFont(new Font("Arial", Font.BOLD, 40));
		title.setForeground(Color.BLACK);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(title, BorderLayout.NORTH);
		
		add(Box.createRigidArea(new Dimension(0, getHeight() / 10)));
		
		DisplayPanel humanPlayerSubPanel = new DisplayPanel();
		
		humanPlayerSubPanel.setLayout(new GridLayout(1, 2));
		JLabel label = new JLabel("Number of Human Players");
		label.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
		label.setForeground(Color.BLACK);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		humanPlayerSubPanel.add(label);
		
		JRadioButton noHuman = new JRadioButton("None");
		noHuman.setActionCommand("noHuman");
		noHuman.addActionListener(al);
		humanPlayerSubPanel.add(noHuman);
		
		JRadioButton oneHuman = new JRadioButton("One");
		oneHuman.setActionCommand("oneHuman");
		oneHuman.addActionListener(al);
		oneHuman.setSelected(true);
		humanPlayerSubPanel.add(oneHuman);
		
		JRadioButton twoHumans = new JRadioButton("Two");
		twoHumans.setActionCommand("twoHumans");
		twoHumans.addActionListener(al);
		humanPlayerSubPanel.add(twoHumans);
		
		JRadioButton threeHumans = new JRadioButton("Three");
		threeHumans.setActionCommand("threeHumans");
		threeHumans.addActionListener(al);
		humanPlayerSubPanel.add(threeHumans);
		
		JRadioButton fourHumans = new JRadioButton("Four");
		fourHumans.setActionCommand("fourHuman");
		fourHumans.addActionListener(al);
		humanPlayerSubPanel.add(fourHumans);
		
		JRadioButton fiveHumans = new JRadioButton("Five");
		fiveHumans.setActionCommand("fiveHumans");
		fiveHumans.addActionListener(al);
		humanPlayerSubPanel.add(fiveHumans);
		
		JRadioButton sixHumans = new JRadioButton("Six");
		sixHumans.setActionCommand("sixHumans");
		sixHumans.addActionListener(al);
		humanPlayerSubPanel.add(sixHumans);
		
		ButtonGroup group = new ButtonGroup();
		group.add(noHuman);
		group.add(oneHuman);
		group.add(twoHumans);
		group.add(threeHumans);
		group.add(fourHumans);
		group.add(fiveHumans);
		group.add(sixHumans);
		
		add(humanPlayerSubPanel);
		
		add(Box.createRigidArea(new Dimension(0, SPACER)));
		
		DisplayPanel nextPanel = new DisplayPanel();
		nextPanel.setLayout(new GridLayout(1, 2));
		
		JButton back = new JButton("Back");
		back.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		back.setBackground(Color.WHITE);
		back.setForeground(Color.BLACK);
		back.setActionCommand("mainMenu");
		back.addActionListener(al);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextPanel.add(back);
		
		JButton create = new JButton("Create");
		create.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		create.setBackground(Color.WHITE);
		create.setForeground(Color.BLACK);
		create.setActionCommand("startsix");
		create.addActionListener(al);
		create.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextPanel.add(create);
		
		add(nextPanel);
	}
}
