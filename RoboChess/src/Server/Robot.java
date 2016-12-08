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
 * Class: Robot
 */
package Server;

import java.awt.Point;

/** 
 * Represents a robot
 * @author Ryan Park, Yuchen Lin
 * @version 1.00
 */ 
public class Robot {

	/** indicate which player this robot belongs to */
	private int playerID;
	
	/** indicate what type this robot is: 0 - Scout, 1 - Sniper, 2 - Tank */ 
	private int type;
	
	/** indicate if this robot is alive or not */ 
	private boolean alive;
	
	/** indicate which direction this robot is facing (0 - 5) */ 
	private int direction;
	
	/** indicate the current health of this robot */ 
	private int health;
	
	/** indicate total movement of this robot */ 
	private int movement;
	
	/** indicate the location of this robot */ 
	private Point location;
	
	/** indicates if the robot has fired or not. */
	private boolean fired;
	
	/**
	 * Constructor for Robot class. 
	 * @param playerID
	 * @param type
	 * @throws Exception thrown when invalid type # is input
	 */
	public Robot(int playerID, int type) {
		this.playerID = playerID;
		this.type = type;
		this.alive = true;
		this.direction = 0;

		ReferenceSheet refSheet = new ReferenceSheet();
	
		this.health = refSheet.getHealth(type);
		this.movement = refSheet.getMovement(type);
		this.location = new Point(0, 0);
		this.fired = false;
	}
	
	/**
	 * Overloaded constructor for Robot.
	 * @param playerID
	 * @param type
	 * @param x
	 * @param y
	 */
	public Robot(int playerID, int type, int x, int y) {
		this.playerID = playerID;
		this.type = type;
		this.alive = true;
		this.direction = 0;

		ReferenceSheet refSheet = new ReferenceSheet();
	
		this.health = refSheet.getHealth(type);
		this.movement = refSheet.getMovement(type);
		this.location = new Point(x, y);
		this.fired = false;
	}

	/**
	 * Gets the alive field, to check if the robot is still alive.
	 * @return true if the robot is still alive otherwise false.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Gets the current health of the robot.
	 * @return A integer that is equal to the current health of the robot.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Gets the number of movement points the robot currently has.
	 * @return A integer that is equal to the remaining movement points the robot has.
	 */
	public int getMovement() {
		return movement;
	}

	/**
	 * Gets the type of the robot.
	 * @return A integer that represents the robot type: 0 = scout, 1 = sniper, 2 = tank. 
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the current location of the robot.
	 * @return A 2D array that shows where the robot is on the game map.
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * Gets the direction the robot is currently facing.
	 * @return A integer that represents which direction the is facing.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Gets the playerID of the robot.
	 * @return A integer that represents the ID of the robot's owner.
	 */
	public int getPlayerID(){
		return playerID;
	}

	/**
	 * Changes the alive field to false signifying the robot has died.
	 */
	public void setDead() {
		alive = false;
	}

	/**
	 * Changes the health of the robot to a new value. 
	 * @param newHealth The new health value of the robot.
	 */
	public void setHealth(int newHealth) { 
		health = newHealth;
	}
	
	/**
	 * Decrements the robots health by the given damage. 
	 * @param damage
	 */
	public void decrementHealth(int damage) { 
		health -= damage;
		if (health <= 0) {
			this.setDead();
		}
	}

	/**
	 * Changes the remaining movement points of the robot to a new value.
	 * @param newMovement The new remaining movement points the robot has.
	 */
	public void setMovement(int newMovement){
		movement = newMovement;
	}
	
	/**
	 * Decrements the robots movement points by the given amount.
	 * @param points
	 */
	public void decrementMovement(int points) {
		movement -= points;
	}

	/**
	 * Changes the current location of the robot.
	 * @param newLocation The new location for the robot on the gamefield.
	 */
	public void setLocation(Point newLocation) {
		location = newLocation;
	}

	/**
	 * Changes the current direction the robot is facing.
	 * @param newDirection The new direction that the robot will be facing.
	 */
	public void setDirection(int newDirection) {
		direction = newDirection;
	}
	
	/**
	 * Changes the current direction the robot is facing.
	 * @param newDirection The new direction that the robot will be facing.
	 */
	public void turnDirection(int newDirection) {
		direction = (direction + newDirection) % 6;
	}
	
	/**
	 * getter for fired field.
	 * @return true if this robot has fired, false otherwise.
	 */
	public boolean hasFired() {
		return this.fired;
	}
	
	/**
	 * setter for fired field.
	 * @param toggle
	 */
	public void setFired(boolean toggle) {
		this.fired = toggle;
	}
	
	/**
	 * Robot is moveable if it is alive and its movement points are greater than 0.
	 */
	public boolean isMoveable() {
		return this.alive && this.movement > 0;
	}

	/**
	 * The main method to perform a series of tests to verify that each method has the correct pre & post conditions
	 * @param args
	 */
	public static void main(String args[]) {
		Robot r1 = new Robot(1,0);

		String errorLog = "";
		int errorCount = 0;

		if (r1.isAlive() != true) {
			errorLog = errorLog + "The robot should be alive after construction";
			errorCount++;
		}

		if (r1.direction != 0) {
			errorLog = errorLog + "Direction of r1 should be 0";
			errorCount++;
		}

		if (r1.getHealth() != 1) {
			errorLog = errorLog + "Health of r1 should be 1";
			errorCount++;
		}

		if (r1.getMovement() != 3) {
			errorLog = errorLog + "Movement of r1 should be 3";
			errorCount++;
		}

		/*
		r1.setLocation([1][1]);

		System.out.println(errorLog);
		 */

		if (r1.getPlayerID() != 1) {
			errorLog = errorLog + "PlayerID of r1 should be 1";
			errorCount++;
		}

		r1.setHealth(0);
		if (r1.getHealth() != 0) {
			errorLog = errorLog + "Health of r1 should be 0";
			errorCount++;
		}

		r1.setMovement(1);
		if (r1.getMovement() != 1) {
			errorLog = errorLog + "Movement of r1 should be 1";
			errorCount++;
		}

		r1.setDead();
		if (r1.isAlive() != false) {
			errorLog = errorLog + "r1 should be marked as dead after running setDead()";
			errorCount++;
		}

		if (errorLog.length() > 0 && errorCount > 0) {
			System.out.println("Total number of errors found in Robot static main: " + errorCount);
			System.out.println(errorLog);
		} else {
			System.out.println("Robot static main: 0 errors found");
		}
		
		
		Robot rob = new Robot(0,0, 6, 10);
		rob.setDirection(3);
		rob.turnDirection(3);
		
		System.out.println(rob.getDirection());
	}
}
