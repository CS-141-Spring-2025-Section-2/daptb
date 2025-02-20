package daptb;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;

import java.io.File;
import java.io.IOException;

public class Enemy extends Entity {
    GamePanel gp;
    private final int patrolRange = 200;
    private final int startingX;
    private int animationCounter = 0;
    private int animationSpeed = 30;
    private int currentFrame = 0;
    private boolean attacking = false;  // True when attacking
    private int attackCooldown = 60;    // Cooldown frames between attacks (1 sec at 60 FPS)
    private int attackCounter = 0;      // Tracks cooldown time
    private int maxHealth = 60;
    private int currentHealth = maxHealth; 
    private boolean knockedBack = false;
    private int knockbackCounter = 0;
    private int knockbackDuration = 10;
    private int knockbackSpeed = 4;
    private String knockbackDirection;
    private boolean isDead = false;  // True when enemy is dead
    private int deathTimer = 90;  // Countdown before enemy disappears (1 second at 60 FPS)
    private boolean flashing = false;  // True when flashing
    private int flashCounter = 0;  // Countdown timer
    private int flashDuration = 45;  // Flash for 45 frames (3 times for 15 frames each)
    private int hitPauseDuration = 10; // Pause duration when hit (10 frames)
    private int hitPauseCounter = 0;  // Countdown timer for hit pause

    public Enemy(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.startingX = startX;
        this.speed = 1;
        this.direction = "left";

        setEnemySprites();
    }

    public void setEnemySprites() {
        try {
            left1 = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-walk-left.png"));
            leftIdle = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-facing-left.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-walk-right.png"));
            rightIdle = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-facing-right.png"));
            // Load attack animations
            attackLeft = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-attack-left.png"));
            attackRight = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-attack-right.png"));
            
            leftDead = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-attack-left.png"));
            rightDead = ImageIO.read(getClass().getResourceAsStream("/daptb/stump-attack-right.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void die() {
        isDead = true;  // Mark enemy as dead
        knockedBack = true;  // Apply knockback on death
        knockbackCounter = knockbackDuration;
        
        Timer timer = new Timer(1000, e -> {
            if ("LevelOne".equals(gp.currentLevel)) {
                gp.showYouWinScreen();  // 🏆 Show YouWinPanel (Level One)
            } else if ("FinalLevel".equals(gp.currentLevel)) {
            	AudioPlayer.stopMusic();
            	new EndPanel();  // 🎬 Show EndPanel for Final Level
            	gp.getParentFrame().dispose();  // 🚪 Close the final level window
            }
        });
        timer.setRepeats(false);  
        timer.start();  
    }
    
    private void showEndCredits() {
        System.out.println("Showing end credits...");  // 📝 Debug log
        new EndPanel();  // 🎬 Show end credits panel
    }

    
    public int getCurrentHealth() {
        return currentHealth;
    }

    public void takeDamage(int amount, String playerDirection) {
        if (isDead) return;  // Prevent taking damage if already dead

        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            die();
            return;
        }
        
        // **Play Hit Sound Effect**
        AudioPlayer.playSound("weak-enemy-hit.wav");  // Ensure hit.wav is inside /sounds/

        // **Trigger Knockback**
        knockedBack = true;
        knockbackCounter = knockbackDuration;
        knockbackDirection = playerDirection.equals("left") ? "left" : "right";  // Push enemy opposite direction

        // **Trigger Flashing Effect**
        flashing = true;
        flashCounter = flashDuration;

        // **Trigger Hit Pause**
        hitPauseCounter = hitPauseDuration;
        
        System.out.println("Enemy took " + amount + " damage! Remaining HP: " + currentHealth);
    }
    
    public void onPlayerHit() {
        playSound("/res/weak-enemy-hit.wav");
    }

    public void playSound(String soundFile) {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void update() {
        if (isDead) {
            deathTimer--;
            if (deathTimer <= 0) {
                gp.enemy = null;  // Remove enemy from the game
            }
            return;  // Stop further actions if dead
        }
        
        if (flashing) {
            flashCounter--;
            if (flashCounter <= 0) {
                flashing = false;  // Stop flashing effect
            }
        }
        
        if (hitPauseCounter > 0) {
            hitPauseCounter--;  // Pause updates when hit
            return;
        }

        if (knockedBack) {
            if (knockbackDirection.equals("left")) {
                worldX -= knockbackSpeed;
            } else {
                worldX += knockbackSpeed;
            }

            knockbackCounter--;
            if (knockbackCounter <= 0) {
                knockedBack = false;  // Stop knockback effect
            }
        } else if (!attacking) {  // Move only when not attacking
            if (direction.equals("left")) {
                worldX -= speed;
                if (worldX < startingX - patrolRange) {
                    direction = "right";
                }
            } else {
                worldX += speed;
                if (worldX > startingX + patrolRange) {
                    direction = "left";
                }
            }
        }

        // Check if player is in range and attack only if not currently attacking
        if (isPlayerInAttackRange() && attackCounter == 0 && !gp.player.isDead()) {  
            attacking = true;
            attackCounter = attackCooldown;  // Start cooldown
            gp.player.takeDamage(10, direction);  // Damage player only if alive
       
            AudioPlayer.playSound("weak-enemy-hit.wav");   // Play attack sound
        }

        // Attack cooldown logic
        if (attackCounter > 0) {
            attackCounter--;
            if (attackCounter == 0) {
                attacking = false;  // Reset attack state after cooldown
            }
        }

        // Normal walking animation (when not attacking)
        if (!attacking) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                currentFrame = (currentFrame + 1) % 2;
                animationCounter = 0;
            }
        }
    }

    public boolean isPlayerInAttackRange() {
        int attackRange = 2; // Adjust based on sprite size
        Rectangle enemyHitbox = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        Rectangle attackZone;

        if (direction.equals("left")) {
            attackZone = new Rectangle(worldX - attackRange, worldY, attackRange, gp.tileSize);
        } else {
            attackZone = new Rectangle(worldX + gp.tileSize, worldY, attackRange, gp.tileSize);
        }

        Rectangle playerHitbox = new Rectangle(gp.player.worldX, gp.player.worldY, gp.tileSize, gp.tileSize);
        return attackZone.intersects(playerHitbox);
    }

    public void draw(Graphics2D g2) {
        if (isDead) {
            BufferedImage image = (direction.equals("left")) ? leftDead : rightDead;
            int screenX = worldX - gp.cameraX;
            int screenY = worldY - gp.cameraY;
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            return;
        }
        
        BufferedImage image = null;
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;
        
        if (flashing) {
            if ((flashCounter / 15) % 2 == 0) {  // Flashing logic: 3 times for 15 frames each
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% transparency
            } else {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Normal transparency
            }
        }

        if (attacking && attackCounter > attackCooldown - 20) {
            // Show attack sprite ONLY when in attack state
            image = (direction.equals("left")) ? attackLeft : attackRight;
        } else {
            // Normal movement animation
            if (direction.equals("left")) {
                image = (currentFrame == 0) ? left1 : leftIdle;
            } else {
                image = (currentFrame == 0) ? right1 : rightIdle;
            }
        } 

        // Ensure image is not null before drawing
        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            System.out.println("Error: Enemy image is null!");
        }

        if (flashing) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset transparency
        }
    }
}