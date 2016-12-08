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
 * Class: Interpreter
 */
package Interpreter;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Messages.*;
import Server.ReferenceSheet;


/**
 * A representation of a Forth language interpreter. 
 * Each AI player thread should have 3 instances of this interpreter (one for
 * each robot type). Upon creation, the given script is parsed into a list of 
 * Forth words.
 * 
 * @version 1.02
 * @author Ryan Park
 */
public class Interpreter extends UntypedActor {

	/** The JSON object containing the script */
	private JSONObject jsonObj;
	
	/** The directory path to the saved script */
	private String scriptPath;
	
	/** The robot type of this interpreter */
	private int robotType;
	
	/** The team color of this interpreter */
	private String teamColor;
	
	/** The actor reference of the player class. Used to send messages (via Akka library) */
	private ActorRef player;
	
	/** The list of mailbox queues used by robots to communicate with each other */
	private List<Queue<String>> mailboxes;
	
	/** Static ReferenceSheet class */
	private final ReferenceSheet refSheet = new ReferenceSheet();
	
	/** The number of seconds this interpreter should wait before timing out */
	private final Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
	
	/** A stack used to interpret Forth scripting language */
	private Stack<String> stack;
	
	/** A parsed list of words for a given script */
	private List<String> words;
	
	/** A map of conditional branches */
	private BiMap<String, Branch> branchPoints;
	
	/** A map of counted loops */
	private BiMap<String, CountedLoop> countedLoops;
	
	/** A map of counted loops */
	private BiMap<String, GuardedLoop> guardedLoops;
	
	/** A map to store user-defined words */
	private BiMap<String, List<String>> userDefinedWords;
	
	/** A map to store user-defined variables */
	private BiMap<String, String> userDefinedVariables;
	
	/**
	 * Constructor for Interpreter class
	 * @param scriptPath absolute path of script file location
	 * @param robotType the robot type of this interpreter
	 * @param player the ActorRef of the player
	 * @param mailbox the list of mailbox queues
	 */
	public Interpreter(String scriptPath, int robotType, String teamColor, ActorRef player, List<Queue<String>> mailboxes) {
		this.jsonObj = null;
		this.scriptPath = scriptPath;
		init(robotType, teamColor, player, mailboxes);
	}
	
	/**
	 * An overloaded constructor accepting JSON object instead of filepath.
	 * @param jsonObj
	 * @param robotType
	 * @param teamColor
	 * @param player
	 * @param mailboxes
	 */
	public Interpreter(JSONObject jsonObj, int robotType, String teamColor, ActorRef player, List<Queue<String>> mailboxes) {
		this.jsonObj = jsonObj;
		this.scriptPath = null;
		init(robotType, teamColor, player, mailboxes);
	}
	
	/**
	 * Handles initializing the interpreter. 
	 */
	private void init(int robotType, String teamColor, ActorRef player, List<Queue<String>> mailboxes) {
		this.robotType = robotType;
		this.teamColor = teamColor;
		this.player = player;
		this.mailboxes = mailboxes;
		this.stack = new Stack<String>();
		this.words = new LinkedList<String>();
		this.branchPoints = HashBiMap.create();
		this.countedLoops = HashBiMap.create();
		this.guardedLoops = HashBiMap.create();
		this.userDefinedWords = HashBiMap.create();
		this.userDefinedVariables = HashBiMap.create();
		
		parseFile();
		/*for (String word : this.words) {			
			System.out.println(word);
		}*/
		
		processIfElseBranch(this.words, 0);

		this.words.clear();
		this.words.addAll(this.stack);
		this.stack.clear();
		
		linkUserDefinedWords();
	}
	
