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
 * Class: CreateFrame
 */
package GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * A class which creates the main frame that displays the game.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class CreateFrame {
	
	/** The frame to display the game. */
	private JFrame frame;
	
	/** The width of the frame. */
	private int width;
	
	/** The height of the frame. */
	private int height;
	
	/** The title of the frame. */
	private String title;
	
	/**
	 * Creates the frame for the game.
	 * @param width the width of the frame
	 * @param height the height of the frame
	 * @param title the title used at the top of the frame
	 */
	public CreateFrame(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		init();
	}
	
	/**
	 * Initializes the frame and displays it on the screen.
	 */
	public void init() {
		/* Create the frame for the game */
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);
	}
	
	/**
	 * Returns the frame for the game
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Returns the width of the frame, in pixels
	 * @return the width of the frame
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of the frame, in pixels
	 * @return the height of the frame
	 */
	public int getHeight() {
		return height;
	}
}
