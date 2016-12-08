package Messages;

public class ReplyHealthLeftMsg {
	public int robotType;
	public int healthLeft;
	
	public ReplyHealthLeftMsg(int robotType, int HealthLeft) {
		this.robotType = robotType;
		this.healthLeft = HealthLeft;
	}
}
