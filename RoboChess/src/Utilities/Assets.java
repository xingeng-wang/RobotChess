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
 * Class: Assets
 */
package Utilities;

import java.awt.image.BufferedImage;

/**
 * Creates and stores image assets used in the game.
 * @author Jordan Nelson
 * @version 1.01
 */
public class Assets {
	
	/** An array of image representing the animated tank robot for each player. */
	public static BufferedImage[] redTank, orangeTank, yellowTank, greenTank, 
							blueTank, purpleTank;
	
	/** An array of image representing the animated sniper robot for each player. */
	public static BufferedImage[] redSniper, orangeSniper, yellowSniper, greenSniper,
							blueSniper, purpleSniper;
	
	/** An array of image representing the animated scout robot for each player. */
	public static BufferedImage[] redScout, orangeScout, yellowScout, greenScout,
							blueScout, purpleScout;
	
	
	/** A SpriteSheet for each of the tank robots. */
	private static SpriteSheet redTankSheet, orangeTankSheet, yellowTankSheet, greenTankSheet, 
	blueTankSheet, purpleTankSheet;
	
	/** A SpriteSheet for each of the sniper robots. */
	private static SpriteSheet redSniperSheet, orangeSniperSheet, yellowSniperSheet, greenSniperSheet,
	blueSniperSheet, purpleSniperSheet;
	
	/** A SpriteSheet for each of the scout robots. */
	private static SpriteSheet redScoutSheet, orangeScoutSheet, yellowScoutSheet, greenScoutSheet,
	blueScoutSheet, purpleScoutSheet;
	
	
	/** The width and height of the robot in the sprite sheet, in pixels. */
	private static int width = 256;
	private static int height = 256;
	
