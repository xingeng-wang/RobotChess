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
 * Class: PlayerGUIHandler
 */
package Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import GUI.ControlPanel;
import GUI.CreateFrame;
import GUI.DisplayPanel;
import GUI.GamePanel;
import GUI.LogPanel;
import Messages.ContinueMsg;
import Messages.EndTurnMsg;
import Messages.MoveMsg;
import Messages.ShootMsg;
import Server.Robot;
import Server.Tile;
import Utilities.Assets;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * Communicates between the player and all GUI classes the player handles.
 * The class is therefor responsible for interpreting player input during the main game session,
 * as well as commanding the panels that display the game board, log, and control buttons.
 * 
 * @version 1.02
 * @author Jordan Nelson, Ryan Park
 */
public class PlayerGUIHandler implements ActionListener {
	
	/** Variables used for the ControlPanel GUI class. */
	private boolean moveClicked, fireClicked, endTurnClicked;
	
	/** A panel displaying a log to the screen */
	private LogPanel logPanel;
	
	/** A panel showing the controls along the bottom of the screen. */
	private ControlPanel controlPanel;
	
	/** A panel showing the main game board. */
	private GamePanel gamePanel;
	
	/** The main panels that displays all the other panels on it. */
	private DisplayPanel panel;
	
	private int playerID;
	
	/** The Akka ActorRef of the player controlling the handler. Needed to send messages impersonating as player. */
	private ActorRef player;
	
	/** The Akka ActorRef of the Server. Needed send messages to the Server. */
	private ActorRef server;
	
	private ActorRef lastSender;
	
	/** The hex that was clicked, used when confirm is clicked. Can be null. */
	private Point clickedHex;
	
	/** The current robotType, needed to communicate back to the player */
	private int robotType;
	
	/** Time to wait for server to reply */
	private final Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
	
	private String command;
	
	/**
	 * Constructs a playerGUIHandler
	 * @param player the player who's GUI is being handled
	 */
	public PlayerGUIHandler(int playerID, ActorRef playerRef, ActorRef serverRef, CreateFrame frame) {
		frame.getFrame().setSize(1000, 700);
		frame.getFrame().setLocationRelativeTo(null);
		frame.getFrame().getContentPane().removeAll();
		
		this.playerID = playerID;
		this.player = playerRef;
		this.server = serverRef;
		
		moveClicked = false;
		fireClicked = false;
		endTurnClicked = false;
		
		gamePanel = new GamePanel(frame.getWidth() - 400, 350, this);
		logPanel = new LogPanel(400, 500);
		controlPanel = new ControlPanel(frame.getWidth() - 400, 150, this);
		controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
		
		panel = new DisplayPanel();
		panel.setSize(frame.getWidth(), frame.getHeight());
		panel.setBackground(Color.WHITE);
		panel.setLayout(null);
		
		panel.add(gamePanel);
		panel.add(controlPanel);
		panel.add(logPanel);
		
		gamePanel.setLocation(0, 0);
		controlPanel.setLocation(0, 600 - 16);
		logPanel.setLocation(602, 0);
		
		gamePanel.setSize(800, 600 - 16);
		controlPanel.setSize(800, 100);
		logPanel.setSize(600, 700 - 16);
		
		frame.getFrame().getContentPane().add(panel, BorderLayout.CENTER);
		frame.getFrame().getContentPane().validate();
		Component focusComponent = panel.getFocusComponent();
		if (focusComponent != null) {
			focusComponent.requestFocusInWindow();
		}
		frame.getFrame().repaint();
		frame.getFrame().setVisible(true);
	}
	
	
	/**
	 * Update the robot's stats on the screen
	 * @param remainingHealth the robot's remaining health
	 * @param movementPoints the robot's remaining movement points
	 * @param range the robot's range
	 * @param attackDamage the robot's damage
	 */
	public void updateRobotStats(int remainingHealth, int movementPoints, int range, int attackDamage) {
		controlPanel.updateStats(remainingHealth, movementPoints, range, attackDamage);
	}
	
	
	/**
	 * Posts a log to the logPanel.
	 * @param log the message to post to the panel
	 */
	public void post(String log) {
		this.logPanel.post(log);
	}
	
	
	/**
	 * Clears the log of all messages.
	 */
	public void clearLog() {
		this.logPanel.clearLog();
	}
	
	
	/**
	 * Updates the board on the main game panel.
	 * @param board the new board to be drawn
	 */
	public void updateBoard(Tile[][] board, int robotType) {
		this.robotType = robotType;
		gamePanel.update(board);
	}
	
