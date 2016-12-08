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
 * Class: Server
 */
package Server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import GUI.CreateFrame;
import GUI.LogPanel;
import GUI.StatsPanel;
import GUI.EndGamePanel;
import GUI.TransitionPanel;

import Messages.*;
import Player.Player;
import Player.PlayerInfo;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * The Server is responsible for all the interactions that the players have
 * with the system. The Server regulates the flow of the program by determining
 * whose turn it is and what they can and cannot do.
 * 
 * @version 1.01
 * @author Yuchen Lin, Ryan Park, Xingeng Wang, Jordan Nelson
 */
public class Server extends UntypedActor {
	
	/** */ // TODO: add javadoc
	private ServerGUIHandler GUIHandler;
	
	/** A flag to enable/disable debug mode. */
	private boolean debugMode;
	
	/** A flag to enable/disable spectator mode.  */
	private boolean specMode;
	
	/** Total number of players for this game. Should be 2, 3, or 6. */
	private int numPlayers;
	
	/** Number of AI players. Should be less then numPlayers. */
	private int numAIPlayers;
	
	/** Size of the game map. Should be 5 or 7. */
	private int mapSize;
	
	/** The current player in the turn queue. */
	private int curPlayerID;
	
	/** The player ID of the winner. */
	private int winnerIdx;
	
	/** List of ActorRef of player actors. Used for sending messages. */
	private List<ActorRef> players;
	
	/** Queue to keep track of players turns. */ 
	private List<Integer> turnQueue;
	
	/** List of player information. The index of the list identifies the player ID. */
	private List<PlayerInfo> playerInfo;
	
	/** List of player statistics. The index of the list identifies the player ID. */
	//private List<PlayerStats> playerStats;
	
	/** Two dimensional array representing the game board. */
	private Tile[][] board;
	
	/** Indicated whether the current game has been resolved. */
	private boolean gameResolved;
	
	/** Static ReferenceSheet class */
	private final ReferenceSheet refSheet = new ReferenceSheet();
	
	/** The number of seconds the server should wait for an AI player before timing out */
	private final Timeout timeoutAI = new Timeout(5, TimeUnit.SECONDS);
	
	/** The number of seconds the server should wait for a human player before timing out */
	private final Timeout timeoutHuman = new Timeout(180, TimeUnit.SECONDS);
	
	/** the frame the game uses to display GUI */
	private CreateFrame frame;
	
	/**
	 * Constructor for Server class.
	 * @param frame
	 * @param debugMode
	 * @param specMode
	 * @param numPlayers
	 * @param numAIPlayers
	 * @param mapSize
	 */
	public Server(CreateFrame frame, boolean debugMode, boolean specMode, int numPlayers, int numAIPlayers, int mapSize) {
		init(frame, debugMode, specMode, numPlayers, numAIPlayers, mapSize);
	}
	
	/**
	 * Initialize gameboard and all the pieces. Used for initial start-up
	 * @param frame
	 * @param debugMode
	 * @param specMode
	 * @param numPlayers
	 * @param numAIPlayers
	 * @param mapSize
	 */
	private void init(CreateFrame frame, boolean debugMode, boolean specMode, int numPlayers, int numAIPlayers, int mapSize) {
		this.frame = frame;
		this.debugMode = debugMode;
		this.specMode = specMode;
		this.numPlayers = numPlayers;
		this.numAIPlayers = numAIPlayers;
		this.mapSize = mapSize;
		this.curPlayerID = 0;
		this.winnerIdx = -1;
		this.players = new ArrayList<ActorRef>(numPlayers);
		this.turnQueue = new ArrayList<Integer>(numPlayers);
		this.playerInfo = new ArrayList<PlayerInfo>(numPlayers);
		//this.playerStats = new ArrayList<PlayerStats>(numPlayers);
		this.GUIHandler = new ServerGUIHandler(getSelf(), frame, playerInfo);
		initBoard();
		
		/* 
		 * Used to uniquely set player ID's. 
		 * DO NOT modify outside of the following for-loops.
		 */
		int playerID = 0;

		// Create AI Player actors
		for (int i = 0; i < this.numAIPlayers; ++i) {
			ActorRef playerRef = getContext().actorOf(
					Props.create(Player.class, 
							true, // is AI
							playerID, // unique ID
							refSheet.getTeamColor(playerID),
							frame,
							getSelf(), 
							"./res/ForthScripts/sample_random.json", // TODO: get input from robot librarian.
							"./res/ForthScripts/sample_random.json", // TODO: get input from robot librarian.
							"./res/ForthScripts/sample_random.json"), // TODO: get input from robot librarian.
					"PLAYER_" + playerID);
			//players.add(playerRef);
			players.add(playerID, playerRef);
			turnQueue.add(playerID); // add player to turn queue
			this.playerInfo.add(new PlayerInfo(playerID, true));
			//this.playerStats.add(new PlayerStats());
			
			++playerID;
		}
		
		// Create Human Player actors
		for (int i = 0; i < this.numPlayers - this.numAIPlayers; ++i) {
			ActorRef playerRef = getContext().actorOf(
					Props.create(Player.class, 
							false, // is AI
							playerID, // unique ID
							refSheet.getTeamColor(playerID),
							frame, 
							getSelf(), 
							"", // leave as blank strings since humans don't need an AI script
							"", // leave as blank strings since humans don't need an AI script
							""), // leave as blank strings since humans don't need an AI script
					"PLAYER_" + playerID);
			//players.add(playerRef);
			players.add(playerID, playerRef);
			turnQueue.add(playerID); // add player to turn queue
			this.playerInfo.add(new PlayerInfo(playerID, false));
			//this.playerStats.add(new PlayerStats());
			
			++playerID;
		}
		
		placeRobotsForAll();
		this.gameResolved = false;
	}
	
