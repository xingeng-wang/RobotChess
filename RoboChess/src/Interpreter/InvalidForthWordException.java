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
 * Class: InvalidForthWordException
 */
package Interpreter;

/**
 * Custom exception to handle invalid/unknown Forth words
 * 
 * @version 1.00
 * @author Ryan Park
 */
public class InvalidForthWordException extends java.lang.Exception {

	/** Auto-generated ID by Eclipse */
	private static final long serialVersionUID = -7442730833168247847L;

	/**
	 * Constructor for InvalidForthWordException class
	 * @param message
	 */
	public InvalidForthWordException(String message) {
		super(message);
	}
}
