package daptb;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import daptb.GamePanel;
import daptb.KeyHandler;



public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    // Physics Variables
    int velocityY = 0;
    int gravity = 1;
    int jumpStrength = -24;
    boolean jumping = false;
    boolean onGround = false;

    // Animation & Direction Variables
    String lastDirection = "right";             // Stores the last horizontal direction
    public boolean movingHorizontally = false;    // Indicates if moving left/right

    // Animation frame variables for running animation
    private int animationCounter = 0;             // Counts update frames for switching frames
    private int animationSpeed = 10;              // Number of update frames per frame switch
    private int currentFrame = 0;                 // Alternates between 0 and 1

    // **REMOVED:** Jump cooldown variable has been removed
    // private int jumpCooldown = 0;

    // Edge-trigger jump flag; set to false once a jump is triggered and reset when the key is released.
    private boolean canJump = true;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = 150;
        // Set starting worldY so that the knight’s feet (worldY + tileSize) are on the ground.
        worldY = getGroundLevel() - gp.tileSize;
        speed = 3;
        lastDirection = "right";
        onGround = true;
    }

    public void getPlayerImage() {
        try {
            jumpRight = ImageIO.read(getClass().getResourceAsStream("/player/knight-jumping-right-pixilart.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/knight-moving-right-pixilart-2.png"));
            rightIdle = ImageIO.read(getClass().getResourceAsStream("/player/knight-facing-right.png"));
            jumpLeft = ImageIO.read(getClass().getResourceAsStream("/player/knight-jumping-left-pixilart.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/knight-moving-left-pixilart.png"));
            leftIdle = ImageIO.read(getClass().getResourceAsStream("/player/knight-facing-left.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        movingHorizontally = false;

        // Horizontal Movement
        if (keyH.leftPressed) {
            worldX -= speed;
            movingHorizontally = true;
            lastDirection = "left";
        } else if (keyH.rightPressed) {
            worldX += speed;
            movingHorizontally = true;
            lastDirection = "right";
        }

        // Jump Logic:
        // Trigger a jump only if:
        //   - "w" is pressed,
        //   - the knight is on the ground,
        //   - and the jump key is freshly tapped (canJump is true).
        if (keyH.wPressed && onGround && canJump) {
            velocityY = jumpStrength;
            jumping = true;
            onGround = false;
            canJump = false; // Prevent repeated jumping until key is released
        }
        // Reset canJump when "w" is not pressed (edge-trigger reset)
        if (!keyH.wPressed) {
            canJump = true;
        }

        // Apply Gravity
        velocityY += gravity;
        if (velocityY > 10)
            velocityY = 10; // Terminal velocity

        // Update Vertical Position
        worldY += velocityY;

        // Ground Collision Check:
        if (worldY + gp.tileSize >= getGroundLevel()) {
            worldY = getGroundLevel() - gp.tileSize; // Snap to ground
            velocityY = 0;
            jumping = false;
            onGround = true;
        } else {
            onGround = false;
        }

        // Update running animation when moving horizontally and not jumping
        if (movingHorizontally && !jumping) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                currentFrame = (currentFrame + 1) % 2; // Alternate between 0 and 1
                animationCounter = 0;
            }
        } else {
            currentFrame = 0; // Default frame when not moving
        }

        // Camera Movement: 
        // Follows left/right always; vertical follows only when not jumping.
        gp.cameraX = worldX - gp.cameraOffsetX;
        if (!jumping) {
            gp.cameraY = worldY - gp.cameraOffsetY;
        }

        // Prevent Camera from going out of bounds horizontally
        if (gp.cameraX < 0)
            gp.cameraX = 0;
        if (gp.cameraX > gp.worldWidth - gp.screenWidth)
            gp.cameraX = gp.worldWidth - gp.screenWidth;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        // Choose image based on state and animation frame:
        if (jumping) {
            image = lastDirection.equals("right") ? jumpRight : jumpLeft;
        } else if (movingHorizontally) {
            if (lastDirection.equals("right")) {
                image = (currentFrame == 0) ? rightIdle : right1;
            } else {
                image = (currentFrame == 0) ? leftIdle : left1;
            }
        } else {
            // Idle: face last direction
            image = lastDirection.equals("right") ? rightIdle : leftIdle;
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    // getGroundLevel returns the world Y coordinate for the top of the collidable ground.
    private int getGroundLevel() {
        return 650 + gp.tileSize;
    }
}