	/**
	 * Overloaded init. Used for reinitializing a game (play again).
	 */
	private void init() {
		this.curPlayerID = 0;
		this.winnerIdx = -1;
		this.turnQueue = new ArrayList<Integer>(numPlayers);
		this.playerInfo = new ArrayList<PlayerInfo>(numPlayers);
		//this.playerStats = new ArrayList<PlayerStats>(numPlayers);
		this.GUIHandler = new ServerGUIHandler(getSelf(), frame, playerInfo);
		initBoard();
		
		/* 
		 * Used to uniquely set player ID's. 
		 * DO NOT modify outside of the following for-loops.
		 */
		int playerID = 0;
		
		// Reinitialize AI Player actors. Note: they should already be created at this point.
		for (int i = 0; i < this.numAIPlayers; ++i) {
			turnQueue.add(playerID); // add player to turn queue
			this.playerInfo.add(new PlayerInfo(playerID, true));
			//this.playerStats.add(new PlayerStats());
			this.players.get(i).tell(new PlayAgainMsg(), getSelf());
			++playerID;
		}
		
		// Reinitialize Human Player actors. Note: they should already be created at this point.
		for (int i = 0; i < this.numPlayers - this.numAIPlayers; ++i) {
			turnQueue.add(playerID); // add player to turn queue
			this.playerInfo.add(new PlayerInfo(playerID, false));
			//this.playerStats.add(new PlayerStats());
			this.players.get(i).tell(new PlayAgainMsg(), getSelf());
			++playerID;
		}
		
		placeRobotsForAll();
		this.gameResolved = false;
	}
	
	/**
	 * Initializes the board.
	 * MUST be called before calling initRobots.
	 */
	private void initBoard() {
        this.board = new Tile[13][13];
        
        //Initialize the map to either be 5 or 7 tiles per side
        if (mapSize == 5) {
        	for(int x = 0; x < 13; x++) {
            	for(int y = 0; y < 13; y++) {
            		if((y < 2) || (y > 10) ||
            				(x < 2) || (x > 10) ||
            				(x == 2 && (y < 6 || y > 6)) ||
            				(x == 3 && (y < 4 || y > 8)) ||
            				(x == 9 && (y < 3 || y > 9)) ||
            				(x == 10 && (y < 5 || y > 7))) {
            			board[x][y] = null;
            		} else {
            			board[x][y] = new Tile(x, y);
            		}
            	}
            }
        } else if (mapSize == 7) {
        	for(int x = 0; x < 13; x++) {
            	for(int y = 0; y < 13; y++) {
            		if((x == 13) ||
            				(x == 0 && y != 6) ||
            				(x == 1 && (y < 4 || y > 8)) ||
            				(x == 2 && (y < 2 || y > 10)) ||
            				(x == 10 && (y < 1 || y > 11)) ||
            				(x == 11 && (y < 3 || y > 9)) ||
            				(x == 12 && (y < 5 || y > 7))) {
            			board[x][y] = null;
            		} else {
            			board[x][y] = new Tile(x, y);
            		}
            	}
            }
        } else {
        	System.out.println("FATAL ERROR: Invalid map size.");
        	System.exit(1);
        }
	}
	
