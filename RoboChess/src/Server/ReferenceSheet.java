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
 * Class: ReferenceSheet
 */
package Server;

/**
 * A quick reference sheet for the various statistics each robot has.
 * To request a statistic for a type use the appropriate getter with the
 * corresponding type # (scout = 0, sniper = 1, tank = 2)
 * @author Willie Van Heerde, Ryan Park
 * @version 1.02
 */
public final class ReferenceSheet {

	/**
	 * The attack values for each robot,
	 * used whenever a robot performs a attack.
	 */
	private static final int ATTACK[] = {1,2,3};
	
	/**
	 * The starting health values for each robot, 
	 * used when setting up the game board.
	 */
	private static final int HEALTH[] = {1,2,3};
	
	/**
	 * The initial movement points for each robot type,
	 * on a new round each robot has it's movement points set to this value.
	 */
	private static final int MOVEMENT[] = {3,2,1};
	
	/**
	 * The range values for each robot type,
	 * range determines a robot's vision and attack distance
	 */
	private static final int RANGE[] = {2,3,1};
	
	/** List of viable team colors. */
	private static final String TEAM_COLORS[] = { "RED", "GREEN", "BLUE", "YELLOW", "PURPLE", "ORANGE" };
		
	/**
	 * Returns The attack value of a requested robot type.
	 * @param type The robot type, used to return corresponding attack.
	 * @return A integer that is the attack for that robot type.
	 */
	public int getAttack(int type) {
		return ATTACK[type];
	}
	
	/**
	 * Returns The starting health of a requested robot type.
	 * @param type The robot type, used to return corresponding health.
	 * @return A integer that is the initial starting health for that robot type.
	 */
	public int getHealth(int type) {
		return HEALTH[type];
	}
	
	/**
	 * Returns The starting movement points of a requested robot type.
	 * @param type The robot type, used to return the initial movement points for that type.
	 * @return A integer that is the starting amount of movement points for the robot type.
	 */
	public int getMovement(int type) {
		return MOVEMENT[type];
	}
	
	/**
	 * Returns The range of the requested robot type.
	 * @param type The robot type, used to return corresponding range.
	 * @return A integer that represents the range for that robot type.
	 */
	public int getRange(int type) {
		return RANGE[type];
	}
	
	/**
	 * Returns the array of team colors.
	 */
	public String getTeamColor(int idx) {
		return TEAM_COLORS[idx];
	}
	
	/**
	 * The main method to perform a series of tests to verify that each method has the correct pre & post conditions
	 * @param args
	 */
	public static void main(String args[]) {
		ReferenceSheet rSheet = new ReferenceSheet();
		String errorLog = "";		
		int errorCount = 0;

		/* Testing getAttack**/
		if (rSheet.getAttack(0) != 1) {
			errorLog = errorLog + "Output of getAttack(0) should be 1";
			errorCount++;
		}

		if (rSheet.getAttack(1) != 2) {
			errorLog = errorLog + "Output of getAttack(1) should be 2";
			errorCount++;
		}
		
		if (rSheet.getAttack(2) != 3) {
			errorLog = errorLog + "Output of getAttack(2) should be 3";
			errorCount++;
		}
		
		/* Testing getHealth**/
		if (rSheet.getHealth(0) != 1) {
			errorLog = errorLog + "Output of getHealth(0) should be 1";
			errorCount++;
		}
		
		if (rSheet.getHealth(1) != 2) {
			errorLog = errorLog + "Output of getHealth(1) should be 2";
			errorCount++;
		}
		
		if (rSheet.getHealth(2) != 3) {
			errorLog = errorLog + "Output of getHealth(2) should be 3";
			errorCount++;
		}
		
		/* Testing getMovement**/
		if (rSheet.getMovement(0) != 3) {
			errorLog = errorLog + "Output of getMovement(0) should be 3";
			errorCount++;
		}
		
		if (rSheet.getMovement(1) != 2) {
			errorLog = errorLog + "Output of getMovement(1) should be 2";
			errorCount++;
		}
		
		if (rSheet.getMovement(2) != 1) {
			errorLog = errorLog + "Output of getMovement(2) should be 1";
			errorCount++;
		}
		
		/* Testing getRange**/
		if(rSheet.getRange(0) != 2) {
			errorLog = errorLog + "Output of getRange(0) should be 2";
			errorCount++;
		}
		
		if(rSheet.getRange(1) != 3) {
			errorLog = errorLog + "Output of getRange(1) should be 3";
			errorCount++;
		}
		
		if(rSheet.getRange(2) != 1) {
			errorLog = errorLog + "Output of getRange(2) should be 1";
			errorCount++;
		}
		
		if (errorLog.length() > 0 && errorCount > 0) {
			System.out.println("Total number of errors found in ReferenceSheet static main: " + errorCount);
			System.out.println(errorLog);
		} else {
			System.out.println("ReferenceSheet static main: 0 errors found");
		}
	}
}
