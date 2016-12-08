package Messages;

public class ReplyCheckMsg {

	public int robotType;
	public String Status;
	
	public ReplyCheckMsg(int robotType, String status) {
		this.robotType = robotType;
		this.Status = status;
	}
}
