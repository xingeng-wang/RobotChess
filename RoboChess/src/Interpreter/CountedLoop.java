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
 * Class: CountedLoop
 */
package Interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A representation of a counted loop body
 * 
 * @version 1.00
 * @author Ryan Park
 */
public class CountedLoop {
	
	/** A unique ID to identify this loop body. */
	private String cLoopID;
	
	/** The list of Forth words to execute in the body. */
	private List<String> body;
	
	/** The list counter. */
	private int I;
	
	/** 
	 * Constructor for CountedLoop class
	 */
	public CountedLoop() {
		this.cLoopID = UUID.randomUUID().toString();
		this.body = new LinkedList<String>();
		this.I = 0;
	}

	/** 
	 * Getter for cLoopId
	 * @return loop body ID 
	 */
	public String getCountLoopID() {
		return cLoopID;
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

	/**
	 * Getter for I (loop counter)
	 * @return loop counter
	 */
	public int getI() {
		return I;
	}

	/**
	 * Setter for I (loop counter)
	 * @param i loop counter
	 */
	public void setI(int i) {
		I = i;
	}
}
