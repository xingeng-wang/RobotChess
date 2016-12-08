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
 * Class: GuardedLoop
 */
package Interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A representation of a guarded loop body
 * 
 * @version 1.00
 * @author Ryan Park
 */
public class GuardedLoop {
	
	/** A unique ID to identify this loop body. */
	private String gLoopID;
	
	/** The list of Forth words to execute in the body. */
	private List<String> body;
	
	/** 
	 * Constructor for GuardedLoop class
	 */
	public GuardedLoop() {
		this.gLoopID = UUID.randomUUID().toString();
		this.body = new LinkedList<String>();
	}
	
	/** 
	 * Getter for gLoopId
	 * @return loop body ID 
	 */
	public String getGuardLoopID() {
		return gLoopID;
	}
	
	/**
	 * Getter for body
	 * @return list of Forth words
	 */
	public List<String> getBody() {
		return body;
	}

	/**
	 * Setter for body
	 * @param body list of Forth Words
	 */
	public void setBody(List<String> body) {
		this.body = body;
	}
}
