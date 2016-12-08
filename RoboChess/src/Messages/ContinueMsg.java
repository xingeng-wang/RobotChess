package Messages;

import Player.PlayerInfo;
import Server.Tile;

public class ContinueMsg {
	public Tile[][] board;
	public PlayerInfo playerInfo;
	public int robotType;
	
	public ContinueMsg(Tile[][] board, PlayerInfo playerInfo, int robotType) {
		this.board = board;
		this.playerInfo = playerInfo;
		this.robotType = robotType;
	}
}