	/**
	 * Initializes robots on the board.
	 */
	private void placeRobotsForAll() {
		// if the board failed to initialize, don't bother placing robots.
    	if (board == null) {
    		System.out.println("FATAL ERROR: Failed to initialize board.");
        	System.exit(1);
    	}
    	
		switch (mapSize) {
		case 5:
			switch (numPlayers) {
			case 2:
        		placeRobots(0, 2, 6); // Player 0 starts at (2, 6)
        		placeRobots(1, 10, 6); // Player 1 starts at (10, 6)
				break;
			case 3:
        		placeRobots(0, 2, 6); // Player 0 starts at (2, 6)
        		placeRobots(1, 8, 2); // Player 1 starts at (8, 2)
        		placeRobots(2, 8, 10); // Player 2 starts at (8, 10)
				break;
			default:
				System.out.println("FATAL ERROR: Invalid number of players.");
        		System.exit(1);
			}
			break;
		case 7:
			switch (numPlayers) {
			case 2:
        		placeRobots(0, 0, 6); // Player 0 starts at (0, 6)
        		placeRobots(1, 12, 6); // Player 1 starts at (12, 6)
				break;
			case 3:
        		placeRobots(0, 0, 6); // Player 0 starts at (0, 6)
        		placeRobots(1, 9, 0); // Player 1 starts at (9, 0)	
        		placeRobots(2, 9, 12); // Player 2 starts at (9, 12)
				break;
			case 6:	
        		placeRobots(0, 0, 6); // Player 0 starts at (0, 6)
        		placeRobots(1, 3, 0); // Player 1 starts at (3, 0)
        		placeRobots(2, 9, 0); // Player 2 starts at (9, 0)		
        		placeRobots(3, 12, 6); // Player 3 starts at (12, 6)
        		placeRobots(4, 9, 12); // Player 4 starts at (9, 12)
        		placeRobots(5, 3, 12); // Player 5 starts at (3, 12)
				break;
			default:
				System.out.println("FATAL ERROR: Invalid number of players.");
        		System.exit(1);
			}
			break;
		default:
			System.out.println("FATAL ERROR: Invalid map size.");
        	System.exit(1);
		}
	}
	
	/**
	 * Places robots at the given location. Used at initialization only.
	 * @param playerIdx index of the player
	 * @param x coordinate x
	 * @param y coordinate y
	 */
	private void placeRobots(int playerIdx, int x, int y) {
		PlayerInfo player = playerInfo.get(playerIdx);
		Robot scout = player.getRobot(0);
		Robot sniper = player.getRobot(1);
		Robot tank = player.getRobot(2);
		Point startLocation = new Point(x, y);
		
		scout.setLocation(startLocation);
		sniper.setLocation(startLocation);
		tank.setLocation(startLocation);
			
		board[x][y].addRobot(scout);
		board[x][y].addRobot(sniper);
		board[x][y].addRobot(tank);
	}
	
	/**
	 * Increments and returns the index of the turn queue (player ID)
	 * @return the player ID of the next player in the turn queue.
	 */
	private int getNextPlayer() {
		this.curPlayerID = (this.curPlayerID + 1) % this.numPlayers;
		
		if (this.turnQueue.get(this.curPlayerID).equals(-1)) {
			return this.getNextPlayer();
		} else {
			return this.curPlayerID;
		}
	}
	
	/**
	 * Get robot with highest movement points (fastest robot that is alive). 
	 * @param playerID the player ID for current player
	 * @return next robot type, or -1 if no moveable robots left
	 */
	private int getNextMoveableRobot(int playerID) {		
		PlayerInfo playerInfo = this.playerInfo.get(playerID);
		List<Robot> moveableRobots = playerInfo.getMoveableRobots();
		
		if (moveableRobots.isEmpty()) {
			return -1;
		}
		
		Robot fastestRobot = moveableRobots.get(0);
		
		for (Robot curRobot : moveableRobots) {
			if (curRobot.getMovement() > fastestRobot.getMovement()) {
				fastestRobot = curRobot;
			}
		}
		
		return fastestRobot.getType();
	}
	
