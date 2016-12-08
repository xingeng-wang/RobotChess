package Messages;

public class ReplyMovesLeftMsg {
	public int robotType;
	public int movesLeft;
	
	public ReplyMovesLeftMsg(int robotType, int movesLeft) {
		this.robotType = robotType;
		this.movesLeft = movesLeft;
	}


	
}
