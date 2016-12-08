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
 * Class: DisplayPanel
 */
package GUI;

import java.awt.Component;

import javax.swing.JPanel;

/**
 * A subclass of every panel class, allowing focus to be set and gotten from each panel. 
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class DisplayPanel extends JPanel {
	public static final long serialVersionUID = 1;
	
	/** The componenet currently in focus. */
	protected Component focusComponent;
	
	/**
	 * Gets the current component in focus.
	 * @return the component currently in focus
	 */
	public Component getFocusComponent() {
		return focusComponent;
	}
	
	/**
	 * Sets a component to be in focus.
	 * @param focusComponent the component to focus upon
	 */
	public void setFocusComponent(Component focusComponent) {
		this.focusComponent = focusComponent;
	}
}
