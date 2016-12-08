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
 * Class: Branch
 */
package Interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A representation of a branch point in a Forth script
 * 
 * @version 1.00
 * @author Ryan Park
 */
public class Branch {
	
	/** A unique ID to identify this branch point. */
	private String branchID;
	
	/** The list of Forth words to execute in the true branch. */
	private List<String> trueBranch;
	
	/** The list of Forth words to execute in the false branch. */
	private List<String> falseBranch;
	
	/**
	 * Constructor for Branch class
	 */
	public Branch() {
		this.branchID = UUID.randomUUID().toString();
		this.trueBranch = new LinkedList<String>();
		this.falseBranch = new LinkedList<String>();
	}

	/**
	 * Getter for branchID
	 * @return the branch ID
	 */
	public String getBranchID() {
		return this.branchID;
	}
	
	/**
	 * Getter for trueBranch
	 * @return list of Forth words
	 */
	public List<String> getTrueBranch() {
		return trueBranch;
	}

	/**
	 * Setter for trueBranch
	 * @param trueBranch list of Forth words
	 */
	public void setTrueBranch(List<String> trueBranch) {
		this.trueBranch = trueBranch;
	}

	/**
	 * Getter for falseBranch
	 * @return list of Forth words
	 */
	public List<String> getFalseBranch() {
		return falseBranch;
	}

	/**
	 * Setter for falseBranch
	 * @param falseBranch list of Forth words
	 */
	public void setFalseBranch(List<String> falseBranch) {
		this.falseBranch = falseBranch;
	}
}
