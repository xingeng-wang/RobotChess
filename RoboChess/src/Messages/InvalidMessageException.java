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
 * Class: InvalidMessageException
 */
package Messages;

/**
 * Custom exception to handle invalid/unknown messages
 * 
 * @version 1.00
 * @author Ryan Park, Xingeng Wang
 */
public class InvalidMessageException  extends java.lang.Exception {

	/** Auto-generated ID by Eclipse */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for InvalidForthWordException class
	 * @param message
	 */
	public InvalidMessageException(String message) {
		super(message);
	}
}
