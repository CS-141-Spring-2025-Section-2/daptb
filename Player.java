package daptb;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;

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

    // Edge-trigger jump flag; set to false once a jump is triggered and reset when the key is released.
    private boolean canJump = true;

    // NEW FIELDS for attack states:
    private boolean canPunch = false;
    private boolean canKick = false;

    // Declare missing variables
    private boolean punching = false;
    private boolean kicking = false;

    public void playJumpSound() {
        new Thread(() -> {
            try {
                // Load the sound file
                File soundFile = new File("src/daptb/JumpSound.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

                // Get a sound clip resource
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Play the sound immediately
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void playAttackSound() {
        new Thread(() -> {
            try {
                // Load the sound file
                File soundFile = new File("src/daptb/AirAttack.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

                // Get a sound clip resource
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Play the sound immediately
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

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
            // Jump images
            jumpRight = ImageIO.read(getClass().getResourceAsStream("/player/knight-jumping-right-pixilart.png"));
            jumpLeft  = ImageIO.read(getClass().getResourceAsStream("/player/knight-jumping-left-pixilart.png"));

            // Running and idle images
            right1    = ImageIO.read(getClass().getResourceAsStream("/player/knight-moving-right-pixilart-2.png"));
            rightIdle = ImageIO.read(getClass().getResourceAsStream("/player/knight-facing-right.png"));
            left1     = ImageIO.read(getClass().getResourceAsStream("/player/knight-moving-left-pixilart.png"));
            leftIdle  = ImageIO.read(getClass().getResourceAsStream("/player/knight-facing-left.png"));

            // Attack images for punching (key "I")
            attackRight = ImageIO.read(getClass().getResource("/player/knight-punch-right.png"));
            attackLeft  = ImageIO.read(getClass().getResource("/player/knight-punch-left.png"));
            // Attack images for kicking (key "O")
            attackRight2 = ImageIO.read(getClass().getResource("/player/knight-kick-right.png"));
            attackLeft2  = ImageIO.read(getClass().getResource("/player/knight-kick-left.png"));
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

        // --- Attack Logic (Edge-Triggered) ---
        if (keyH.iPressed && canPunch) {
            punching = true;
            playAttackSound();  // Play sound only when the key is freshly pressed
            canPunch = false;   // Prevent repeat sound until key is released
        } else if (!keyH.iPressed) {
            canPunch = true;    // Reset when key is released
            punching = false;
        }

        if (keyH.oPressed && canKick) {
            kicking = true;
            playAttackSound();  // Play sound only when the key is freshly pressed
            canKick = false;    // Prevent repeat sound until key is released
        } else if (!keyH.oPressed) {
            canKick = true;     // Reset when key is released
            kicking = false;
        }

        // --- Jump Logic ---
        if (keyH.wPressed && onGround && canJump) {
            playJumpSound();  // Play the sound as soon as jump is triggered
            velocityY = jumpStrength;
            jumping = true;
            onGround = false;
            canJump = false; // Prevent repeated jumping until key is released
        }

        // Reset canJump when "W" is not pressed (edge-trigger reset)
        if (!keyH.wPressed) {
            canJump = true;
        }

        // Apply Gravity
        velocityY += gravity;
        if (velocityY > 10) velocityY = 10; // Terminal velocity

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

        // Running animation update
        if (movingHorizontally && !jumping) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                currentFrame = (currentFrame + 1) % 2; // Alternate between 0 and 1
                animationCounter = 0;
            }
        } else {
            currentFrame = 0; // Default frame when not moving
        }

        // Camera Movement
        gp.cameraX = worldX - gp.cameraOffsetX;
        if (!jumping) {
            gp.cameraY = worldY - gp.cameraOffsetY;
        }

        // Prevent Camera from going out of bounds horizontally
        if (gp.cameraX < 0) gp.cameraX = 0;
        if (gp.cameraX > gp.worldWidth - gp.screenWidth) gp.cameraX = gp.worldWidth - gp.screenWidth;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        // Choose image based on state and animation frame:
        if (punching) {
            // When punching, show the attack image based on lastDirection.
            image = lastDirection.equals("right") ? attackRight : attackLeft;
        } else if (kicking) {
            // When kicking, show the kick image based on lastDirection.
            image = lastDirection.equals("right") ? attackRight2 : attackLeft2;
        } else if (jumping) {
            image = lastDirection.equals("right") ? jumpRight : jumpLeft;
        } else if (movingHorizontally) {
            if (lastDirection.equals("right")) {
                image = (currentFrame == 0) ? rightIdle : right1;
            } else {
                image = (currentFrame == 0) ? leftIdle : left1;
            }
        } else {
            // Idle: face the last direction.
            image = lastDirection.equals("right") ? rightIdle : leftIdle;
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    // getGroundLevel returns the world Y coordinate for the top of the collidable ground.
    private int getGroundLevel() {
        return 650 + gp.tileSize;
    }
}