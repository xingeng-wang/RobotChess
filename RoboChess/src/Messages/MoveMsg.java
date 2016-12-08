package Messages;

import java.awt.Point;

public class MoveMsg {

	public Point destination;
	
	public MoveMsg() {
		this.destination = null;
	}
	
	public MoveMsg(Point destination) {
		this.destination = destination;
	}
}
