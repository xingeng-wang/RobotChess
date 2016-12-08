package Messages;

public class ReplyScanMsg {

	public int robotType;
	public int visibleRobots;
	
	public ReplyScanMsg(int robotType, int visibleRobots) {
		this.robotType = robotType;
		this.visibleRobots = visibleRobots;
	}
}