	/**
	 * Move the specified robot to the specified destination
	 * @param destination the point the robot should be moved to
	 * @param curRobotType the robot that should be moving
	 * @return return the distance robot move this time
	 */
	private int move(Point destination, int curRobotType) {
		Robot curRobot = this.playerInfo.get(curPlayerID).getRobot(curRobotType);
		Point curLocation = curRobot.getLocation();
		/***************debug************************/
		System.out.println("Distance is " + getDistance(destination, curLocation));
		if(curRobot.getType() == 0) {
			System.out.println("Current Robot is scout");
			System.out.println("Range should be " + refSheet.getRange(curRobotType));
		}
		if(curRobot.getType() == 1) {
			System.out.println("Current Robot is sniper");
			System.out.println("Range should be " + refSheet.getRange(curRobotType));
		}
		if(curRobot.getType() == 2) {
			System.out.println("Current Robot is tank");
			System.out.println("Range should be " + refSheet.getRange(curRobotType));
		}
		System.out.println("Current range is " + refSheet.getRange(curRobotType));
		/*********************************************/
		if (checkRange(curLocation, destination, refSheet.getRange(curRobotType))) {
			curRobot.setLocation(destination);
			this.board[destination.x][destination.y].addRobot(curRobot);
			this.board[curLocation.x][curLocation.y].removeRobot(curRobot);
			
			return getDistance(destination, curLocation);
		} else {
			System.out.println(
					String.format("SERVER: Requested destination (%d, %d) is out of range", 
							destination.x, destination.y));
			return 0;
		}
	}

	/**
	 * Overloaded move method, to be used for handling AI player robots.
	 * Moves forward 1 step, in current direction.
	 * @param curRobotType the robot that should be moving
	 * @return return the distance robot move this time
	 */
	private int move(int curRobotType) {
		Robot curRobot = this.playerInfo.get(curPlayerID).getRobot(curRobotType);
		Point destination = this.getDirectionPoint(curRobot);
		
		return move(destination, curRobotType);
	}
	
	/** 
	 * Have curRobot fire at the given destination
	 * @param destination
	 * @param curRobotIdx
	 * @return true on success, false if out of range.
	 */
	private boolean fire(int curRobotIdx, Point destination) {
		Point attackerLocation = this.playerInfo.get(curPlayerID).getRobot(curRobotIdx).getLocation();
		int attackerRange = refSheet.getRange(curRobotIdx);
		
		if (checkRange(attackerLocation, destination, attackerRange)) {
			List<Robot> targetRobots = new ArrayList<Robot>();
			targetRobots = this.board[destination.x][destination.y].getRobots();
			
			for (Robot target : targetRobots) {
				target.decrementHealth(refSheet.getAttack(curRobotIdx));
			}
		
			return true;
		} else {
			return false; // not in range.
		}
	}
	
	/** 
	 * Overloaded fire method, to be used for handling AI player robots.
	 * @param direction
	 * @param range
	 * @param curRobotIdx
	 */
	private boolean fire(int curRobotIdx, int direction, int range) {
		Robot attacker = this.playerInfo.get(curPlayerID).getRobot(curRobotIdx);
		Point attackerLocation = this.playerInfo.get(curPlayerID).getRobot(curRobotIdx).getLocation();
		int attackerRange = refSheet.getRange(curRobotIdx);
		Point destination = this.getAIFirePoint(attacker, direction, range);
		
		if (checkRange(attackerLocation, destination, attackerRange)) {
			List<Robot> targetRobots = new ArrayList<Robot>();
			targetRobots = this.board[destination.x][destination.y].getRobots();
			
			for (Robot target : targetRobots) {
				target.decrementHealth(refSheet.getAttack(curRobotIdx));
			}
		
			return true;
		} else {
			return false; // not in range.
		}
	}
	
	/** */ // TODO: add javadoc
	private Point getAIFirePoint(Robot attacker, int direction, int range) {
		Tile attackerTile = board[attacker.getLocation().x][attacker.getLocation().y];
		List<Tile> tilesAtRange = new ArrayList<Tile>();
		
		tilesAtRange = getTilesWithinRange(attackerTile, range);
		if (range >= 2) {
			tilesAtRange.removeAll(getTilesWithinRange(attackerTile, range - 1));
		}
		
		int counter = 0;
		for (int i = 0; i < tilesAtRange.size(); ++i) {
			if (tilesAtRange.get(i) != null) {
				counter = i;
			}
			
			if (counter == direction) {
				break;
			}
		}
		
		Tile targetTile = tilesAtRange.get(counter);
		
		return new Point(targetTile.getX(), targetTile.getY());
	}
	
	
	/**
	 * Returns the coordinate point of the hex that the given robot is currently facing.
	 * @param robot
	 * @return a coordinate point
	 */
	private Point getDirectionPoint(Robot robot) {
		int x = robot.getLocation().x;
		int y = robot.getLocation().y;
		
		boolean yEven = y % 2 == 0;
		
		switch (robot.getDirection()) {
		case 0:
			++x;
			break;
		case 1:
			if (yEven) {
				++x;
			}
			++y;
			break;
		case 2:
			if (!yEven) {
				--x;
			}
			++y;
			break;
		case 3:
			--x;
			break;
		case 4:
			if (!yEven) {
				--x;
			}
			--y;
			break;
		case 5:
			if (yEven) {
				--x;
			}
			--y;
			break;
		default:
			System.out.println("Robot is facing an invalid direction.");
		}
		
		return new Point(x, y);
	}
	
