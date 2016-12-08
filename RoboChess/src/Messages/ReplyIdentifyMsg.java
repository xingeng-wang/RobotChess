package Messages;

public class ReplyIdentifyMsg {

	public int robotType;
	public String teamColor;
	public int range;
	public int direction;
	public int health;
	
	public ReplyIdentifyMsg(int robotType, String teamColor, int range, int direction, int health) {
		this.robotType = robotType;
		this.teamColor = teamColor;
		this.range = range;
		this.direction = direction;
		this.health = health;
	}
	
}
