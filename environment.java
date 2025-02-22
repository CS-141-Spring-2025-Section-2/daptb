package daptb;


// Benjamin Nukunu Davis

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class environment extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener, ComponentListener {
    private int WIDTH;  // Dynamic width
    private int HEIGHT; // Dynamic height
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final int PLAYER_HEALTH = 60;
    private static final int ENEMY_HEALTH = 5;
    private static final int ENEMIES_PER_WAVE = 10;
    private static final int TOTAL_ENEMIES = 25;
    private static final int ENEMY_SHOOT_DISTANCE = 400; // Increased shooting distance
    private static final int ENEMY_SPEED = 2;
    private static final int ENEMY_STOP_DISTANCE = 100; // Distance at which enemies stop moving
    private static final int BOMB_RANGE = 200; // Range of the bomb
    private static final int BOMB_COOLDOWN = 1; // Bomb can be used once per level

    private int playerX, playerY;
    private int playerHealth;
    private boolean isGameRunning;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> playerBullets;
    private ArrayList<Bullet> enemyBullets;
    private ArrayList<Animation> animations; // For death animations
    private int currentLevel;
    private int enemiesRemaining;
    private Font customFont;
    private boolean[] keysPressed;
    private int mouseX, mouseY;
    private double playerAngle; // Angle for player rotation
    private String playerName; // Player's name
    private boolean showLevelCompleteBanner; // Flag to show level complete banner
    private Image backgroundImage; // Background image for each level
    private Clip shootSound; // Sound clip for shooting
    private Clip backgroundMusic; // Sound clip for background music
    private boolean isEnemySpawning; // Flag to control staggered enemy spawning
    private long lastEnemySpawnTime; // Time of last enemy spawn
    private int currentWave; // Current wave of enemies
    private int bombUsesRemaining; // Number of bomb uses remaining
    private boolean isPlayerBoxedIn; // Flag to check if the player is boxed in by enemies

    public environment() {
        // Initialize dynamic width and height
        WIDTH = 800;
        HEIGHT = 600;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this); // Add component listener for resizing

        playerX = WIDTH / 2 - 25; // Center player horizontally
        playerY = HEIGHT / 2 - 25; // Center player vertically
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        animations = new ArrayList<>();
        currentLevel = 1;
        enemiesRemaining = TOTAL_ENEMIES;
        keysPressed = new boolean[4];
        mouseX = playerX;
        mouseY = playerY;
        playerAngle = 0; // Initial angle
        showLevelCompleteBanner = false;
        isEnemySpawning = false;
        lastEnemySpawnTime = 0;
        currentWave = 0;
        bombUsesRemaining = BOMB_COOLDOWN;
        isPlayerBoxedIn = false;

        // Ask for player's name
        playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player"; // Default name
        }

        loadCustomFont();
        loadBackgroundImage(); // Load background for the current level
        loadShootSound(); // Load the shooting sound
        loadBackgroundMusic(); // Load background music
        startLevel();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // Update width and height when the window is resized
        WIDTH = getWidth();
        HEIGHT = getHeight();

        // Reposition the player to the center
        playerX = WIDTH / 2 - 25;
        playerY = HEIGHT / 2 - 25;

        // Repaint the game to reflect the new size
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    private void loadCustomFont() {
        try {
            // Load custom font from the classpath
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("custom_font.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.out.println("Custom font not found! Using default font.");
            customFont = new Font("Arial", Font.BOLD, 24);
        }
    }

    private void loadBackgroundImage() {
        String imagePath = "";
        switch (currentLevel) {
            case 1:
                imagePath = "egypt_background.jpg"; // Ancient Egypt
                break;
            case 2:
                imagePath = "medieval_background.jpg"; // Medieval Europe
                break;
            case 3:
                imagePath = "ww1_background.jpg"; // World War I
                break;
            case 4:
                imagePath = "ww2_background.jpg"; // World War II
                break;
            case 5:
                imagePath = "future_background.jpg"; // Future Warfare
                break;
            default:
                imagePath = "default_background.jpg"; // Default background
                break;
        }

        try {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.out.println("Background image not found! Using gradient background instead.");
            backgroundImage = null;
        }
    }

    private void loadShootSound() {
        try {
            // Load the sound file from the classpath
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("ak-47-14501.wav"));
            shootSound = AudioSystem.getClip();
            shootSound.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | NullPointerException e) {
            System.out.println("Error loading shoot sound: " + e.getMessage());
        }
    }

    private void loadBackgroundMusic() {
        try {
            // Load the background music file from the classpath
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("background_music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);

            // Set volume to 50% to ensure it's not too loud
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-3.0f); // Reduce volume by 3 decibels

            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // Loop the background music
            backgroundMusic.start(); // Start playing the background music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | NullPointerException e) {
            System.out.println("Error loading background music: " + e.getMessage());
        }
    }

    private void playShootSound() {
        if (shootSound != null) {
            shootSound.setFramePosition(0); // Rewind the sound to the beginning
            shootSound.start(); // Play the sound
        }
    }

    private void startLevel() {
        isEnemySpawning = true;
        lastEnemySpawnTime = System.currentTimeMillis();
        currentWave = 0;
        playerHealth = PLAYER_HEALTH; // Reset player health
        bombUsesRemaining = BOMB_COOLDOWN; // Reset bomb uses
    }

    private void spawnEnemyWave() {
        Random random = new Random();
        String era = getEraForLevel(currentLevel);
        int formationRows = 2; // Number of rows in the formation
        int formationCols = ENEMIES_PER_WAVE / formationRows; // Number of columns in the formation

        for (int i = 0; i < ENEMIES_PER_WAVE && enemiesRemaining > 0; i++) {
            int enemyX = WIDTH / 2 - (formationCols * 50) / 2 + (i % formationCols) * 50;
            int enemyY = 50 + (i / formationCols) * 50;

            Enemy enemy = createEnemy(enemyX, enemyY, ENEMY_HEALTH, currentLevel, era);

            // Enable shooting from afar starting from level 2
            if (currentLevel >= 2) {
                enemy.setCanShootFromAfar(true);
            }

            enemies.add(enemy);
            enemiesRemaining--;
        }
        currentWave++;
    }

    private String getEraForLevel(int level) {
        switch (level) {
            case 1:
                return "Ancient";
            case 2:
                return "Medieval";
            case 3:
                return "WW1";
            case 4:
                return "WW2";
            case 5:
                return "Future";
            default:
                return "Unknown";
        }
    }

    private Enemy createEnemy(int x, int y, int health, int level, String era) {
        switch (level) {
            case 1:
                return new Enemy(x, y, health, "Pharaoh", new Color(255, 215, 0), level, 2, "circle", era); // Ancient Egypt (gold)
            case 2:
                return new Enemy(x, y, health, "Knight", new Color(105, 105, 105), level, 3, "rectangle", era); // Medieval Europe (dark gray)
            case 3:
                return new Enemy(x, y, health, "Soldier", new Color(107, 142, 35), level, 4, "triangle", era); // World War I (olive green)
            case 4:
                return new Enemy(x, y, health, "Tank", new Color(34, 139, 34), level, 5, "rectangle", era); // World War II (green)
            case 5:
                return new Enemy(x, y, health, "Robot", new Color(0, 191, 255), level, 6, "circle", era); // Future Warfare (neon blue)
            default:
                return new Enemy(x, y, health, "Enemy", Color.RED, level, 2, "circle", era); // Default enemy
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 60), WIDTH, HEIGHT, new Color(10, 10, 30));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Draw player name banner at the top of the window
        drawPlayerNameBanner(g);

        // Draw level name
        drawLevelName(g);

        // Draw player
        drawPlayer(g);

        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // Draw bullets
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }

        // Draw animations
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation animation = animations.get(i);
            animation.draw(g);
            if (animation.isFinished()) {
                animations.remove(i);
            }
        }

        // Draw health bar
        drawHealthBar(g);

        // Draw level and enemies remaining
        g.setColor(Color.WHITE);
        g.setFont(customFont);
        g.drawString("Level: " + currentLevel, 10, 60);
        g.drawString("Enemies Remaining: " + (enemiesRemaining + enemies.size()), 10, 90);

        // Draw bomb uses remaining
        g.drawString("Bombs: " + bombUsesRemaining, 10, 120);

        // Draw level complete banner if needed
        if (showLevelCompleteBanner) {
            drawLevelCompleteBanner(g);
        }
    }

    private void drawPlayerNameBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 255, 255, 150)); // Semi-transparent white
        g2d.fillRoundRect(WIDTH / 2 - 150, 10, 300, 40, 20, 20); // Rounded rectangle centered at the top
        g2d.setColor(Color.BLACK);
        g2d.setFont(customFont.deriveFont(20f));
        g2d.drawString("Player: " + playerName, WIDTH / 2 - 130, 35); // Player name centered
    }

    private void drawLevelName(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        String levelName = "";
        switch (currentLevel) {
            case 1:
                levelName = "Ancient Egypt";
                break;
            case 2:
                levelName = "Medieval Europe";
                break;
            case 3:
                levelName = "World War I";
                break;
            case 4:
                levelName = "World War II";
                break;
            case 5:
                levelName = "Future Warfare";
                break;
        }
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(customFont.deriveFont(30f));
        g2d.drawString(levelName, WIDTH / 2 - 100, HEIGHT - 20); // Display level name at the bottom
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw player as a circle with a line indicating direction
        g2d.setColor(Color.BLUE);
        g2d.fillOval(playerX, playerY, 50, 50); // Player body

        // Draw a larger red line indicating the direction the player is facing
        int lineX = playerX + 25 + (int) (50 * Math.cos(playerAngle)); // Increased length
        int lineY = playerY + 25 + (int) (50 * Math.sin(playerAngle)); // Increased length
        g2d.setColor(Color.RED); // Changed to red
        g2d.setStroke(new BasicStroke(3)); // Thicker line
        g2d.drawLine(playerX + 25, playerY + 25, lineX, lineY);
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 200;
        int barHeight = 20;
        int barX = 10;
        int barY = 10;
        int healthWidth = (int) ((double) playerHealth / PLAYER_HEALTH * barWidth);

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private void drawLevelCompleteBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black
        g2d.fillRect(WIDTH / 2 - 150, HEIGHT / 2 - 100, 300, 200); // Banner background

        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont.deriveFont(30f));
        g2d.drawString("Level " + currentLevel + " Complete!", WIDTH / 2 - 130, HEIGHT / 2 - 50);
        g2d.drawString("Advance?", WIDTH / 2 - 50, HEIGHT / 2 - 20);

        // Draw "Continue" button
        g2d.setColor(Color.GREEN);
        g2d.fillRect(WIDTH / 2 - 120, HEIGHT / 2 + 10, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Yes", WIDTH / 2 - 100, HEIGHT / 2 + 40);

        // Draw "Quit" button
        g2d.setColor(Color.RED);
        g2d.fillRect(WIDTH / 2 + 20, HEIGHT / 2 + 10, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("No", WIDTH / 2 + 50, HEIGHT / 2 + 40);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) keysPressed[0] = true; // Left
        if (key == KeyEvent.VK_RIGHT) keysPressed[1] = true; // Right
        if (key == KeyEvent.VK_UP) keysPressed[2] = true; // Up
        if (key == KeyEvent.VK_DOWN) keysPressed[3] = true; // Down
        if (key == KeyEvent.VK_SPACE) {
            // Shoot in the direction the player is facing
            double bulletSpeedX = BULLET_SPEED * Math.cos(playerAngle);
            double bulletSpeedY = BULLET_SPEED * Math.sin(playerAngle);
            playerBullets.add(new Bullet(playerX + 25, playerY + 25, bulletSpeedX, bulletSpeedY, Color.RED)); // Red bullets
            playShootSound(); // Play the shooting sound
        }
        if (key == KeyEvent.VK_G && bombUsesRemaining > 0) {
            activateBomb();
            bombUsesRemaining--;
        }
    }

    private void activateBomb() {
        // Find the three closest enemies
        ArrayList<Enemy> closestEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            double distance = Math.hypot(playerX - enemy.x, playerY - enemy.y);
            if (distance <= BOMB_RANGE) {
                closestEnemies.add(enemy);
                if (closestEnemies.size() >= 3) {
                    break;
                }
            }
        }

        // Kill the closest enemies
        for (Enemy enemy : closestEnemies) {
            animations.add(new Animation(enemy.x, enemy.y, 50, 50, 10)); // Add death animation
            enemies.remove(enemy);
        }

        // Free the player if boxed in
        isPlayerBoxedIn = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) keysPressed[0] = false; // Left
        if (key == KeyEvent.VK_RIGHT) keysPressed[1] = false; // Right
        if (key == KeyEvent.VK_UP) keysPressed[2] = false; // Up
        if (key == KeyEvent.VK_DOWN) keysPressed[3] = false; // Down
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        // Update player angle based on mouse position
        mouseX = e.getX();
        mouseY = e.getY();
        playerAngle = Math.atan2(mouseY - (playerY + 25), mouseX - (playerX + 25));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // This method is required by MouseMotionListener but is not used in this game
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (showLevelCompleteBanner) {
            int x = e.getX();
            int y = e.getY();

            // Check if "Continue" button is clicked
            if (x >= WIDTH / 2 - 120 && x <= WIDTH / 2 - 20 && y >= HEIGHT / 2 + 10 && y <= HEIGHT / 2 + 50) {
                showLevelCompleteBanner = false;
                if (currentLevel < 5) {
                    currentLevel++;
                    enemiesRemaining = TOTAL_ENEMIES;
                    loadBackgroundImage(); // Load new background for the next level
                    startLevel();
                } else {
                    // Player has completed the final level
                    // Transition to the EndPanel
                    JFrame endFrame = new JFrame("Game Over");
                    EndScreenPanel endPanel = new EndScreenPanel(endFrame, true); // Pass the JFrame and playEndMusic flag
                    endFrame.add(endPanel); // Add the EndPanel to the JFrame
                    endFrame.pack(); // Resize the JFrame to fit the EndPanel
                    endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
                    endFrame.setLocationRelativeTo(null); // Center the window on the screen
                    endFrame.setVisible(true); // Show the EndPanel

                    // Close the current game window
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    frame.dispose();
                }
            }

            // Check if "Quit" button is clicked
            if (x >= WIDTH / 2 + 20 && x <= WIDTH / 2 + 120 && y >= HEIGHT / 2 + 10 && y <= HEIGHT / 2 + 50) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void run() {
        while (isGameRunning) {
            updateGame();
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        showGameOverPopup();
    }

    private void updateGame() {
        // Update player position based on keys pressed
        if (!isPlayerBoxedIn) {
            if (keysPressed[0] && playerX > 0) playerX -= PLAYER_SPEED; // Left
            if (keysPressed[1] && playerX < WIDTH - 50) playerX += PLAYER_SPEED; // Right
            if (keysPressed[2] && playerY > 0) playerY -= PLAYER_SPEED; // Up
            if (keysPressed[3] && playerY < HEIGHT - 50) playerY += PLAYER_SPEED; // Down
        }

        // Spawn enemies in waves of 5
        if (isEnemySpawning && enemies.isEmpty() && enemiesRemaining > 0) {
            spawnEnemyWave();
        }

        // Move enemies and make them shoot
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            double distanceToPlayer = Math.hypot(playerX - enemy.x, playerY - enemy.y);
            if (distanceToPlayer > ENEMY_STOP_DISTANCE) {
                enemy.moveTowards(playerX, playerY, ENEMY_SPEED);
            } else {
                // Stop moving and shoot
                if (enemy.canShoot()) {
                    double angle = Math.atan2(playerY - enemy.y, playerX - enemy.x);
                    double bulletSpeedX = BULLET_SPEED * Math.cos(angle);
                    double bulletSpeedY = BULLET_SPEED * Math.sin(angle);
                    enemyBullets.add(new Bullet(enemy.x + 25, enemy.y + 25, bulletSpeedX, bulletSpeedY, Color.YELLOW));
                    enemy.resetShootCooldown();
                    playShootSound(); // Play the shooting sound
                }
            }

            // Avoid collisions with other enemies
            enemy.avoidCollisions(enemies);

            // Maintain distance between enemies
            enemy.maintainDistance(enemies);

            // Dodge bullets
            for (Bullet bullet : playerBullets) {
                double distanceToBullet = Math.hypot(bullet.x - enemy.x, bullet.y - enemy.y);
                if (distanceToBullet < 50) { // If bullet is close
                    double angle = Math.atan2(bullet.y - enemy.y, bullet.x - enemy.x);
                    enemy.x += 5 * Math.cos(angle + Math.PI / 2); // Move perpendicular to the bullet's trajectory
                    enemy.y += 5 * Math.sin(angle + Math.PI / 2);
                }
            }
        }

        // Move bullets
        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                bulletIterator.remove();
            }
        }
        bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                bulletIterator.remove();
            }
        }

        // Check collisions
        checkCollisions();

        // Check if the player is boxed in by enemies
        checkIfPlayerIsBoxedIn();

        // Check level completion
        if (enemies.isEmpty() && enemiesRemaining == 0) {
            showLevelCompleteBanner = true;
        }
    }

    private void checkIfPlayerIsBoxedIn() {
        int boxedInThreshold = 5; // Number of enemies required to box in the player
        int enemiesNearPlayer = 0;

        for (Enemy enemy : enemies) {
            double distance = Math.hypot(playerX - enemy.x, playerY - enemy.y);
            if (distance <= ENEMY_STOP_DISTANCE) {
                enemiesNearPlayer++;
            }
        }

        if (enemiesNearPlayer >= boxedInThreshold) {
            isPlayerBoxedIn = true;
        } else {
            isPlayerBoxedIn = false;
        }
    }

    private void checkCollisions() {
        // Create a copy of the playerBullets list to avoid concurrent modification
        ArrayList<Bullet> playerBulletsCopy = new ArrayList<>(playerBullets);
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemies);

        // Player bullets hitting enemies
        for (Bullet bullet : playerBulletsCopy) {
            for (Enemy enemy : enemiesCopy) {
                if (bullet.x >= enemy.x && bullet.x <= enemy.x + 50 &&
                    bullet.y >= enemy.y && bullet.y <= enemy.y + 50) {
                    enemy.health--;
                    if (enemy.health <= 0) {
                        // Add death animation
                        animations.add(new Animation(enemy.x, enemy.y, 50, 50, 10));
                        enemies.remove(enemy); // Remove the enemy from the original list
                    }
                    playerBullets.remove(bullet); // Remove the bullet from the original list
                    break; // Exit the inner loop after handling the collision
                }
            }
        }

        // Create a copy of the enemyBullets list to avoid concurrent modification
        ArrayList<Bullet> enemyBulletsCopy = new ArrayList<>(enemyBullets);

        // Enemy bullets hitting player
        for (Bullet bullet : enemyBulletsCopy) {
            if (bullet.x >= playerX && bullet.x <= playerX + 50 &&
                bullet.y >= playerY && bullet.y <= playerY + 50) {
                playerHealth--;
                enemyBullets.remove(bullet); // Remove the bullet from the original list
                if (playerHealth <= 0) {
                    isGameRunning = false;
                    restartLevel(); // Restart the current level on player death
                }
                break; // Exit the loop after handling the collision
            }
        }
    }

    private void restartLevel() {
        playerX = WIDTH / 2 - 25; // Center player horizontally
        playerY = HEIGHT / 2 - 25; // Center player vertically
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;
        enemies.clear();
        playerBullets.clear();
        enemyBullets.clear();
        animations.clear();
        enemiesRemaining = TOTAL_ENEMIES;
        showLevelCompleteBanner = false;

        // Reset enemy speed to default
        for (Enemy enemy : enemies) {
            enemy.resetSpeed(); // Add this method to the Enemy class
        }

        startLevel();
        new Thread(this).start();
    }

    private void showGameOverPopup() {
        String[] options = {"Restart Level", "Quit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Game Over!",
            "Game Over",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            restartLevel(); // Restart the current level
        } else {
            System.exit(0);
        }
    }

    private int calculateFinalScore() {
        // Example calculation, you can adjust this based on your game's scoring system
        return (currentLevel * 1000) + (playerHealth * 10);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Environment");
        environment game = new environment();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(game).start();
    }

    // Inner classes for Enemy, Bullet, and Animation
    class Enemy {
        int x, y, health;
        private long lastShotTime;
        private static final long SHOOT_COOLDOWN = 1000; // 1 second cooldown
        private int level;
        private String type;
        private Color color;
        private int speed; // Speed of the enemy
        private String shape; // Shape of the enemy
        private String era; // Era of the enemy
        private boolean canShootFromAfar; // Flag to enable shooting from afar

        Enemy(int x, int y, int health, String type, Color color, int level, int speed, String shape, String era) {
            this.x = x;
            this.y = y;
            this.health = health;
            this.lastShotTime = System.currentTimeMillis();
            this.level = level;
            this.type = type;
            this.color = color;
            this.speed = speed; // Set enemy speed based on level
            this.shape = shape; // Set enemy shape
            this.era = era; // Set enemy era
            this.canShootFromAfar = false; // Default to not shooting from afar
        }

        void setCanShootFromAfar(boolean canShootFromAfar) {
            this.canShootFromAfar = canShootFromAfar;
        }

        void resetSpeed() {
            // Reset speed to default based on level
            switch (level) {
                case 1:
                    speed = 2;
                    break;
                case 2:
                    speed = 3;
                    break;
                case 3:
                    speed = 4;
                    break;
                case 4:
                    speed = 5;
                    break;
                case 5:
                    speed = 6;
                    break;
                default:
                    speed = 2; // Default speed
                    break;
            }
        }

        void moveTowards(int targetX, int targetY, int speed) {
            double angle = Math.atan2(targetY - y, targetX - x);
            switch (era) {
                case "Ancient":
                    // Ancient enemies move more randomly
                    angle += (Math.random() - 0.5) * 0.5;
                    break;
                case "Medieval":
                    // Medieval enemies move in a straight line
                    break;
                case "WW1":
                    // WW1 enemies move in a zigzag pattern
                    angle += Math.sin(System.currentTimeMillis() / 500.0) * 0.5;
                    break;
                case "WW2":
                    // WW2 enemies move in a straight line but faster
                    speed *= 1.5;
                    break;
                case "Future":
                    // Future enemies move in a circular pattern
                    angle += Math.sin(System.currentTimeMillis() / 300.0) * 0.5;
                    break;
            }
            x += speed * Math.cos(angle);
            y += speed * Math.sin(angle);
        }

        void avoidCollisions(ArrayList<Enemy> enemies) {
            double minDistance = 50; // Minimum distance between enemies
            for (Enemy other : enemies) {
                if (other != this) {
                    double distance = Math.hypot(x - other.x, y - other.y);
                    if (distance < minDistance) {
                        // Move away from the other enemy
                        double angle = Math.atan2(y - other.y, x - other.x);
                        x += (minDistance - distance) * Math.cos(angle);
                        y += (minDistance - distance) * Math.sin(angle);
                    }
                }
            }
        }

        void maintainDistance(ArrayList<Enemy> enemies) {
            double minDistance = 60; // Minimum distance between enemies
            for (Enemy other : enemies) {
                if (other != this) {
                    double distance = Math.hypot(x - other.x, y - other.y);
                    if (distance < minDistance) {
                        // Move away from the other enemy
                        double angle = Math.atan2(y - other.y, x - other.x);
                        x += (minDistance - distance) * Math.cos(angle);
                        y += (minDistance - distance) * Math.sin(angle);
                    }
                }
            }
        }

        boolean canShoot() {
            return System.currentTimeMillis() - lastShotTime >= SHOOT_COOLDOWN / level; // Faster shooting at higher levels
        }

        void resetShootCooldown() {
            lastShotTime = System.currentTimeMillis();
        }

        void draw(Graphics g) {
            g.setColor(color);
            switch (shape) {
                case "circle":
                    g.fillOval(x, y, 50, 50); // Draw enemy as a circle
                    break;
                case "rectangle":
                    g.fillRect(x, y, 50, 50); // Draw enemy as a rectangle
                    break;
                case "triangle":
                    int[] xPoints = {x + 25, x, x + 50};
                    int[] yPoints = {y, y + 50, y + 50};
                    g.fillPolygon(xPoints, yPoints, 3); // Draw enemy as a triangle
                    break;
                default:
                    g.fillOval(x, y, 50, 50); // Default to circle
                    break;
            }
        }
    }

    class Bullet {
        double x, y, speedX, speedY;
        Color color;

        Bullet(double x, double y, double speedX, double speedY, Color color) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.color = color;
        }

        void move() {
            x += speedX;
            y += speedY;
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval((int) x, (int) y, 10, 10);
        }
    }

    class Animation {
        int x, y, width, height, frames;
        int currentFrame;

        Animation(int x, int y, int width, int height, int frames) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.frames = frames;
            this.currentFrame = 0;
        }

        void draw(Graphics g) {
            if (currentFrame < frames) {
                g.setColor(new Color(255, 0, 0, 255 - (currentFrame * 25))); // Fading effect
                g.fillOval(x + currentFrame * 2, y + currentFrame * 2, width - currentFrame * 4, height - currentFrame * 4);
                currentFrame++;
            }
        }

        boolean isFinished() {
            return currentFrame >= frames;
        }
    }
}