	/**
	 * TODO: Finish javadoc
	 * @param destination
	 * @param curLocation
	 * @return
	 */
	private int getDistance(Point destination, Point curLocation) {
		int des_x = (int)destination.getX();
		int des_y = (int) destination.getY();
		int cur_x = (int) curLocation.getX();
		int cur_y = (int) curLocation.getY();
		int offset = 0;
		if (cur_x == des_x) {
			return Math.abs(cur_y - des_y);
		}
		
		if (cur_y == des_y) {
			return Math.abs(cur_x - des_x);
		}
		
		/* set the transition point at the smae line of destination point */
		
		Point tranPoint = new Point((int)curLocation.getX(), (int)destination.getY());
		if (curLocation.getX() > destination.getX()) {
			offset = 1;
		}
		if (curLocation.getX() < destination.getX()) {
			offset = 0;
		}
		if (destination.getY() - curLocation.getY() >= 4) {
			offset = 2;
		}
		return getDistance(destination, tranPoint) + getDistance(tranPoint, curLocation) - offset;
	}
	
	/**
	 * Check if a hex is in range.
	 * @param curPoint the location of the current robot
	 * @param destination the hex being checked
	 * @param range the range to check
	 * @return true if destination is in range, false otherwise.
	 */
	private boolean checkRange(Point curPoint, Point destination, int range) {
		// Convert given Points to Tiles
		Tile curTile = board[curPoint.x][curPoint.y];
		Tile destinationTile = board[destination.x][destination.y];
		
		// get all tiles in range
		List<Tile> tilesInRange = getTilesWithinRange(curTile, range);
		
		return tilesInRange.contains(destinationTile);
	}
	
	
	/**
	 * resets the game boards fog of war.
	 */
	private void resetFOW() {
		for (int x = 0; x < 13; ++x) {
			for (int y = 0; y < 13; ++y) {
				if (board[x][y] != null) {
					this.board[x][y].setVisibility(false);
				}
			}
		}
	}
	
	/** 
	 * Updates the fog of war for all robots of the given player.
	 * @param playerIdx index of the player 
	 */
	private void updateFOW(int playerIdx) {	
		resetFOW();
		
		PlayerInfo curPlayer = this.playerInfo.get(playerIdx);
		for (Robot liveRobot : curPlayer.getRobotsAlive()) {
			updateFOW(playerIdx, liveRobot.getType());
		}
	}
	