	/**
	 * Helper function used by the constructor.
	 * Reads in characters from the file and creates Forth words and adds them
	 * to the words list. (Ignores comments)
	 */
	private void parseFile() {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		
		if (this.scriptPath == null) {
			jsonObject = this.jsonObj;
		} else {
			try {
				jsonObject = (JSONObject) jsonParser.parse(new FileReader(this.scriptPath));
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}			
		}

		JSONObject jsonScriptObject = (JSONObject) jsonObject.get("script");

		JSONArray code = (JSONArray) jsonScriptObject.get("code");
		
		Iterator<?> iterator = code.iterator();

		Pattern pattern = Pattern.compile(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");

		Splitter spaceSplitter = Splitter.on(pattern).omitEmptyStrings().trimResults();
		
		while (iterator.hasNext()) {
			Iterable<String> tokens = spaceSplitter.split((CharSequence) iterator.next());
			Iterator<?> tokensIterator = tokens.iterator();
			
			while (tokensIterator.hasNext()) {
				String curWord = (String) tokensIterator.next();
				
				if (!curWord.equals("(")) {
					this.words.add(curWord);
				} else {
					while (tokensIterator.hasNext() && !tokensIterator.next().equals(")")) {
						// nothing else needed here, call to next() in condition is sufficient
					}
				}
			}
		}
	}
	
	/**
	 * Processes conditional branches and loop bodies
	 * @param words
	 * @param startIdx
	 * @return the index of the current point of execution
	 */
	private int processIfElseBranch(List<String> words, int startIdx) {
		int curIdx = startIdx;
		
		while (curIdx < words.size()) {
			String curWord = (String) words.get(curIdx);
			
			if (curWord.equals("if") || curWord.equals("do") || curWord.equals("begin")) {
				stack.push(curWord);
				curIdx = processIfElseBranch(words, ++curIdx);
			} else if (curWord.equals("then")) {
				Branch branch = new Branch();
				String branchIdentifier = branch.getBranchID();
				
				// pop off stack until you get 'else'
				while (!stack.peek().equals("else")) {
					branch.getFalseBranch().add(stack.pop());
				}
				branch.setFalseBranch(Lists.reverse(branch.getFalseBranch()));
				
				stack.pop(); // pop off 'else'
				
				// pop off stack until you get 'if'
				while (!stack.peek().equals("if")) {
					branch.getTrueBranch().add(stack.pop());		
				}
				branch.setTrueBranch(Lists.reverse(branch.getTrueBranch()));

				
				stack.pop(); // pop off 'if'
				
				branchPoints.put(branchIdentifier, branch);
				stack.push("branch_" + branchIdentifier);
				return ++curIdx;
			} else if (curWord.equals("loop")) {
				CountedLoop cLoopBody = new CountedLoop();
				String cLoopIdentifier = cLoopBody.getCountLoopID();
				
				// pop off stack until you get 'do'
				do {
					cLoopBody.getBody().add(stack.pop());
				} while (!stack.peek().equals("do"));
				cLoopBody.setBody(Lists.reverse(cLoopBody.getBody()));
				
				stack.pop(); // pop off 'do'
				
				countedLoops.put(cLoopIdentifier, cLoopBody);
				stack.push("cLoop_" + cLoopIdentifier);
				return ++curIdx;
			} else if (curWord.equals("until")) {
				GuardedLoop gLoopBody = new GuardedLoop();
				String gLoopIdentifier = gLoopBody.getGuardLoopID();
				
				// pop off stack until you get 'do'
				do {
					gLoopBody.getBody().add(stack.pop());
				} while (!stack.peek().equals("begin"));
				gLoopBody.setBody(Lists.reverse(gLoopBody.getBody()));
				
				stack.pop(); // pop off 'begin'
				
				guardedLoops.put(gLoopIdentifier, gLoopBody);
				stack.push("gLoop_" + gLoopIdentifier);
				return ++curIdx;
			} else {
				stack.push(curWord);
				++curIdx;
			}
		}
		return curIdx;
	}
	
	/**
	 * Processes words list and links user-defined words.
	 * @param words list of Forth words
	 */
	private void linkUserDefinedWords() {
		Iterator<?> iterator = this.words.iterator();
		
		while (iterator.hasNext()) {
			String curWord = (String) iterator.next();
			
			if (curWord.equals(":")) {
				curWord = (String) iterator.next();

				String key = curWord;
				LinkedList<String> value = new LinkedList<String>();

				curWord = (String) iterator.next();
				do {
					value.add(curWord);
					curWord =  (String) iterator.next();
				} while (!curWord.equals(";") && iterator.hasNext());

				this.userDefinedWords.put(key, value);
			} else if (curWord.equals("variable")) {
				
				System.out.println(this.userDefinedVariables.toString());
				
				this.userDefinedVariables.put((String) iterator.next(), "0");
				
				System.out.println(this.userDefinedVariables.toString());
				iterator.next();
			} else {
				try {
					List<String> singleList = new LinkedList<String>();
					singleList.add(curWord);
					eval(singleList, singleList.get(0));
				} catch (InvalidForthWordException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Evaluates the given word, pushing/popping them onto/off the stack.
	 * @param word String representation of a valid Forth word
	 * @param lastWord last word that was executed (needed to handle I, loop counter)
	 * @return index of the current point of execution. A -1 indicates a kill switch to end execution.
	 * @throws InvalidForthWordException 
	 */
	private int eval(List<String> words, String lastWord) throws InvalidForthWordException {
		int curIdx = 0;
		
		while (curIdx < words.size()) {
			String curWord = (String) words.get(curIdx);
			++curIdx;
			
			if (curWord.isEmpty() || curWord.equals(null)) {
				continue;
			} else if (isLiteral(curWord) || this.userDefinedVariables.containsKey(curWord)) {
				// if Forth literal
				stack.push(curWord);
			} else if (userDefinedWords.containsKey(curWord)) {
				// if user-defined word
				eval(userDefinedWords.get(curWord), userDefinedWords.get(curWord).get(0));
			} else if (curWord.startsWith("branch_")) {
				// if branch point
				String branchId = curWord.split("_")[1];
				Branch targetBranch = this.branchPoints.get(branchId);
				
				if (Boolean.parseBoolean(stack.pop())) {
					eval(targetBranch.getTrueBranch(), targetBranch.getTrueBranch().get(0));
				} else {
					eval(targetBranch.getFalseBranch(), targetBranch.getFalseBranch().get(0));
				}
			} else if (curWord.equals("I")) {
				// if loop counter
				String loopId = lastWord.split("_")[1];
				CountedLoop targetCLoop = this.countedLoops.get(loopId);
				stack.push(String.valueOf(targetCLoop.getI()));
			} else if (curWord.equals("leave")) {
				return -1;
			} else if (curWord.startsWith("cLoop_")) {
				// if counted loop
				String loopId = curWord.split("_")[1];
				CountedLoop targetCLoop = this.countedLoops.get(loopId);

				int start = Integer.parseInt(stack.pop());
				int end = Integer.parseInt(stack.pop());
				
				targetCLoop.setI(start);
				
				int ret;
				while (targetCLoop.getI() <= end) {
					ret = eval(targetCLoop.getBody(), curWord);
					if (ret == -1) {
						return -1; // kill switch to end execution 
					} 
					targetCLoop.setI(targetCLoop.getI() + 1);
				}
			} else if (curWord.startsWith("gLoop_")) {
				// if guarded loop
				String loopId = curWord.split("_")[1];
				GuardedLoop targetGLoop = this.guardedLoops.get(loopId);
				
				int ret;
				do {
					ret = eval(targetGLoop.getBody(), curWord);
					if (ret == -1) {
						return -1; // kill switch to end execution 
					}
				} while (!Boolean.parseBoolean(stack.pop()));
			} else {
				// else Forth operator
				String s;
				String s1;
				String s2;
				String v; 
				String v1; 
				String v2;
				String v3;
				int i;
				int i1;
				int i2;
				int iq;
				int ir;
				int ie;
				int iv;
				int id;
				boolean b;
				boolean b1;
				boolean b2;
				Future<Object> future;
				
				switch (curWord) {
				// Stack words
				case "drop":
					stack.pop();
					break;
				case "dup":
					stack.push(stack.peek());
					break;
				case "swap":
					v1 = stack.pop();
					v2 = stack.pop();
					stack.push(v1);
					stack.push(v2);
					break;
				case "rot":
					v1 = stack.pop();
					v2 = stack.pop();
					v3 = stack.pop();
					stack.push(v2);
					stack.push(v1);
					stack.push(v3);
					break;
				// Arithmetic words
				case "+":
					i = stackToInt(stack.pop()) + stackToInt(stack.pop());
					stack.push(String.valueOf(i));
					break;
				case "-":
					i2 = stackToInt(stack.pop());
					i1 = stackToInt(stack.pop()) - i2;
					stack.push(String.valueOf(i1));
					break;
				case "*":
					i = stackToInt(stack.pop()) * stackToInt(stack.pop());
					stack.push(String.valueOf(i));
					break;
				case "/mod":
					ie = stackToInt(stack.pop());
					iv = stackToInt(stack.pop());
					iq = iv / ie;
					ir = iv % ie;
					stack.push(String.valueOf(ir));
					stack.push(String.valueOf(iq));
					break;
				// Comparisons
				case "<":
					stack.push(stackToInt(stack.pop()) > stackToInt(stack.pop()) ? "true" : "false");
					break;
				case "<=":
					stack.push(stackToInt(stack.pop()) >= stackToInt(stack.pop()) ? "true" : "false");
					break;
				case "=":
					s1 = stack.pop();
					s2 = stack.pop();
					
					try {
						i1 = stackToInt(s1);	
						i2 = stackToInt(s2);
						
						s = i1 == i2 ? "true" : "false";
					} catch (Exception e1) {
						try {
							b1 = stackToBool(s1);
							b2 = stackToBool(s2);
							
							s = b1 == b2 ? "true" : "false";
						} catch (Exception e2) {
							s = s1.equals(s2) ? "true" : "false";
						}
					}
					
					stack.push(s);
					break;
				case "<>":
					s1 = stack.pop();
					s2 = stack.pop();
					
					try {
						i1 = stackToInt(s1);	
						i2 = stackToInt(s2);
						
						s = i1 != i2 ? "true" : "false";
					} catch (Exception e1) {
						try {
							b1 = stackToBool(s1);
							b2 = stackToBool(s2);
							
							s = b1 != b2 ? "true" : "false";
						} catch (Exception e2) {
							s = !s1.equals(s2) ? "true" : "false";
						}
					}
					
					stack.push(s);
					break;
				case "=>":
					stack.push(stackToInt(stack.pop()) <= stackToInt(stack.pop()) ? "true" : "false");
					break;
				case ">":
					stack.push(stackToInt(stack.pop()) < stackToInt(stack.pop()) ? "true" : "false");
					break;
				// Logic
				case "and":
					b1 = stackToBool(stack.pop());
					b2 = stackToBool(stack.pop());
					stack.push(Boolean.toString(b1 && b2));
					break;
				case "or":
					b1 = stackToBool(stack.pop());
					b2 = stackToBool(stack.pop());
					stack.push(Boolean.toString(b1 || b2));
					break;
				case "invert":
					b = stackToBool(stack.pop());
					stack.push(Boolean.toString(!b));
					break;
				// Variables
				case "!":
					String value = stack.pop();
					String key = stack.pop();
					userDefinedVariables.put(key, value);
					break;
				case "?":
					stack.push(userDefinedVariables.get(stack.pop()));
					break;
				// Misc.
				case ".":
					System.out.println(stack.pop());
					break;
				case "random":
					Random r = new Random();
					stack.push(String.valueOf(r.nextInt(Integer.parseInt(stack.pop()))));
					break;
				// Robot Library - Status
				case "health":
					i = this.refSheet.getHealth(this.robotType);
					stack.push(String.valueOf(i));
					break;
				case "healthLeft":
					System.out.println("Send 'healthLeft'");
					future = Patterns.ask(player, new HealthLeftMsg(robotType), timeout);

				    try {
				    	ReplyHealthLeftMsg reply = (ReplyHealthLeftMsg) Await.result(future, timeout.duration());
				    	this.stack.push(String.valueOf(reply.healthLeft));
				    } catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "moves":
					i = this.refSheet.getMovement(this.robotType);
					stack.push(String.valueOf(i));
					break;
				case "movesLeft":
					System.out.println("Send 'movesLeft'");
					future = Patterns.ask(player, new MovesLeftMsg(robotType), timeout);
					
					try {
				    	ReplyMovesLeftMsg reply = (ReplyMovesLeftMsg) Await.result(future, timeout.duration());
				    	this.stack.push(String.valueOf(reply.movesLeft));
				    } catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "attack":
					i = this.refSheet.getAttack(this.robotType);
					stack.push(String.valueOf(i));
					break;
				case "range":
					i = this.refSheet.getRange(this.robotType);
					stack.push(String.valueOf(i));
					break;
				case "team":
					stack.push(".\"" + teamColor + "\"");
					break;
				case "type":
					switch (robotType) {
					case 0:
						stack.push(".\"SCOUT\""); break;
					case 1:
						stack.push(".\"SNIPER\""); break;
					case 2:
						stack.push(".\"TANK\""); break;
					default:
						stack.push(".\"SCOUT\"");
					}
					break;
				// Actions
				case "turn!":
					System.out.println("Send 'turn!'");
					i = Integer.parseInt(stack.pop());
					System.out.println("Turn to direction " + i);
					future = Patterns.ask(player, new TurnMsg(i), timeout);
					
					try {
						Await.result(future, timeout.duration());
					} catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "move!":
					System.out.println("Move forward");
					future = Patterns.ask(player, new MoveMsg(), timeout);
			
					try {
						Await.result(future, timeout.duration());
					} catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "shoot!":
					ir = Integer.parseInt(stack.pop());
					id = Integer.parseInt(stack.pop());
					System.out.println("Shoot at distance " + ir + " and direction " + id);
					
					player.tell(new ShootMsg(id, ir), getSelf());
					return -1; 
				case "check!":
					
					System.out.println("send 'check!'");
					i = Integer.parseInt(stack.pop());
					future = Patterns.ask(player, new CheckMsg(i), timeout);
					
					try {
				    	ReplyCheckMsg reply = (ReplyCheckMsg) Await.result(future, timeout.duration());
						if (reply.Status.equals("EMPTY") || reply.Status.equals("OCCUPIED") || reply.Status.equals("OUT OF BOUNDS")) {			
							this.stack.push(".\"" + reply.Status + "\"");
						} else {
							System.out.println("Unknown message received: " + reply.Status);
							this.stack.push(".\"OUT OF BOUNDS\"");

						}
				    } catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "scan!":
					System.out.println("Send 'scan!'");
					future = Patterns.ask(player, new ScanMsg(), timeout);
					
					try {
						ReplyScanMsg reply = (ReplyScanMsg) Await.result(future, timeout.duration());
						this.stack.push(String.valueOf(reply.visibleRobots));
					} catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				case "identify!":
					System.out.println("send identify");
					i = Integer.parseInt(stack.pop());
					future = Patterns.ask(player, new IdentifyMsg(i), timeout);
			
					try {
						ReplyIdentifyMsg reply = (ReplyIdentifyMsg) Await.result(future, timeout.duration());
						this.stack.push(".\"" + reply.teamColor + "\"");
						this.stack.push(String.valueOf(reply.range));
						this.stack.push(String.valueOf(reply.direction));
						this.stack.push(String.valueOf(reply.health));
					} catch (Exception e) {
						e.printStackTrace();
						return -1;
				    }
					break;
				// Mailboxes
				case "send!":
					try {
						mailboxes.get(Integer.parseInt(stack.pop())).add(stack.pop());
						b = true;
					} catch (Exception e) {
						b = false;						
					}
					stack.push(String.valueOf(b));
					break;
				case "mesg?":
					s = stack.pop();
					b = mailboxes.get(Integer.parseInt(s)).size() > 0 ? true : false;
					stack.push(String.valueOf(b));
					break;
				case "recv!":
					s = stack.pop();
					v = mailboxes.get(Integer.parseInt(s)).poll();
					stack.push(v);
					break;
				default:
					throw new InvalidForthWordException("Invalid or unknown Forth word: " + curWord);
				}
			}
		}
		return curIdx;
	}
	
	/**
	 * Determines if word is a Forth literal or a user-defined variable
	 * @param word
	 * @return true if word is a literal or variable, false otherwise.
	 */
	private boolean isLiteral(String word) {
		return word.matches("^-?\\d+$") // check if integer
				|| ((word.startsWith(".\"") && (word.endsWith("\"")))) // check if string 
				|| word.toLowerCase().equals("true") || word.toLowerCase().equals("false") // check if bool
				|| this.userDefinedVariables.containsKey(word);
	}
	
	/**
	 * Converts Forth string from the stack to an integer, checking to see if it is a user defined variable
	 * @param s
	 * @return an integer
	 */
	private int stackToInt(String s) {
		return Integer.parseInt(this.userDefinedWords.containsKey(s) ? this.userDefinedVariables.get(s) : s);
	}
	
	/**
	 * Converts Forth string from the stack to a boolean, checking to see if it is a user defined variable
	 * @param s
	 * @return a boolean
	 */
	private boolean stackToBool(String s) {
		return Boolean.parseBoolean(this.userDefinedWords.containsKey(s) ? this.userDefinedVariables.get(s) : s);
	}
	
 	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof ContinueMsg) {
			// should ONLY receive this message at the beginning of a turn.			
			try {
				this.eval(this.userDefinedWords.get("play"), null);
			} catch (InvalidForthWordException e) {
				e.printStackTrace();
			}
			
			//getSender().tell(new EndTurnMsg(), getSelf());
		} 
	}
	
	/**
	 * Testing scaffold for testing Interpreter class
	 * @param args array of command-line arguments
	 */
	public static void main(String [] args) {
		/*List<Queue<String>> mailboxes = new ArrayList<Queue<String>>(3);
		mailboxes.add(new LinkedList<String>());
		mailboxes.add(new LinkedList<String>());
		mailboxes.add(new LinkedList<String>());
		Interpreter interpreter = new Interpreter("./res/ForthScripts/Centralizer.jsn", 0, "RED", null, mailboxes);
		
		System.out.println(interpreter.userDefinedVariables.toString());
		System.out.println(interpreter.userDefinedWords.toString());
		
		System.out.println(interpreter.branchPoints.toString());
		System.out.println(interpreter.countedLoops.toString());
		System.out.println(interpreter.guardedLoops.toString());
		
		try {
			interpreter.eval(interpreter.userDefinedWords.get("play"), null);
		} catch (InvalidForthWordException e) {
			e.printStackTrace();
		}*/
	}
}
