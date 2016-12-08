package Messages;

import java.awt.Point;

public class ShootMsg {
	
	public int id, ir; // Used by AI
	public Point target; // Used by human
	
	public ShootMsg(int id, int ir) {
		this.id = id;
		this.ir = ir;
		this.target = null;
	}
	
	public ShootMsg(Point target) {
		this.id = -1;
		this.ir = -1;
		this.target = target;
	}
}
