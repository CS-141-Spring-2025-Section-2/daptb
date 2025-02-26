package daptb;


//Benjamin Nukunu Davis

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class environment extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener, ComponentListener {
    // Game constants
    private int WIDTH;  // Dynamic width of the game window
    private int HEIGHT; // Dynamic height of the game window
    private static final int PLAYER_SPEED = 5; // Speed of the player
    private static final int BULLET_SPEED = 10; // Speed of bullets
    private static final int PLAYER_HEALTH = 60; // Player's starting health
    private static final int ENEMY_HEALTH = 5; // Enemy's starting health
    private static final int ENEMIES_PER_WAVE = 10; // Number of enemies per wave
    private static final int TOTAL_ENEMIES = 25; // Total enemies in the level
    private static final int ENEMY_SHOOT_DISTANCE = 400; // Distance at which enemies shoot
    private static final int ENEMY_SPEED = 2; // Speed of enemies
    private static final int ENEMY_STOP_DISTANCE = 100; // Distance at which enemies stop moving
    private static final int BOMB_RANGE = 200; // Range of the bomb
    private static final int BOMB_COOLDOWN = 1; // Number of bomb uses per level

    // Game variables
    private int playerX, playerY; // Player's position
    private int playerHealth; // Player's current health
    private boolean isGameRunning; // Flag to check if the game is running
    private ArrayList<Enemy> enemies; // List of enemies
    private ArrayList<Bullet> playerBullets; // List of player bullets
    private ArrayList<Bullet> enemyBullets; // List of enemy bullets
    private ArrayList<Animation> animations; // List of animations (e.g., explosions)
    private int currentLevel; // Current level
    private int enemiesRemaining; // Number of enemies remaining in the level
    private Font customFont; // Custom font for the game
    private boolean[] keysPressed; // Array to track which keys are pressed
    private int mouseX, mouseY; // Mouse position
    private double playerAngle; // Angle of the player's gun
    private String playerName = "baptb customer"; // Player's name
    private boolean showLevelCompleteBanner; // Flag to show level complete banner
    private Image backgroundImage; // Background image for the current level
    private Clip shootSound; // Sound clip for shooting
    private Clip backgroundMusic; // Sound clip for background music
    private boolean isEnemySpawning; // Flag to control enemy spawning
    private long lastEnemySpawnTime; // Time of the last enemy spawn
    private int currentWave; // Current wave of enemies
    private int bombUsesRemaining; // Number of bomb uses remaining
    private boolean isPlayerBoxedIn; // Flag to check if the player is boxed in by enemies

    // Player character
    private String selectedCharacter; // Selected character from the character selection screen
    private Image playerSpriteLeft, playerSpriteRight; // Sprites for the selected character
    private boolean isFacingLeft; // Flag to track player's facing direction

    // Constructor
    public environment(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
        loadSprites(); // Loading player sprites
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        // Initializing game window size
        WIDTH = 800;
        HEIGHT = 600;

        // Setting up the game panel
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true); // Making sure the panel is focusable
        addKeyListener(this); // Adding key listener for player movement
        addMouseListener(this); // Adding mouse listener for shooting
        addMouseMotionListener(this); // Adding mouse motion listener for aiming
        addComponentListener(this); // Adding component listener for resizing

        // Initializing player position and health
        playerX = WIDTH / 2 - 25; // Centering player horizontally
        playerY = HEIGHT / 2 - 25; // Centering player vertically
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;

        // Initializing lists
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        animations = new ArrayList<>();

        // Initializing game state
        currentLevel = 1;
        enemiesRemaining = TOTAL_ENEMIES;
        keysPressed = new boolean[4]; // Tracking arrow keys (left, right, up, down)
        mouseX = playerX;
        mouseY = playerY;
        playerAngle = 0; // Initial angle of the gun
        showLevelCompleteBanner = false;
        isEnemySpawning = false;
        lastEnemySpawnTime = 0;
        currentWave = 0;
        bombUsesRemaining = BOMB_COOLDOWN;
        isPlayerBoxedIn = false;
        isFacingLeft = false;

        // Loading resources
        loadCustomFont(); // Loading custom font
        loadBackgroundImage(); // Loading background image for the current level
        loadShootSound(); // Loading shooting sound
        loadBackgroundMusic(); // Loading background music

        // Starting the level
        startLevel();
    }

    
    
    
    // Loading player sprites based on the selected character
    private void loadSprites() {
        String basePath = "C:\\Users\\benti\\eclipse-workspace\\Java_assignments\\src\\daptb\\";
        String leftSpritePath = "";
        String rightSpritePath = "";

        // Determining sprite paths based on the selected character
        switch (selectedCharacter.toLowerCase()) {
            case "warrior":
                leftSpritePath = basePath + "warrior-ship-left.png";
                rightSpritePath = basePath + "warrior-ship-right.png";
                break;
            case "mage":
                leftSpritePath = basePath + "mage-ship-left.png";
                rightSpritePath = basePath + "mage-ship-right.png";
                break;
            case "rogue":
                leftSpritePath = basePath + "rogue-ship-left.png";
                rightSpritePath = basePath + "rogue-ship-right.png";
                break;
            case "assassin":
                leftSpritePath = basePath + "assasin-ship-left.png";
                rightSpritePath = basePath + "assasin-ship-right.png";
                break;
            case "druid":
                leftSpritePath = basePath + "druid-ship-left.png";
                rightSpritePath = basePath + "druid-ship-right.png";
                break;
            case "paladin":
                leftSpritePath = basePath + "paladin-ship-left.png";
                rightSpritePath = basePath + "paladin-ship-right.png";
                break;
            default:
                leftSpritePath = basePath + "default-ship-left.png";
                rightSpritePath = basePath + "default-ship-right.png";
                break;
        }

        // Loading left and right sprites
        try {
            playerSpriteLeft = new ImageIcon(leftSpritePath).getImage();
            playerSpriteRight = new ImageIcon(rightSpritePath).getImage();
        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
        }
    }

    
    
    
    // Handling window resizing
    @Override
    public void componentResized(ComponentEvent e) {
        WIDTH = getWidth();
        HEIGHT = getHeight();
        playerX = WIDTH / 2 - 25; // Re-centering player
        playerY = HEIGHT / 2 - 25;
        repaint(); // Redrawing the game
    }

    
    
    
    // Unused component listener methods
    @Override
    public void componentMoved(ComponentEvent e) {}
    @Override
    public void componentShown(ComponentEvent e) {}
    @Override
    public void componentHidden(ComponentEvent e) {}

    // Loading custom font
    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("custom_font.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.out.println("Custom font not found! Using default font.");
            customFont = new Font("Arial", Font.BOLD, 24);
        }
    }

    
    
    // Loading background image for the current level
    private void loadBackgroundImage() {
        String imagePath = "";
        switch (currentLevel) {
            case 1:
                imagePath = "egypt_background.jpg";
                break;
            case 2:
                imagePath = "medieval_background.jpg";
                break;
            case 3:
                imagePath = "ww1_background.jpg";
                break;
            case 4:
                imagePath = "ww2_background.jpg";
                break;
            case 5:
                imagePath = "future_background.jpg";
                break;
            default:
                imagePath = "default_background.jpg";
                break;
        }

        try {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.out.println("Background image not found! Using gradient background instead.");
            backgroundImage = null;
        }
    }

    
    
    
    
    // Loading shooting sound
    private void loadShootSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("ak-47-14501.wav"));
            shootSound = AudioSystem.getClip();
            shootSound.open(audioInputStream);
        } catch (Exception e) {
            System.out.println("Error loading shoot sound: " + e.getMessage());
        }
    }

    
    
    
    // Loading background music
    private void loadBackgroundMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("background_music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-3.0f); // Reducing volume
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // Looping music
            backgroundMusic.start(); // Starting music
        } catch (Exception e) {
            System.out.println("Error loading background music: " + e.getMessage());
        }
    }

    // Playing shooting sound
    private void playShootSound() {
        if (shootSound != null) {
            shootSound.setFramePosition(0); // Rewinding sound
            shootSound.start(); // Playing sound
        }
    }

    // Starting the level
    private void startLevel() {
        isEnemySpawning = true;
        lastEnemySpawnTime = System.currentTimeMillis();
        currentWave = 0;
        playerHealth = PLAYER_HEALTH; // Resetting player health
        bombUsesRemaining = BOMB_COOLDOWN; // Resetting bomb uses
    }
    
    
    
    
    
    

    // Spawning a wave of enemies
    private void spawnEnemyWave() {
        Random random = new Random();
        String era = getEraForLevel(currentLevel);
        int formationRows = 2; // Number of rows in the formation
        int formationCols = ENEMIES_PER_WAVE / formationRows; // Number of columns in the formation

        for (int i = 0; i < ENEMIES_PER_WAVE && enemiesRemaining > 0; i++) {
            int enemyX = WIDTH / 2 - (formationCols * 50) / 2 + (i % formationCols) * 50;
            int enemyY = 50 + (i / formationCols) * 50;

            Enemy enemy = createEnemy(enemyX, enemyY, ENEMY_HEALTH, currentLevel, era);
            enemies.add(enemy);
            enemiesRemaining--;
        }
        currentWave++;
    }

    // Getting the era for the current level
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
    
    
    
    

    // Creating an enemy
    private Enemy createEnemy(int x, int y, int health, int level, String era) {
        String enemyName = "";
        switch (level) {
            case 1:
                enemyName = "pharaoh";
                break;
            case 2:
                enemyName = "knight";
                break;
            case 3:
                enemyName = "soldier";
                break;
            case 4:
                enemyName = "robot";
                break;
            case 5:
                enemyName = "robot";
                break;
            default:
                enemyName = "default";
                break;
        }

        String enemySpritePath = "C:\\Users\\benti\\eclipse-workspace\\Java_assignments\\src\\daptb\\" + enemyName + "-enemy.png";
        Image enemySprite = null;

        try {
            enemySprite = new ImageIcon(enemySpritePath).getImage();
        } catch (Exception e) {
            System.err.println("Error loading enemy sprite: " + e.getMessage());
        }

        return new Enemy(x, y, health, enemyName, Color.RED, level, ENEMY_SPEED, "circle", era, enemySprite);
    }
    
    
    
    
    

    // Drawing the game
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Drawing background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 60), WIDTH, HEIGHT, new Color(10, 10, 30));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Drawing player name banner
        drawPlayerNameBanner(g);

        // Drawing level name
        drawLevelName(g);

        // Drawing player
        drawPlayer(g);

        // Drawing enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // Drawing bullets
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }

        // Drawing animations
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation animation = animations.get(i);
            animation.draw(g);
            if (animation.isFinished()) {
                animations.remove(i);
            }
        }

        // Drawing health bar
        drawHealthBar(g);

        // Drawing level and enemies remaining
        g.setColor(Color.WHITE);
        g.setFont(customFont);
        g.drawString("Level: " + currentLevel, 10, 60);
        g.drawString("Enemies Remaining: " + (enemiesRemaining + enemies.size()), 10, 90);

        // Drawing bomb uses remaining
        g.drawString("Bombs: " + bombUsesRemaining, 10, 120);

        // Drawing level complete banner if needed
        if (showLevelCompleteBanner) {
            drawLevelCompleteBanner(g);
        }
    }
    
    
    
    
    
    

    // Drawing player name banner
    private void drawPlayerNameBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 255, 255, 150)); // Semi-transparent white
        g2d.fillRoundRect(WIDTH / 2 - 150, 10, 300, 40, 20, 20); // Rounded rectangle
        g2d.setColor(Color.BLACK);
        g2d.setFont(customFont.deriveFont(20f));
        g2d.drawString("Player: " + playerName, WIDTH / 2 - 130, 35); // Player name
    }

    // Drawing level name
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
        g2d.drawString(levelName, WIDTH / 2 - 100, HEIGHT - 20); // Displaying level name
    }

    
    
    
    
    
    // Drawing player
    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Drawing the selected character's sprite
        if (isFacingLeft) {
            g2d.drawImage(playerSpriteLeft, playerX, playerY, 50, 50, null);
        } else {
            g2d.drawImage(playerSpriteRight, playerX, playerY, 50, 50, null);
        }

        // Drawing the gun extension
        int gunX = playerX + 25 + (int) (50 * Math.cos(playerAngle)); // Increased length
        int gunY = playerY + 25 + (int) (50 * Math.sin(playerAngle)); // Increased length
        g2d.setColor(Color.RED); // Changed to red
        g2d.setStroke(new BasicStroke(3)); // Thicker line
        g2d.drawLine(playerX + 25, playerY + 25, gunX, gunY);
    }
    
    
    
    

    // Drawing health bar
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
    
    
    
    

    // Drawing level complete banner
    private void drawLevelCompleteBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black
        g2d.fillRect(WIDTH / 2 - 150, HEIGHT / 2 - 100, 300, 200); // Banner background

        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont.deriveFont(30f));
        g2d.drawString("Level " + currentLevel + " Complete!", WIDTH / 2 - 130, HEIGHT / 2 - 50);
        g2d.drawString("Advance?", WIDTH / 2 - 50, HEIGHT / 2 - 20);

        // Drawing "Continue" button
        g2d.setColor(Color.GREEN);
        g2d.fillRect(WIDTH / 2 - 120, HEIGHT / 2 + 10, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Yes", WIDTH / 2 - 100, HEIGHT / 2 + 40);

        // Drawing "Quit" button
        g2d.setColor(Color.RED);
        g2d.fillRect(WIDTH / 2 + 20, HEIGHT / 2 + 10, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("No", WIDTH / 2 + 50, HEIGHT / 2 + 40);
    }

    
    
    
    
    // Handling key presses
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            keysPressed[0] = true; // Left
            isFacingLeft = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            keysPressed[1] = true; // Right
            isFacingLeft = false;
        }
        if (key == KeyEvent.VK_UP) {
            keysPressed[2] = true; // Up
        }
        if (key == KeyEvent.VK_DOWN) {
            keysPressed[3] = true; // Down
        }
        if (key == KeyEvent.VK_SPACE) {
            // Shooting in the direction the player is facing
            double bulletSpeedX = BULLET_SPEED * Math.cos(playerAngle);
            double bulletSpeedY = BULLET_SPEED * Math.sin(playerAngle);
            playerBullets.add(new Bullet(playerX + 25, playerY + 25, bulletSpeedX, bulletSpeedY, Color.RED)); // Red bullets
            playShootSound(); // Playing the shooting sound
        }
        if (key == KeyEvent.VK_G && bombUsesRemaining > 0) {
            activateBomb();
            bombUsesRemaining--;
        }
    }

    
    
    
    // Activating bomb
    private void activateBomb() {
        // Finding the three closest enemies
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

        // Killing the closest enemies
        for (Enemy enemy : closestEnemies) {
            animations.add(new Animation(enemy.x, enemy.y, 50, 50, 10)); // Adding death animation
            enemies.remove(enemy);
        }

        // Freeing the player if boxed in
        isPlayerBoxedIn = false;
    }

    // Handling key releases
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            keysPressed[0] = false; // Left
        }
        if (key == KeyEvent.VK_RIGHT) {
            keysPressed[1] = false; // Right
        }
        if (key == KeyEvent.VK_UP) {
            keysPressed[2] = false; // Up
        }
        if (key == KeyEvent.VK_DOWN) {
            keysPressed[3] = false; // Down
        }
    }

    // Unused key listener method
    @Override
    public void keyTyped(KeyEvent e) {}

    // Handling mouse movement for aiming
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        playerAngle = Math.atan2(mouseY - (playerY + 25), mouseX - (playerX + 25));
    }
    
    
    
    

    // Unused mouse motion listener method
    @Override
    public void mouseDragged(MouseEvent e) {}

    // Handling mouse clicks
    @Override
    public void mousePressed(MouseEvent e) {
        if (showLevelCompleteBanner) {
            int x = e.getX();
            int y = e.getY();

            // Checking if "Continue" button is clicked
            if (x >= WIDTH / 2 - 120 && x <= WIDTH / 2 - 20 && y >= HEIGHT / 2 + 10 && y <= HEIGHT / 2 + 50) {
                showLevelCompleteBanner = false;
                if (currentLevel < 5) {
                    currentLevel++;
                    enemiesRemaining = TOTAL_ENEMIES;
                    loadBackgroundImage(); // Loading new background for the next level
                    startLevel();
                } else {
                    // Player has completed the final level
                    if (shootSound != null && shootSound.isRunning()) {
                        shootSound.stop();
                    }
                    if (backgroundMusic != null && backgroundMusic.isRunning()) {
                        backgroundMusic.stop();
                    }
                    startFadeOutAnimation(); // Starting fade-out animation
                }
            }

            // Checking if "Quit" button is clicked
            if (x >= WIDTH / 2 + 20 && x <= WIDTH / 2 + 120 && y >= HEIGHT / 2 + 10 && y <= HEIGHT / 2 + 50) {
                System.exit(0);
            }
        }
    }
    
    
    

    // Starting fade-out animation
    private void startFadeOutAnimation() {
        Timer fadeTimer = new Timer(10, new ActionListener() {
            private float alpha = 1.0f; // Initial opacity

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.02f; // Reducing opacity
                if (alpha <= 0) {
                    ((Timer) e.getSource()).stop(); // Stopping the timer
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(environment.this);
                    frame.getContentPane().removeAll(); // Removing the current panel
                    EndScreenPanel endPanel = new EndScreenPanel(frame, true); // Creating the EndPanel
                    frame.add(endPanel); // Adding the EndPanel to the frame
                    frame.revalidate(); // Refreshing the frame
                    frame.repaint(); // Repainting the frame
                } else {
                    repaint(); // Repainting the panel with the updated opacity
                }
            }
        });

        fadeTimer.start(); // Starting the fade-out animation
    }
    
    
    
    
    

    // Unused mouse listener methods
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    // Game loop
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

    
    
    
    
    
    // Updating game state
    private void updateGame() {
        // Updating player position based on keys pressed
        if (!isPlayerBoxedIn) {
            if (keysPressed[0] && playerX > 0) playerX -= PLAYER_SPEED; // Left
            if (keysPressed[1] && playerX < WIDTH - 50) playerX += PLAYER_SPEED; // Right
            if (keysPressed[2] && playerY > 0) playerY -= PLAYER_SPEED; // Up
            if (keysPressed[3] && playerY < HEIGHT - 50) playerY += PLAYER_SPEED; // Down
        }

        // Spawning enemies in waves
        if (isEnemySpawning && enemies.isEmpty() && enemiesRemaining > 0) {
            spawnEnemyWave();
        }

        // Moving enemies and making them shoot
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            double distanceToPlayer = Math.hypot(playerX - enemy.x, playerY - enemy.y);
            if (distanceToPlayer > ENEMY_STOP_DISTANCE) {
                enemy.moveTowards(playerX, playerY, ENEMY_SPEED);
            } else {
                // Stopping and shooting
                if (enemy.canShoot()) {
                    double angle = Math.atan2(playerY - enemy.y, playerX - enemy.x);
                    double bulletSpeedX = BULLET_SPEED * Math.cos(angle);
                    double bulletSpeedY = BULLET_SPEED * Math.sin(angle);
                    enemyBullets.add(new Bullet(enemy.x + 25, enemy.y + 25, bulletSpeedX, bulletSpeedY, Color.YELLOW));
                    enemy.resetShootCooldown();
                    playShootSound(); // Playing the shooting sound
                }
            }

            // Avoiding collisions with other enemies
            enemy.avoidCollisions(enemies);

            // Maintaining distance between enemies
            enemy.maintainDistance(enemies);

            // Dodging bullets
            for (Bullet bullet : playerBullets) {
                double distanceToBullet = Math.hypot(bullet.x - enemy.x, bullet.y - enemy.y);
                if (distanceToBullet < 50) { // If bullet is close
                    double angle = Math.atan2(bullet.y - enemy.y, bullet.x - enemy.x);
                    enemy.x += 5 * Math.cos(angle + Math.PI / 2); // Moving perpendicular to the bullet's trajectory
                    enemy.y += 5 * Math.sin(angle + Math.PI / 2);
                }
            }
        }

        
        
        
        // Moving player bullets and removing off-screen bullets
        Iterator<Bullet> playerBulletIterator = playerBullets.iterator();
        while (playerBulletIterator.hasNext()) {
            Bullet bullet = playerBulletIterator.next();
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                playerBulletIterator.remove(); // Removing off-screen bullets
            }
        }

        // Moving enemy bullets and removing off-screen bullets
        Iterator<Bullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            Bullet bullet = enemyBulletIterator.next();
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                enemyBulletIterator.remove(); // Removing off-screen bullets
            }
        }

        // Checking collisions
        checkCollisions();

        // Checking if the player is boxed in by enemies
        checkIfPlayerIsBoxedIn();

        // Checking level completion
        if (enemies.isEmpty() && enemiesRemaining == 0) {
            showLevelCompleteBanner = true;
        }
    }
    
    
    

    // Checking if the player is boxed in by enemies
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

    
    
    // Checking collisions between bullets and enemies/player
    private void checkCollisions() {
        // Creating a copy of the playerBullets list to avoid concurrent modification
        ArrayList<Bullet> playerBulletsCopy = new ArrayList<>(playerBullets);
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemies);

        // Player bullets hitting enemies
        for (Bullet bullet : playerBulletsCopy) {
            if (bullet == null) continue; // Skipping null bullets
            for (Enemy enemy : enemiesCopy) {
                if (enemy == null) continue; // Skipping null enemies
                if (bullet.x >= enemy.x && bullet.x <= enemy.x + 50 &&
                    bullet.y >= enemy.y && bullet.y <= enemy.y + 50) {
                    enemy.health--;
                    if (enemy.health <= 0) {
                        animations.add(new Animation(enemy.x, enemy.y, 50, 50, 10));
                        enemies.remove(enemy); // Removing the enemy from the original list
                    }
                    playerBullets.remove(bullet); // Removing the bullet from the original list
                    break; // Exiting the inner loop after handling the collision
                }
            }
        }
        
        

        // Creating a copy of the enemyBullets list to avoid concurrent modification
        ArrayList<Bullet> enemyBulletsCopy = new ArrayList<>(enemyBullets);

        // Enemy bullets hitting player
        for (Bullet bullet : enemyBulletsCopy) {
            if (bullet == null) continue; // Skipping null bullets
            if (bullet.x >= playerX && bullet.x <= playerX + 50 &&
                bullet.y >= playerY && bullet.y <= playerY + 50) {
                playerHealth--;
                enemyBullets.remove(bullet); // Removing the bullet from the original list
                if (playerHealth <= 0) {
                    isGameRunning = false;
                    restartLevel(); // Restarting the current level on player death
                }
                break; // Exiting the loop after handling the collision
            }
        }
    }
    
    

    // Restarting the current level
    private void restartLevel() {
        playerX = WIDTH / 2 - 25; // Centering player horizontally
        playerY = HEIGHT / 2 - 25; // Centering player vertically
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;
        enemies.clear();
        playerBullets.clear();
        enemyBullets.clear();
        animations.clear();
        enemiesRemaining = TOTAL_ENEMIES;
        showLevelCompleteBanner = false;

        // Resetting enemy speed to default
        for (Enemy enemy : enemies) {
            enemy.resetSpeed(); // Adding this method to the Enemy class
        }

        startLevel();
        new Thread(this).start();
    }

    // Showing game over popup
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
            restartLevel(); // Restarting the current level after death
        } else {
            System.exit(0);
        }
    }

    // Calculating final score
    private int calculateFinalScore() {
        return (currentLevel * 1000) + (playerHealth * 10);
    }

    // Main method to start the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Environment");
        environment game = new environment("Assassin"); // Default character for testing
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
        private static final long SHOOT_COOLDOWN = 500; // Reduced cooldown for faster shooting
        private int level;
        private String type;
        private Color color;
        private int speed; // Speed of the enemy
        private String shape; // Shape of the enemy
        private String era; // Era
        private boolean canShootFromAfar; // Flag to enable shooting from afar
        private Image sprite; // Enemy sprite

        Enemy(int x, int y, int health, String type, Color color, int level, int speed, String shape, String era, Image sprite) {
            this.x = x;
            this.y = y;
            this.health = health;
            this.lastShotTime = System.currentTimeMillis();
            this.level = level;
            this.type = type;
            this.color = color;
            this.speed = speed; // Setting enemy speed based on level
            this.shape = shape; // Setting enemy shape
            this.era = era; // Setting enemy era
            this.canShootFromAfar = false; // Defaulting to not shooting from afar
            this.sprite = sprite; // Setting enemy sprite
        }

        void setCanShootFromAfar(boolean canShootFromAfar) {
            this.canShootFromAfar = canShootFromAfar;
        }

        void resetSpeed() {
            // Resetting speed to default based on level
            switch (level) {
                case 1:
                    speed = 2;
                    break;
                case 2:
                    speed = 3;
                    break;
                case 3:
                    speed = 6;
                    break;
                case 4:
                    speed = 7;
                    break;
                case 5:
                    speed = 8;
                    break;
                default:
                    speed = 2; 
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
                        // Moving away from the other enemy
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
                        // Moving away from the other enemy
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
            if (sprite != null) {
                g.drawImage(sprite, x, y, 50, 50, null); // Drawing enemy sprite
            } else {
                g.setColor(color);
                switch (shape) {
                    case "circle":
                        g.fillOval(x, y, 50, 50); // Drawing enemy as a circle
                        break;
                    case "rectangle":
                        g.fillRect(x, y, 50, 50); // Drawing enemy as a rectangle
                        break;
                    case "triangle":
                        int[] xPoints = {x + 25, x, x + 50};
                        int[] yPoints = {y, y + 50, y + 50};
                        g.fillPolygon(xPoints, yPoints, 3); // Drawing enemy as a triangle
                        break;
                    default:
                        g.fillOval(x, y, 50, 50); // Defaulting to circle
                        break;
                }
            }
        }
    }

    // Bullet class
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

    // Animation class
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