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
 * Class: GamePanel
 */
package GUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import Player.PlayerGUIHandler;
import Server.Robot;
import Server.Tile;
import Utilities.Assets;

/**
 * Displays the main board of the game, including hexagonal tiles and the robots on them.
 * Takes user mouse input when a tile is selected and interprets it as a fire, or move command based
 * on previous conditions.
 * 
 * @version 1.01
 * @author Jordan Nelson
 */
public class GamePanel extends DisplayPanel implements MouseListener {
	public static final long serialVersionUID = 1;

	/** The frame number used for each robot animation */
	static int frameNum = 0;

	/** The size of each hex from center to a corner */
	private int HEX_SIZE = 29;	//TODO HEX_SIZE 29 on large board, make changes for different sized boards

	/** The size of one dimension of the robot (width or height) */
	private int ROBOT_SIZE = 80;

	/** The board of the game */
	Tile[][] board;
	
	/** A list of the hexes in GUI coordinate form, as polygons */
	LinkedList<Polygon> hexes;
	
	/** A list of linked points for each hex in hexes. These are synched so the first coordinate 
	 * will be logically linked to the first hex in hexes */ 
	LinkedList<Point> coordinates;

	/** The size of the panel */
	int height, width;
	
	/** The class that is handling this panel. */
	PlayerGUIHandler handler;

	/**
	 * Constructs the gamePanel which displays the game board.
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public GamePanel(int width, int height, PlayerGUIHandler handler) {
		this.width = width;
		this.height = height;
		this.handler = handler;
		this.hexes = new LinkedList<Polygon>();
		this.coordinates = new LinkedList<Point>();
		this.addMouseListener(this);
	}

	/**
	 * Takes a version of the board and displays it to the panel.
	 * @param board a version of the board to display
	 */
	public void update(Tile[][] board) {
		this.board = board;
		repaint();
	}

	/**
	 * Gives a point the corresponds to a corner of a hexagon, based on cornerNum. cornerNum can
	 * be any value from 0 to 5, and taking all of the corners given by this function with those
	 * values with the same center point and size is equivalent to having all the corners of a hexagon.
	 * @param centre the center point of the hexagon
	 * @param size the size of the hexagon from the center point to any of the corners
	 * @param cornerNum the corner number of the corner you wish to retrieve, from 0 to 5
	 * @return the point value of cornerNum, based on the center point and size, or null if a parameter is invalid
	 */
	private Point getHexCorner(Point centre, int size, int cornerNum) {
		// Check if any parameters are invalid, and return null if they are
		if (centre.x < 0 || centre.y < 0 || size < 0 || cornerNum < 0) {
			return null;
		}

		// Calculate the hex's corner using trig given the parameters 
		int degAngle = 60 * cornerNum + 30;
		double radAngle = ((Math.PI / 180) * degAngle);
		return new Point((int) (centre.x + size * Math.cos(radAngle)),
				(int) (centre.y + size * Math.sin(radAngle)));
	}

	/**
	 * Given a center point returns a list of points representing the 6 corners of a hexagon.
	 * @param centre the center point of the hexagon
	 * @return a list of points representing the 6 corners of the hexagon
	 */
	private LinkedList<Point> getHex(Point centre) {
		//A list containing the 6 corners of a hex, to represent a hex
		LinkedList<Point> hex = new LinkedList<Point>();

		//Get the 6 corners a hex contains
		for (int i = 0; i < 6; i++) {
			hex.add(getHexCorner(centre, HEX_SIZE, i));
		}

		return hex;
	}

