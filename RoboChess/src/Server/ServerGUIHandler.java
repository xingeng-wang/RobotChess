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
 * Class: ServerGUIHandler
 */
package Server;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import GUI.CreateFrame;
import GUI.DisplayPanel;
import GUI.EndGamePanel;
import GUI.Launcher;
import GUI.LogPanel;
import GUI.StatsPanel;
import GUI.TransitionPanel;
import Messages.PlayAgainMsg;
import Player.PlayerGUIHandler;
import Player.PlayerInfo;
import akka.actor.ActorRef;

/**
 * Communicates between the server and all GUI classes the server handles. This includes the
 * end game screen, statistics screen, and more. The class is responsible for user input across those panels,
 * and interpreting commands from the server to display individual panels.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class ServerGUIHandler implements ActionListener {

	/** A stats panel used by the server */
	private LinkedList<StatsPanel> statsPanels;
	
	/** An endGamePanel used by the server */
	private EndGamePanel endGame;
	
	/** Stores the frame the game is displayed on */
	private CreateFrame frame;
	
	/** Stores a reference to the server */
	private ActorRef server;
	
	/** An index to keep track of which stats page to show */
	private int statsIndex;
	
	/**
	 * Creates an object that handles GUI panels displayed by the server after game completion.
	 * @param frame the frame the GUI is displayed to
	 * @param server the server calling this class (will be null if DEBUG_MODE = true)
	 * @param statistics a list of statistics to display (will be null unless DEBUG_MODE = true)
	 * @param DEBUG_MODE tells the handler to test rather than use normally
	 */
	public ServerGUIHandler(ActorRef serverRef, CreateFrame frame, List<PlayerInfo> statistics) {
		frame.getFrame().setSize(1000, 500);
		this.frame = frame;
		this.server = serverRef;
		
		statsPanels = new LinkedList<StatsPanel>();
		
		List<PlayerInfo> stats = statistics;
		
		for(int i = 0; i < stats.size(); i++) {
			statsPanels.add(new StatsPanel(frame.getWidth(), frame.getHeight(), stats.get(i), this));
		}
		statsIndex = 0;
		
	}
	
	/**
	 * Displays the end game screen.
	 * @param winningPlayerID - the ID of the winning player
	 */
	public void endGame(int winningPlayerID) {
		endGame = new EndGamePanel(frame.getWidth(), frame.getHeight(), this, winningPlayerID);
		displayPanel(endGame);
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("playAgain")) {
			server.tell(new PlayAgainMsg(), server);
		} else if (command.equals("statistics")) {
			displayPanel(statsPanels.get(statsIndex));
			
		} else if (command.equals("quit")) {
			System.exit(0);
			
		} else if (command.equals("prev")) {
			statsIndex--;
			if(statsIndex < 0) statsIndex = statsPanels.size() - 1;
			displayPanel(statsPanels.get(statsIndex));
			
		} else if (command.equals("next")) {
			statsIndex++;
			if(statsIndex >= statsPanels.size()) statsIndex = 0;
			displayPanel(statsPanels.get(statsIndex));
			
		} else if (command.equals("back")) {
			displayPanel(endGame);
		}
	}
	
	public static void main(String[] args) {
		//Create and set up the window.
        CreateFrame frame = new CreateFrame(1000, 500, "TEST");
        
        /*LinkedList<PlayerStats> stats = new LinkedList<PlayerStats>();
        
        PlayerStats stat1 = new PlayerStats();
        stat1.setDamageDealt(1);
        stat1.setDamageTaken(1);
        stat1.setRobotsDestroyed(1);
        stat1.setTilesMoved(1);
        stats.add(stat1);
        
        PlayerStats stat2 = new PlayerStats();
        stat2.setDamageDealt(2);
        stat2.setDamageTaken(2);
        stat2.setRobotsDestroyed(2);
        stat2.setTilesMoved(2);
        stats.add(stat2);
        
        PlayerStats stat3 = new PlayerStats();
        stat3.setDamageDealt(3);
        stat3.setDamageTaken(3);
        stat3.setRobotsDestroyed(3);
        stat3.setTilesMoved(3);
        stats.add(stat3);
        
        ServerGUIHandler handler = new ServerGUIHandler(null, frame, stats);
        handler.endGame(2);*/
	}
}