	/**
	 * Overloaded updateFOW for single robot.
	 * @param playerIdx index of the player 
	 * @param robotType
	 */
	private void updateFOW(int playerIdx, int robotType) {
		PlayerInfo curPlayerInfo = playerInfo.get(playerIdx);
		Robot curRobot = curPlayerInfo.getRobot(robotType);
		Point curLocation = curRobot.getLocation();
		int range = refSheet.getRange(robotType);
		
		List<Tile> visibleTiles = new LinkedList<Tile>();
		
		// add robot's current location to list of tiles to set visible
		visibleTiles.add(board[curLocation.x][curLocation.y]);
		
		// add all tiles that are in range
		visibleTiles.addAll(getTilesWithinRange(board[curLocation.x][curLocation.y], range));
			
		for (Tile tile : visibleTiles) {
			if (tile != null) {
				tile.setVisibility(true);
			}
		}
	}
	
	
	/**
	 * Gets a list of all adjacent hexes in the given tile.
	 * @param tile
	 * @return list of tiles
	 */
	private List<Tile> getAdjacentTiles(Tile tile) {
		List<Tile> adjacentTiles = new ArrayList<Tile>(6);
		adjacentTiles.add(null);
		adjacentTiles.add(null);
		adjacentTiles.add(null);
		adjacentTiles.add(null);
		adjacentTiles.add(null);
		adjacentTiles.add(null);
		
		int x, y;
		
		// get the adjacent tile in the 0th direction
		x = tile.getX() + 1;
		y = tile.getY();
		
		if(x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(0, board[x][y]);
		}
		
		// get the adjacent tile in the 1st direction
		if (tile.getY() % 2 == 0) {
			x = tile.getX() + 1;
		} else {
			x = tile.getX();
		}
		
		y = tile.getCoordinates().y + 1;
		
		if (x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(1, board[x][y]);
		}
		
		// get the adjacent tile in the 2nd direction
		if (tile.getCoordinates().y % 2 != 0) {
			x = tile.getX() - 1;
		} else {
			x = tile.getX();
		}
		
		y = tile.getY() + 1;
		
		if (x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(2, board[x][y]);
		}
		
		// get the adjacent tile in the 3rd direction
		x = tile.getX() - 1;
		y = tile.getY();
		
		if (x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(3, board[x][y]);
		}
		
		// get the adjacent tile in the 4th direction
		if (tile.getCoordinates().y % 2 != 0) {
			x = tile.getX() - 1;
		} else {
			x = tile.getX();
		}
		
		y = tile.getY() - 1;
		
		if (x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(4, board[x][y]);
		}
		
		// get the adjacent tile in the 5th direction
		if (tile.getY() % 2 == 0) {
			x = tile.getX() + 1;
		} else {
			x = tile.getX();
		}
		
		y = tile.getY() - 1;
		
		if (x >= 0 && x < 13 && y >= 0 && y < 13 && board[x][y] != null && !adjacentTiles.contains(board[x][y])) {
			//adjacentTiles.add(board[x][y]);
			adjacentTiles.set(5, board[x][y]);
		}
		
		return adjacentTiles;
	}

	/**
	 * Gets a list of all tiles in range of the given tile.
	 * If range is 1 (one), then the result should be same as getAdjacentTiles.
	 * NOTE: resulting list does NOT include the input tile.
	 * @param tile
	 * @param range a number no greater than 3
	 * @return list of tiles
	 */
	private List<Tile> getTilesWithinRange(Tile tile, int range) {
		/* Check parameters */
		if (tile == null || range > 3 || range < 1) {
			return null;
		}
				
		List<Tile> tilesInRange = new LinkedList<Tile>();
		List<Tile> range1 = new LinkedList<Tile>();
		List<Tile> range2 = new LinkedList<Tile>();
		List<Tile> range3 = new LinkedList<Tile>();
		
		switch (range) {
		case 1:
			range1.addAll(getAdjacentTiles(tile));
			break;
		case 2:
			range1.addAll(getAdjacentTiles(tile));
			for (Tile curTile : range1) {
				if (curTile != null) {
					range2.addAll(getAdjacentTiles(curTile));
				}
			}
			break;
		case 3:
			range1.addAll(getAdjacentTiles(tile));
			
			for (Tile curTile : range1) {
				if (curTile != null) {
					range2.addAll(getAdjacentTiles(curTile));
				}
			}
			
			for (Tile curTile : range2) {
				if (curTile != null) {
					range3.addAll(getAdjacentTiles(curTile));
				}
			}
			break;
		default: // Do nothing
		}

		tilesInRange.addAll(range1);
		tilesInRange.addAll(range2);
		tilesInRange.addAll(range3);
		
		return tilesInRange;
	}
	
	
	/** 
	 * Checks for a stalemate.
	 */
	private void checkStalemate() {
		System.out.print("SERVER: Checking stalemate... ");
		boolean stalemate = false;
		
		for (int i = 0; i < this.numPlayers; i++) {
			
			PlayerInfo curPlayer = this.playerInfo.get(i);
			
			if (curPlayer.isPlayerAlive() && curPlayer.isAI()) {
				
				//PlayerStats curStats = this.playerStats.get(i);
				
				if ((curPlayer.getTurnsSinceLastFire() >= 3) || // condition a
						(curPlayer.getTurnsSinceLastMove() >= 3) || // condition b
						(curPlayer.getDamageDealt() < 0)) { // condition c
					stalemate = true;
					break;
				}
			}
		}
		
		this.gameResolved = stalemate;
		System.out.println(stalemate);
	}