	/**
	 * Draws a hexagon to the screen giving the array index of that hex from the board
	 * @param x the x index from the board for this hex
	 * @param y the y index from the board for this hex
	 * @param g the graphics object to draw with
	 */
	public void drawHex(int x, int y, Graphics2D g) {
		//check parameters
		if (x < 0 || x > 12 || y < 0 || y > 12) {
			return;
		}

		//if the tile is null don't draw it
		if (board == null || board[x][y] == null) {
			return;
		}

		//calculate the height and width of a hex based on its size
		int HEX_HEIGHT = HEX_SIZE * 2;
		int HEX_WIDTH = (int) ((Math.sqrt(3)/2) * HEX_HEIGHT);

		//convert the array point to a pseudo corner point of the hex
		int xIndex = x * HEX_WIDTH;
		int yIndex = y * (HEX_HEIGHT * 3) /4;

		//offset every second row of hexs
		if (y % 2 == 0) {
			xIndex += (HEX_WIDTH / 2);
		}

		//find the centre of the hex
		Point centre = new Point(xIndex + (HEX_WIDTH / 2), yIndex + (HEX_HEIGHT /2));

		//get the hex
		LinkedList<Point> hex = getHex(centre);

		//convert points to arrays for fillpolygon
		int[] xPoints = new int[6];
		int[] yPoints = new int[6];

		for (int i = 0; i < 6; i++) {
			xPoints[i] = hex.get(i).x;
			yPoints[i] = hex.get(i).y;
		}

		if (board[x][y].getVisibility()) {
			g.setColor(Color.ORANGE); // TODO: maybe change the color???
			g.setStroke(new BasicStroke(3.0f, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
		} else {
			g.setColor(Color.DARK_GRAY);
			g.setStroke(new BasicStroke(3.0f, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
		}

		//draw the hex
		g.fillPolygon(xPoints, yPoints, 6);
		
		//add to list of hexes
		hexes.add(new Polygon(xPoints, yPoints, 6));
		coordinates.add(new Point(x, y));

		//draw an outline around the hex
		g.setColor(Color.BLACK);
		for (int i = 0; i < 6; i++) {
			int next = i + 1;
			if(next > 5) next = 0;
			g.drawLine(hex.get(i).x, hex.get(i).y, hex.get(next).x, hex.get(next).y);
		}
	}

	/**
	 * Draws all robots on a hex on there respective hex. This is done so all robots are drawn evenly across the hex. This means 
	 * if only one robot is drawn, it is located in the center, but if two are drawn, one is located a third of the way into the hex 
	 * from the left, and the other is located two thirds of the way into the hex from the left.
	 * @param x the x coordinate of the hex
	 * @param y the y coordinate of the hex
	 * @param g the graphics object to draw with
	 */
	public void drawRobots(int x, int y, Graphics g) {
		//check parameters
		if (x < 0 || x > 12 || y < 0 || y > 12) {
			return;
		}
		if (board == null || board[x][y] == null) {
			return;
		}

		/* if the tile is in the FOW, do not draw robots */
		if (!board[x][y].getVisibility()) {
			return;
		}

		/* if there are no robots, return */
		if (board[x][y].getRobots() == null || board[x][y].getRobots().size() <= 0) {
			return;
		}

		/* Find the point the robots will be drawn at */

		//calculate the height and width of a hex based on its size
		int HEX_HEIGHT = HEX_SIZE * 2;
		int HEX_WIDTH = (int) ((Math.sqrt(3)/2) * HEX_HEIGHT);

		/* Calculate hex segment, which is a fraction of a hex divided into #robots on tile + 1 segments. This
		 * is used to evenly spread robots out across a hex.
		 */
		int HEX_SEGMENT = HEX_WIDTH / (board[x][y].getRobots().size() + 1);

		//convert the array point to a point located at the top left corner of a box outlining the hex
		int xIndex = x * HEX_WIDTH;
		int yIndex = y * (HEX_HEIGHT * 3) /4;

		//offset every second row of hexes by one half, so they interlock with each other
		if (y % 2 == 0) {
			xIndex += (HEX_WIDTH / 2);
		}

		//find the centre of the hex in the y direction, and the first segment in the hex in the x direction (this will be
		//the centre of the hex if one robot only is on the hex
		Point point = new Point(xIndex + HEX_SEGMENT, yIndex + (HEX_HEIGHT /2));

		//Compensate for robot size (move point from centre of robot to top left corner of robot's box)
		point.x -= ROBOT_SIZE / 2;
		point.y -= ROBOT_SIZE / 2;

		//Create a new point to store the original final values so they are not altered while drawing
		Point original = new Point();
		original.x = point.x;
		original.y = point.y;

		//for each robot on the hex
		for (int i = 0; i < board[x][y].getRobots().size(); i++) {
			//get the original x coordinate of the point
			point.x = original.x;

			//get the player ID and robot type from the list
			int ID = board[x][y].getRobots().get(i).getPlayerID();
			int type = board[x][y].getRobots().get(i).getType();

			//set the x coordinate to draw at i segments into the hex
			point.x += HEX_SEGMENT * i;

			//draw the robot given from the player ID and robot type at the given coordinate
			switch(ID) {
			case 0: 
				if (type == 0) {
					g.drawImage(Assets.redTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.redSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.redScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			case 1:
				if (type == 0) {
					g.drawImage(Assets.greenTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.greenSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.greenScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			case 2:
				if (type == 0) {
					g.drawImage(Assets.blueTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.blueSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.blueScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			case 3:
				if (type == 0) {
					g.drawImage(Assets.yellowTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.yellowSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.yellowScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			case 4:
				if (type == 0) {
					g.drawImage(Assets.purpleTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.purpleSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.purpleScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			case 5:
				if (type == 0) {
					g.drawImage(Assets.orangeTank[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 1) {
					g.drawImage(Assets.orangeSniper[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				} else if (type == 2) {
					g.drawImage(Assets.orangeScout[frameNum], point.x, point.y, ROBOT_SIZE, ROBOT_SIZE, this);
				}
				break;

			default:
				System.out.println("GAME PANEL: Invalid Player ID: " + ID);
			}
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point click = e.getPoint();
		for(int i = 0; i < hexes.size(); i++) {
			if (hexes.get(i).contains(click)) {/* condition never true sometime*/
				handler.hexClicked(coordinates.get(i));
				return;
			}
		}
		System.out.println("ERROR: no hex clicked");
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	
	/**
	 * Paints the board to the screen, including all hexes and robots.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//remove drawn hexes to eliminate duplicates
		hexes.clear();
		coordinates.clear();
		
		/* draw each robot and then each hex. The order here is important as the robots
		 * need to be drawn after each hex so that they are on top of each hex. */

		for (int x = 0; x < 14; x++) {
			for (int y = 0; y < 13; y++) {
				drawHex(x, y, (Graphics2D) g);
			}
		}

		for (int x = 0; x < 14; x++) {
			for (int y = 0; y < 13; y++) {
				drawRobots(x, y, g);
			}
		}
	}

	public static void main(String[] args) {
		//Create and set up the window.
		CreateFrame frame = new CreateFrame(1000, 500, "TEST");

		GamePanel game = new GamePanel(1000, 500, null);
		Assets.init();

		Tile[][] board = new Tile[14][14];
		board[2][0] = new Tile();
		board[3][0] = new Tile();
		board[2][1] = new Tile();
		board[3][1] = new Tile();
		board[4][1] = new Tile();
		board[2][2] = new Tile();
		board[3][2] = new Tile();

		board[2][0].setVisibility(true);
		board[3][0].setVisibility(true);
		board[2][1].setVisibility(true);
		board[3][1].setVisibility(true);
		board[4][1].setVisibility(true);
		board[2][2].setVisibility(true);
		board[3][2].setVisibility(true);

		Robot robot1 = new Robot(1, 0);
		Robot robot2 = new Robot(1, 1);
		Robot robot3 = new Robot(1, 2);
		board[3][1].addRobot(robot1);
		board[3][1].addRobot(robot3);
		board[3][1].addRobot(robot2);

		frame.getFrame().getContentPane().removeAll();
		frame.getFrame().getContentPane().add(game, BorderLayout.CENTER);
		frame.getFrame().getContentPane().validate();
		Component focusComponent = game.getFocusComponent();
		if (focusComponent != null) {
			focusComponent.requestFocusInWindow();
		}
		frame.getFrame().setVisible(true);
		game.update(board);
	}
}

