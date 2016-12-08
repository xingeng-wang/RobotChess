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
 * Class: PlayerInfo
 */
package Player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Server.ReferenceSheet;
import Server.Robot;

/**
 * The class responsible for maintaining all info relevant to the each player,
 * This includes:
 * 	the condition of each robot
 * 	Whether the player is AI controlled or not 
 * 	the assigned playerID
 * 	the current robot type used for the current round.
 * 
 * @version 1.01
 * @author Ryan Park
 */
public class PlayerInfo {
	
	/** Unique player ID (also thread identifier). */
	private int playerID;
	
	/** A list that stores the robots of this player. */
	private List<Robot> robots;
	
	/** Specifies if the player is AI controlled or not */
	private boolean AI;
	
	/** track the current robot type */
	private int curRobotType;
	
	/** Static reference sheet */
	private static final ReferenceSheet refSheet = new ReferenceSheet();
	
	/* Player statistics */
	
	/** An integer that represents the total damage dealt to enemies */
	private int damageDealt = 0;
	
	/** An integer that represents the total damage points taken during the game. */
	private int damageTaken = 0;
	
	/** An integer represents the total number of robots destroyed. */
	private int robotsDestroyed = 0;
	
	/** An integer represents that holds the total number of tiles that the player has moved. */
	private int tilesMoved = 0;
	
	/** An integer that stores the number of turns since the player last moved. */
	private int turnsSinceLastMove = 0;
	
	/** An integer that stores the number of turns since the player has fired. */
	private int turnsSinceLastFire = 0;
	
	/** 
	 * Constructor for PlayerInfo class.
	 * @param playerID 
	 */
	public PlayerInfo(int playerID, boolean AI) {
		this.playerID = playerID;
		this.AI = AI;
		this.robots = new ArrayList<Robot>(3);
		
		/* 0 = scout , 1 = sniper, 2 = tank */
		this.robots.add(new Robot(playerID, 0));
		this.robots.add(new Robot(playerID, 1));
		this.robots.add(new Robot(playerID, 2));
		
		this.curRobotType = 0;
	}
	
	/**
	 * Resets stats and refreshes all robots.
	 */
	public void resetPlayerInfo() {
		this.damageDealt = 0;
		this.damageTaken = 0;
		this.robotsDestroyed = 0;
		this.tilesMoved = 0;
		this.turnsSinceLastMove = 0;
		this.turnsSinceLastFire = 0;
		
		this.robots = new ArrayList<Robot>(3);
		
		/* 0 = scout , 1 = sniper, 2 = tank */
		this.robots.add(new Robot(playerID, 0));
		this.robots.add(new Robot(playerID, 1));
		this.robots.add(new Robot(playerID, 2));
	}
	
	/**
	 * Returns the AI field.
	 * @return true if the player is AI controlled otherwise return false.
	 */
	public boolean isAI() {
		return AI;
	}
	
	/**
	 * Return the robot type for current robot.
	 * @return robot type of current robot
	 * 
	 */
	public int getCurType(){
		return this.curRobotType;
	}
	
	/**
	 * Specific which robot is the current robot
	 * @param CurType type of current robot
	 */
	public void setCurType(int CurType){
		this.curRobotType = CurType;
	}
	
	/**
	 * Returns the robot that has the specified type for this player.
	 * @param type The specific robot that is being requested as a String
	 * @return If the desired Robot is still alive it is returned otherwise null is returned.
	 * @throws Exception 
	 */
	public Robot getRobot(String type) throws Exception {
		int index;
		
		switch (type.toLowerCase()) {
		case "scout":
			index = 0;
			break;
		case "sniper":
			index = 1;
			break;
		case "tank":
			index = 2;
			break;
		default:
			throw new Exception("Unknown/Invalid robot type.");
		}
		
		return this.robots.get(index);
	}
	
	/**
	 * Returns the robot that has the specified type for this player.
	 * @param type The specific robot that is being requested (0 = scout , 1 = sniper, 2 = tank) 
	 * @return the desired robot
	 */
	public Robot getRobot(int type) {
		return this.robots.get(type);
	}
	
