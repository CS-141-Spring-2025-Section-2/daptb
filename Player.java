package daptb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        //System.out.println("Player Constructor: gp = " + gp + ", keyH = " + keyH);
        
        if (gp == null) {
            throw new IllegalArgumentException("GamePanel is NULL!");
        }
        if (keyH == null) {
            throw new IllegalArgumentException("KeyHandler passed to Player is null!");
        }
    
        this.gp = gp;
        this.keyH = keyH;  

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        setDefaultValues();
        getPlayerImage();
    }
    
    public Player(GamePanel gp, KeyHandler keyH, int startX, int startY) {
        this.gp = gp;
        this.keyH = keyH;  // ✅ Use the passed KeyHandler instead of creating a new one

        this.worldX = startX;  
        this.worldY = startY;  

        this.screenX = gp.screenWidth / 2 - (gp.tileSize / 2);  
        this.screenY = gp.screenHeight / 2 - (gp.tileSize / 2);  

        setDefaultValues();  
        getPlayerImage();  
    }




    public final int screenX; 
    public final int screenY;

    // Physics Variables
    int velocityY = 0;
    int gravity = 1;
    int jumpStrength = -24;
    boolean jumping = false;
    boolean onGround = false;
    
    // Health (player has 100 HP)
    private int health = 100;
    private int maxHealth = 100;  // Maximum health
    private int currentHealth = maxHealth;  // Player's current health
    private boolean recentlyDamaged = false;  // Tracks if the player was recently hit
    private int damageFlashDuration = 20;  // How long the health bar flashes
    private int damageFlashCounter = 0; // Tracks the flash time
    private boolean knockedBack = false;  // True when knockback is active
    private int knockbackCounter = 0;  // How long knockback lasts
    private int knockbackDuration = 15;  // Number of frames for knockback
    private int knockbackSpeed = 5;  // Knockback distance per frame
    private String knockbackDirection = "left";  // Stores direction of knockback
    private boolean attacking = false;  // True when the player is attacking
    private int attackCounter = 0;  // Counts attack duration
    private int attackDuration = 30;  // Attack lasts for 30 frames (0.5 sec at 60 FPS)
    private boolean attackOnCooldown = false;  // Prevents spamming
    private int attackCooldown = 15;  // Frames before the next attack is allowed (0.25 sec at 60 FPS)
    private int attackCooldownCounter = 0;  // Tracks cooldown time
    private boolean enemyHit = false;

    
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
    private boolean isDead = false;  // 🔴 Tracks if player is dead

    
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
    



    public void setDefaultValues() {
        worldX = 150;
        // Set starting worldY so that the knight’s feet (worldY + tileSize) are on the ground.
        worldY = getGroundLevel() - gp.tileSize;
        speed = 2;
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
    
    // Public getter for health
    public int getHealth() {
        return health;
    }
    
    public void takeDamage(int amount, String enemyDirection) {
        if (isDead) return;  // 🛑 Prevent damage if already dead

        currentHealth -= amount;
        
        if (!isDead) {  
     // Trigger Knockback
        if (enemyDirection.equals("left")) {
            knockbackDirection = "left";  // Push player to the right
        } else {
            knockbackDirection = "right";  // Push player to the left
        }

        knockedBack = true;
        knockbackCounter = knockbackDuration;
        }
        System.out.println("Player took " + amount + " damage! Remaining Health: " + currentHealth);
        
     // 🩸 Check if incoming damage will kill the player
        if (currentHealth - amount <= 0) {
            currentHealth = 0;  // Set health to zero
            die();  // Trigger death sequence immediately
            return;  // Stop further processing
        }
            
         // Check if player is dead
         if (currentHealth <= 0) {
             System.out.println("Player has died!");  // Debugging info
             
             gp.pauseGame();  // 🔴 Pause all game actions
             AudioPlayer.stopMusic();  // 🔇 Stop background music

             
             gp.showGameOverScreen();  // Show Game Over screen (we'll implement this next) 
            }
        

        recentlyDamaged = true;
        damageFlashCounter = damageFlashDuration;

        

        // Optional: Check if the player is dead
        if (health <= 0) {
            System.out.println("Player has died!");
        }
      }
    
    private void die() {
        if (isDead) return;  // 🛑 Prevent multiple calls
        isDead = true;  // Mark player as dead

        System.out.println("Player has died!");  // Debug log

        gp.pauseGame();  // 🔴 Pause all actions
        AudioPlayer.stopMusic();  // 🔇 Stop background music
        gp.showGameOverScreen();  // Show Game Over screen
    }
    public boolean isDead() {
        return isDead;  // ✅ Returns the player's death state
    }


    public void attack(String attackType) {
        if (!attacking && !attackOnCooldown) {  // Prevents spam attacks
            attacking = true;
            attackCounter = attackDuration;  // Start attack duration timer
            attackOnCooldown = true;  // Start cooldown
            attackCooldownCounter = attackCooldown;  // Set cooldown time
            
            boolean enemyHit = false; // ✅ Declare variable inside method (resets per attack)

            System.out.println("Player used " + attackType);

            Rectangle attackHitbox;
            int attackRange = 50;

            if (lastDirection.equals("left")) {
                attackHitbox = new Rectangle(worldX - attackRange, worldY, attackRange, gp.tileSize);
            } else {
                attackHitbox = new Rectangle(worldX + gp.tileSize, worldY, attackRange, gp.tileSize);
            }

            if (gp.enemy != null && gp.enemy.getCurrentHealth() > 0) {  // ✅ Ensure enemy is alive
                Rectangle enemyHitbox = new Rectangle(gp.enemy.worldX, gp.enemy.worldY, gp.tileSize, gp.tileSize);
                if (attackHitbox.intersects(enemyHitbox)) {
                    enemyHit = true;  // ✅ Mark that we hit an enemy
                    gp.enemy.takeDamage(20, lastDirection);
                    System.out.println("Enemy HP: " + gp.enemy.getCurrentHealth());
                }
            }

            // ✅ Play only one sound based on attack result
            if (enemyHit) {
                AudioPlayer.playSound("weak-enemy-hit.wav");  // ✅ Only play hit sound if attack lands
            } else {
                playAttackSound();  // ✅ Play air attack sound only if we missed
            }
        }
    }
    public void checkKickHit() {
        for (Enemy enemy : gp.enemies) {  // Assuming gp.enemies stores all enemies
            Rectangle kickHitbox;
            
            if (direction.equals("right")) {
                kickHitbox = new Rectangle(worldX + gp.tileSize, worldY, gp.tileSize, gp.tileSize);
            } else {
                kickHitbox = new Rectangle(worldX - gp.tileSize, worldY, gp.tileSize, gp.tileSize);
            }
            
            Rectangle enemyHitbox = new Rectangle(enemy.worldX, enemy.worldY, gp.tileSize, gp.tileSize);
            
            if (kickHitbox.intersects(enemyHitbox)) {
                enemy.takeDamage(15, direction);  // Deal damage to the enemy
            }
        }
    }



    public boolean isAttacking() {
        return attacking;
    }

    public boolean isAttackOnCooldown() {
        return attackOnCooldown;
    }

    private void handleKnockback() {
        int knockbackSpeed = 5;  // Adjust as needed

        if (knockbackDirection.equals("left")) {
            worldX -= knockbackSpeed;  // Knock player left
        } else {
            worldX += knockbackSpeed;  // Knock player right
        }

        knockbackCounter--;
        if (knockbackCounter <= 0) {
            knockedBack = false;  // ✅ End knockback after duration
        }
    }

    public void update() {
    	 if (isDead) {
    		 if (knockedBack) handleKnockback();  // ✅ Allow knockback even after death
         return;  // 🛑 Prevent other actions when dead
    	 }
      // ✅ Process knockback for alive player
         if (knockedBack) handleKnockback();
    	 
        if (keyH == null) {
            System.out.println("KeyHandler is null in Player update()!");
            return;  // Prevent further execution
        }
        
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
            playAttackSound();  // Play sound only when key is freshly pressed
            canKick = false;  // Prevent repeat sound until key is released
            attackCounter = 30;  // Set attack duration
        } else if (!keyH.oPressed) {
            canKick = true; // Reset when key is released
            kicking = false;
        }
        if (attacking) {
            attackCounter--;
            if (attackCounter <= 0) {
                attacking = false;  // Reset attack state after 0.5 sec
            }
        }
        if (kicking) {
            attackCounter--;
            if (attackCounter == attackCounter - 10) {
                checkKickHit();  // Check if the kick connects with an enemy
            }
            if (attackCounter <= 0) {
                kicking = false;  // End kick attack
            }
        }


        if (attackOnCooldown) {  // Reduce cooldown timer
            attackCooldownCounter--;
            if (attackCooldownCounter <= 0) {
                attackOnCooldown = false;  // Cooldown ends, player can attack again
            }
        }

        if (!attacking) {  // Only allow movement when not attacking
            if (keyH.leftPressed) {
                worldX -= speed;
            } else if (keyH.rightPressed) {
                worldX += speed;
            }
        }

        // --- Jump Logic ---
        if (keyH != null && keyH.wPressed && onGround && canJump) {
            playJumpSound();
            velocityY = jumpStrength;
            jumping = true;
            onGround = false;
            canJump = false;
        } else if (keyH == null) {
            System.out.println("ERROR: keyH is null in update()");
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
        if (knockedBack) {
            if (knockbackDirection.equals("left")) {
                worldX -= knockbackSpeed;  // Move left if knocked back left
            } else {
                worldX += knockbackSpeed;  // Move right if knocked back right
            }

            knockbackCounter--;
            if (knockbackCounter <= 0) {
                knockedBack = false;  // Stop knockback effect
            }

            // Ensure the player stays within the screen boundaries
            if (worldX < 0) worldX = 0;  // Prevent moving off the left edge
            if (worldX > gp.worldWidth - gp.tileSize) worldX = gp.worldWidth - gp.tileSize;  // Prevent moving off the right edge

            // Camera Movement
            gp.cameraX = worldX - gp.cameraOffsetX;
            if (!jumping) {
                gp.cameraY = worldY - gp.cameraOffsetY;
            }

            // Prevent Camera from going out of bounds horizontally
            if (gp.cameraX < 0) gp.cameraX = 0;
            if (gp.cameraX > gp.worldWidth - gp.screenWidth) gp.cameraX = gp.worldWidth - gp.screenWidth;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        // Choose image based on state and animation frame:
        if (attacking) {
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
        
        // Fix the disappearing issue when flashing
        if (recentlyDamaged) {
            damageFlashCounter--;
            if (damageFlashCounter <= 0) {
                recentlyDamaged = false;  // Reset flashing effect
            } else if (damageFlashCounter % 10 < 5) {
                return;  // Skip drawing every few frames (blinking effect)
            }
        }
        
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        
        // Draw Health Bar
        drawHealthBar(g2, screenX, screenY - 10);
    }
    
    // Method to Draw Health Bar Above Player
    private void drawHealthBar(Graphics2D g2, int x, int y) {
        int barWidth = gp.tileSize;  // Match player width
        int barHeight = 5;  // Thin bar

        // Health ratio (fills the bar proportionally)
        int healthWidth = (int) ((double) currentHealth / maxHealth * barWidth);
        
        Color healthColor = Color.RED;
        if (recentlyDamaged) {
            healthColor = (damageFlashCounter % 10 < 5) ? Color.WHITE : Color.RED;  // Flash between red and white
        }

        // Draw Background Bar (Gray)
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, barWidth, barHeight);
        
        // Draw Flashing Health Bar
        g2.setColor(healthColor);
        g2.fillRect(x, y, healthWidth, barHeight);

        // Draw Health Bar (Red)
        g2.setColor(Color.RED);
        g2.fillRect(x, y, healthWidth, barHeight);

        // Draw Border
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, barWidth, barHeight);
        
        // Use Pixelated Font
        try {
            Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/PixelFont.ttf")).deriveFont(14f);
            g2.setFont(pixelFont);
        } catch (Exception e) {
            g2.setFont(new Font("Courier", Font.BOLD, 14));  // Fallback font
        }
        
        // Display HP Number
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(currentHealth + " / " + maxHealth, x + barWidth + 5, y + barHeight);  // HP number beside bar

        // Decrease Flash Counter
        if (recentlyDamaged) {
            damageFlashCounter--;
            if (damageFlashCounter <= 0) {
                recentlyDamaged = false;  // Stop flashing
            }
        }
    }

    // getGroundLevel returns the world Y coordinate for the top of the collidable ground.
    private int getGroundLevel() {
        return 650 + gp.tileSize;
    }
}