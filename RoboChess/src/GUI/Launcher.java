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
 * Class: Launcher
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Messages.StartGameMsg;
import Server.Server;
import Utilities.Assets;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Initializes and launches the game and all assets, opening it to the main menu.
 * @version 1.02
 * @author Jordan Nelson, Ryan Park
 */
public class Launcher implements ActionListener {
	
	/** The main frame used for the game. */
	private CreateFrame frame;
	
	/** The current panel the menu is displaying. */
	private DisplayPanel panel; 
	
	/** A list of options passed to the server upon construction. */
	private int numHumans;
	
	/** A panel containing the options sub menu. */
	private Options options;
	
	/** Size of the game map. */
	private int mapSize;
	
	/**
	 * Constructs the Launcher class
	 * @param width the width for the frame 
	 * @param height the height for the frame
	 * @param title the title to display on the top of the frame
	 */
	public Launcher(int width, int height, String title) {
		/* Initialize frame and display loading screen. */
		frame = new CreateFrame(width, height, title);
		showLoadScreen();
		
		/* Initialize gui assets */
		Assets.init();
		options = new Options(frame.getWidth(), frame.getHeight(), this);
		numHumans = 1;
		mapSize = 5; // TODO: allow player to decide this.
		
		/* Display main menu */
		showMainMenu();
	}
	
	/**
	 * Displays a given panel on the frame
	 * @param panel the panel to display
	 */
	private void displayPanel(DisplayPanel panel) {
		frame.getFrame().getContentPane().removeAll();
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
	 * Displays the loading screen on the frame
	 */
	public void showLoadScreen() {
		panel = new TransitionPanel(frame.getWidth(), frame.getHeight());
		((TransitionPanel) panel).loadingScreen();
		displayPanel(panel);
	}
	
	/**
	 * Displays the main menu panel on the frame
	 */
	public void showMainMenu() {
		panel = new MainMenu(frame.getWidth(), frame.getHeight(), this);
		displayPanel(panel);
	}
	
	/**
	 * Displays the CreateGame panel on the frame
	 */
	public void showCreateGame() {
		panel = new CreateGame(frame.getWidth(), frame.getHeight(), this);
		displayPanel(panel);
	}
	
	/**
	 * Displays the options panel on the frame
	 */
	public void showOptions() {
		displayPanel(options);
	}
	
	/**
	 * Shows the two player game set up screen.
	 */
	public void showTwoPlayer() {
		panel = new TwoPlayerPanel(frame.getWidth(), frame.getHeight(), this);
		displayPanel(panel);
	}
	
	/**
	 * Shows the three player game set up screen.
	 */
	public void showThreePlayer() {
		panel = new ThreePlayerPanel(frame.getWidth(), frame.getHeight(), this);
		displayPanel(panel);
	}
	
	/**
	 * Shows the six player game set up screen.
	 */
	public void showSixPlayer() {
		panel = new SixPlayerPanel(frame.getWidth(), frame.getHeight(), this);
		displayPanel(panel);
	}

	/** 
	 * Creates akka system, server actor and then starts the game.
	 * @param numPlayers
	 * @param numAIPlayers
	 * @param mapSize
	 */
	private void startGame(int numPlayers, int numAIPlayers, int mapSize) {
		// Create actor system
		ActorSystem roboChessSystem = ActorSystem.create("ROBOCHESS_SYSTEM");

		// Create server actor
		ActorRef serverRef = roboChessSystem.actorOf(
				Props.create(Server.class, frame, Options.DEBUG_MODE, options.SPECTATOR_MODE, numPlayers, numAIPlayers, mapSize),
				"SERVER");

		// Send message to server to start the game
		serverRef.tell(new StartGameMsg(), null);
	}
	
	/**
	 * Called with user input. Interprets input and reacts accordingly.
	 */
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand(); // The action performed
		
		switch (command) {
		case "createGame":
			showCreateGame();
			break;
		case "options":
			showOptions();
			break;
		case "quit":
			System.exit(0);
		case "mainMenu":
			showMainMenu();
			break;
		case "two":
			showTwoPlayer();
			break;
		case "three":
			showThreePlayer();
			break;
		case "six":
			showSixPlayer();
			break;
		case "starttwo":
			this.startGame(2, 2 - numHumans, 5);
			break;
		case "startthree":
			this.startGame(3, 3 - numHumans, mapSize);
			break;
		case "startsix":
			this.startGame(6, 6 - numHumans, 7);
			break;
		case "noHuman":
			numHumans = 0;
			break;
		case "oneHuman":
			numHumans = 1;
			break;
		case "twoHumans":
			numHumans = 2;
			break;
		case "threeHumans":
			numHumans = 3;
			break;
		case "fourHumans":
			numHumans = 4;
			break;
		case "fiveHumans":
			numHumans = 5;
			break;
		case "sixHumans":
			numHumans = 6;
			break;
		case "fiveSpaces":
			mapSize = 5;
			break;
		case "sevenSpaces":
			mapSize = 7;
			break;
		default:
			throw new IllegalStateException("Invalid command: " + command);
		}
	}
	
	public static void main(String[] args) {
		/* Entry point for RoboChess program */
		new Launcher(1000, 500, "RoboChess");
	}
}
