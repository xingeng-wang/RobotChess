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
 * Class: Player
 */
package Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.simple.JSONObject;

import GUI.CreateFrame;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import Interpreter.Interpreter;
import Messages.*;
import Server.ReferenceSheet;
import Server.Robot;

/**
 * This class is responsible for redirecting message between the Server and the 
 * GUIHandler or Interpreter (depending on human or AI player).
 * 
 * @version 1.03
 * @author Ryan Park
 */
public class Player extends UntypedActor {

	/** Specifies if the player is AI controlled or not */
	private boolean AI;
	
	/** This players ID */
	private int playerID;
	
	/** Akka actor reference to the server. Used to send messages to the server. */
	private ActorRef serverRef;
	
	/** 
	 * A list of interpreters ActorRef. 
	 * Only AI players have this. 
	 */
	private List<ActorRef> interpreters;
	
	/** 
	 * A list of mailbox queues. 
	 * First mailbox is for the SCOUT, second for SNIPER, third for TANK.
	 * Only AI players have this.
	 */
	private List<Queue<String>> mailboxes;
	
	/** GUI handler. Used to send messages to the GUI handler. */
	private PlayerGUIHandler GUIHandler;
	
	/**
	 * The constructor for the Player class.
	 */
	public Player(boolean AI, int playerID, String teamColor, CreateFrame frame, ActorRef serverRef, JSONObject scout, JSONObject sniper, JSONObject tank) {
		this.AI = AI;
		this.playerID = playerID;
		this.serverRef = serverRef;
		this.mailboxes = new ArrayList<Queue<String>>(3);
		this.interpreters = new ArrayList<ActorRef>(3);
		
		if (this.AI) {
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Scout
			JSONObject scoutJsonObj = scout;
			ActorRef scoutRef = getContext().actorOf(
					Props.create(Interpreter.class, scoutJsonObj, 0, teamColor, getSelf(), this.mailboxes), 
					"SCOUT_INTERPRETER_" + playerID);
			this.interpreters.add(scoutRef);
			
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Sniper
			JSONObject sniperJsonObj = sniper;
			ActorRef sniperRef = getContext().actorOf(
					Props.create(Interpreter.class, sniperJsonObj, 1, teamColor, getSelf(), this.mailboxes), 
					"SNIPER_INTERPRETER_" + playerID);
			this.interpreters.add(sniperRef);
			
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Tank
			JSONObject tankJsonObj = tank;
			//JSONObject tankJsonObj = "./res/ForthScripts/sample_random.json";
			ActorRef tankRef = getContext().actorOf(
					Props.create(Interpreter.class, tankJsonObj, 2, teamColor, getSelf(), this.mailboxes), 
					"TANK_INTERPRETER_" + playerID);
			this.interpreters.add(tankRef);
		} else {
			GUIHandler = new PlayerGUIHandler(playerID, getSelf(), serverRef, frame);
		}
	}
	