	public void updateSender(ActorRef sender) {
		this.lastSender = sender;
	}
	
	
	public void hexClicked(Point hex) {
		clickedHex = hex;
		System.out.println("x: " + hex.x + " y: " + hex.y);
		controlPanel.requestFocus();
	}
	
	public String getCommand() {
		return this.command;
	}
	
	/**
	 * A function that handles user input from the GUI, which is called automatically when the user 
	 * inputs a given command. The function then interprets the command and acts accordingly.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		this.command = event.getActionCommand(); //The action performed
		
		if (command.equals("move")) {
			moveClicked = true;
			fireClicked = false;
			endTurnClicked = false;
			controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
		} else if(command.equals("fire")) {
			moveClicked = false;
			fireClicked = true;
			endTurnClicked = false;
			controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
		} else if(command.equals("endTurn")) {
			moveClicked = false;
			fireClicked = false;
			endTurnClicked = true;
			controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
		} else if(command.equals("back")) {
			moveClicked = false;
			fireClicked = false;
			endTurnClicked = false;
			
			clickedHex = null;
			controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
		} else if(command.equals("confirm")) {
			if(moveClicked) {
				if(clickedHex != null) {
					moveClicked = false;
					controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
					
					System.out.println(String.format("PLAYER %d: Requesting to move (%d, %d)", playerID, clickedHex.x, clickedHex.y));
					lastSender.tell(new MoveMsg(clickedHex), player);
					clickedHex = null;
				} else {
					gamePanel.requestFocus();
				}
				
			} else if (fireClicked && clickedHex != null) {
				if(clickedHex != null) {
					fireClicked = false;
					controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
				
					lastSender.tell(new ShootMsg(clickedHex), player);
					System.out.println(String.format("PLAYER %d: Firing at (%d, %d)", playerID, clickedHex.x, clickedHex.y));
					clickedHex = null;
				} else {
					gamePanel.requestFocus();
				}
			} else if (endTurnClicked) {
				if(clickedHex == null) {
					endTurnClicked = false;
					controlPanel.updateButtons(moveClicked, fireClicked, endTurnClicked);
				
					lastSender.tell(new EndTurnMsg(), player);
					System.out.println(String.format("PLAYER %d: End turn", playerID));
					clickedHex = null;
				} else {
					gamePanel.requestFocus();
				}
			}
		}

	}

	public static void main(String[] args) {
		//Create and set up the window.
        CreateFrame frame = new CreateFrame(1000, 700, "TEST");
        
        PlayerGUIHandler handler = new PlayerGUIHandler(0, null, null, frame);
        handler.updateRobotStats(2, 3, 3, 1);
        handler.controlPanel.updateButtons(false, false, false);
        
        
        Assets.init();
        Tile[][] board = new Tile[13][13];
        
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
        			board[x][y] = new Tile();
        			board[x][y].setVisibility(true);
        		}
        	}
        }
        
		
		

		board[3][0].setVisibility(true);
		board[4][0].setVisibility(true);
		board[3][1].setVisibility(true);
		board[4][1].setVisibility(true);
		board[5][1].setVisibility(true);
		board[3][2].setVisibility(true);
		board[4][2].setVisibility(true);

		Robot robot1 = new Robot(1, 0);
		Robot robot2 = new Robot(1, 1);
		Robot robot3 = new Robot(1, 2);
		board[3][1].addRobot(robot1);
		board[3][1].addRobot(robot3);
		board[3][1].addRobot(robot2);
		
		handler.updateBoard(board, 0);
		
		frame.getFrame().setVisible(true);
		
		handler.post("Hello!");
		handler.updateBoard(board, 0);
		
		for(int i = 0; i < 100; i++) handler.post("Hello.");
	}
}