	/**
	 * Returns all robots that the player controls that are currently alive.
	 * @return A list that contains all the robots that are still alive.
	 */
	public List<Robot> getRobotsAlive() {
		List<Robot> listOfAlive = new ArrayList<Robot>();

		for(int x = 0; x < 3; ++x) {
			if (robots.get(x).isAlive()) {
				listOfAlive.add(robots.get(x));
			}
		}

		return listOfAlive;
	}
	
	/**
	 * Returns all robots that are alive and able to move.
	 * @return A list that contains all the robots that are still alive and able to move.
	 */
	public List<Robot> getMoveableRobots() {
		List<Robot> moveable = new ArrayList<Robot>();

		for (Robot robot : this.getRobotsAlive()) {
			if (robot.getMovement() > 0) {
				moveable.add(robot);
			}
		}

		return moveable;
	}
	
	/**
	 * Checks if the player is still in the game (at least 1 robot that is alive)
	 * @return True if the player still has any robots that are alive otherwise return false
	 */
	public boolean isPlayerAlive() {
		return !(this.getRobotsAlive().isEmpty());
	}
	
	/**
	 * Gets the playerID field of the object.
	 * @return A integer that represents the player's assigned ID.
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * Sets the specified robot to dead.
	 * @param robotType
	 */
	public void killRobot(int robotType) {
		robots.get(robotType).setDead();
		++this.robotsDestroyed;
	}
	
	/**
	 * Overloaded killRobot to receive String as input.
	 * @param robotType
	 */
	public void killRobot(String robotType) {
		if (robotType.toLowerCase().equals("scout")) {
			killRobot(0);
		} else if (robotType.toLowerCase().equals("sniper")) {
			killRobot(1);
		} else if (robotType.toLowerCase().equals("tank")) {
			killRobot(2);
		} else {
			System.out.println("Invalid/unknown robot type.");
		}
	}
	
	/**
	 * Refreshes the movement points of all robots.
	 */
	public void refreshRobots() {
		for (Robot curRobot : this.robots) {
			if (curRobot.isAlive()) {
				curRobot.setMovement(refSheet.getMovement(curRobot.getType()));
				curRobot.setFired(false);
			}
		}
	}
	
	/** Returns the total damage dealt statistic. */
	public int getDamageDealt () {
		return this.damageDealt;
	}
	
	/**
	 * Relocates the given robot to the given destination
	 * @param robotType
	 * @param destination
	 */
	public void setRobotLocation(int robotType, Point destination) {
		this.getRobot(robotType).setLocation(destination);
	}
	
	/* Player stat getters */
	
	/** Returns the current total damage taken stat. */
	public int getDamageTaken () {
		return this.damageTaken;
	}
	
	/** Returns the total number of robots destroyed. */
	public int getRobotsDestroyed () {
		return this.robotsDestroyed;
	}
	
	/** Returns the current number of tiles moved statistic. */
	public int getTilesMoved () {
		return this.tilesMoved;
	}
	
	/** Get the number of turns since the player last moved. */
	public int getTurnsSinceLastMove () {
		return this.turnsSinceLastMove;
	}
	
	/** Get the number of turns since the player last attacked. */
	public int getTurnsSinceLastFire () {
		return this.turnsSinceLastFire;
	}
	
	/**
	 * The main method to perform a series of tests to verify that each method has the correct pre & post conditions.
	 * @param args
	 */
	public static void main(String args[]) {
		PlayerInfo pInfo = new PlayerInfo(1,true);

		String errorLog = "";
		int errorCount = 0;

		if (pInfo.getRobotsAlive().size() != 3) {
			errorLog = errorLog + "Size of list after construction should be == 3";
			errorCount++;
		}

		if (pInfo.getPlayerID() != 1) {
			errorLog = errorLog + "PlayerID should be == 1";
			errorCount++;
		}

		if (errorLog.length() > 0 && errorCount > 0) {
			System.out.println("Total number of errors found in PlayerInfo static main: " + errorCount);
			System.out.println(errorLog);
		} else {
			System.out.println("PlayerInfo static main: 0 errors found");
		}
	}
}