	/**
	 * An overloaded constructor for the Player class accepting filepaths for scripts.
	 */
	public Player(boolean AI, int playerID, String teamColor, CreateFrame frame, ActorRef serverRef, String scout, String sniper, String tank) {
		this.AI = AI;
		this.playerID = playerID;
		this.serverRef = serverRef;
		this.mailboxes = new ArrayList<Queue<String>>(3);
		this.interpreters = new ArrayList<ActorRef>(3);
		
		if (this.AI) {
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Scout
			String scoutScript = scout;
			ActorRef scoutRef = getContext().actorOf(
					Props.create(Interpreter.class, scoutScript, 0, teamColor, getSelf(), this.mailboxes), 
					"SCOUT_INTERPRETER_" + playerID);
			this.interpreters.add(scoutRef);
			
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Sniper
			String sniperScript = sniper;
			ActorRef sniperRef = getContext().actorOf(
					Props.create(Interpreter.class, sniperScript, 1, teamColor, getSelf(), this.mailboxes), 
					"SNIPER_INTERPRETER_" + playerID);
			this.interpreters.add(sniperRef);
			
			this.mailboxes.add(new LinkedList<String>()); // Create mailbox for Tank
			String tankScript = tank;
			ActorRef tankRef = getContext().actorOf(
					Props.create(Interpreter.class, tankScript, 2, teamColor, getSelf(), this.mailboxes), 
					"TANK_INTERPRETER_" + playerID);
			this.interpreters.add(tankRef);
		} else {
			GUIHandler = new PlayerGUIHandler(playerID, getSelf(), serverRef, frame);
		}
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof ContinueMsg) {
			this.serverRef = getSender();
			ContinueMsg msg = (ContinueMsg) message;
			
			if (this.AI) {
				System.out.println("PLAYER " + playerID + " (AI): Received ContinueMsg");
				this.interpreters.get(msg.robotType).tell(msg, getSelf());
			} else {
				System.out.println("PLAYER " + playerID + ": Received ContinueMsg");
				Robot curRobot = msg.playerInfo.getRobot(msg.robotType);
				ReferenceSheet refSheet = new ReferenceSheet();
				
				int remainingHealth = curRobot.getHealth();
				int movementPoints = curRobot.getMovement();
				int range = refSheet.getRange(msg.robotType); 
				int attackDamage = refSheet.getAttack(msg.robotType);
				
				this.GUIHandler.updateSender(getSender());
				//this.GUIHandler.clearLog();
				this.GUIHandler.updateBoard(msg.board, msg.robotType);
				this.GUIHandler.updateRobotStats(remainingHealth, movementPoints, range, attackDamage);
				this.GUIHandler.post("Your turn!");
				
				/*while (!this.GUIHandler.getCommand().equals("confirm")) {
					//Thread.sleep(1000);
				}*/
				Thread.sleep(1000);
			}
		} else if (message instanceof ReplyHealthLeftMsg) {
			this.serverRef = getSender();
			ReplyHealthLeftMsg msg = (ReplyHealthLeftMsg) message;
			if (!this.AI) {
				throw new InvalidMessageException ("A human player received: ReplyHealthLeftMsg");
			} 
			this.interpreters.get(msg.robotType).tell(msg, getSelf());
		} else if (message instanceof ReplyMovesLeftMsg) {
			this.serverRef = getSender();
			ReplyMovesLeftMsg msg = (ReplyMovesLeftMsg) message;
			if (!this.AI) {
				throw new InvalidMessageException ("A human player received: ReplyMovesLeftMsg");
			} 
			this.interpreters.get(msg.robotType).tell(msg, getSelf());
		} else if (message instanceof ReplyCheckMsg) { 
			this.serverRef = getSender();
			ReplyCheckMsg msg = (ReplyCheckMsg) message;
			if (!this.AI) {
				throw new InvalidMessageException ("A human player received: ReplyCheckMsg");
			}
			this.interpreters.get(msg.robotType).tell(msg, getSelf());
		} else if (message instanceof ReplyScanMsg) { 
			this.serverRef = getSender();
			ReplyScanMsg msg = (ReplyScanMsg) message;
			if (!this.AI) {
				throw new InvalidMessageException ("A human player received: ReplyScanMsg");
			}
			this.interpreters.get(msg.robotType).tell(msg, getSelf());
		} else if (message instanceof ReplyIdentifyMsg) { 
			this.serverRef = getSender();
			ReplyIdentifyMsg msg = (ReplyIdentifyMsg) message;
			if (!this.AI) {
				throw new InvalidMessageException ("A human player received: ReplyIdentifyMsg");
			}
			this.interpreters.get(msg.robotType).tell(msg, getSelf());
		} else if (message instanceof LogMsg) {
			// TODO: this is messing things up.
			//this.GUIHandler.post(((LogMsg) message).log);
		} else if (message instanceof PlayAgainMsg) {
			this.serverRef = getSender();
			// Re-initialize mailboxes
			this.mailboxes = new ArrayList<Queue<String>>(3);
			this.mailboxes.add(new LinkedList<String>());
			this.mailboxes.add(new LinkedList<String>());
			this.mailboxes.add(new LinkedList<String>());
		} else if (message instanceof EndTurnMsg || 
				message instanceof HealthLeftMsg ||
				message instanceof MovesLeftMsg ||
				message instanceof TurnMsg ||
				message instanceof MoveMsg ||
				message instanceof ShootMsg ||
				message instanceof CheckMsg ||
				message instanceof ScanMsg ||
				message instanceof IdentifyMsg) {
			this.serverRef.tell(message, getSelf());
		} else {
			throw new InvalidMessageException ("Invalid/unknown message: " + message);
		}
		
		System.out.println("PLAYER " + playerID + ": End onReceive");
	}
} 