	/**
	 * Returns the playerID of the winner.
	 * @return player ID of the winner if there is one, -1 otherwise.
	 */
	private int getWinner() {
		List<Integer> potentialWinner = new ArrayList<Integer>(3);
		
		for (PlayerInfo curPlayer : this.playerInfo) {
			if (curPlayer.isPlayerAlive()) {
				potentialWinner.add(curPlayer.getPlayerID());
			}
		}
		
		if (potentialWinner.size() == 1) {
			// We have a winner!
			this.gameResolved = true;
			return potentialWinner.get(0);
		} else {
			// No winner yet
			this.gameResolved = false;
			return -1;
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof StartGameMsg) {
			System.out.println("Starting RoboChess...");
			System.out.println("\t" + numPlayers + " player game");
			System.out.println("\t" + numAIPlayers + " AI's active");
			System.out.println("\t" + mapSize + " hex map");
			
			int round = 1;
			// Loop per round
			do {
				System.out.println("SERVER: Begin Round " + round);
				
				// Reset movement points for all robots of all players
				System.out.println("SERVER: Replenish MP for all robots.");
				for (PlayerInfo player : this.playerInfo) {
					player.refreshRobots();
				}
				
				int curRobotType = 0;
				do {
					// Determine the next player.
					curPlayerID = this.getNextPlayer();
					
					// Declare PlayerStats and PlayerInfo for easy access.
					//PlayerStats curPlayerStats = this.playerStats.get(curPlayerID);
					PlayerInfo curPlayerInfo = this.playerInfo.get(curPlayerID);
					
					if (curPlayerInfo.getMoveableRobots().size() <= 0) {
						continue;
					}
					
					// Determine the next robot.
					curRobotType = this.getNextMoveableRobot(curPlayerID);
					
					// Declare Robot and robot stats for easy access.
					Robot curRobot = curPlayerInfo.getRobot(curRobotType);
					int curRobotAttack = refSheet.getAttack(curRobotType);
					int curHealthLeft = curRobot.getHealth();
					int curMovesLeft = curRobot.getMovement();
					int curRobotRange = refSheet.getRange(curRobotType);
					Point curRobotLocPoint = curRobot.getLocation();
					Tile curRobotLocTile = board[curRobotLocPoint.x][curRobotLocPoint.y];
					List<Robot> lastScanResults = new LinkedList<Robot>();
					
					// Determine timeout length. AI players get 5 seconds, humans get 3 minutes.
					Timeout timeout = curPlayerInfo.isAI() ? timeoutAI : timeoutHuman;
					
					System.out.println("SERVER: Active Player: " + curPlayerID);
					System.out.println("        Active robot: " + curRobotType);
					System.out.println("        Attack: " + curRobotAttack);
					System.out.println("        HP: " + curHealthLeft);
					System.out.println("        MP: " + curMovesLeft);
					
					// Loop until server receives an EndTurnMsg or until timeout or while player still has movement points left.
					Object reply = null;
					Future<Object> future = null;
					do {
						updateFOW(curPlayerID);

						/*
						 * SEND MESSAGES HERE.
						 * Either a ContinueMsg or reply to a previous message and wait for a respons.
						 */
						try {
							if (reply instanceof CheckMsg) {
								System.out.println("SERVER: Received CheckMsg");
								CheckMsg checkMsg = (CheckMsg) reply;
								int direction = checkMsg.id;

								String status = "EMPTY"; // TODO: determine what is in the adjacent space

								ReplyCheckMsg msg = new ReplyCheckMsg(curRobotType, status);
								future = Patterns.ask(getSender(), msg, timeout);
								
								System.out.println("SERVER: Sending Player #" + curPlayerID + " a reply to a CheckMsg request.");
							} else if (reply instanceof HealthLeftMsg) {
								System.out.println("SERVER: Received HealthLeftMsg");
								
								ReplyHealthLeftMsg msg = new ReplyHealthLeftMsg(curRobotType, curHealthLeft);
								future = Patterns.ask(getSender(), msg, timeout);
								
								System.out.println("SERVER: Sending Player #" + curPlayerID + " a reply to a HealthLeftMsg request.");
							} else if (reply instanceof IdentifyMsg) {
								System.out.println("SERVER: Received IdentifyMsg");
								IdentifyMsg identifyMsg = (IdentifyMsg) reply;

								Robot targetRobot = lastScanResults.get(identifyMsg.target);
								PlayerInfo targetPlayer = playerInfo.get(targetRobot.getPlayerID());

								int targetHealth = targetRobot.getHealth();
								int targetDirection = targetRobot.getDirection();
								int targetRange = refSheet.getRange(targetRobot.getType());
								String targetColor = refSheet.getTeamColor(targetPlayer.getPlayerID());

								ReplyIdentifyMsg msg = new ReplyIdentifyMsg(curRobotType, targetColor, targetRange, targetDirection, targetHealth);
								future = Patterns.ask(getSender(), msg, timeout);
								
								System.out.println("SERVER: Sending Player #" + curPlayerID + " a reply to a IdentifyMsg request.");
							} else if (reply instanceof MovesLeftMsg) {
								System.out.println("SERVER: Received MovesLeftMsg");
								
								ReplyMovesLeftMsg msg = new ReplyMovesLeftMsg(curRobotType, curMovesLeft);
								future = Patterns.ask(getSender(), msg, timeout);
								
								System.out.println("SERVER: Sending Player #" + curPlayerID + " a reply to a MovesLeftMsg request.");
							} else if (reply instanceof ScanMsg) {
								System.out.println("SERVER: Received ScanMsg");

								for (Tile tile : getTilesWithinRange(curRobotLocTile, curRobotRange)) {
									List<Robot> robotsOnTile = tile.getRobots();
									lastScanResults.addAll(robotsOnTile); 
								}
								
								ReplyScanMsg msg = new ReplyScanMsg(curRobotType, lastScanResults.size());
								future = Patterns.ask(getSender(), msg, timeout);
								
								System.out.println("SERVER: Sending Player #" + curPlayerID + " a reply to a ScanMsg request.");
							} else {
								// Send ContinueMsg
								ContinueMsg msg = new ContinueMsg(board, curPlayerInfo, curRobotType);
								future = Patterns.ask(this.players.get(curPlayerID), msg, timeout);

								System.out.println("SERVER: Player #" + curPlayerID + " begin/continue your turn...");
							}
							
							reply = Await.result(future, timeout.duration());
						} catch (TimeoutException e) {
							// if timeout, go to next player
							System.out.println("SERVER: TIME'S UP!!! PLAYER " + curPlayerID + " ENDS TURN.");
							break; // breaks inner loop
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(-1); // End execution of game 
						}
						
						System.out.println("SERVER: Received reply/request from Player #" + curPlayerID);
						
						/*
						 * RECEIVE MESSAGES HERE. (WARNING: NO OUTGOING MESSAGES IN THIS SECTION)
						 * Once a request is received, determine what kind of request it is.
						 * Then service the request accordingly. 
						 * DO NOT REPLY TO THE MESSAGE HERE.
						 */
						if (reply instanceof MoveMsg) {
							System.out.println("SERVER: Received MoveMsg");
							if (curPlayerInfo.isAI()) {
								curMovesLeft -= move(curRobotType); 
							} else {
								MoveMsg moveMsg = (MoveMsg) reply;
								Point destination = moveMsg.destination;
								curRobot.decrementMovement(move(destination, curRobotType));
							}
							
							updateFOW(curPlayerID);
						} else if (reply instanceof ShootMsg) {
							System.out.println("SERVER: Received ShootMsg");
							ShootMsg shootMsg = (ShootMsg) reply;
							
							boolean success;
							if (curPlayerInfo.isAI()) {
								success = fire(curRobotType, shootMsg.id, shootMsg.ir);
							} else {
								success = fire(curRobotType, shootMsg.target);
							}
							curRobot.setFired(true);
						} else if (reply instanceof TurnMsg) {
							System.out.println("SERVER: Received ShootMsg");
							TurnMsg turnMsg = (TurnMsg) reply;
							curRobot.turnDirection(turnMsg.id);
						}
						
						checkStalemate();
						if (this.gameResolved) { // if stalemate
							break;
						} else {
							// check if someone has won.
							this.winnerIdx = getWinner();
						}
					} while (!(reply instanceof EndTurnMsg) && curRobot.getMovement() > 0 && !curRobot.hasFired() && !gameResolved);
					System.out.println("SERVER End Turn");
				} while (curRobotType != -1 && !gameResolved);
				
				System.out.println("SERVER End Round " + round);
				System.out.println("SERVER: Game resolved... " + gameResolved);
				++round;
			} while (!gameResolved);
			
			if (winnerIdx == -1) {
				// TODO: Handle stalemate
			} else {
				// TODO: Handle victory
			}
		} else if (message instanceof PlayAgainMsg) {
			init();
			getSelf().tell(new PlayAgainMsg(), getSelf());
		} else {
			// Game does not start.
			throw new InvalidMessageException ("Invalid/unknown message: " + message);
		}
	}
}