	/**
	 * Initializes all the assets and crops the out of their sprite sheets.
	 */
	public static void init() {
		/* Initialize all sprite sheets. */
		redTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/RedTank.png"));
		orangeTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/OrangeTank.png"));
		yellowTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/YellowTank.png"));
		greenTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/GreenTank.png"));
		blueTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/BlueTank.png"));
		purpleTankSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Tank/PurpleTank.png"));
	
		redSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/RedSniper.png"));
		orangeSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/OrangeSniper.png"));
		yellowSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/YellowSniper.png"));
		greenSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/GreenSniper.png"));
		blueSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/BlueSniper.png"));
		purpleSniperSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Sniper/PurpleSniper.png"));
		
		redScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/RedScout.png"));
		orangeScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/OrangeScout.png"));
		yellowScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/YellowScout.png"));
		greenScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/GreenScout.png"));
		blueScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/BlueScout.png"));
		purpleScoutSheet = new SpriteSheet(ImageLoader.loadImage("/SpriteSheets/Scout/PurpleScout.png"));
		
		/* Initialize assets by cropping respective sprites out of the sprite sheets. */
		
		/* Initialize animation arrays for each asset */
		redTank = new BufferedImage[5];
		orangeTank = new BufferedImage[5];
		yellowTank = new BufferedImage[5];
		greenTank = new BufferedImage[5];
		blueTank = new BufferedImage[5];
		purpleTank = new BufferedImage[5];
		
		redSniper = new BufferedImage[5];
		orangeSniper = new BufferedImage[5];
		yellowSniper = new BufferedImage[5];
		greenSniper = new BufferedImage[5];
		blueSniper = new BufferedImage[5];
		purpleSniper = new BufferedImage[5];
		
		redScout = new BufferedImage[5];
		orangeScout = new BufferedImage[5];
		yellowScout = new BufferedImage[5];
		greenScout = new BufferedImage[5];
		blueScout = new BufferedImage[5];
		purpleScout = new BufferedImage[5];
		
		/* Crop image from sheets for each tank frame */
		redTank[0] = redTankSheet.crop(width, 0, width, height);
		redTank[1] = redTankSheet.crop(width * 2, 0, width, height);
		redTank[2] = redTankSheet.crop(width * 3, 0, width, height);
		redTank[3] = redTankSheet.crop(width * 4, 0, width, height);
		redTank[4] = redTankSheet.crop(0, height, width, height);
		
		orangeTank[0] = orangeTankSheet.crop(width, 0, width, height);
		orangeTank[1] = orangeTankSheet.crop(width * 2, 0, width, height);
		orangeTank[2] = orangeTankSheet.crop(width * 3, 0, width, height);
		orangeTank[3] = orangeTankSheet.crop(width * 4, 0, width, height);
		orangeTank[4] = orangeTankSheet.crop(0, height, width, height);
		
		yellowTank[0] = yellowTankSheet.crop(width, 0, width, height);
		yellowTank[1] = yellowTankSheet.crop(width * 2, 0, width, height);
		yellowTank[2] = yellowTankSheet.crop(width * 3, 0, width, height);
		yellowTank[3] = yellowTankSheet.crop(width * 4, 0, width, height);
		yellowTank[4] = yellowTankSheet.crop(0, height, width, height);
		
		greenTank[0] = greenTankSheet.crop(width * 2, 0, width, height);
		greenTank[1] = greenTankSheet.crop(width * 3, 0, width, height);
		greenTank[2] = greenTankSheet.crop(width * 4, 0, width, height);
		greenTank[3] = greenTankSheet.crop(width * 4, 0, width, height);
		greenTank[4] = greenTankSheet.crop(0, height, width, height);
		
		blueTank[0] = blueTankSheet.crop(width, 0, width, height);
		blueTank[1] = blueTankSheet.crop(width * 2, 0, width, height);
		blueTank[2] = blueTankSheet.crop(width * 3, 0, width, height);
		blueTank[3] = blueTankSheet.crop(width * 4, 0, width, height);
		blueTank[4] = blueTankSheet.crop(0, height, width, height);
		
		purpleTank[0] = purpleTankSheet.crop(width, 0, width, height);
		purpleTank[1] = purpleTankSheet.crop(width * 2, 0, width, height);
		purpleTank[2] = purpleTankSheet.crop(width * 3, 0, width, height);
		purpleTank[3] = purpleTankSheet.crop(width * 4, 0, width, height);
		purpleTank[4] = purpleTankSheet.crop(0, height, width, height);
		
		/* Crop image from each sheet for each sniper */
		redSniper[0] = redSniperSheet.crop(width, 0, width, height);
		redSniper[1] = redSniperSheet.crop(width * 2, 0, width, height);
		redSniper[2] = redSniperSheet.crop(width * 3, 0, width, height);
		redSniper[3] = redSniperSheet.crop(width * 4, 0, width, height);
		redSniper[4] = redSniperSheet.crop(0, height, width, height);
		
		orangeSniper[0] = orangeSniperSheet.crop(width, 0, width, height);
		orangeSniper[1] = orangeSniperSheet.crop(width * 2, 0, width, height);
		orangeSniper[2] = orangeSniperSheet.crop(width * 3, 0, width, height);
		orangeSniper[3] = orangeSniperSheet.crop(width * 4, 0, width, height);
		orangeSniper[4] = orangeSniperSheet.crop(0, height, width, height);
		
		yellowSniper[0] = yellowSniperSheet.crop(width, 0, width, height);
		yellowSniper[1] = yellowSniperSheet.crop(width * 2, 0, width, height);
		yellowSniper[2] = yellowSniperSheet.crop(width * 3, 0, width, height);
		yellowSniper[3] = yellowSniperSheet.crop(width * 4, 0, width, height);
		yellowSniper[4] = yellowSniperSheet.crop(0, height, width, height);
		
		greenSniper[0] = greenSniperSheet.crop(width, 0, width, height);
		greenSniper[1] = greenSniperSheet.crop(width * 2, 0, width, height);
		greenSniper[2] = greenSniperSheet.crop(width * 3, 0, width, height);
		greenSniper[3] = greenSniperSheet.crop(width * 4, 0, width, height);
		greenSniper[4] = greenSniperSheet.crop(0, height, width, height);
		
		blueSniper[0] = blueSniperSheet.crop(width, 0, width, height);
		blueSniper[1] = blueSniperSheet.crop(width * 2, 0, width, height);
		blueSniper[2] = blueSniperSheet.crop(width * 3, 0, width, height);
		blueSniper[3] = blueSniperSheet.crop(width * 4, 0, width, height);
		blueSniper[4] = blueSniperSheet.crop(0, height, width, height);
		
		purpleSniper[0] = purpleSniperSheet.crop(width, 0, width, height);
		purpleSniper[1] = purpleSniperSheet.crop(width * 2, 0, width, height);
		purpleSniper[2] = purpleSniperSheet.crop(width * 3, 0, width, height);
		purpleSniper[3] = purpleSniperSheet.crop(width * 4, 0, width, height);
		purpleSniper[4] = purpleSniperSheet.crop(0, height, width, height);
		
		/* Crop image from sheets for each scout frame */
		redScout[0] = redScoutSheet.crop(width, 0, width, height);
		redScout[1] = redScoutSheet.crop(width * 2, 0, width, height);
		redScout[2] = redScoutSheet.crop(width * 3, 0, width, height);
		redScout[3] = redScoutSheet.crop(width * 4, 0, width, height);
		redScout[4] = redScoutSheet.crop(0, height, width, height);
		
		orangeScout[0] = orangeScoutSheet.crop(width, 0, width, height);
		orangeScout[1] = orangeScoutSheet.crop(width * 2, 0, width, height);
		orangeScout[2] = orangeScoutSheet.crop(width * 3, 0, width, height);
		orangeScout[3] = orangeScoutSheet.crop(width * 4, 0, width, height);
		orangeScout[4] = orangeScoutSheet.crop(0, height, width, height);
		
		yellowScout[0] = yellowScoutSheet.crop(width, 0, width, height);
		yellowScout[1] = yellowScoutSheet.crop(width * 2, 0, width, height);
		yellowScout[2] = yellowScoutSheet.crop(width * 3, 0, width, height);
		yellowScout[3] = yellowScoutSheet.crop(width * 4, 0, width, height);
		yellowScout[4] = yellowScoutSheet.crop(0, height, width, height);
		
		greenScout[0] = greenScoutSheet.crop(width, 0, width, height);
		greenScout[1] = greenScoutSheet.crop(width * 2, 0, width, height);
		greenScout[2] = greenScoutSheet.crop(width * 3, 0, width, height);
		greenScout[3] = greenScoutSheet.crop(width * 4, 0, width, height);
		greenScout[4] = greenScoutSheet.crop(0, height, width, height);
		
		blueScout[0] = blueScoutSheet.crop(width, 0, width, height);
		blueScout[1] = blueScoutSheet.crop(width * 2, 0, width, height);
		blueScout[2] = blueScoutSheet.crop(width * 3, 0, width, height);
		blueScout[3] = blueScoutSheet.crop(width * 4, 0, width, height);
		blueScout[4] = blueScoutSheet.crop(0, height, width, height);
		
		purpleScout[0] = purpleScoutSheet.crop(width, 0, width, height);
		purpleScout[1] = purpleScoutSheet.crop(width * 2, 0, width, height);
		purpleScout[2] = purpleScoutSheet.crop(width * 3, 0, width, height);
		purpleScout[3] = purpleScoutSheet.crop(width * 4, 0, width, height);
		purpleScout[4] = purpleScoutSheet.crop(0, height, width, height);
	}
}
