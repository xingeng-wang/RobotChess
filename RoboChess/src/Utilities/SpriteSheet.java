package Utilities;

import java.awt.image.BufferedImage;

/**
 * Stores a sprite sheet and provides functionality to crop individual sprites.
 * @author Jordan Nelson
 */
public class SpriteSheet {
    
    private BufferedImage sheet;
    
    /**
     * Stores an image as a sprite sheet object.
     * @param sheet the image to store as a sprite sheet
     */
    public SpriteSheet(BufferedImage sheet) {
        this.sheet = sheet;
    }
    
    /**
     * Crops a sprite out of a sprite sheet.
     * @param x the x coordinate at the top left corner of the image to be cropped
     * @param y the y coordinate at the top left corner of the image to be cropped
     * @param width the width of the image to be cropped
     * @param height the height of the image to be cropped
     * @return the cropped image as a BufferedImage object
     */
    public BufferedImage crop(int x, int y, int width, int height) {
        return sheet.getSubimage(x, y, width, height);
    }
    
}
