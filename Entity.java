package daptb;

import java.awt.image.BufferedImage;

public class Entity {

	public int worldX, worldY;
	public int speed;
	
	public BufferedImage left1, leftIdle, right1, rightIdle, jumpRight, jumpLeft, attackRight, attackLeft, attackRight2, attackLeft2;
	public String direction;
	
	public int spriteCounter = 0;
	public int spriteNum = 1;
}
