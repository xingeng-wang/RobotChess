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
 * Class: Tile
 */
package Server;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * Representation of a game tile (hexagon). 
 * 
 * @version 0.02
 * @author Ryan Park, Yuchen Lin
 */
public class Tile {
	/** Indicates whether this tile is visible or not. */
	private boolean isVisible;
	
	/** List of robots occupying this tile. */
	private List<Robot> robots;
	
	/** The tiles coordinates. */
	private Point coordinates;
	
	/** 
	 * Constructor for Tile class.
	 * By default all tiles are initially not visible.
	 */
	public Tile() {
		this.isVisible = false;
		this.robots = new LinkedList<Robot>();
		this.coordinates = new Point(0, 0);
	}
	
	/** 
	 * Overloaded constructor for Tile.
	 * By default all tiles are initially not visible.
	 */
	public Tile(int x, int y) {
		this.isVisible = false;
		this.robots = new LinkedList<Robot>();
		this.coordinates = new Point(x, y);
	}

	/**
	 * Getter for isVisible
	 * @return true if tile is visible, false otherwise
	 */
	public boolean getVisibility() {
		return isVisible;
	}
	
	/**
	 * Setter for isVisible
	 * @param toggle boolean to toggle visibility
	 */
	public void setVisibility(boolean toggle) {
		this.isVisible = toggle;
	}
	
	/** 
	 * Returns a list of all the current robots on the tile
	 * @return a list of robots on this tile
	 */
	public List<Robot> getRobots() {
		return this.robots;
	}
	
	/** 
	 * Adds a robot to the end of the list
	 * @param robot the robot to add to this tile
	 */
	public void addRobot(Robot robot) {
		this.robots.add(robot);
		this.setVisibility(true);
	}
	
	/** 
	 * Remove the 1st occurrence of the given robot
	 * @param robot the robot to remove
	 */
	public void removeRobot(Robot robot) {
		this.robots.remove(robot);
		this.setVisibility(false);
	}
	
	/** 
	 * Determines if the tile is occupied by at least one robot
	 * @return true if there is at least one robot on this tile, false otherwise  
	 */
	public boolean isEmpty() {
		return this.robots.isEmpty();
	}	
	
	/**
	 * Returns the coordinate of the tile
	 * @return the coordinate of the tile
	 */
	public Point getCoordinates() {
		return coordinates;
	}
	
	/**
	 * Returns x coordinate of the tile
	 * @return x coordinate of the tile
	 */
	public int getX() {
		return this.coordinates.x;
	}
	
	/**
	 * Returns y coordinate of the tile
	 * @return y coordinate of the tile
	 */
	public int getY() {
		return this.coordinates.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
	    	return true;
	    } else if (!(obj instanceof Tile)) {
	    	return false;
	    } else {
	    	Tile tileObj = (Tile) obj;
	    	
			return this.getX() == tileObj.getX() && this.getY() == tileObj.getY();
	    }
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", this.getX(), this.getY());
	}
	
	/**
	 * The main method to perform a series of tests to verify that each method has the correct pre & post conditions
	 * @param args
	 */
	public static void main(String args[]) {
		Tile t = new Tile();

		Robot r1 = new Robot(1, 0);
		Robot r2 = new Robot(2, 1);
		Robot r3 = new Robot(3, 2);

		String errorLog = "";
		int errorCount = 0;
		
		if (t.isVisible  == true) { //Testing if it constructed properly (visiblity should be false after constructing)
			errorLog = errorLog + "Tile should not be visible after construction";
			errorCount++;
		}

		if (t.isEmpty() == false) {	//Testing to see if the if the tile is empty after construction (should be empty by default)
			errorLog = errorLog + "Tile should not have any robots on it after construction";
			errorCount++;
		}

		t.setVisibility(true);
		if (t.isVisible != true) {		//Testing isVisible(bool) to make sure it properly works
			errorLog = errorLog + "Tile should not be visible after using setVisiblity method";
			errorCount++;
		}

		t.addRobot(r1);
		if (t.isEmpty() == true) {		//Testing  addRobot(Robot) to make sure it works properly
			errorLog = errorLog + "Tile should not empty after r1 is added";
			errorCount++;
		}

		if (t.getRobots().size() != 1) {		//Making sure the list size has increased by one after adding a robot
			errorLog = errorLog + "There should be 1 robot in the list after adding r1";
			errorCount++;
		}

		t.addRobot(r2);
		if (t.getRobots().size() != 2) {		//Making sure the list size has increased after adding a second robot
			errorLog = errorLog + "There should be 2 robots in the list after adding r2";
			errorCount++;
		}

		t.addRobot(r3);			
		if (t.getRobots().size() != 3) {	//Making sure the list size has increased after adding a third robot
			errorLog = errorLog + "There should be 3 robots in the list after adding r3";
			errorCount++;
		}

		t.removeRobot(r1);
		if (t.getRobots().size() != 2) {	//Making sure that the list size is decreased after removing a robot
			errorLog = errorLog + "There should be 2 robots in the list after removing r1";
			errorCount++;
		}


		if (t.getRobots().get(0).equals(r2) == false) {	//Testing to verify that the correct ordering is maintained after removing a robot (inorder)
			errorLog = errorLog + "The first robot in the list should be r2 after removing r1";
			errorCount++;
		}

		t.removeRobot(r2);
		t.removeRobot(r3);
		if (t.isEmpty() != true) {		//Testing isEmpty() to make after emptying the list
			errorLog = errorLog + "The List should be empty after removing r1,r2, & r3";
			errorCount++;
		}

		if (errorLog.length() > 0 && errorCount > 0) {			//if the log's length is greater than 0 that means a error was found
			System.out.println("Total number of errors found in Tile static main: " + errorCount);
			System.out.println(errorLog);
		} else {
			System.out.println("Tile static main: 0 errors found");
		}
	}
}
